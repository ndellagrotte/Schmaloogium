# Schmaloogium — Phase 6: Uniform & sampler system — Architecture

## 0. Header

| Field | Value |
|---|---|
| Phase | 6 — Uniform & sampler system |
| Document revision | v1, maintained architecture through §0.29 |
| Date | 2026-07-29 |
| Governing design | `docs/design/v2.0-RC3/DESIGN.md` |
| Milestone | v0.1; shadow/celestial values v0.2 |
| Declared dependencies | Phases 1, 3, and 4 |
| Additional maintained dependency | Phase 5's pure fixed-sampler resolver, adopted under R7-10; owner verification gates in §5.2 |
| Assigned open questions | none |

This document originated as the Phase 6 build-session deliverable and still designs architecture
only. The original fresh build session did not implement source code, change a dependency
document, create a verification profile, or perform an adversarial review; §§0.3–0.24 record later
governed maintenance. The governing assignment says the deliverable is `PHASE_6_DOC.md`
(`docs/design/v2.0-RC3/DESIGN.md:1702`, “**Deliverable.** `PHASE_6_DOC.md` per §G9”), and the
mandatory skeleton is the thirteen sections reproduced at
`docs/design/v2.0-RC3/DESIGN.md:790`.

### 0.1 Inputs actually read

Governing and repository inputs:

- `AGENTS.md`, complete.
- `docs/MOVES.md`, complete, including the `DESIGN.md` collision warning.
- `docs/design/v2.0-RC3/DESIGN.md`, all of Part I (§G0–§G12) and the complete Phase 6
  assignment at lines 1689–1801. In particular, the objective is the exact built-in inventory,
  sampler re-pointing, smoothing, and pure provider seam
  (`docs/design/v2.0-RC3/DESIGN.md:1698`, “The ~50 built-in uniforms with their exact semantics
  and update cadences”).

Primary contract inputs:

- `docs/research/v1/RESEARCH.md` §0 and §1, §3.4, §4.2, §4.4, Appendix A.3,
  Appendix B.3, and Appendix D in full. The authority rule matters here: “The contract” is the
  fixed pack-author-facing surface (`docs/research/v1/RESEARCH.md:47`), and the built-in inventory
  is Appendix D (`docs/research/v1/RESEARCH.md:290`).
- `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt`, “Uniforms”, “GBuffers Uniforms”,
  “Shadow Uniforms”, and “Composite and Deferred Uniforms”, lines 123–266. This is a shipped
  pack-author document, not decompiled implementation source.

Pintonium evidence inputs, under §G11:

- `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` §6 in full and §17 rows B1/B6, as
  assigned. The dynamic-unit divergence in §18 was also read because sampler mechanics cannot be
  adopted safely without its fixed-map warning.
- The following LGPL-3.0 source files were opened to verify the load-bearing PD claims:
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/program/ProgramUniforms.java`,
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/program/ProgramSamplers.java`,
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/uniform/Uniform.java`,
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/CommonUniforms.java`,
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/FrameUpdateNotifier.java`,
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/CapturedRenderingState.java`,
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/MatrixUniforms.java`,
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/SystemTimeUniforms.java`,
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/transforms/SmoothedFloat.java`,
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/transforms/SmoothedVec2f.java`,
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pathways/CenterDepthSampler.java`,
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/state/StateUpdateNotifiers.java`,
  and
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/properties/PackDirectives.java`.
- The 1.12.2 capture claim was checked at
  `reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java`
  and
  `reference-src/pintonium-9c2fcc1/forge122/src/main/java/org/taumc/celeritas/mixin/core/terrain/ActiveRenderInfoAccessor.java`.
  The former copies the projection/model-view buffers
  (`reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java:38`,
  “setGbufferProjection”) at the ordinal-zero clear boundary
  (`reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java:102`);
  the latter exposes the two vanilla FloatBuffers
  (`reference-src/pintonium-9c2fcc1/forge122/src/main/java/org/taumc/celeritas/mixin/core/terrain/ActiveRenderInfoAccessor.java:11`,
  “PROJECTION”).

Dependency documents, read complete:

- `docs/phase1/v14/PHASE_1_DOC.md`
- `docs/phase3/v1/PHASE_3_DOC.md`
- `docs/phase4/v1/PHASE_4_DOC.md`

At build-session time, the highest-numbered reviews were:
`docs/phase1/reviews/PHASE_1_REVIEW_18.md`,
`docs/phase3/reviews/PHASE_3_REVIEW_14.md`, and
`docs/phase4/reviews/PHASE_4_REVIEW_11.md`. They were the verified dependency gates actually read
by the fresh build session. Section 0.6 separately records the later verified amendments adopted by
this maintenance session.

### 0.2 Deviations and limits

- Appendix A.3 was read in addition to the assigned RESEARCH sections because the GPU
  `centerDepthSmooth` candidate depends on the declaration-trigger contract:
  `docs/research/v1/RESEARCH.md:1167` says “`uniform … centerDepthSmooth`” enables the readback.
- Phase 5 was not read. It is a same-wave sibling, not a declared dependency. This document
  preserves the assigned split: Phase 5 owns the texture object behind each unit; Phase 6 owns
  sampler-uniform integer values.
- No Oculus digest/content was used. RC3 §G12 does not add it to Phase 6's required reading.
- No `docs/**/chatlogs/` path and no repository-root `*.txt` was read.
- No file from `glsl-transformation-lib`, the prohibited AGPL dependency, was opened or used.
- At build-session time there was no Phase 6 brief or `verification/targets/phase6*.json` in the
  checkout. That historical absence was not a reason to invent an assignment; §0.6 records the
  data-only target added later.

### 0.3 Review-1 corrections

Review 1 completed the loader-neutral request/sample/event schemas and made their validation,
identity, absence, and copy rules explicit. It also fixed the held-light contract: old-hand-light
publishes the brighter hand through `heldBlockLightValue` while `heldBlockLightValue2` remains the
off-hand light value. Full rationale is in the review's `## Resolutions`.

### 0.4 Review-2 corrections

Review 2 closed the construction/frame-begin result types and the Phase 11 view, typed-command,
sink, and refresh-result boundary. The binding definitions are in §§2.2, 4.6, 4.13, 5.1, and 6.

### 0.5 Review-3 corrections

Review 3 fixed custom-bridge installation and aborted-prefix disposition, and added the missing
frame-begin-before-resize/clear conformance-map row. Binding details are in §§4.13 and 5.1.

### 0.6 Dependency-adoption maintenance addendum (2026-07-29)

After round four's literal PASS, the two dependency requests in the original §5.3 were implemented
through their governed fix-up/re-verification loops. Phase 3 now publishes final attributed
`DeclaredUniformCatalog`s and a closed structural type algebra; round twenty verifies that
interface (`docs/phase3/reviews/PHASE_3_REVIEW_20.md:56`–`:67`). Phase 4 now merges those catalogs
into an effective handle-free `ProgramUniformLayout` and supplies callback-scoped lookup plus an
operation-free activity token; round eighteen verifies that interface
(`docs/phase4/reviews/PHASE_4_REVIEW_18.md:55`–`:70`). Sections 2, 4, 5, 8, 9, 11, and 12 now
consume those grants rather than describing unimplemented requests.

The data-only `verification/targets/phase-6.json` profile also exists and drove rounds one through
four. The RESEARCH custom-expression discrepancy and future-DESIGN Candidate-B clarification
remain pending authority-maintainer items. Neither blocks this architecture: §4.13 keeps the
conservative union exclusion and D-P6-1 keeps the verified Candidate A.

**Current §G1.3 status:** Reviews 19–23 continued correction of the §0.18 maintenance. Review 23
required the §0.22 fix-up, which changes §5; the current bytes remain **not verified** until a
fresh review returns literal PASS with zero blocking findings and zero corrections. The version
directory remains `v1` meanwhile.

### 0.7 Review-5 correction

The conditional `shadow` sampler now derives from the effective program's published
`ProgramUniformLayout`; Phase 6 consumes no Phase 3 water-shadow field or world constants.

### 0.8 Review-6 correction

Maintenance provenance and §G1.3 status now include the resolved Review-5 correction and the
current verification state.

### 0.9 Review-9 correction

Construction now installs the initial Phase 4 publication generation, and the runtime exports an
atomic replacement-generation adoption handshake before frame begin or activation.

### 0.10 Review-10 corrections

Shaders-off retains borrowed providers until terminal close, GL-context loss is included in the
lifecycle reset inventory, and maintenance provenance now includes §0.9.

### 0.11 Review-11 correction

All compact maintenance-provenance markers now include the Review-10 and Review-11 addenda.

### 0.12 Review-12 corrections

The current verification status is synchronized, and the public reset-reason domain and operation
partition are now closed in §§2.2, 4.14, and 5.1.

### 0.13 Review-13 correction

The threading table now names generation adoption separately from direct reset.

### 0.14 Review-14 corrections

Replacement adoption and direct resets now state their observable lifecycle ordering.

### 0.15 Review-15 correction

Terminal close now occurs after final Phase 6 participant use and before Phase 7 initiates the
existing atomic Phase 4 teardown operation.

### 0.16 Review-16 corrections

Terminal close is teardown-only; ordinary publication replacement remains exclusively a generation
adoption. The `blendFunc` conformance row now cites the matching Appendix E hook row.

### 0.17 Review-17 correction

All compact maintenance-provenance markers now include §0.16 and this fix-up addendum.

### 0.18 Phase-11 dependency-closure maintenance addendum (2026-08-03)

Phase 11 requested a load-time exact-name input schema, a boolean upload command whose GL encoding
stays in Phase 6, and a normal absent-active-program submission outcome
(`docs/phase11/v1/PHASE_11_DOC.md:894`–`:910`, “Compile-time input schema”, “Boolean upload”, and
“Normal active-program absence”). This Phase-6-owned maintenance publishes those contracts in
§§4.13 and 5.1 and synchronizes the failure, test, milestone, decision/handoff, and implementation
surfaces. It does not assume Phase 11 evaluator internals.

The permitted schema and runtime view retain the conservative union already recorded here:
Appendix D.4 calls its entire five-row table “excluded from custom-uniform expressions”
(`docs/research/v1/RESEARCH.md:1370`), while Appendix F.6 separately names only
`entityColor entityId blockEntityId fogMode fogColor`
(`docs/research/v1/RESEARCH.md:1501`–`:1505`). Therefore all five D.4 names plus `fogMode` and
`fogColor` remain excluded. The conflicting protected-source wording is reported, not claimed
resolved, and still requires an explicitly authorized RESEARCH-maintainer action.

Review 18 had verified the preceding bytes with `PASS` and `blocking=0; corrections=0`
(`docs/phase6/reviews/PHASE_6_REVIEW_18.md:49`–`:50`). Because this addendum changes §5, RC3's
protocol says the document “goes through a fresh verify session”
(`docs/design/v2.0-RC3/DESIGN.md:327`–`:329`). **Current §G1.3 status:** the new interface is
unreviewed and Phase 6 is not a valid dependency input until that fresh review returns literal PASS
with zero blocking findings and zero corrections. No version roll occurs while that loop is open.

### 0.19 Review-19 corrections

Custom-refresh counter violations now discard that refresh's batch and return an existing Phase 4
participant degradation, with a fresh retry only at the next activation. Two stale Phase 1
conformance-map coordinates now cite their current binding-contract rows.

### 0.20 Review-20 corrections

Compact maintenance provenance was synchronized. Its former additive Phase 1 upload request is
superseded and removed by §0.22.

### 0.21 Review-21 corrections

Current status, §5 synchronization coverage, and two Pintonium row-local mappings are corrected.

### 0.22 Review-23 corrections

Custom upload outputs now match the authoritative six-type declaration grammar. Section 5 now
incorporates §2.2's complete consumer-visible runtime signatures and synchronizes their changes.

### 0.23 R7-10 sole-sampler-policy adoption (2026-09-07)

This maintainer-authorized architecture-only amendment adopts R7-10 from
`docs/phase7/v1/PHASE_7_DOC.md:4030`: “The unchanged Phase 6 sampler participant calls it
using binding.samplerLayout and context”. `UniformRuntimeFactory.create` now receives Phase 5's
`FixedSamplerResolver` immediately after configuration. Active §§1–5/7–9/11–12 consume that sole
policy instead of publishing or implementing a second map. The `afterBind` signature, three
participant positions, integer upload order, exact-name location/value caches, activity-token
invalidation, and attributed error replay remain unchanged. No participant, allocator, physical
texture binding, independent activation, or lifecycle operation is added.

Additional scoped inputs actually read: `docs/MOVES.md`; RC3 Part I and the Phase 6 assignment;
`docs/research/v1/RESEARCH.md` §§0–1.3 and Appendix B.3;
`docs/phase7/v1/PHASE_7_DOC.md` §0 and §§5.3–5.4's R7-10/11 contracts;
`docs/phase5/v1/PHASE_5_DOC.md` §0.39, §§2.4/4.12.1 and their incorporated §5 interfaces;
and `docs/phase4/v1/PHASE_4_DOC.md` §0, sampler/descriptor/callback declarations and their §5
incorporation. Phases 4, 5, 6, and 7 all retain their declared RC3 governance. These reads are the
requested maintenance's narrow sibling/downstream-contract exception, not a dependent build or a
claim that the newly coordinated owner bytes have passed §G5.3.

The original §0.2 “Phase 5 was not read” and §0.7 uniform-layout-derived `shadow` record remain
historical; §4.9 now delegates the conditional rule to Phase 5 using `ProgramSamplerLayout`.
Phase 5's published resolver and Phase 4's effective sampler-layout contract are present but await
fresh owner verification (§5.2). **R7-11 remains ungranted**: this amendment does not change
§4.14's reset/CLOSE contract or unblock Phase 7's candidate/replacement retirement protocol.

**Current §G1.3 status (superseding earlier status notes):** R7-10 is adopted in Phase 6's current
binding bytes, not a verified grant. Changed §5, including incorporated §§2.2/4.9, requires fresh
whole-document verification returning literal PASS with zero blocking findings and zero
corrections before verified downstream consumption
(`docs/design/v2.0-RC3/DESIGN.md:327-332`, “before any dependent consumes it”). No code, reviews,
builds, tests, verification runs, other documents, or directory rolls are part of this amendment.

### 0.24 R7-11 permanent-runtime retirement adoption (2026-09-07)

This maintainer-authorized architecture-only amendment adopts
`docs/phase7/v1/PHASE_7_DOC.md:4038-4044`: “UniformRetirementResult retire(UniformRetirementReason
reason)”. The adoption-time clause “its owner must reconcile that rule, not add a conflicting
alias” is historical; current bytes state “Phase 6 removed reset(CLOSE); Phase 11's own terminal
controller CLOSE is distinct and precedes this retirement after final custom use”. Sections 2.2/4.14/5
now publish non-GL retirement for `UNPUBLISHED_ABORT`, `REPLACEMENT`, and `SHUTDOWN`, with
`Retired`, `AlreadyRetired`, and `Rejected(WRONG_THREAD|ACTIVE_CALLBACK)` outcomes.
`CLOSE` is removed from the active reset domain; there is no `reset(CLOSE)` compatibility route
or runtime `close()`. Non-terminal generation adoption remains distinct from retiring an old
runtime and adopting the accepted generation on its replacement.

Scoped inputs actually read: `docs/MOVES.md`; RC3 Part I and the Phase 6 assignment;
`docs/research/v1/RESEARCH.md` §§0–1.3, §4.2 and the §4.4 frame sequence;
this document's runtime/event/participant/custom/lifecycle contracts and maintained hand-offs;
`docs/phase7/v1/PHASE_7_DOC.md` §0, §4.1 and §§5.2–5.4's lifecycle request/protocol;
and `docs/phase4/v1/PHASE_4_DOC.md` §0, activity-token/publication lifetime and their §5
incorporation. These are narrow maintenance reads authorized by this request, not a dependent
build's verified consumption. All three phase documents retain their declared RC3 governance.

Earlier addenda, including §§0.10/0.15/0.16/0.23, remain historical and unchanged. This addendum
supersedes their terminal-close, replacement-only-adoption, and R7-11-ungranted statements,
not their record of what earlier sessions changed. Active contracts, threading, failure handling,
testability, staging and implementation hand-offs now distinguish reset from terminal retirement.

**Current §G1.3 status (superseding earlier status notes):** R7-10 and R7-11 are adopted in
Phase 6's current binding bytes, not verified grants. Changed §5, including incorporated
§§2.2/4.9/4.13/4.14, requires fresh whole-document verification returning literal PASS with
zero blocking findings and zero corrections before verified downstream consumption
(`docs/design/v2.0-RC3/DESIGN.md:327-332`, “before any dependent consumes it”).
Section 5.2 preserves ungranted dependency and owner-verification gates. No code, reviews,
builds, tests, verification runs, other documents, or directory rolls are part of this amendment.

### 0.25 Integration remediation (2026-09-07)

IR-03/04/10/11/21 synchronize current dependency grants, split frame/matrix timing, retain the
governing v0.1 Phase 7 entityColor producer and adopt the authenticated atlas-bind adapter.
RC3 remains governing; historical addenda/reviews are preserved. Active §§4/5/11 retain terminal
`retire(reason)` and its rejection/service-lifetime rules, never CLOSE compatibility.
§5 changed: this document remains unverified and requires fresh whole-document owner verification
before implementation consumption. Documentation only; no builds/tests/formatters or PASS claims.

---

### 0.26 R39 timing-owner and R55 schema receipts (2026-09-08)

Architecture-only D-P6-25 grants the actual accepted-frame timing snapshot to P7/P2;
D-P6-26 adopts P3 schema21. §5 changed and remains unverified; historical reviews,
governing RC3 and normal gameplay clocks are unchanged. No execution or PASS is claimed.

### 0.27 R25 replay transport and P8 angular-policy receipt (2026-09-08)

D-P6-27 publishes the required P7-supplied replay observer and immutable ordered report;
D-P6-28 receives P8's pure angular policy only through P7's mod provider route. D-P6-29
reconciles active instance bounds and D-P6-30 closes the two authority/tooling notes.
R25's original review and verdict remain unchanged; its separate Resolutions record these
fix-ups, not a self-PASS. Changed §5 requires fresh whole-document owner/receiver review.
P1 R29/P3 R56 PASS and P7 R37 historical PASS are not recertification of these changed bytes;
P7's receiving change requires R38. Architecture only; no execution is claimed.

### 0.28 Capture-wire receiving amendment (2026-09-08)

D-P6-31 receives P2's current `/4` capture envelope for the unchanged timing and replay
values. Earlier dated `/3` handoffs record the prior envelope, not an alternate current
encoding. P6 does not serialize profiles, comparisons or resources; P2 owns those fields
and P7 copies the P6 values without changing their error law or attribution.
R26's literal PASS remains evidence for its frozen document, not certification of this
receiving amendment or the coordinated backend changes. Fresh verification remains required.

### 0.29 R27 active-order and provenance correction (2026-09-08)

D-P6-33 aligns every active flow/table/explanation with P4's close/bind/acquire/effective
notification/participant order. P6 consumes observed effective state, not a prediction of
a later lock. D-P6-34 qualifies the historical source label: the independently read
[pinned root LICENSE](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/LICENSE)
is GPL version 3. The older LGPL input label alone is not a file-specific reuse grant;
future incorporation must establish applicable per-file licensing and preserve notices.
No implementation is copied. Original review/authority text remains historical, and
the changed §5-incorporated choreography requires fresh review.

## 1. Scope & boundaries

### 1.1 Owned here

Phase 6 owns:

1. the complete Appendix D built-in uniform catalog: exact pack-facing names, GLSL types,
   meanings, acquisition cadence, upload policy, provider, and milestone;
2. the pure-`:engine` cadence/value-cache machinery and the three Phase 4 barrier participants;
3. per-effective-program uniform-location caches, value deduplication, and the matrix
   always-upload exception;
4. sampler-uniform integer re-pointing through Phase 5's sole fixed policy for gbuffers/shadow and
   deferred/composite/final programs;
5. frame-begin sampling, temporal snapshots, CPU smoothing, center-depth readback policy, matrix
   capture/inversion, and event-driven per-draw values;
6. loader-neutral provider and event interfaces implemented or invoked from `mod.glue`;
7. the Phase 11 extension boundary: fixed expression-input schema, conforming built-in view,
   ordered typed submission/refresh outcomes, and custom-uniform participant placed after built-ins;
8. per-uniform GL upload isolation using Phase 1's attributed replay protocol;
9. the notifier-to-producer contract consumed by Phase 7 and later hook owners; and
10. permanent non-GL runtime retirement, including invalidation of every retained event/participant
    capability and release of borrowed references; Phase 7 owns quiescence and service ordering.

### 1.2 Adjacent concerns, explicitly not owned here

- **Owned by Phase 1:** module/seam rules, `engine.gl`, opaque handles, `UniformService`,
  `FramebufferService.readDepthPixel`, recording/replay, diagnostics, and the GL-error drain
  protocol. Phase 6 consumes them.
- **Owned by Phase 3:** parsing uniform declarations and half-life directives, the immutable pack
  configuration, resource requirements, source materialization, and the reserved macro-contributor
  slot. Phase 6 neither reopens nor rescans shader source.
- **Owned by Phase 4:** program compilation, handles, effective-provider fallback, generation,
  program activation, participant ordering, and alpha/blend locks. Phase 6 never calls
  `ShaderService.use` or resolves a backup chain itself.
- **Owned by Phase 5:** the sole fixed sampler-name/unit policy and pure resolver, texture
  creation/lifetime, the texture object bound behind each fixed unit, framebuffer estate, flip
  state, and depth copies. Phase 6 consumes the resolver; re-pointing a sampler uniform to unit 7
  is not binding a texture to unit 7.
- **Owned by Phase 7:** frame orchestration and v0.1 vanilla/Mixin producers, including frame
  begin, post-camera matrix capture, GlStateManager observation and scoped `entityColor`.
  Its celestial event wiring is v0.2 and composite/deferred `instanceId` loop is v0.5.
- **Owned by Phase 8:** shadow-camera setup and the values of the shadow/celestial set at v0.2.
- **Owned by Phase 9:** alias resolution and the values of held-item, `entityId`, and
  `blockEntityId` inputs at v0.3.
- **Owned by Phase 10:** vertex-pipeline work. It does not upload Appendix D uniforms.
- **Owned by Phase 11:** parsing/evaluating custom expressions and rung-1 expression isolation.
  Phase 6 supplies only the ordered activation bridge and upload/error plumbing.
- **Owned by Phase 13:** the `atlasSize` query/value source at v0.5; Phase 7 authenticates actual
  current-bind evidence and joins it to this phase's existing sink (§4.12).
- **Owned by Phase 14:** the optional PBO/fence replacement for synchronous center-depth readback.
  This document deliberately leaves that ledger item live.

Custom expressions, vanilla hooks, alias computation, shader compilation, texture binding, and
buffer allocation are therefore scope-out, matching the assignment at
`docs/design/v2.0-RC3/DESIGN.md:1776`.

---

## 2. Architecture overview

### 2.1 Placement

Production types split across the Phase 1 seam:

```text
:engine
  com.schmaloogium.engine.uniforms
    catalog/       Appendix D built-in definitions (no sampler-name/unit map)
    runtime/       value cells, cadence clock, temporal state, program caches
    smooth/        tick-domain asymmetric EMA
    matrix/        immutable 4×4 values, inverse and previous snapshots
    barrier/       sampler, built-in and custom participants
    spi/           loader-neutral providers and event inputs

:mod
  com.schmaloogium.mod.glue.uniforms
    Minecraft/Forge/GlStateManager-backed providers and Phase 7 hook adapters
```

`:engine` sees JDK types plus Phase 1/3/4/5 published `:engine` interfaces, subject to §5.2's gates.
No Minecraft, Forge, Cleanroom, Mixin, LWJGL, JOML, or raw GL name crosses the seam. This is a correctness constraint,
not a packaging preference: D-6 requires the core to remain headless-testable
(`docs/research/v1/RESEARCH.md:100`, “Engine-core / loader-glue seam”).

### 2.2 Key types and relationships

The public shape is:

```java
public interface UniformRuntimeFactory {
    UniformBuildResult create(
        long initialRegistryGeneration,
        UniformConfiguration configuration,
        FixedSamplerResolver samplerResolver,
        UniformPlatformProvider platform,
        CenterDepthSource centerDepth,
        GLDevice gl,
        DiagnosticReporter diagnostics,
        UniformReplayErrorSink replayErrors);
}

public interface UniformReplayErrorSink {
    void accept(UniformReplayReport report);
}

public record UniformReplayReport(
    ProgramUniformCacheKey program, List<ReplayAwareGLError> errors) {}

public sealed interface UniformBuildResult {
    record Success(UniformRuntime runtime) implements UniformBuildResult {}
    record Failure(String diagnosticId) implements UniformBuildResult {}
}

public interface UniformRuntime {
    RegistryGenerationAdoptionResult adoptRegistryGeneration(
        long registryGeneration, UniformResetReason reason);
    FixedExpressionInputSchema fixedExpressionInputSchema();
    FrameBeginResult beginFrame(FrameBeginInput input);
    Optional<UniformFrameTiming> frameTiming(long registryGeneration, long frameId);
    UniformEventSink events();
    ProgramBindingParticipant samplerParticipant();
    ProgramBindingParticipant builtInParticipant();
    ProgramBindingParticipant customParticipant();
    MacroContributor centerDepthMacroContributor();
    void installCustomUniformBridge(CustomUniformBridge bridge);
    void reset(UniformResetReason reason);
    UniformRetirementResult retire(UniformRetirementReason reason);
}

public enum RegistryGenerationAdoptionResult {
    ADOPTED, ALREADY_CURRENT, REJECTED_RETIRED_GENERATION
}

public enum UniformResetReason {
    PACK_REPLACEMENT, SHADERS_OFF, GL_CONTEXT_LOSS, WORLD_EPOCH
}

public enum UniformRetirementReason {
    UNPUBLISHED_ABORT, REPLACEMENT, SHUTDOWN
}

public sealed interface UniformRetirementResult {
    record Retired() implements UniformRetirementResult {}
    record AlreadyRetired() implements UniformRetirementResult {}
    record Rejected(UniformRetirementRejection reason) implements UniformRetirementResult {}
}

public enum UniformRetirementRejection {
    WRONG_THREAD, ACTIVE_CALLBACK
}

public enum FrameBeginResult {
    ACCEPTED, DUPLICATE, REJECTED_STALE_FRAME, REJECTED_GENERATION
}

public record FrameBeginInput(
    long registryGeneration,
    long frameId,
    long worldEpoch,
    long logicalTick,
    double smoothingTimeTicks,
    float frameTimeSeconds,
    int targetViewWidth,
    int targetViewHeight,
    int priorFramebufferWidth,
    int priorFramebufferHeight) {}

public record UniformFrameTiming(
    long registryGeneration, long frameId, long worldEpoch, long logicalTick,
    double smoothingTimeTicks, float frameTimeSeconds,
    int frameCounter, float frameTimeCounter) {}

public interface UniformEventSink {
    void captureGbufferMatrices(long frameId, Matrix4Value modelView, Matrix4Value projection);
    void updateCelestial(CelestialSample sample);
    void updateShadowMatrices(ShadowMatrixSample sample);
    void updateFog(FogSample sample);
    void updateBlend(BlendSample sample);
    void updateEntityColor(Float4 value);
    void updateEntityId(int value);
    void updateBlockEntityId(int value);
    void updateInstanceId(int value);
    void updateAtlasSize(Int2 value);
    void updateHeldItems(HeldItemSample value);
}
```

The signatures are illustrative but the data contracts are binding. `Success` contains the only
operational runtime and transfers its lifecycle to the caller; the factory retains nothing.
`Failure` contains one non-empty stable diagnostic ID, contains no runtime, performs no GL
work, and leaves all supplied services caller-owned. A successful runtime retains borrowed service
references until successful `retire`; it owns only its caches, snapshots, and participants. The complete
provider-record validation, absence, and copy rules are in §4.2. `Matrix4Value` stores exactly 16
floats in the facade's upload order and exposes no mutable array.

`samplerResolver` is the non-null borrowed pure service returned by Phase 5's
`FixedSamplerPolicies.resolver()`, paired with the same table/schema/fingerprint as the
`FixedSamplerPolicies.appB3()` policy supplied to Phase 4 compilation. Both factories are
available before any runtime, registry, estate, or GL object exists
(`docs/phase5/v1/PHASE_5_DOC.md:2441`, “They share one table/schema”).
Missing resolver input rejects construction through `UniformBuildResult.Failure` without GL;
there is no default/local-map fallback. The runtime retains this borrowed reference under the
existing service lifetime and releases it on `Retired`; it never owns or closes Phase 5.
The new dependency and its owner-review gates are binding in §5.2.

`replayErrors` is required, non-null and P7-supplied/owned; missing input returns
`UniformBuildResult.Failure` before GL or callbacks. The exact eight-argument factory has
no old overload, default observer or no-op fallback. P6 owns the sink interface/report in
`engine.uniforms`; P1 retains `engine.gl.ReplayAwareGLError` and its attribution law.
The observer is borrowed through successful terminal retirement, including final restoration
uploads; reset/adoption do not release it. §4.11 specifies synchronous delivery and containment.
Reports are detached immutable values: non-null handle-free `program`, a defensive immutable
nonempty list with non-null entries, and the original immutable P1 `GLError` objects unchanged.
P7 may retain/copy them without retaining a runtime, token, location or borrowed service.

`UniformRuntime` owns no program handle. Phase 4 owns the active linked program and calls the three
participants in sampler → built-in → custom order
(`docs/phase4/v1/PHASE_4_DOC.md:1751-1755`, “The three positions are”).
`BoundProgramUniformAccess` supplies callback-scoped location lookup without revealing that handle
(`docs/phase4/v1/PHASE_4_DOC.md:1646-1656`, “UniformLocation locate(String exactName)”).
The current coordinated Phase 4 bytes remain subject to §5.2's fresh-owner-review gate.

### 2.3 Data flow

```text
Phase 7 frame-begin hook
  → beginFrame (world/tick/frame sample + previous snapshots + sync center-depth sample)
  → Phase 5 resize/clear may begin
  → later Phase 7 post-camera hook captures current gbuffer matrices (not ordinal-zero clear)
  → Phase 4 authenticates, invalidates predecessor activity, closes its lease, binds effective program
      → acquires effective alpha/blend lease and publishes/samples coherent effective blend
      → sampler participant uploads fixed integers resolved by Phase 5 from effective layout/context
      → built-in participant uploads current immutable cells
      → custom participant asks Phase 11 to evaluate from the stable built-in view
  → per-draw/celestial/fog/atlas hooks replace event cells and, while a Phase-4 activity token
    remains current, immediately upload changed values without rebinding
  → shadow/matrix hooks replace cells for the next activation
```

Provider evaluation never occurs during the barrier's GL-error replay. A frame/tick/event updates a
typed value cell once; an activation snapshots the cells into immutable upload commands; replay uses
those exact commands.

### 2.4 Two different meanings of cadence

This design separates:

- **acquisition cadence** — when a provider/event may replace a cell (`ONCE`, `PER_TICK`,
  `PER_FRAME`, or `SIGNAL`); and
- **activation policy** — every successful Phase 4 shader activation visits every declared,
  enabled built-in and sampler, regardless of acquisition cadence.

That separation reconciles “everything refreshes on program switch”
(`docs/research/v1/RESEARCH.md:1380`) with tick/frame sampling and redundant skipping. “Refresh”
means participate in the activation sweep; it does not mean resample Minecraft or advance an EMA.

---

## 3. Contract conformance map

| In-scope contract item | Design element | Provenance / disposition |
|---|---|---|
| Full Appendix D names, types, and values | §4.4 exhaustive inventory | `[V:doc]`; source inventory begins at `docs/research/v1/RESEARCH.md:1319`, “Built-in uniform inventory” |
| Everything refreshes on program switch | §4.3 activation sweep and §4.10 participant trace | `[V:observed]`; `docs/research/v1/RESEARCH.md:1380`, “everything refreshes on program switch” |
| Per-program location cache and redundant skip; matrices always upload | §4.3, §4.10, §4.11 | `[V:observed]`; `docs/research/v1/RESEARCH.md:1380`, “matrices always upload” |
| Final declaration/type truth without source reopening | Phase 3 catalog → Phase 4 merged effective layout → Phase 6 plan validation, §§4.9–4.10/5.3 | verified dependency contracts at `docs/phase3/v1/PHASE_3_DOC.md:1435` and `docs/phase4/v1/PHASE_4_DOC.md:1910` |
| Bound lookup and between-activation proof without a program handle | callback-scoped access, generation/provider/layout cache key, and operation-free activity token in §4.10 | verified Phase 4 contract at `docs/phase4/v1/PHASE_4_DOC.md:2131`; D-6 |
| Celestial, shadow, and per-draw event moments | typed `UniformEventSink`, §4.6/§4.12 | `[V:observed]`; exact cadence list at `docs/research/v1/RESEARCH.md:1380` |
| Custom expressions consume a fixed typed input set and upload `float/int/bool/vec2/vec3/vec4` only after built-ins | versioned schema, conforming runtime view, typed command/disposition algebra, and third barrier participant in §4.13 | `[V:doc]`; declaration types at `docs/research/v1/RESEARCH.md:1495`, “`uniform.<float\|int\|bool\|vec2\|vec3\|vec4>`”; cadence at `docs/research/v1/RESEARCH.md:1383`, “custom uniforms … after built-ins” |
| Fixed unit map, including stage variants | Phase 5 sole resolver consumed by §4.9; no Phase 6 map | `[V:doc]`; `docs/research/v1/RESEARCH.md:1228`, “packs rely on these numbers”; owner interface at `docs/phase5/v1/PHASE_5_DOC.md:2726`, “same fingerprint, exact spellings and conditional watershadow rule” |
| `depthtex1` is unit 11 | shared-resolver upload regression in §8.1 | `[V:doc]` + ruling; `docs/research/v1/RESEARCH.md:1254`, “Treat **11 as authoritative**” |
| P5/P6 split | Phase 5 alone resolves fixed units and binds objects; Phase 6 locates exact names and writes sampler integers | R7-10 at `docs/phase7/v1/PHASE_7_DOC.md:4033`, “No second map”; Phase 5 owner interface at `docs/phase5/v1/PHASE_5_DOC.md:2726`, “no second map or free-unit allocation” |
| World state sampled at frame begin | stable `FrameSnapshot`, §4.6 | `[V:observed]`; `docs/research/v1/RESEARCH.md:534`, “frame start” |
| Frame-begin sampling completes before resize/clear | ordering rule in §§4.6 and 5.1 | governing REV1 constraint; `docs/design/v2.0-RC3/DESIGN.md:1721-1725`, “before any buffer resize or clear” |
| Previous camera/matrix snapshots | explicit rotate-before-overwrite rules, §4.6/§4.7 | `[V:observed]`; `docs/research/v1/RESEARCH.md:537`, “snapshot previous-frame camera + matrices” |
| Synchronous center-depth readback when declared | CPU candidate selected in §4.8 | `[V:observed]`; `docs/research/v1/RESEARCH.md:558`, “synchronous center-depth readback” |
| Exact tick-domain smoothing | `TickEma` / `AsymmetricTickEma`, §4.5 | contract interpretation; RC3 requires “per-tick exponential decay” at `docs/design/v2.0-RC3/DESIGN.md:1733` |
| Fixed-function matrix capture and inverses | §4.7 | `[V:observed — Pintonium]`; verified source copies vanilla buffers at `reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java:38` |
| `blendFunc` observation through GlStateManager cooperation | typed producer plus mandatory audit row, §4.12 | `[V:doc]`; App E class row at `docs/research/v1/RESEARCH.md:1416` |
| Every value behind a pure provider/event seam | `UniformPlatformProvider`, `CenterDepthSource`, immutable event records | D-6; assignment at `docs/design/v2.0-RC3/DESIGN.md:1771` |
| Per-uniform GL upload isolation | immutable upload batch + attributed replay, §4.11 | Phase 1 binding contract at `docs/phase1/v14/PHASE_1_DOC.md:5475` |
| PD cadence buckets and cache mechanics | acquisition buckets plus activation sweep | **Adopted as non-contract-visible mechanics**; PD says ONCE/PER_TICK/PER_FRAME/dynamic at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:288`; source confirms dynamic first at `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/program/ProgramUniforms.java:200` |
| PD sampler queue/dedup mechanics | deterministic unit/name plan and cached integer uploads | **Mechanics adopted; allocation policy rejected**. PD warns its units are dynamic at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:344`; fixed App B.3 wins |
| PD GPU `centerDepthSmooth` | not populated; CPU path selected | **Contract-visible rejection, D-P6-1**; PD §6.3 at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:312`; §4.8 checks App D, Phase 3's macro placement, and unit map |
| PD smoothing math | closed-form EMA only | **Contract-visible math adoption after unit conversion; wiring rejected, D-P6-3**. PD §6.4 at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:333`; source says deciseconds at `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/transforms/SmoothedFloat.java:45`; RESEARCH/RC3 tick units win |
| PD B1 | separate wetness/dryness fields and directive-to-field tests | **Not inherited**; the source writes dryness into wetness at `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/properties/PackDirectives.java:254` and the next line |
| PD B6 | notifier objects are constructed non-null; every consumer has a producer audit row | **Not inherited**; source declares but does not initialize `blendFuncNotifier` at `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/state/StateUpdateNotifiers.java:10` |
| PD B10 | no sibling sampler overload family; one plan-record path | **Not inherited**; the landmine overload begins at `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/program/ProgramSamplers.java:320` and returns false |
| PD §18 dynamic texture-unit allocation | Phase 5's sole App B.3 policy, no Phase 6 allocation or duplicate map | **Pre-decided rejection**; divergence table states fixed map at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:808` |

There is no unmapped in-scope item. The exact per-uniform provider/cadence/milestone mapping follows
in §4.4, and the Phase 4 barrier is traced operation by operation in §4.10.

---

## 4. Detailed design

### 4.1 Configuration and lifecycle roots

`UniformConfiguration` is an immutable derivation of one Phase 3 `PackConfiguration` and contains:

- Phase 3 schema/configuration fingerprint;
- the exact half-lives in **ticks**: wetness, dryness, eye brightness, and center depth;
- center-depth requirement;
- enabled v0.1/v0.2 features; and
- a fixed catalog version.

That fixed catalog version is also the version of §4.13's immutable
`FixedExpressionInputSchema`. Any change to a permitted exact name or its closed input type requires
a new catalog version; pack replacement obtains the schema from the replacement runtime, while a
runtime's schema never mutates.

It never holds a source string, parser, Minecraft object, program handle, framebuffer handle, or
mutable provider. Phase 3 remains the single parse truth
(`docs/phase3/v1/PHASE_3_DOC.md:3356`, “single validated downstream truth”). The effective
`ProgramUniformLayout` is deliberately not part of this pack-global configuration; Phase 4 passes
it with each resolved program binding, and `ProgramCache` retains that immutable per-key layout.
Sampler routing separately consumes the same effective descriptor's `ProgramSamplerLayout` through
the injected Phase 5 resolver (§4.9); neither configuration nor the Phase 6 catalog contains a
sampler-name/unit table or a second conditional-alias rule.

Construction validates every half-life as finite and non-negative. A malformed directive is a
Phase 3 diagnostic/default matter; an invariant breach arriving here rejects the uniform candidate
without GL work. Phase 3 already publishes distinct tick fields—for example
`SmoothingConstants.drynessHalfLifeTicks` (`docs/phase3/v1/PHASE_3_DOC.md:1770`)—so Phase 6 performs
no parser-side unit conversion. `create` installs the generation from the current Phase 4
`PublishedRegistry` as the runtime's initial current generation. A new published registry
generation creates a new program-cache namespace. Old locations and disabled scopes are discarded;
temporal world values survive only when `worldEpoch` is unchanged.

After Phase 4 accepts a replacement, Phase 7 calls
`adoptRegistryGeneration(replacement.generation(), reason)` on the surviving or new runtime,
never on a runtime retired under §4.14, on the render thread before any
`beginFrame`, event, or participant activation against that replacement. Adoption is atomic: a
different, never-retired generation becomes current and the prior generation becomes retired; the
current value is an idempotent `ALREADY_CURRENT`; a retired value is rejected without mutation.
Generation values are compared only for equality, never ordered or subtracted, matching Phase 4's
wrap-safe protocol. Phase 7 must pass the generation from the newly reacquired authoritative
publication, not a candidate view or delayed snapshot. `ADOPTED` applies §4.14's reset scope for
the supplied semantic reason before returning. A rejection forbids shader drawing and requires
reacquiring Phase 4's current publication; a retired runtime cannot recover by reacquisition and
must never be reused. Coordinated Phase 7 replacement retires the old runtime after old-barrier
invalidation and adopts the actual accepted generation on the new runtime (§4.14).

Lifecycle:

```text
NEW
  → CONFIGURED(configuration, samplerResolver, providers, diagnostics, replayErrors)
  → FRAME_READY after first beginFrame
  → ACTIVE through any number of barrier activations/events
  → RESET on world epoch or adopted pack/shaders-off/GL-context-loss generation
  → FRAME_READY/ACTIVE again only while the runtime remains live
Any live state, including CONFIGURED without publication
  → RETIRED by retire(UNPUBLISHED_ABORT | REPLACEMENT | SHUTDOWN), terminal
```

No GL work occurs in `reset`, generation adoption, or `retire`. Program-location objects belong
to a registry generation and are dropped, not deleted. Reset/adoption never releases borrowed
services or revives a retired runtime; retirement releases references without calling those services.

### 4.2 Provider seam and stable values

The platform SPI is intentionally coarse enough to sample related Minecraft values once:

```java
public interface UniformPlatformProvider {
    OnceUniformSample sampleOnce();
    TickUniformSample sampleTick(long worldEpoch, long logicalTick);
    FrameUniformSample sampleFrame(FrameSampleRequest request);
}

public interface CenterDepthSource {
    CenterDepthResult readCenter(CenterDepthRequest request);
}

public sealed interface CenterDepthResult {
    record Sample(float depth) implements CenterDepthResult {}
    record Unavailable(String diagnosticId) implements CenterDepthResult {}
}
```

The SPI records are exactly:

```java
public record OnceUniformSample(float nearPlane) {}
public record TickUniformSample(
    long worldEpoch, long logicalTick, long worldTicks, int moonPhase, float rainStrength) {}
public record FrameSampleRequest(
    long registryGeneration, long worldEpoch, long frameId, long logicalTick,
    float frameTimeSeconds, int targetViewWidth, int targetViewHeight) {}
public record FrameUniformSample(
    long worldEpoch, long frameId, Double3 cameraPosition, float eyeAltitude,
    Int2 eyeBrightness, int isEyeInWater, float nightVision, float blindness,
    float screenBrightness, boolean hideGui, float farPlane, float sunAngle,
    OptionalFloat shadowAngle, Float3 skyColor, OptionalValue<FogSample> fogFallback) {}
public record CenterDepthRequest(
    long registryGeneration, long worldEpoch, long frameId,
    int framebufferWidth, int framebufferHeight, int pixelX, int pixelY) {}
```

`OnceUniformSample.nearPlane` must be exactly `0.05f`; the record exists to keep the value behind
the provider seam rather than to make it configurable. `TickUniformSample` identities must equal
the call arguments, `moonPhase` is 0…7, and `rainStrength` is finite in `[0,1]`.
`FrameSampleRequest` is constructed by the runtime from the accepted `FrameBeginInput`;
dimensions are non-negative and `frameTimeSeconds` is finite and non-negative.
`FrameUniformSample` must echo the request's world/frame identity. Its vectors and floats are
finite; `eyeBrightness` components are 0…240; `isEyeInWater` is 0, 1, or 2; effect strengths and
screen brightness are in `[0,1]`; `farPlane` is non-negative; and `sunAngle` and a present
`shadowAngle` are in `[0,1)`. `fogFallback` is absent only when no valid frame fallback exists.

**P8 independent celestial receipt (D-P6-37, superseding D-P6-28's Ready-plan prerequisite).**
P7's `mod.glue.uniforms` provider obtains the existing frame `shadowAngle` from P8's pure
`CelestialMath.angles(float sunAngle) -> ShadowCelestialAngles` before camera capture.
That same angular law backs any retained `ShadowCelestialPolicy`; it is available at v0.2
even when shadows are NotRequested, disabled or unavailable. Angular data is not a frame event.
After validating the same accepted frame/live main-camera association, P7's H-SKY-02 route uses
`CelestialMath.sample(ShadowFrameView frame, CameraSnapshot mainCamera, float sunPathRotationDegrees)`
to supply the unchanged `CelestialSample`. Inputs are the retained sun/sky values, actual
post-camera main modelView and finite configuration-derived rotation, not current GL state.
No Ready shadow plan, shadow target, extent, publication or shadow-hook health is required.
Real P8 camera computation delegates to the same math and still delivers its sample before
shadow activation; matching accepted frame/camera/rotation permits exact-sample reuse.
P6 preserves its current-world/nonregressing-frame/finite-value event admission, immediate
active upload and every-switch cache semantics. It imports no shadow implementation and adds
no provider/frame/celestial field. Main celestial availability is independent of shadow matrices;
v0.1 staging remains neutral, but zero shadow-buffer demand cannot excuse neutral v0.2 vectors.
`CenterDepthRequest` names the completed prior framebuffer: positive dimensions imply
`pixelX=floor(width/2)` and `pixelY=floor(height/2)` in bottom-left pixel coordinates; zero
dimensions produce `Unavailable` without calling the source. `CenterDepthResult.Sample.depth` is
finite in `[0,1]`; `Unavailable.diagnosticId` is a non-empty stable diagnostic key.

Event records are exactly:

```java
public record CelestialSample(
    long worldEpoch, long frameId, Float3 sunPosition, Float3 moonPosition,
    Float3 shadowLightPosition, Float3 upPosition) {}
public record ShadowMatrixSample(
    long worldEpoch, long frameId, Matrix4Value projection, Matrix4Value modelView) {}
public record FogSample(
    long worldEpoch, long frameId, int fogMode, float density, Float3 color) {}
public record BlendSample(
    long worldEpoch, long frameId, boolean enabled,
    int srcRgb, int dstRgb, int srcAlpha, int dstAlpha) {}
public record HeldItemSample(
    long worldEpoch, long logicalTick, int heldItemId, int heldBlockLightValue,
    int heldItemId2, int heldBlockLightValue2) {}
public record Double3(double x, double y, double z) {}
public record Float3(float x, float y, float z) {}
public record Float4(float x, float y, float z, float w) {}
public record Int2(int x, int y) {}
```

All event identities must match the runtime's current world and must not regress; frame-bearing
events apply only to that frame, while `HeldItemSample` replaces the value for its logical tick.
Every vector and matrix component is finite. Fog density is in `[0,1]`; `fogMode`, blend factors,
and alias-derived item IDs intentionally retain their full `int` domains. Held-light values are
0…15. When old-hand-light is disabled, Phase 9 constructs the pair as
`(mainLight, offHandLight)`; when enabled it constructs
`(max(mainLight, offHandLight), offHandLight)`. Thus old packs reading only
`heldBlockLightValue` see the brighter hand, while `heldBlockLightValue2` remains the actual
off-hand value.

`Double3`, `Float3`, `Float4`, `Int2`, and `Matrix4Value` are immutable by-value engine records.
`OptionalFloat` and `OptionalValue<T>` are closed present/absent value types, never nullable.
Constructors reject invalid identity/range/finite data before mutation. Records contain no
Minecraft, GL, supplier, buffer, or mutable collection reference; array-taking value constructors
copy on entry and expose values only by scalar access or fresh copy. Provider exceptions, invalid
records, and unavailable optionals retain the last valid affected cells (neutral on first sample),
emit the stable diagnostic once, and do not affect sibling cells. Phase 9 and Phase 13 feed their
later-owned values through events; the uniform layer does not pretend those values exist early.

Each sample is copied into a typed `UniformCell`. A cell holds:

```text
definition id · immutable value · acquisition revision · valid/disabled state · diagnostic id
```

Providers are invoked once at their acquisition boundary, never from `afterBind`. A provider
exception is caught at the SPI boundary and affects only the cells supplied by that provider.
Headless tests supply scripted providers.

### 4.3 Cadence engine and upload cache

Acquisition buckets:

| Bucket | Trigger | Examples |
|---|---|---|
| `ONCE` | configuration/world-provider install | constants, inert legacy metrics |
| `PER_TICK` | logical tick differs from the last sampled tick | world time/day, moon phase, rain target, held inputs |
| `PER_FRAME` | accepted new frame ID | camera/player/render/time/fog/sky, smoothing, center depth |
| `SIGNAL` | typed hook/event method | matrices, celestial, shadow, entity/TE IDs, blend, instance, atlas |

Every activation then walks a precomputed `ProgramUploadPlan`. For each entry:

1. absent location: no-op;
2. disabled scope: no-op;
3. matrix: upload unconditionally;
4. scalar/vector unchanged by exact canonical comparison: skip;
5. otherwise snapshot an `UploadCommand` and upload it.

Float comparison uses `Float.floatToIntBits` after normalizing `-0.0f` to `0.0f`; NaNs never enter a
valid cell. Ints compare exactly. Vectors compare every component. Matrices deliberately ignore
equality. The last-uploaded value is per **effective linked program**, uniform name, and registry
generation—not per requested fallback child—because Phase 4 resolves one effective provider and
does not overlay child state (`docs/phase4/v1/PHASE_4_DOC.md:1016`, “one immutable compiled
binding”).

`SIGNAL` has two policies. `NEXT_ACTIVATION` only replaces cells (gbuffer/shadow matrices and held
inputs). `IMMEDIATE_IF_ACTIVE` replaces cells and builds a small upload batch for the currently
active effective program (celestial, fog, blend, entity color/IDs, instance, and atlas size). The
batch runs only if Phase 4's retained activity token still reports current; otherwise the value is
carried to the next activation. It never calls `use`, resolves fallback, or looks up a new
location. This is what lets `instanceId` change between repeated draws while the same program stays
bound.

Pintonium's bucket order validates the shape but is not copied wholesale. Its source executes
dynamic values before once/tick/frame
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/program/ProgramUniforms.java:200`),
while this design samples
outside activation so an error replay cannot observe a second world state.

### 4.4 Complete built-in inventory

“Acquisition” below describes value replacement. “Activation” is always **every successful shader
activation**, with exact-value skip except matrices. “Pending” means the Phase 6 interface exists at
v0.1 and uploads a documented neutral value until the owning value producer lands.

#### 4.4.1 Held item and player

| Uniform | Type and exact value | Provider / acquisition | Activation | Milestone |
|---|---|---|---|---|
| `heldItemId` | `int`; Phase 9 alias-mapped main-hand item ID | held-item event; pending 0 | every switch, skip equal | interface v0.1; value v0.3 |
| `heldBlockLightValue` | `int`; main-hand emitted light, or `max(main,off)` when old-hand-light is enabled | held-item event; pending 0 | every switch, skip equal | interface v0.1; value v0.3 |
| `heldItemId2` | `int`; Phase 9 alias-mapped off-hand item ID | held-item event; pending 0 | every switch, skip equal | interface v0.1; value v0.3 |
| `heldBlockLightValue2` | `int`; actual off-hand emitted light in both modes | held-item event; pending 0 | every switch, skip equal | interface v0.1; value v0.3 |
| `wetness` | `float`; rain strength smoothed with asymmetric wetness/dryness half-lives | tick rain target + frame EMA | every switch, skip equal | v0.1 |
| `eyeAltitude` | `float`; view-entity world Y | frame provider | every switch, skip equal | v0.1 |
| `eyeBrightness` | `ivec2`; block/sky brightness, each clamped 0…240 | frame provider | every switch, skip equal | v0.1 |
| `eyeBrightnessSmooth` | `ivec2`; component-wise smoothed eye brightness | frame provider + vector EMA | every switch, skip equal | v0.1 |
| `isEyeInWater` | `int`; 0 neither, 1 water, 2 lava | frame provider | every switch, skip equal | v0.1 |
| `nightVision` | `float`; effect strength clamped 0…1 | frame provider | every switch, skip equal | v0.1 |
| `blindness` | `float`; effect strength clamped 0…1 | frame provider | every switch, skip equal | v0.1 |
| `screenBrightness` | `float`; video setting clamped 0…1 | frame provider | every switch, skip equal | v0.1 |
| `hideGUI` | `int`; 1 when GUI hidden, else 0 | frame provider | every switch, skip equal | v0.1 |

These types and meanings are the contract table beginning at `docs/research/v1/RESEARCH.md:1326`; for
example, `eyeBrightness` is “x block / y sky light, 0–240”
(`docs/research/v1/RESEARCH.md:1332`).

#### 4.4.2 World, time, and weather

| Uniform | Type and exact value | Provider / acquisition | Activation | Milestone |
|---|---|---|---|---|
| `worldTime` | `int`; Java remainder `worldTicks % 24000`, then int narrowing | tick provider | every switch, skip equal | v0.1 |
| `worldDay` | `int`; Java integer division `worldTicks / 24000`, then int narrowing | tick provider | every switch, skip equal | v0.1 |
| `moonPhase` | `int`; 0…7 | tick provider | every switch, skip equal | v0.1 |
| `frameCounter` | `int`; starts 0 on runtime creation, increments once per accepted frame, wraps to 0 after 720719 | cadence engine | every switch, skip equal | v0.1 |
| `frameTime` | `float`; elapsed seconds for the immediately preceding rendered frame, finite and ≥0 | frame clock provider | every switch, skip equal | v0.1 |
| `frameTimeCounter` | `float`; accumulated rendered seconds; after addition, any value ≥3600 resets to 0 | cadence engine | every switch, skip equal | v0.1 |
| `sunAngle` | `float`; normalized celestial angle in `[0,1)` | frame provider | every switch, skip equal | v0.1 |
| `shadowAngle` | `float`; normalized active shadow-light angle in `[0,1)` | frame provider; shadow policy supplied at v0.2 | every switch, skip equal | interface v0.1; value v0.2 |
| `rainStrength` | `float`; current unsmoothed rain strength clamped 0…1 | tick provider | every switch, skip equal | v0.1 |
| `fogMode` | `int`; engine enum encoded to legacy `GL_LINEAR`/`GL_EXP`/`GL_EXP2` numeric values by `mod.glue` | fog signal | immediate if active + every switch | v0.1 |
| `fogDensity` | `float`; current density clamped 0…1 | fog signal/frame fallback | immediate if active + every switch | v0.1 |
| `fogColor` | `vec3`; current linear RGB supplied by vanilla state | fog signal/frame fallback | immediate if active + every switch | v0.1 |
| `skyColor` | `vec3`; sampled current sky RGB | frame provider | every switch, skip equal | v0.1 |

The wrap values are contract, not convenience:
`docs/research/v1/RESEARCH.md:1346` says `frameCounter` “wraps at 720720”, and
`docs/research/v1/RESEARCH.md:1348` says `frameTimeCounter` “wraps at 3600”. The exact boundary
mechanics are corroborated by the working reference: the counter uses `(count + 1) % 720720`
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/SystemTimeUniforms.java:49`)
and elapsed time resets to zero at 3600
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/SystemTimeUniforms.java:89`).

#### 4.4.3 Camera, matrices, and screen

| Uniform | Type and exact value | Provider / acquisition | Activation | Milestone |
|---|---|---|---|---|
| `aspectRatio` | `float`; `targetViewWidth / targetViewHeight`, or 1 when height is zero during recovery | frame-begin input | every switch, skip equal | v0.1 |
| `viewWidth` | `float`; coming frame's render width in pixels | frame-begin input | every switch, skip equal | v0.1 |
| `viewHeight` | `float`; coming frame's render height in pixels | frame-begin input | every switch, skip equal | v0.1 |
| `near` | `float`; constant 0.05 | once provider | every switch, skip equal | v0.1 |
| `far` | `float`; render-distance chunks × 16 | frame provider | every switch, skip equal | v0.1 |
| `sunPosition` | `vec3`; eye-space sun vector captured at celestial rotation | celestial signal; pending neutral | immediate if active + every switch | interface v0.1; value v0.2 |
| `moonPosition` | `vec3`; eye-space moon vector captured at celestial rotation | celestial signal; pending neutral | immediate if active + every switch | interface v0.1; value v0.2 |
| `shadowLightPosition` | `vec3`; active sun-or-moon shadow light in eye space | celestial/shadow signal; pending neutral | immediate if active + every switch | interface v0.1; value v0.2 |
| `upPosition` | `vec3`; eye-space world-up vector | celestial signal; pending neutral | immediate if active + every switch | interface v0.1; value v0.2 |
| `cameraPosition` | `vec3`; current world-space view-entity/camera position | frame provider | every switch, skip equal | v0.1 |
| `previousCameraPosition` | `vec3`; prior accepted frame's `cameraPosition` | temporal store at frame begin | every switch, skip equal | v0.1 |
| `gbufferModelView` | `mat4`; model-view captured once after camera setup, later than first clear | gbuffer matrix signal | every switch, **always upload** | v0.1 |
| `gbufferModelViewInverse` | `mat4`; inverse of current gbuffer model-view | matrix engine | every switch, **always upload** | v0.1 |
| `gbufferPreviousModelView` | `mat4`; last accepted frame's captured model-view | temporal matrix store | every switch, **always upload** | v0.1 |
| `gbufferProjection` | `mat4`; projection captured once after camera setup, later than first clear | gbuffer matrix signal | every switch, **always upload** | v0.1 |
| `gbufferProjectionInverse` | `mat4`; inverse of current gbuffer projection | matrix engine | every switch, **always upload** | v0.1 |
| `gbufferPreviousProjection` | `mat4`; last accepted frame's captured projection | temporal matrix store | every switch, **always upload** | v0.1 |
| `shadowProjection` | `mat4`; Phase 8 shadow-camera projection | shadow signal; pending identity | every switch, **always upload** | interface v0.1; value v0.2 |
| `shadowProjectionInverse` | `mat4`; inverse of shadow projection | matrix engine | every switch, **always upload** | interface v0.1; value v0.2 |
| `shadowModelView` | `mat4`; Phase 8 shadow-camera model-view | shadow signal; pending identity | every switch, **always upload** | interface v0.1; value v0.2 |
| `shadowModelViewInverse` | `mat4`; inverse of shadow model-view | matrix engine | every switch, **always upload** | interface v0.1; value v0.2 |
| `centerDepthSmooth` | `float`; center depth in `[0,1]`, tick-domain EMA | sync center-depth source at frame begin | every switch, skip equal | v0.1 |
| `atlasSize` | `ivec2`; current atlas width/height while atlas is bound | atlas signal; pending `(0,0)` | immediate if active + every switch | interface v0.1; value v0.5 |
| `terrainTextureSize` | `ivec2`; documented unused, deterministic `(0,0)` | once neutral provider | every switch, skip equal | v0.1 |
| `terrainIconSize` | `int`; documented unused, deterministic `0` | once neutral provider | every switch, skip equal | v0.1 |

Near/far are fixed at “0.05 / renderDistance×16”
(`docs/research/v1/RESEARCH.md:1360`). The two terrain metrics remain present because the shipped
document declares them but calls them “not used”
(`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:169`); zeros preserve linked-program default
semantics without inventing a Minecraft query.

#### 4.4.4 Per-draw dynamics

| Uniform | Type and exact value | Provider / acquisition | Activation | Milestone |
|---|---|---|---|---|
| `entityColor` | `vec4`; current hurt/flash tint; neutral `(0,0,0,0)` outside a scoped producer | Phase 7 scoped signal | immediate if active + every switch | v0.1 |
| `entityId` | `int`; Phase 9 alias-mapped entity ID; 0 outside scope | Phase 9 scoped signal | immediate if active + every switch | interface v0.1; value v0.3 |
| `blockEntityId` | `int`; Phase 9 alias-mapped block ID for current block entity; 0 outside scope | Phase 9 scoped signal | immediate if active + every switch | interface v0.1; value v0.3 |
| `blendFunc` | `ivec4`; effective draw-state `(srcRGB,dstRGB,srcAlpha,dstAlpha)`, or zeros while blending is disabled | GlStateManager observation plus effective Phase 4 blend-lock overlay | immediate if active + every switch | v0.1 |
| `instanceId` | `int`; total N draws IDs0…N−1, saved predecessor restored, outer neutral0 | Phase 7 draw-loop signal | immediate before each prepared draw + every switch | interface v0.1; fullscreen/authenticated non-fullscreen values v0.5 |

All five are excluded from Phase 11's expression-input view. That exclusion is contract-visible:
`docs/research/v1/RESEARCH.md:1370` calls them “Per-draw dynamics (excluded from custom-uniform
expressions)”.

### 4.5 Smoothing mathematics

All four half-life directives are interpreted in **ticks**. Pintonium's implementation labels its
input as deciseconds/two ticks
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/transforms/SmoothedFloat.java:45`)
and multiplies by 0.1 seconds
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/transforms/SmoothedFloat.java:53`).
RC3 explicitly calls for a per-tick formula, so Pintonium supplies
the closed form, not the unit.

For a target \(x\), prior accumulator \(s\), elapsed smoothing time \(\Delta t\) in ticks, and
half-life \(h\) in ticks:

\[
d =
\begin{cases}
0 & h \le 0 \\
2^{-\Delta t/h} = e^{-\ln(2)\Delta t/h} & h > 0
\end{cases}
\]

\[
s' = x + (s-x)d
\]

Equivalently, \(\alpha=1-d\) and \(s'=s+\alpha(x-s)\). The implementation evaluates in `double`,
casts the final value to float, and advances once per accepted frame. Exact edge rules:

1. the first valid sample initializes \(s=x\) with no fade;
2. \(\Delta t = 0\) retains \(s\);
3. negative/regressing time is an invariant failure; the affected smoother retains its previous
   value and reports once;
4. \(h=0\) snaps to the target;
5. non-finite inputs never enter the accumulator; and
6. world-epoch change reinitializes on the new world's first sample.

`smoothingTimeTicks` is a monotonic game-time coordinate supplied by `mod.glue`, derived from the
world tick plus the current render partial tick. It does not advance while game time is paused.
In ordinary gameplay `frameTime` remains elapsed wall/render seconds. Only P7's authenticated
capture session supplies controlled rendered seconds through the same `FrameBeginInput`;
P6 neither reads a replacement clock nor knows the capture plan. In both modes seconds are
never reused as smoothing ticks. A stalled host frame alone cannot advance logical ticks.

#### Wetness

The target is `clamp(rainStrength,0,1)`. If target > accumulator, use
`wetnessHalflife`; otherwise use `drynessHalflife`. Equality leaves the accumulator unchanged.
The two fields are independently carried from Phase 3 and independently tested. This directly avoids
PD B1, whose source routes `drynessHalflife` into `wetnessHalfLife`
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/properties/PackDirectives.java:254`
and the next line).

#### Eye brightness

The formula is applied independently to block and sky components with the same
`eyeBrightnessHalflife`. Accumulators remain continuous doubles. The pack-facing `ivec2` is obtained
by truncation toward zero, then clamped to 0…240. Inputs are non-negative, so this is floor. This
matches the verified reference mechanic, which constructs ints with casts
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/CommonUniforms.java:113`),
while retaining the Appendix D type/range. The rounding rule
is a Phase 6 decision because the shipped contract says only “smoothed”; it is covered by T2
conformance rather than claimed as independently documented OptiFine behavior.

#### Center depth

The raw value is clamped to `[0,1]`, then uses the same formula with
`centerDepthHalflife`. A provider-unavailable frame retains the prior accumulator and does not
advance it. On the first frame with a sample, the accumulator initializes to that sample. The
center-depth read itself occurs once, before resize/clear, only when Phase 3 reports that some
materialized program declares `centerDepthSmooth`.

### 4.6 Frame-begin ordering and temporal snapshots

The terminal-state guard in §4.14 runs first: a retired runtime returns `REJECTED_GENERATION`,
even for the last accepted frame or a freshly supplied generation, without sampling or GL work.
For a live runtime, `beginFrame` returns `ACCEPTED` only for a new adopted-current-generation
frame and then performs this exact sequence:

1. validate registry generation and world epoch;
2. rotate `cameraPosition` into `previousCameraPosition`;
3. rotate the last successfully captured gbuffer matrices into both `gbufferPrevious*` cells;
4. sample tick state if the logical tick changed;
5. sample frame state exactly once and install the new current camera/player/render values;
6. read the previous frame's main depth attachment at
   `(floor(priorFramebufferWidth/2), floor(priorFramebufferHeight/2))`;
7. advance wetness, eye-brightness, and center-depth smoothers once; and
8. publish the immutable `FrameSnapshot` and fire no user callback.

The same `(worldEpoch, frameId)` returns `DUPLICATE`; a lower frame ID or obsolete world epoch
returns `REJECTED_STALE_FRAME`; a registry-generation mismatch returns `REJECTED_GENERATION`.
Those three outcomes perform no sampling, smoothing, snapshot rotation, event publication, or GL
work. Phase 7 may proceed to resize/clear only on `ACCEPTED` or `DUPLICATE`; on either rejection it
must abandon the shader draw and reacquire the current publication. Thus only after an accepted or
duplicate `beginFrame` return may Phase 7 allow Phase 5 to resize, clear, or replace buffers. This is
the exported ordering contract required by
`docs/design/v2.0-RC3/DESIGN.md:1721`, “before any buffer resize or clear”.

The center-depth dimensions and depth source refer to the framebuffer containing the completed
previous image. Width/height ≤0 produce `Unavailable`, not negative coordinates. The source's
`mod.glue` implementation uses Phase 1's synchronous
`FramebufferService.readDepthPixel`; the exact verb is published at
`docs/phase1/v14/PHASE_1_DOC.md:3388`.

Gbuffer matrices are not sampled in `beginFrame` or at ordinal-zero clear. Phase 7 calls
`captureGbufferMatrices(frameId,...)` exactly once at its later post-camera setup hook.
The prior matrices have already been snapshotted before resize/clear, so that later capture
overwrites current matrices without destroying temporal state.
A second capture for the same frame is an invariant diagnostic and is ignored; a missing capture
leaves main-program matrix cells invalid so only those
uniforms are disabled for that frame.

**Actual timing query (D-P6-25).** `frameTiming(registryGeneration,frameId)` is render-thread
confined like `beginFrame`; a wrong-thread call throws `IllegalStateException` before reading
state, including after retirement. Otherwise it returns empty before the first accepted frame,
after reset/world-epoch invalidation, after adoption until a new frame is accepted, for any
generation unequal to the adopted current generation, for any frame other than the latest
accepted frame, and after retirement. A duplicate or rejected begin never changes the report.
The report copies the exact accepted input identity/tick/seconds and the actual cadence-engine
`frameCounter`/`frameTimeCounter` cells after that accepted begin's existing counter update.
It is not recomputed from frameId, capture ordinal, wall time or requested values; counter
initialization/wrap and accumulation remain §4.4.2's existing semantics.
An issued record is immutable historical evidence and remains readable after invalidation;
it cannot authorize a later runtime/frame. P7 authenticates its epoch against its live frame.
Query performs no provider call, GL, sampling, counter update, reset or second clock work.
Keep scalar state in the existing accepted snapshot; allocate a report only when queried
(and optionally reuse that immutable report for repeated queries of that same accepted frame).


The Pintonium evidence validates the capture source, not the exact hook contract:
`reference-src/pintonium-9c2fcc1/forge122/src/main/java/org/taumc/celeritas/mixin/core/terrain/ActiveRenderInfoAccessor.java:11`
and
`reference-src/pintonium-9c2fcc1/forge122/src/main/java/org/taumc/celeritas/mixin/core/terrain/ActiveRenderInfoAccessor.java:16`
expose vanilla's projection/model-view buffers, and
`reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java:102`
places capture after the first clear. Phase 7 owns the
final injection catalog and must preserve the “after camera setup, once” semantics.

### 4.7 Matrix representation, inversion, and overwrite rules

`Matrix4Value` is column-major in the same element order passed to
`UniformService.uploadMatrix4(..., transpose=false)`. Capture copies all 16 floats immediately;
the provider's FloatBuffer position/limit and later mutation cannot affect it.

Inversion uses a deterministic pure-Java 4×4 algorithm in `engine.uniforms.matrix`, computed once
when a current matrix signal arrives. It computes in `double`, returns finite floats, and reports a
singular/non-finite result instead of manufacturing an inverse. A bad inverse disables only the
corresponding `*Inverse` uniform; the original matrix remains valid. Previous values are copies,
never suppliers that mutate when read.

Overwrite rules:

- previous camera/matrices rotate only at an accepted frame boundary;
- repeated program switches never rotate temporal state;
- a frame that performs no matrix capture does not replace the last valid current matrices;
- a world-epoch reset initializes previous=current on the first valid new-world sample, preventing
  cross-world motion vectors; and
- shadow matrices have no `previous*` contract and update only on Phase 8's shadow-camera signal.

Pintonium's `MatrixUniforms.Previous.get()` advances previous state when the supplier is read
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/MatrixUniforms.java:75`).
That mechanism is deliberately not adopted: a program switch
must not advance a per-frame snapshot.

### 4.8 `centerDepthSmooth` decision

#### Candidate A — selected

Once per frame, synchronously read the center depth through
`FramebufferService.readDepthPixel`, then advance the CPU tick-domain EMA in §4.5. This is the
observed contract path: the frame-flow source calls it a “synchronous center-depth readback”
(`docs/research/v1/RESEARCH.md:558`), and Appendix D exposes a default-block `float`
(`docs/research/v1/RESEARCH.md:1366`).

#### Candidate B — rejected for v0.1

The candidate is a 1×1 `R32F` ping-pong smoothing pass and a source-level redirect. The mechanism is
real: Pintonium constructs two `R32F` textures
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pathways/CenterDepthSampler.java:32`)
and renders a 1×1 viewport
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pathways/CenterDepthSampler.java:78`).
It is not adoptable through the currently
published contracts:

1. **Declaration contract.** Packs declare `uniform float centerDepthSmooth;`
   (`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:176`). Phase 3's object-like macro is
   injected before the first restored pack line (`docs/phase3/v1/PHASE_3_DOC.md:1536-1537`) and therefore
   also substitutes the identifier inside its declaration. With no declaration-aware removal or
   rename operation in Phase 3 §5, the proposed replacement expression turns a legal declaration
   into invalid GLSL.
2. **Fixed-unit contract.** A sampled texture requires a sampler assigned to a unit. Appendix B.3
   fixes all units 0–15 by stage (table beginning at `docs/research/v1/RESEARCH.md:1229`) and
   publishes no
   universally reserved center-depth sampler. Phase 6 cannot silently allocate one dynamically;
   that is a pre-decided rejection at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:808`.
3. **Observable value.** The GPU result is behaviorally similar but not bit-identical to the
   CPU-readback value, as RC3 itself records
   (`docs/design/v2.0-RC3/DESIGN.md:1747`). No conformance evidence justifies spending two upstream
   interface changes for a v0.1 optimization.
4. **License-safe path.** Pintonium's declaration-aware transformer is not a source for this
   project; the AGPL transformer dependency is prohibited at
   `docs/design/v2.0-RC3/DESIGN.md:888`.

`[D-P6-1]` selects candidate A. `centerDepthMacroContributor()` consequently returns
`MacroContribution.Empty` for every configuration. Phase 14's PBO/fence item is **not obviated** and
remains the sole async-readback modernization ledger entry. Reconsideration requires a verified,
declaration-safe Phase 3 operation, an explicit sampler/unit contract extension, and T2 evidence.

### 4.9 Shared fixed-sampler policy and re-point algorithm

Phase 5 is the sole policy owner. Phase 6 neither publishes nor implements a sampler-name/unit
map. It consumes `FixedSamplerPolicies.resolver()` through §2.2's required constructor argument.
The binding policy is Phase 5 §4.12.1, incorporated by its §5.1
(`docs/phase5/v1/PHASE_5_DOC.md:2726`, “exact complete §2.4 declarations and §4.12.1 table/schema
incorporated”). Appendix B.3 remains the authority
(`docs/research/v1/RESEARCH.md:1228-1255`, “packs rely on these numbers”).

The exact Phase-5-owned operation and result algebra consumed here are:

```java
FixedSamplerPlanResult resolve(ProgramSamplerLayout layout, StageId stage, StageBand band);

// FixedSamplerPlanResult alternatives:
Ready(List<ResolvedSamplerBinding> bindings, FixedSamplerPolicyFingerprint policy)
Invalid(SamplerLayoutValidation reason)

// Each Ready row:
ResolvedSamplerBinding(String exactName, DeclaredGlslType.Sampler shape, int unit)
```

These are the existing owner types, not Phase 6 redeclarations
(`docs/phase5/v1/PHASE_5_DOC.md:1021`, “record ResolvedSamplerBinding”).
The pure resolver uses appB3's same table/schema/fingerprint; Ready rows are immutable, ascending
unit then fixed-name declaration order, preserving distinct exact names sharing one unit.
Invalid preserves the complete `SamplerLayoutValidation` evidence, including unsupported
names/domains/shapes and same-unit incompatible types. Arrays/structs containing samplers are not
coerced to scalar samplers. Fixed/virtual empty layouts resolve to empty Ready; unsupported shader
domains never fall back to a fullscreen map
(`docs/phase5/v1/PHASE_5_DOC.md:2499-2502`, “Invalid retains the complete validation evidence”).

The conditional `shadow` rule is evaluated only by Phase 5 from the effective provider's complete
sampler layout: a direct sampler-compatible `watershadow` declaration selects unit 5; otherwise
unit 4. Driver location absence and shadow-buffer count do not affect that declaration rule.
Both gbuffers bands share the policy, and `tex` remains shadow-only
(`docs/phase5/v1/PHASE_5_DOC.md:2478`, “Both gbuffers bands share this map”).
`depthtex1` remains unit 11, and unit 12 has no gbuffers/shadow sampler; these are consequences of
the sole owner policy, not a second local table. Phase 6 never infers the alias from
`ProgramUniformLayout`, scans source, or assigns units to unsupported names.

Inside the existing `SamplerRepointParticipant.afterBind(binding, context, uniforms)`:

1. Use the effective `binding.samplerLayout()` and Phase-4-issued activation context, never the
   requested fallback child's layout or state. Obtain the plan through
   `samplerResolver.resolve(binding.samplerLayout(), context.stage(), context.band())`.
   Phase 4 has already authenticated the effective provider's stage/band membership. The same
   effective layout/context governed Phase 5's preceding object binds; Phase 6 does not select,
   activate, or resolve a backup chain again.
2. Reuse the immutable resolved plan within §4.10's existing `ProgramUniformCacheKey` cache.
   Resolve/build it on first use of that effective sampler-layout/policy and stage/band combination,
   not on every steady-state activation. Plan reuse must match those inputs; both gbuffers bands
   may share the identical plan. The outer key, generation lifetime, location/value caches and
   activity-token semantics are unchanged. A cached Invalid remains a sampler-participant
   degradation for that program/generation; it never becomes an empty successful plan.
3. On Invalid, preserve the typed reason in the diagnostic and return the existing Phase 4
   `BarrierParticipantResult.Degraded` scoped to that effective program's sampler participant.
   Issue no sampler uploads, choose no replacement map, and do not disable unrelated uniforms.
   Invalid layouts should already have failed Phase 4's provider validation before activation;
   this branch contains an invariant breach rather than authorizing a different fallback.
4. On Ready, use only its exact-name/full-sampler-shape/unit rows. Locate each exact name once
   through the callback-scoped `BoundProgramUniformAccess`; retain full declaration types, never
   reduce the shape or infer driver activity from declaration presence.
5. Queue `(name, location, fixedUnit)` in the existing ascending-unit then catalog-name order,
   using Phase 5's fixed-name declaration order as that canonical catalog order; do not reorder
   by driver location or physical texture identity.
6. Remove duplicate `(location,unit)` pairs within the plan.
7. On every activation, visit the plan and upload only when that effective program's cached unit
   differs.
8. Apply §4.11's unchanged attempted-batch error isolation to sampler uploads.

An absent/optimized-out location is cached as absent. Two different names at different locations may
legally receive the same unit; they are not deduplicated by unit. A name resolving to the same
location with different required units is a plan invariant failure and disables that program's
sampler participant.

Pintonium validates the useful mechanical shape: it queues external sampler integer calls after a
successful location lookup
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/program/ProgramSamplers.java:134`).
Its dynamic `nextUnit` allocation is visible at
`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/program/ProgramSamplers.java:196`
and is rejected. Phase 6 never calls `TextureService.bindToUnit`, acquires texture leases, or walks
physical handles/texture-binding rows. Phase 5 binds backing objects before Phase 4 activation;
the existing sampler participant then uploads the corresponding integers before built-ins/customs.
No fourth participant, unit allocator, texture-bind loop, or independent activation is introduced.

### 4.10 Phase 4 barrier fulfillment and program caches

Phase 4's binding callback retains its exact callable shape, receiving the handle-free effective
descriptor/layout and a callback-scoped capability over its privately held program
(`docs/phase4/v1/PHASE_4_DOC.md:1646-1656`, “BarrierParticipantResult afterBind”).
The published shape consumed here, subject to §5.2's owner-review gate, is:

```java
public interface BoundProgramUniformAccess {
    ProgramUniformCacheKey cacheKey();
    UniformLocation locate(String exactName);
    BoundProgramActivityToken activityToken();
}

public interface BoundProgramActivityToken {
    boolean isCurrent();
}

public record ProgramUniformCacheKey(
    long registryGeneration,
    ProgramSlotId effectiveProvider,
    ProgramUniformLayoutFingerprint linkedLayout) {}

public interface ProgramBindingParticipant {
    BarrierParticipantResult afterBind(
        ResolvedProgramDescriptor binding,
        BarrierContext context,
        BoundProgramUniformAccess uniforms);
}
```

Phase 4 mints the capability only after binding its private program. It wraps
`UniformService.locate(privateHandle,name)` and exposes no handle, `use`, delete, or link.
`locate` may be called only during `afterBind`; returned locations may be cached until
`registryGeneration` changes, while the access object itself may not be retained. The token alone
may be retained. Phase 4 invalidates it before any later activation (including the same effective
program), fixed-function release, failed-safe/off transition, ready/off replacement, or teardown
(`docs/phase4/v1/PHASE_4_DOC.md:1825`–`:1833`, `:1835`–`:1842`). `isCurrent()` is a pure thread-safe epoch
comparison and performs no GL query. An immediate signal uses already-cached locations and uploads
only while this token is current.

With it, the barrier trace is:

| Phase 4 step | Phase 6 obligation |
|---|---|
| shadow override and backup resolution | accept effective descriptor; never re-resolve |
| invalidate predecessor activity and close prior lease | restoration updates cells but cannot upload to predecessor |
| bind effective shader program | never call `use`; bound-only access is issued for callbacks |
| acquire effective alpha/blend lease and publish/sample effective blend | accept actual effective state before callbacks; failed acquisition/notification dispatches no participants |
| sampler participant | execute §4.9's shared-resolver integer plan; no object binds |
| built-in participant | snapshot current cells, then execute §4.11 batch |
| custom participant | invoke §4.13 from the same stable built-in view |
| participant `Degraded` | ordinary isolated upload/custom failure retains the program; replay-delivery failure follows §4.11's no-draw containment, not ordinary degradation |

Phase 4 promises participants run even when the same handle stays active
(`docs/phase4/v1/PHASE_4_DOC.md:1813`), satisfying the every-switch contract.

`ProgramCache` is keyed by `ProgramUniformCacheKey` and contains:

- immutable `ProgramUniformLayout`;
- sampler plans derived solely from Phase 5, with their effective sampler-layout/policy and
  stage/band reuse identity, plus built-in plans;
- cached present/absent locations;
- last uploaded canonical value per location/name;
- disabled scopes; and
- the count of consecutive clean replays following unattributable drains.

The runtime also retains at most one `(activityToken, ProgramCache)` pair for immediate signals.
Fallback children share the effective provider's cache key. A registry generation change discards
the entire map and active pair before any new activation.

### 4.11 Upload batching and per-uniform GL-error isolation

Each participant builds an immutable ordered list of only the commands it will actually attempt.
Every command carries uniform name, type, copied value, location, and upload operation. It then
executes Phase 1's binding protocol literally:

```text
retain and deliver any pre-upload cleanup drain as false-attribution evidence before upload
upload every command in the immutable batch
trigger = drainErrors()
if trigger empty:
    commit last-uploaded values
else:
    replay each same cached command with drains between individual calls
    disable only names actually isolated by the Phase 1 replay law
    construct exactly one verdict for each original trigger entry, in drain order
    deliver its UniformReplayReport and any cleanup-drain reports in observation order
    return the existing scoped degradation when needed
```

The replay never resamples a provider, advances time, rotates a previous snapshot, recomputes an
inverse, or reevaluates a custom expression. Phase 1 makes this a binding precondition:
`docs/phase1/v14/PHASE_1_DOC.md:6139` (D-P1-32; `:3785`/`:5565`) says “the re-upload uses the values **already computed for this sweep**”.

Disable scope is per generation + effective linked program + uniform name. A type error or GL error
in `fogColor` for one program does not disable `fogColor` in a different linked program, any sibling
uniform, the program, or the pack. Diagnostics are `WARN`/`LOG_ONLY` on
`schmaloogium.uniforms`. Absent locations issue no upload and cannot fail.

If a batched drain is non-empty but no replay command reproduces it, the error may be foreign GL or
non-reproducible. The participant returns `Degraded` only for its diagnostic/reporting state, not for
a guessed uniform. Only ordinary WARN/log repetition
is rate-limited per frame/program. Every typed report still reaches P7 synchronously; P7
classifies unattributable/recurring-clean results and hands them to the existing P1/mod.core
3→4 escalation route, including its frame-boundary drain remedy and elision limits. P6's
per-program recurrence counter resets on an attributable replay or clean attempted batch and
is diagnostic context, not a new threshold or uniform-disable policy. P7's existing broader
policy decides escalation; neither a log rate limiter nor P2 capture availability gates the handoff.

Commands that were skipped as equal are not replayed: no GL operation was attempted for them in the
failed window. Matrices, having no equality skip, are always in the attempted batch when declared.
An `IMMEDIATE_IF_ACTIVE` signal uses the same algorithm over only its changed dynamic commands after
checking the activity token immediately before the first upload. Phase 4 invalidates the token
before rebinding, so a signal can never upload a cached location into fixed function or a different
program.

**Replay evidence, correlation and delivery (D-P6-27).** For every nonempty triggering drain,
emit one `UniformReplayReport` with exactly one P1 `ReplayAwareGLError` per original entry.
The report's `program` is the existing effective cache key under which the attempt was observed,
not proof that this program or any uniform caused the error. List position preserves exact
trigger order; separate callbacks preserve drain/batch observation order (sampler, built-in,
custom, then later immediate operations as actually executed). Equal errors, including equal
errors in different reports, remain distinct occurrences; no set, key-based deduplication or
label-based matching is permitted. Replay probes explain the original trigger, not new
triggering submissions to be counted a second time.

`attributed=true` requires P1's cached replay to reproduce and isolate that original error in
one named facade call's one-call window. If correspondence to an original occurrence or a unique
call cannot be established, the verdict is false. An isolated error of a different kind cannot
turn another triggering error true. Clean, non-reproducing, still-batched, ambiguous and foreign
windows are false even if `op` or `subjectLabel` names a plausible upload. Pre-upload and
between-probe cleanup/foreign drains are not silently discarded: retain each nonempty drain as
its own all-false report, in actual observation order alongside the triggering report. A probe
window that remains foreign/ambiguous supplies false evidence for the original trigger; it is
not duplicated as a new submitted batch. No replay recursively replays a replay probe.

Delivery occurs after verdicts are established and **before** the enclosing `afterBind` returns
or, for an immediate upload, before the void event method returns. A pre-upload cleanup report
may be delivered before starting the attempt; queued replay cleanup reports follow their earlier
trigger. No callback is emitted for an empty drain, and no successful path leaves reports queued
for frame end. The callback runs only on the render thread under the outer operation's
active-callback guard. It performs no GL, provider calls, lifecycle work, frame mutation,
recursive uniform entry or reentrant callback dispatch. P7 synchronously copies/classifies the
detached report into its existing observation/capture route and feeds the existing escalation
owner; P2 flattens the values into current `/4` `gl_errors`, with every error failing T0.
P6 adds no frame field, wire variant, P3 schema, facade operation or fourth participant.

**Rejected/failed delivery is not accepted evidence.** The void sink returns normally only after
accepting the entire report; silent rejection, filtering, overflow or a no-op sink is forbidden.
P7 first retains the original report in its receiving failure-safe storage; if downstream
copy/classification/capture storage rejects or throws, it latches capture failure before throwing,
preserves the accepted prefix and failing original report, and cannot finalize `COMPLETE`.
If even failure storage is unavailable, it latches explicit evidence loss rather than fabricating
an empty/successful capture. Logging is not that storage and may never replace typed delivery.

P6 catches sink exceptions separately from provider and GL isolation. It retains the failing
report and any not-yet-delivered reports from the finite current operation, latches
`phase6.replay.delivery.failed`, and stops new uploads/replay and observer calls; it never
retries an ambiguously accepted report or overwrites it with later evidence. The current and
later barrier positions return existing `Degraded` with that stable diagnostic and
`"uniform replay evidence delivery"` scope, not a fabricated disabled uniform. P4 contains
participant exceptions and continues positions (§4.10), so P7 must inspect that degradation
and its own failure latch before any draw/capture-success claim. An immediate void event throws
`IllegalStateException` carrying the same diagnostic after retention; P7's existing event/scope
failure path stops remaining copies, restores the predecessor and closes admission. This is
delivery-failure containment, not automatic frame abort for an ordinary isolated upload error.
No new GL attempts occur on the failed runtime until P7's existing recovery disposes/replaces it;
reset/adoption cannot erase the failure. Final non-upload restoration/retirement remains possible.
P7 retains borrowed observer/adapters through legal retirement; only terminal retirement releases
P6's pending reports/references after the failure has been recorded as non-complete. Failure
to retain any observed evidence is itself explicit capture loss, never successful delivery.

### 4.12 Notifier-to-producer audit

There are no nullable global listener fields. `UniformEventSink` is constructed with the runtime,
remains usable across non-terminal resets, and every method is safe when no program declares its
consumer. Successful retirement permanently invalidates even previously returned sink references:
later calls fail before cell mutation, provider access or upload (§4.14). Hook owners call typed
methods; they never assign callbacks into Phase 6.

| Signal/notifier | Phase 6 consumer | Required producer and moment | Owner / milestone |
|---|---|---|---|
| frame begin | tick/frame cells, previous snapshots, all three smoothers | before any resize or clear in world-frame orchestration | Phase 7 / v0.1 |
| gbuffer matrix capture | current/inverse gbuffer matrices | later post-camera setup hook, once per frame; ordinal-zero clear precedes camera setup and is not capture | Phase 7 / v0.1 |
| celestial rotation | sun/moon/shadow-light/up vectors | inside sky rotation after FF transforms are established; immediate upload if a shader token is current | Phase 7 invokes; Phase 8 values / v0.2 |
| shadow camera | four shadow matrices | after Phase 8 installs its shadow FF camera, before shadow draw activation | Phase 8 / v0.2 |
| fog mode/start/end/density/color | `fogMode`, `fogDensity`, `fogColor` | GlStateManager-facing fog mutation sites plus frame fallback; immediate upload if active | Phase 7 / v0.1 |
| blend enable/factors | `blendFunc` | successful effective GlStateManager/backend changes only, including P1 override acquisition/release; suppressed attempts emit no event; immediate upload if active and effective Phase 4 `BlendSpec` at activation | Phase 7 + P1 effective state + Phase 4 descriptor / v0.1 |
| texture bind | `atlasSize`; sampler unit integers do not change | actual authenticated current base texture bind/restoration, never stitch-only availability; adapter below | Phase 7 observer/adapter + Phase 13 query / v0.5 |
| normal/specular texture change | no built-in value; future custom/texture bridge invalidation | companion-atlas bind/change | Phase 13 / v0.5 |
| render phase change | no Appendix D `renderStage`; retained as custom-extension signal only | every Phase 4/7 stage transition | Phase 7, Phase 11/G8 consumer later |
| fallback/current entity | `entityId` | scoped entity render push/pop with immediate upload and 0 restoration | Phase 9 values via Phase 7 hook / v0.3 |
| current block entity | `blockEntityId` | scoped TE render push/pop with immediate upload and 0 restoration | Phase 9 values via Phase 7 hook / v0.3 |
| entity color | `entityColor` | P7-owned hurt/flash push/pop with immediate upload and exact nested prior-color restoration, neutral outside scope; independent of alias IDs | Phase 7 / v0.1, not deferred to Phase 9/v0.3 |
| instance | `instanceId` | immediately upload IDs0…N−1 before prepared copies; restore saved predecessor, outer zero | Phase 7 / v0.5 fullscreen and authenticated non-fullscreen boundary; no history/expression refresh |
| held items | four held-item uniforms | tick/inventory change after Phase 9 alias resolution | Phase 9 / v0.3 |
| atlas size | `atlasSize` | authenticated bind → P13 Known dimensions or Unknown/non-atlas `(0,0)` → `updateAtlasSize`; immediate active upload and reload reset | Phase 7 adapter + Phase 13 value / v0.5 |

**D-P6-32 — enforced blend receiving semantics (2026-09-08).** P1 owns the duration
override and reports actual effective state through P7's existing `updateBlend` path.
P6 owns neither suppression nor bypass. An attempted setter on a held aspect causes no
event or underlying-cache update. Successful acquisition/release notifies the effective
value before built-in dispatch; P4's existing activity invalidation prevents restoration
from uploading into the previous program. Existing immediate-event replay delivery and
failure containment still apply. P6's event type and factory remain unchanged.

**Split timeline and governing milestone (D-P6-19).** Frame sampling/previous snapshots/center
depth complete before any resize/clear; ordinal-zero clear remains the earlier Phase 7 hook;
camera setup and current-matrix capture follow at a distinct hook before main shader activation.
RC3 Phase 6 assigns v0.1 and explicitly defers shadow values, alias IDs and atlas sourcing
(`DESIGN.md` §Phase 6 scope/milestone), not `entityColor`. Phase 7 therefore owns real hurt/flash
color at v0.1; Phase 9 may share the render scope but owns only its v0.3 alias IDs.
Before installation neutral color plus missing-producer warning is degraded bring-up, not a
new milestone or feature-complete fallback.

**Authenticated atlas adapter (D-P6-20).** Phase 7 publishes
`AtlasBindingSink.currentBinding(AtlasBindingEvidence) -> SignalResult`; evidence is opaque,
privately retaining `Optional<AtlasId>`, `PipelineVersion`, `resourceReloadEpoch`, and `bindSerial`.
Only its mod binding-observer mints after an actual successful vanilla bind or Phase 5 Bound
physical base-texture bind/restoration. Phase 7 checks issuer, render thread, current composition/
resource epoch and latest serial before querying Phase 13 `atlasSize(id)`. Accepted Known
dimensions become `updateAtlasSize(Int2(width,height))`; Unknown or empty/non-atlas id becomes
`updateAtlasSize(Int2(0,0))`. Stale/rejected evidence never mutates a cell. Reset/off/resource reload
invalidates old evidence and routes `(0,0)` at the quiescent live-runtime boundary before retirement;
never call the retired sink. New runtimes begin neutral and require new authenticated bind evidence.
Phase 6 immediately uploads through a current activity token/cached location, otherwise caches
for next activation. Stitch completion and gbuffers/shadow draw family do not prove the atlas is
bound. This is a separate Phase 7 sink, not a new Phase 6 operation, UniformSignal variant, or
physical binder; Phase 5 retains sole shader texture-object binding ownership.

The blend row is mandatory, not advisory. Pintonium's shared code dereferences its notifier while
registering `blendFunc`
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/CommonUniforms.java:52`),
but its hub is merely a nullable field
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/state/StateUpdateNotifiers.java:10`),
producing PD B6. Here the runtime owns the sink before any plan
is built, so declaring `blendFunc` cannot NPE even before Phase 7 wiring exists; it uploads the
neutral zeros and emits one missing-producer diagnostic in implementation bring-up.

P4 closes the predecessor lease, binds the retained program and successfully acquires its
effective alpha/blend lease before dispatching any participant. P1/P7 publish the coherent
effective blend value first; the built-in participant snapshots that observed cell, and the
custom participant reads the same stable built-in view. Disabled blend yields zero factors.
There is no descriptor overlay predicting later state, second state query by P6, early
P6 mutation or barrier bypass. Acquisition/notification failure cannot reach these callbacks.

The table is the Phase 6 side of the future Phase 7 integration review. Phase 7 must cite each
producer's actual hook-catalog row rather than saying “GlStateManager somewhere”.

### 4.13 Custom-uniform extension point

```java
public interface FixedExpressionInputSchema {
    int catalogVersion();
    FixedExpressionInputLookup lookup(String exactName);
}
public sealed interface FixedExpressionInputLookup {
    record Present(FixedExpressionInputType type) implements FixedExpressionInputLookup {}
    record Absent() implements FixedExpressionInputLookup {}
}
public enum FixedExpressionInputType {
    FLOAT, INT, VEC2, VEC3, VEC4, IVEC2, IVEC3, IVEC4, MAT4
}
public interface CustomUniformBridge {
    CustomRefreshResult refresh(
        ResolvedProgramDescriptor program,
        BuiltInExpressionView values,
        CustomUniformUploadSink uploads);
}

public interface BuiltInExpressionView {
    int catalogVersion();
    ExpressionLookup lookup(String packFacingName);
}
public sealed interface ExpressionLookup {
    record Present(ExpressionValue value) implements ExpressionLookup {}
    record Absent() implements ExpressionLookup {}
}
public sealed interface ExpressionValue {
    record Float1(float x) implements ExpressionValue {}
    record Int1(int x) implements ExpressionValue {}
    record Float2(float x, float y) implements ExpressionValue {}
    record Float3(float x, float y, float z) implements ExpressionValue {}
    record Float4(float x, float y, float z, float w) implements ExpressionValue {}
    record Int2(int x, int y) implements ExpressionValue {}
    record Int3(int x, int y, int z) implements ExpressionValue {}
    record Int4(int x, int y, int z, int w) implements ExpressionValue {}
    record Mat4(Matrix4Value value) implements ExpressionValue {}
}
public interface CustomUniformUploadSink {
    CustomSubmitResult submit(CustomUploadCommand command);
}
public sealed interface CustomUploadCommand {
    String uniformName();
    record Float1(String uniformName, float x) implements CustomUploadCommand {}
    record Int1(String uniformName, int x) implements CustomUploadCommand {}
    record Bool1(String uniformName, boolean x) implements CustomUploadCommand {}
    record Float2(String uniformName, float x, float y) implements CustomUploadCommand {}
    record Float3(String uniformName, float x, float y, float z) implements CustomUploadCommand {}
    record Float4(String uniformName, float x, float y, float z, float w)
        implements CustomUploadCommand {}
}
public sealed interface CustomSubmitResult {
    record Accepted() implements CustomSubmitResult {}
    record SkippedAbsent() implements CustomSubmitResult {}
    record Rejected(String diagnosticId) implements CustomSubmitResult {}
}
public sealed interface CustomRefreshResult {
    record NoCustoms() implements CustomRefreshResult {}
    record Completed(int accepted, int skippedAbsent, int rejected)
        implements CustomRefreshResult {}
    record Aborted(String diagnosticId, int accepted, int skippedAbsent, int rejected)
        implements CustomRefreshResult {}
}
```

The default bridge returns `NoCustoms`. `installCustomUniformBridge` rejects null with
`IllegalArgumentException`; the composition thread may call it only after construction and before
the first `beginFrame` or participant activation. The first non-default bridge installs, repeating
that exact instance is a no-op, and a different instance or a late call throws
`IllegalStateException` without changing the installed bridge. Non-terminal generation adoption
and world reset retain it; successful retirement releases it, never installs or invokes a
replacement default, and permanently rejects subsequent installation. Phase 11 installs one at
the composition root, never as a fourth Phase 4 participant.

`fixedExpressionInputSchema()` is available immediately after successful runtime construction and
does not depend on a frame, active program, provider sample, or linked location. The returned
schema is deeply immutable, exact-name keyed, and stable for that runtime. Its positive closed
type set is the enum above; `catalogVersion()` equals `UniformConfiguration`'s fixed catalog
version and changes whenever any permitted name/type pair changes. Every Appendix D name is
present except the conservative exclusion union: `entityColor`, `entityId`, `blockEntityId`,
`blendFunc`, and `instanceId` from the whole D.4 table, plus `fogMode` and `fogColor`. An unknown or
excluded exact name returns `FixedExpressionInputLookup.Absent`, never null. Current RESEARCH
§3.4 and Appendix F.6 explicitly adopt this seven-name union; F.6 states it does not override
or narrow D.4. The historical discrepancy is resolved upstream, not a pending clarification.

`BuiltInExpressionView` is an immutable snapshot from the same activation as the built-in batch.
Its `catalogVersion()` equals the construction-time schema version. A runtime
`ExpressionLookup.Present(value)` is permitted only when schema lookup for the same exact name is
`Present(type)` with this exhaustive correspondence: `FLOAT↔Float1`, `INT↔Int1`,
`VEC2/3/4↔Float2/3/4`, `IVEC2/3/4↔Int2/3/4`, and `MAT4↔Mat4`. Conversely, every present
runtime value has that matching present schema entry. A schema-present value may still be runtime
`Absent` because its producer is disabled or not yet valid; that transient absence never changes
the schema. Unknown and all seven conservatively excluded names are absent from both surfaces.

The bridge runs after the built-in participant on every successful shader activation. Expression
evaluation errors are Phase 11 rung 1 and disable only the custom uniform. Successful custom values
are handed back as typed immutable upload commands and use the same Phase 1 GL-error replay; an
upload error is rung 2 and disables only that custom uniform for that effective program.

Custom definitions may depend on tick/frame built-ins but cannot resample providers. The view
contains current values regardless of whether their GL upload was skipped as redundant.

Lookup uses the exact pack-facing built-in name; unknown, excluded, disabled, or not-yet-valid
runtime values return `Absent`, never null or a neutral invention. Values and commands are
immutable, finite where numeric, by-value records; each command contains one custom-uniform name
and exactly the float, int, bool, vec2, vec3, or vec4 payload named by its variant.

Phase 11 calls `submit` in validated definition order. Phase 6 processes each call synchronously
in that same order. A name is valid only when it is non-null, non-empty, and already in the exact
pack-facing GLSL-identifier form consumed by `ProgramUniformLayout`; Phase 6 performs no trimming,
case folding, aliasing, or other normalization.

1. an invalid custom-uniform name returns `Rejected(stableDiagnosticId)` with no GL call;
2. the first valid occurrence enters the per-refresh seen set; every later occurrence of the same
   exact name returns `Rejected(stableDiagnosticId)`, regardless of the first occurrence's result;
3. absence from the active effective `ProgramUniformLayout` returns `SkippedAbsent` with no
   location lookup, GL call, warning, or diagnostic;
4. a present declaration whose linked GLSL type does not exactly match the command variant returns
   `Rejected(stableDiagnosticId)` with no GL call;
5. an optimized-out/absent bound location returns `SkippedAbsent` with no GL call, warning, or
   diagnostic; and
6. a type-correct present location returns `Accepted` and appends one immutable command to the
   activation batch.

Thus active-layout absence is a normal per-program outcome, never `Rejected`. Actual type mismatch,
invalid name, and duplicate submission remain rejected. `Bool1` matches **only** a linked GLSL
`bool`; `Int1` matches **only** GLSL `int`. After successful `bool` validation, Phase 6 alone
encodes `false` as integer 0 and `true` as integer 1 for the Phase 1 uniform-upload facade. Phase 11
neither performs that GL encoding nor treats `Int1` as a boolean synonym.

Skipped and rejected calls occupy no batch slot and never reorder accepted calls. Accepted commands
execute and, on failure, replay in their original submission order under §4.11. Both `Completed`
and `Aborted` report the exact non-negative counts of `Accepted`, `SkippedAbsent`, and `Rejected`
sink returns; their sum equals the number of `submit` calls made in that refresh. `NoCustoms` means
zero calls. `Aborted` additionally means Phase 11 stopped before completing definition traversal,
so its counts cover only the submitted prefix. Phase 6's sink ledger is authoritative and the
bridge's three returned counters must match it. Phase 6 commits only the accepted prefix, in order,
during that activation; skipped/rejected prefix entries perform no GL, later definitions are
unsubmitted, and nothing carries into another activation. An `Accepted` count means location/type
validation admitted the command, not that its later isolated GL upload succeeded.
If any returned counter is negative or any counter differs from the sink ledger, Phase 6 emits a
stable counter-contract diagnostic, discards the entire accepted batch without GL, and returns
`BarrierParticipantResult.Degraded(diagnosticId, "custom uniforms for this activation")`. No
submission carries over and no retry occurs within that activation; the next successful shader
activation starts a fresh refresh in definition order. This invalid-result branch supersedes the
otherwise-applicable accepted-prefix commit rule for both `Completed` and `Aborted`.
Phase 6 owns schema/version publication, runtime-view conformance, location/type validation,
duplicate/order/count enforcement, boolean GL encoding, diagnostics, and §4.11 replay; Phase 11
owns expression syntax, dependency/evaluation policy, and whether an evaluation failure omits a
command or aborts the remaining refresh.

### 4.14 Reset, permanent retirement, missing producers, and neutral values

#### 4.14.1 Non-terminal reset and generation adoption

`UniformResetReason` is exactly `PACK_REPLACEMENT`, `SHADERS_OFF`, `GL_CONTEXT_LOSS`, and
`WORLD_EPOCH`. `adoptRegistryGeneration` accepts exactly the first three; direct `reset` accepts
exactly `WORLD_EPOCH`. An invalid operation/reason pairing or null reason fails fast with
`IllegalArgumentException` without mutation. **`CLOSE` is removed**, not deprecated or translated:
there is no `reset(CLOSE)` alias and no `UniformRuntime.close()`.

Registry replacement, shaders-off, and GL-context-loss transitions on a live retained runtime use
atomic generation adoption before the replacement's first frame, event or activation. Pack
replacement and shaders-off discard all program caches and disabled scopes; GL-context loss
additionally discards every location. All three retire the prior **generation**, not the runtime.
Pack replacement retains temporal values only when configuration semantics and world epoch are
unchanged; shaders-off retains borrowed providers and the installed custom bridge; GL-context loss
retains pure temporal values but requires the newly adopted publication before activation.
World-epoch reset keeps the generation and resets smoothers and previous snapshots after final
old-world use and before the next world's `beginFrame`, event, or participant activation.
Shaders-off makes live participants no-ops; this is recoverable and is not terminal retirement.

Generation adoption remains required for a new runtime constructed at the current publication's
generation: after acceptance it must adopt the reacquired authoritative generation before use.
In Phase 7's one-runtime-per-pipeline replacement, the old runtime instead ends through
`retire(REPLACEMENT)`. The historical “ordinary replacement remains exclusively on adoption”
restriction no longer applies to disposal of that old runtime. Adopting on the new/surviving
runtime and retiring the replaced runtime are different operations on different lifetimes.

#### 4.14.2 Terminal operation and closed outcomes

`UniformRetirementResult retire(UniformRetirementReason reason)` accepts exactly
`UNPUBLISHED_ABORT`, `REPLACEMENT`, and `SHUTDOWN`. Null fails fast with `IllegalArgumentException`
without mutation. For a non-null reason, the following precedence is binding:

1. Off the render thread, return `Rejected(WRONG_THREAD)`, including for an already retired
   runtime. Construction on a worker does not authorize worker-thread candidate retirement.
2. On the render thread, if already retired, return `AlreadyRetired` for any valid reason.
   This is idempotent, does not change the original terminal disposition and performs no cleanup.
3. While a Phase 6 operation/callback is in flight, return `Rejected(ACTIVE_CALLBACK)`.
   This includes any of the three participants, frame/event processing, provider invocation,
   custom refresh/submission, upload/error replay, replay-observer delivery and reentrant diagnostics within those entries;
   the guard spans the entire outer operation, not just the user callback's body. A rejected
   retirement does not clear caches/references, cancel that operation, or schedule deferred work.
4. Otherwise mark the runtime terminal and synchronously clear its operational state, then return
   `Retired`. Terminal invalidation and cleanup complete before return, without invoking callbacks.

`Retired` and `AlreadyRetired` authorize the caller to finish releasing that runtime's borrowed
services; `Rejected` does not. The caller keeps ownership and services alive, keeps frame/event
admission closed, and makes the retirement call on the render thread after the outer callback
returns. It must not treat rejection as completed disposal or continue new-pipeline drawing.
Reason-specific ordering below is a composition-root obligation, not a new public publication
flag, barrier query, or extra rejection variant in Phase 6.

Successful retirement drops every cached location (present or absent), sampler/built-in plan,
uploaded-value cache, disabled scope, active `(activityToken, ProgramCache)` pair, pending custom
batch, live value/snapshot/smoother state, and installed custom bridge. It releases all borrowed
references, including `FixedSamplerResolver`, `UniformPlatformProvider`, `CenterDepthSource`,
`GLDevice`/derived services, `DiagnosticReporter` and `UniformReplayErrorSink`, including pending
failed-delivery reports after P7 records non-complete failure; retained sinks/participants must not retain
these indirectly. It owns none of those services and neither closes them nor calls them during
retirement. There is **no GL, location lookup, error drain, barrier release/activation, token
invalidation call, unbind or handle deletion**. Phase 4 alone invalidates its activity tokens;
Phase 6 drops its reference and rejects all further use independently of token currency.

The terminal guard takes precedence over duplicate-frame, generation-equality, shaders-off and
absent-consumer paths, including on previously returned objects:

| Entry after retirement | Permanent outcome before any operational work |
|---|---|
| `adoptRegistryGeneration` with an otherwise valid reason | `REJECTED_RETIRED_GENERATION` for every generation, including the former current value; no reacquisition can revive this instance |
| `beginFrame` | `REJECTED_GENERATION`, never `DUPLICATE` or `ACCEPTED` |
| `frameTiming` | empty on the render thread for every frame/generation; wrong-thread throws IllegalStateException first; detached previously issued records remain historical values only |
| any `UniformEventSink` method, `reset(WORLD_EPOCH)`, or custom-bridge installation | `IllegalStateException`; no cell mutation, sampling or borrowed-service access |
| any retained sampler/built-in/custom participant's `afterBind` | existing `BarrierParticipantResult.Degraded` with stable `phase6.runtime.retired` diagnostic ID and that participant's scope; never successful/no-op refresh, lookup, resolver/provider access or GL |
| a retained custom upload sink's `submit` | existing `Rejected("phase6.runtime.retired")`; never enqueues, counts or uploads a command |

Rejecting stale capabilities does not call the released diagnostic reporter: the result/exception
carries the stable evidence to the caller. Runtime event/participant accessors may return their
same permanently guarded objects; obtaining them confers no new validity. Immutable detached
`FixedExpressionInputSchema`/by-value snapshots already handed out remain readable, and the schema
accessor and always-empty macro contributor remain pure metadata operations, not resurrection.
No operational reference can install a bridge, acquire new values, adopt, or refresh again.

#### 4.14.3 Final-use and borrowed-service ordering
The retired-participant diagnostic is a lifecycle violation, not permission for Phase 7 to draw
with an isolated missing uniform. The caller suppresses that stale pipeline's draw and retains
the closed-admission recovery path; Phase 6 does not rebind or perform recovery itself.


Phase 7 closes admission and drains the final frame, draw/binding/shadow scopes, producer events,
custom refresh and all three Phase 6 participants before retirement. Final-use includes scope
restoration events (for example Phase 9 per-draw resets), not only the last shader draw. No later
event/callback may be deliberately routed to that instance. These ordered obligations implement
R7-11's “after final callback and before borrowed services disappear”
(`docs/phase7/v1/PHASE_7_DOC.md:4042`):

| Reason | Required caller ordering |
|---|---|
| `UNPUBLISHED_ABORT` | candidate runtime was never accepted into a publication; stop pending adapters and abandon/close caller-owned barrier candidates so they cannot be published later, finish any construction-time use, then retire without publishing solely for cleanup; release its borrowed services only after `Retired`/`AlreadyRetired` |
| `REPLACEMENT` | finish old callbacks/restoration and detach old event routes; Phase 4 replaces/releases the old barrier and invalidates its activity token; only then retire the replaced runtime, before releasing its borrowed services; reacquire/adopt the actual accepted generation on the new runtime before any new frame/event/participant/shadow use |
| `SHUTDOWN` | stop admission and finish every final callback/restoration while services remain available; retire every still-live runtime before Phase 7 initiates Phase 4's atomic teardown, then release borrowed services; do not split that atomic teardown merely to insert retirement |

Replacement observes Phase 4's actual result: `Accepted` or `RecoveredOff` establishes old-barrier
invalidation; a pre-release `Rejected` does not. Keep the old runtime/services retained and
admission closed while Phase 7 compensates off through the existing publisher; retire that old
runtime only after invalidation is established. A rejected, never-accepted candidate uses
`UNPUBLISHED_ABORT`; a runtime whose barrier was accepted and then compensated off uses
`REPLACEMENT`, even if no frame was admitted. No failure path revives a retired runtime.
The publisher may already have deleted old program handles when replacement returns; dropping
cached locations is safe because retirement never dereferences them. This differs intentionally
from shutdown's retire-before-teardown order and requires no mid-publication callback.
Phase 4's incorporated publication contract says “invalidate the old activity token” before
“close the old registry” (`docs/phase4/v1/PHASE_4_DOC.md:1957`/`:1961`, incorporated by §5.1).

Closing Phase 8 or retiring a Phase 13 texture owner/registration in Phase 7's earlier quiescence
steps is not permission to destroy Phase 6's borrowed provider/service adapters. Keep those
adapters and the other borrowed services alive through final Phase 6 use and successful retirement;
defer their disposal if necessary. Phase 6 calls no texture-owner/lease API to enforce this.
R7-11 grants no new Phase 8/13 lifecycle capability; ungranted owner dependencies and coordinated
ordering/verification obligations remain explicit in §5.2.

Neutral values are deliberately few and visible: later-milestone IDs and integer metrics use 0;
`entityColor`/blend use zeros; pending shadow matrices use identity; pending celestial vectors use
zero; pending atlas size uses `(0,0)`. A program declaring a value whose producer milestone has not
landed receives a once-per-pack warning. Neutral values are not claimed as feature support and never
turn a future producer into optional work.

---

## 5. Cross-phase interfaces

### 5.1 Interfaces exposed by Phase 6

| Exposed contract | Exact content | Consumer(s) |
|---|---|---|
| `UniformRuntimeFactory` / `UniformBuildResult` | exact §2.2 eight-argument callable shape: `create(long initialRegistryGeneration, UniformConfiguration configuration, FixedSamplerResolver samplerResolver, UniformPlatformProvider platform, CenterDepthSource centerDepth, GLDevice gl, DiagnosticReporter diagnostics, UniformReplayErrorSink replayErrors) -> UniformBuildResult`; resolver is required immediately after configuration, borrowed from Phase 5's pure `FixedSamplerPolicies.resolver()` and paired with compilation's appB3 policy; required P7-owned replay observer follows diagnostics, no old overload/default/no-op. Closed `Success(UniformRuntime runtime)` / `Failure(String diagnosticId)`. Creation installs current `PublishedRegistry.generation`; success transfers sole runtime lifecycle, failure has no runtime, GL work or callback; resolver and observer retention/release follow §§2.2/4.14 through successful terminal retirement | Phase 7 composition/reload; R7-10 and R25-1 adopted, fresh PASS owed |
| `UniformRuntime` / `UniformResetReason` / `RegistryGenerationAdoptionResult` | exact §2.2 callable shape: `adoptRegistryGeneration(long, UniformResetReason) -> RegistryGenerationAdoptionResult`; `fixedExpressionInputSchema() -> FixedExpressionInputSchema`; `beginFrame(FrameBeginInput) -> FrameBeginResult`; `events() -> UniformEventSink`; `samplerParticipant()`, `builtInParticipant()`, and `customParticipant() -> ProgramBindingParticipant`; `centerDepthMacroContributor() -> MacroContributor`; `installCustomUniformBridge(CustomUniformBridge) -> void`; `reset(UniformResetReason) -> void`; `retire(UniformRetirementReason) -> UniformRetirementResult`. Adoption results remain `ADOPTED`, `ALREADY_CURRENT`, `REJECTED_RETIRED_GENERATION`. Reset reasons are exactly `PACK_REPLACEMENT`, `SHADERS_OFF`, `GL_CONTEXT_LOSS`, `WORLD_EPOCH`; adoption accepts the first three, direct reset only `WORLD_EPOCH`; invalid pairings/null fail without mutation. Live adoption uses the reacquired accepted generation before new use, equality-only identity and §4.14.1 state scopes. World reset separates final old-world from first new-world use. Custom bridge installation remains non-null, pre-use, first-instance-wins/idempotent; non-terminal transitions retain it, retirement releases it. No `CLOSE`, `reset(CLOSE)` alias or runtime `close()` remains | Phases 7, 8, 9, 11, 13 |
| `UniformRetirementReason` / `UniformRetirementResult` / `UniformRetirementRejection` | exact §2.2 algebra and complete §4.14 semantics: reasons `UNPUBLISHED_ABORT`, `REPLACEMENT`, `SHUTDOWN`; results `Retired()`, `AlreadyRetired()`, `Rejected(WRONG_THREAD\|ACTIVE_CALLBACK)`. Render-thread-only, terminal/idempotent, synchronous non-GL cleanup without any barrier/provider/service call. Wrong thread precedes already-retired, then active-callback rejection; rejection leaves state/ownership unchanged. Final callback precedes retirement; candidate abort requires no publication, replacement follows actual old-barrier invalidation, shutdown precedes Phase 4 atomic teardown, all precede borrowed-service disposal. Cached locations/plans/values, active token pair, pending batches and all provider/service/bridge references are dropped, not closed/deleted. Every retained operational capability is permanently guarded as §4.14.2 specifies; retirement is never generation adoption or shaders-off reset | Phase 7 composition/abort/replacement/shutdown; R7-11 adopted, fresh PASS owed; all retained-capability consumers |
| `FrameBeginInput` / `FrameBeginResult` | input schema plus `ACCEPTED`, `DUPLICATE`, `REJECTED_STALE_FRAME`, `REJECTED_GENERATION`; only accepted mutates, duplicate is a safe no-op for a live runtime, rejection forbids shader draw; retired runtime always returns `REJECTED_GENERATION` before duplicate or identity handling | Phase 7 |
| `UniformRuntime.frameTiming(long registryGeneration,long frameId) -> Optional<UniformFrameTiming>` | exact immutable §2.2 fields and §4.6 current-frame/epoch/generation/retirement/thread semantics; actual accepted input plus actual post-update owner counters, no GL/parallel clock or allocation on unqueried frames | P7 finalized capture view; P2 evidence through P7 |
| **Frame-begin ordering contract** | `beginFrame` completes sampling, previous snapshots and center-depth read before any Phase 5 resize/clear; distinct later post-camera hook captures current matrices once, never ordinal-zero clear | Phase 7; integration review |
| `UniformEventSink` and immutable sample records | exact §4.2 schemas; world/frame/tick identity; finite/range validation; copy/absence/fallback rules; held-light old-mode mapping; next-activation vs immediate-if-active policy while live; survives non-terminal reset, but every retained sink rejects after retirement with `IllegalStateException` before mutation or service/GL access (§4.14.2) | Phases 7, 8, 9, 13 |
| `SamplerRepointParticipant` | exact §4.9 shared-resolver operation/results and plan-reuse rules; unchanged `afterBind(ResolvedProgramDescriptor, BarrierContext, BoundProgramUniformAccess)`; effective `binding.samplerLayout()` plus `context.stage()/band()`, never child state; Ready exact-name/full-shape rows become ascending-unit then fixed-name declaration-order integer uploads; Invalid retains validation evidence and degrades only the effective program's sampler participant without uploads or replacement mapping; existing absent-location, deduplication, cache/activity-token and §4.11 error semantics remain while live; retirement first rejects every retained callback with `Degraded`/`phase6.runtime.retired` without resolver, lookup, upload or service access (§4.14.2), and the stale pipeline must not draw | Phase 4 composition via Phase 7; R7-10; R7-11 terminal guard |
| `BuiltInUniformRefreshParticipant` | Appendix D plan, every-activation visit, cached-value skip, matrices always upload, error isolation while live; retirement first rejects retained callbacks with `Degraded`/`phase6.runtime.retired`, without lookup, upload or service access (§4.14.2), and the stale pipeline must not draw | Phase 4 composition via Phase 7 |
| `CustomUniformRefreshParticipant` / `CustomUniformBridge` | ordered third participant; closed `NoCustoms`, `Completed(accepted, skippedAbsent, rejected)`, or `Aborted(diagnosticId, accepted, skippedAbsent, rejected)` result while live; both counted results must be non-negative and equal the authoritative sink ledger (an aborted result counts only its submitted prefix); typed immutable commands are submitted in definition order; skipped/rejected calls occupy no batch slot; a valid aborted refresh commits only its accepted prefix in original order with no carry-over; any negative or mismatched counter discards the whole accepted batch without GL, returns `BarrierParticipantResult.Degraded(diagnosticId, "custom uniforms for this activation")`, carries nothing over, and permits a fresh refresh only at the next activation; Phase 6 validates, deduplicates, counts, diagnoses, encodes bools, and isolates uploads. Retirement releases the bridge and first rejects retained callbacks with `Degraded`/`phase6.runtime.retired`, never invokes the bridge/default or uploads (§4.14.2), and the stale pipeline must not draw | Phase 11; Phase 4 composition via Phase 7 |
| `FixedExpressionInputSchema` / `FixedExpressionInputType` | deeply immutable construction-time schema, versioned exactly with the fixed Phase 6 catalog; exact-name `Present(closed type)`/`Absent`; positive types are `FLOAT`, `INT`, `VEC2/3/4`, `IVEC2/3/4`, `MAT4`; every Appendix D name except all five D.4 dynamics plus `fogMode`/`fogColor`; independent of active program/runtime validity | Phase 11 load-time compiler |
| `BuiltInExpressionView` / `CustomUniformUploadSink` | view carries the matching catalog version and exact-name `Present(typed value)`/`Absent`; every present value conforms bidirectionally to the fixed schema's exact name/type mapping; upload commands are closed to `Float1`, `Int1`, `Bool1`, `Float2`, `Float3`, and `Float4`, matching `float`, `int`, `bool`, `vec2`, `vec3`, and `vec4`; sink returns closed `Accepted`, normal no-warning/no-GL `SkippedAbsent`, or `Rejected(stable diagnostic ID)`; while live, active layout or location absence skips, while actual type mismatch, invalid name, and duplicate submission reject; `Bool1` matches GLSL `bool` and Phase 6 owns 0/1 GL encoding; outcomes preserve call order and feed the three refresh counts per §4.13. Retirement first makes retained sink submissions `Rejected("phase6.runtime.retired")`, with no counting, enqueue, service access or GL (§4.14.2); detached immutable values remain readable | Phase 11 |
| `UniformPlatformProvider` / `CenterDepthSource` | exact §4.2 request/result schemas and validation; loader-neutral sampling SPI with no Minecraft or GL-name types | `mod.glue`, Phase 7 |
| `centerDepthMacroContributor` | always `MacroContribution.Empty` under D-P6-1 | Phase 3/4 materialization |
| Authenticated current-atlas adapter | exact §4.12 P7 `AtlasBindingSink`/opaque evidence → P13 Known/Unknown query → existing `updateAtlasSize(Int2)`; reset/non-atlas `(0,0)`, stale evidence no mutation, immediate active/cached inactive behavior and retired-sink rejection | Phases 7/13, adopted/unverified |
| Governing `entityColor` producer | §4.12 Phase 7 hurt/flash scoped values at v0.1, immediate update/nested restoration independent of Phase 9 alias IDs; neutral before installation is degraded bring-up, not milestone deferral | Phases 7/9 |
| `UniformReplayErrorSink` / `UniformReplayReport` | exact §2.2 `void accept(UniformReplayReport report)` and immutable `(ProgramUniformCacheKey program, List<ReplayAwareGLError> errors)`; required final factory input after diagnostics, P7-owned/borrowed through retirement; complete §4.11 cardinality, order, original-error preservation, honest false cases, synchronous barrier/immediate delivery and failed-delivery containment; P1 owns verdict type/law, P6 replay/disable, P7 copies/classifies/escalates, P2 flattens the values into current `/4` `gl_errors` | P7 composition/observation, P2 through P7 |
| P8 shadow-independent celestial provider receipt — D-P6-37 | §4.2: P7 mod provider uses CelestialMath.angles before camera, then actual associated main camera/current frame/rotation through CelestialMath.sample at H-SKY-02, irrespective of shadow demand/availability; real shadow uses same math before activation, unchanged P6 events | P7/P8 |

The exact external schemas and semantics incorporated above from §§2.2, 4.2, 4.9, 4.11, 4.13, and 4.14 are
binding parts of §5. Every consumer-visible API, schema, or semantic change to those incorporated
declarations must update the corresponding §5 row in the same document revision; a reference that
remains textually unchanged does not waive that synchronization requirement.

### 5.2 Dependency contracts consumed

#### Phase 1

| Phase 1 §5 contract | Use |
|---|---|
| module layout, C-1…C-4, package placement | pure engine/runtime plus mod.glue providers |
| `GLDevice.uniforms()` overloads and `UniformLocation.isAbsent()` | all contract-authorized typed uploads and absent-location cache |
| `FramebufferService.readDepthPixel` | synchronous v0.1 center-depth sample |
| `GLDevice.drainErrors`, `GLError`, `ReplayAwareGLError` | §4.11 cached replay and exactly one honest verdict per original triggering error; P1 owns immutable value and true/false attribution law; P6 publishes via required P7 observer without changing GL surface |
| `RecordingGLDevice`, `ScriptedResponses.depthPixel/glError`, profiles | §8 headless tests |
| diagnostics/log channel | isolated warnings and escalation |

Existing Phase 1 overloads and the readback verb are sufficient for every Phase 6 operation.

#### Phase 3

| Phase 3 §5 contract | Use |
|---|---|
| `PackConfiguration`, schema/fingerprint discipline | accept exactly `PackFrontEnd.CURRENT_SCHEMA_VERSION`, with equal containing, nested IdMappingInput and inspection schemas; reject every other schema before derivation, no fabricated defaults or inferred upgrades |
| closed `ResourceRequirements` algebra | center-depth enablement and smoothing half-lives with published defaults/order |
| `DeclaredUniformCatalog`, `DeclaredUniform`, `DeclaredGlslType`, attributed locations | final post-materialization declaration/type provenance; consumed through Phase 4's merged effective layout without reopening source |
| reserved `phase6.centerDepthSmoothRedirect` contributor | deliberately returns Empty |
| `CustomExpressionDecl` | Phase 11 bridge inputs; Phase 6 does not parse |
| materialization/catalog fingerprints | layout and program-cache provenance through Phase 4 |

**Current schema23 receipt — D-P6-36.** Receive P3's typed-selector amendment and exact current
containing/nested-ID/inspection schema23 with MaterializedSource-v23 before derivation/reuse.
Selector ranges and BLOCK alternate provenance remain P3/P9-owned; P6 neither selects nor parses them.
Nine trees/projectionVersion1, same-load assets, options and native source contracts remain.
D-P6-35 and other prior numeric receipts are historical, not alternate current admission.

**Historical schema21 receiver receipt — D-P6-26, 2026-09-08 (unverified).** P3 R55 changes
the nested configuration to payload-free `ScreenProfileEntry()`, exact external
`TEXTURE_RECTANGLE` mapped to existing `TextureTarget.RECTANGLE` (bare `RECTANGLE` invalid),
and a usable source configuration surviving mandatory decode/include validation in base OR
an explicit OVERRIDE; empty disabled folders alone are not usable sources. P6 receives the owner's
current configuration/materialization identity without parsing those fields or sources.
Containing/nested/inspection versions all equal CURRENT_SCHEMA_VERSION (=21 for this receipt).
The following D-P6-24 receipt is historical as to version; its D-P3-69 assets and nine-tree
metadata meanings remain binding and unchanged.

**Schema20 receiver receipt — D-P6-24, 2026-09-08 (unverified).** Adopt P3
§0.62/D-P3-69/§5; earlier current-schema assertions and the schema19 receipt below are
historical version adoptions. Require containing and nested `IdMappingInput` schema20
before derivation/reuse. The required non-null `assets` after `sources` is the exact
same-load P3 capability paired with the exact containing `PackIdentity`, never a foreign
load even if structurally equal. Carry the owner's metadata-derived configuration identity
unchanged through P4/P7 handoffs, with no dummy fields, empty-manifest upgrade, capability
reconstruction or resource-epoch relabeling. P6 acquires/decodes no binary assets.
Center-depth, half-lives, Empty contribution, merged final catalog and event/upload semantics
are unchanged. Any inspection retains only P3's actual ninth canonical assets metadata
section, digest strings via TextHash, original eight meanings and projectionVersion=1;
no bytes/cursors/providers enter it. P13 owns optional owned-sidecar-only recovery;
other P3 safety/bounds/index/container/source/configuration errors remain fatal.
Historical authority/reviews stay intact; fresh owner/receiver review and IR-01 remain.

**Schema19 receiver receipt — 2026-09-07:** adopt P3 D-P3-68/§0.61 and §11 migration.
Containing/nested schema and configuration/materialization identity change; center-depth,
half-lives, singular Empty contribution, merged final catalog, events and upload ownership
do not. P6 consumes the effective P4 layout, never native source/topology parsing or old
translator success. Historical schema18 receipts are superseded only for current consumption;
fresh producer/receiver reviews and IR-01 remain, with no runtime or PASS claim.

#### Phase 4

| Phase 4 §5 contract | Use |
|---|---|
| `ProgramSlotDescriptor` / `ResolvedProgramDescriptor` / `ProgramUniformLayout` | stage/effective identity, whole provider state, immutable exact-name declaration/type layout, and fixed-function empty layout; effective descriptor supplies `samplerLayout` without fallback-child overlay |
| `ProgramSamplerLayout` / `ProgramSamplerDeclaration` / `ProgramSamplerLayoutFingerprint` / `FixedSamplerPolicyFingerprint` / `SamplerLayoutValidation` | exact Phase 4 §2.2 schemas incorporated by its §5.1: lossless effective sampler declarations, policy/layout identities and closed Valid/ConflictingTypes/Unsupported evidence; passed unchanged to Phase 5's resolver, never reconstructed from the uniform catalog |
| `PublishedRegistry.generation` / `RegistryFingerprint` | cache invalidation and reload identity |
| `ProgramUniformCacheKey` / `ProgramUniformLayoutFingerprint` | exact generation + effective provider + linked-layout cache identity shared by fallback children |
| `BarrierContext` | Phase-4-issued, authenticated effective stage/band inputs to the shared resolver and frame validation; not a Phase 6 map selector |
| `ProgramBindingParticipant.afterBind` / `BoundProgramUniformAccess` | callback-only exact-name lookup over the private bound program; no handle or program operation |
| `BoundProgramActivityToken` | retainable operation-free current-activation proof for already-cached immediate uploads |
| three fixed `ProgramBindingParticipant` positions | sampler, built-in, custom ordering on every shader activation |
| `BarrierParticipantResult.Degraded` | isolated uniform/participant degradation |
| per-slot `instanceCount` | Phase 7 loop supplies Phase 6 `instanceId` |

Phase 6 never bypasses `PublishedRegistry.barrier`. Phase 4 binds its retained selection and invokes
exactly the three callbacks; no Phase 6 operation selects or activates a program independently
(`docs/phase4/v1/PHASE_4_DOC.md:1787-1788`, “invoke sampler, built-in, custom in that order”).

For IR-18, P7's v0.5 non-fullscreen adapter sends `updateInstanceId(i)` for each prepared
native copy and restores the saved preceding value (outer zero), not unconditional zero in
a nested scope. Frame sampling/history rotation and custom refresh remain at existing
boundaries; this event invokes no program activation or fourth participant. A propagated
event/protocol/restoration exception follows P7/P8 containment and stops remaining copies;
stale admission never becomes valid. The event still returns void. §4.11's internally isolated
GL upload errors retain their per-uniform degradation and diagnostics, not a new return status
or automatic frame abort. Disabled/absent instance locations do not suppress native copies;
P2 still counts recorded errors against conformance.

#### Phase 5 — R7-10 pure policy dependency

| Phase 5 §5 contract | Use |
|---|---|
| `FixedSamplerPolicies.resolver()` / `FixedSamplerResolver` | required borrowed factory input immediately after configuration; pure and available before runtime/registry/estate creation; same sole table/schema/fingerprint as `FixedSamplerPolicies.appB3()` used by Phase 4 compilation |
| `FixedSamplerPlanResult` / `ResolvedSamplerBinding` | exact `resolve(ProgramSamplerLayout, StageId, StageBand)` result `Ready(List<ResolvedSamplerBinding> bindings, FixedSamplerPolicyFingerprint policy)` or `Invalid(SamplerLayoutValidation reason)`; row `(String exactName, DeclaredGlslType.Sampler shape, int unit)`; complete §4.9 semantics incorporated, with no Phase 6 unit policy, physical handle selection or texture-binding operation |

These existing Phase-5-owned interfaces are published at
`docs/phase5/v1/PHASE_5_DOC.md:2726` (“no second map or free-unit allocation”), incorporating
§§2.4/4.12.1. The user-authorized R7-10 adoption adds this pure policy dependency to the maintained
Phase 6 architecture; it does not silently change RC3's original declared dependency graph.

**Dependency status / implementation gates:** these are current-byte contracts, not fresh verified
grants. Phase 4's coordinated §5 explicitly awaits whole-document PASS
(`docs/phase4/v1/PHASE_4_DOC.md:329`, “Unverified”); Phase 5 states that its prior PASS does not
certify the rebuild and that Phase 3 and Phase 4 remain provisional
(`docs/phase5/v1/PHASE_5_DOC.md:338-354`, “that evidence does not certify this rebuild”).
The older verified declaration/access grants in §5.3 are historical evidence for the unchanged
mechanics, not certification of the new sampler-layout/resolver surface. Implementation of this
seam requires the corresponding current Phase 3/4/5 owner verification gates and a fresh
whole-document Phase 6 literal PASS. R7-10 is adopted here only; sibling request-status rows are
not edited by this owner.

#### R7-11 adoption and remaining lifecycle gates

R7-11 at `docs/phase7/v1/PHASE_7_DOC.md:4038-4049` (the adoption-time quote “its owner must
reconcile that rule” is historical; current bytes state “Phase 6 removed reset(CLOSE)”)
is adopted here by §§2.2/4.14/5.1, **pending fresh whole-document Phase 6 PASS**. No new Phase 4
operation is needed: consume its existing old-token invalidation and actual
`Accepted`/`Rejected`/`RecoveredOff` publication outcomes under its still-open owner-review gate.
Phase 7's current integration amendment adopts R7-10/11 consumption and final-use/service
retention. Phase 4/5's ledgers likewise recognize those grants as owner-designed/unverified
and receiver-adopted/unverified; this coordination does not replace fresh whole-document reviews.

**Remaining gates:** Phase 8 §§0.7–0.8 now adopts R7-12/R7-13 and Phase 3 §0.55 publishes
the required companion input. Phase 1 package/native-configure and Phase 3 lossless/direct
projection grants likewise exist; they are not supplied or verified by retirement. Adopt the
2026-09-07 `docs/decisions/U1_TEXTURE_SAMPLING.md` correction: numeric discriminators and
P13-owned sidecars remain, but no unspecified key-suffix parser or typed suffix grant is required.
jcpp permission, native legacy source preservation and every affected owner's fresh verification
remain distinct. Phase 7 retains provider/service adapters through final use and
Retired/AlreadyRetired even when texture/shadow owners retire earlier; no invented P6 barrier/
close hook or early disposal breaks that ordering.

### 5.3 Historical verified dependency contract changes

The original build document requested the following two interfaces. Both were granted and freshly
verified at the cited rounds. Their unchanged declaration/access mechanics remain in use; those
historical reviews do not certify the current coordinated owner bytes or waive §5.2's gates.

1. **Phase 3 — declaration metadata granted.** Every successful `MaterializedSource` carries a
   complete immutable `DeclaredUniformCatalog`: exact name, closed structural GLSL type, declaring
   stage, attributed identifier location, and the materialization-linked fingerprint. It excludes
   uniform blocks and never claims driver activity
   (`docs/phase3/v1/PHASE_3_DOC.md:1435`). Round twenty's literal PASS makes this a valid dependency
   input (`docs/phase3/reviews/PHASE_3_REVIEW_20.md:58`–`:67`).

2. **Phase 4 — merged layout, lookup, and activity granted.** Phase 4 merges equal structural types,
   rejects same-name conflicts before GL, exposes the effective handle-free layout, and passes each
   shader participant callback-scoped `BoundProgramUniformAccess` with the exact cache key and
   retainable operation-free token. Lookup delegates privately to Phase 1; returned locations live
   only within the publication generation; token invalidation precedes every later activation,
   release/off/replacement/teardown
   (`docs/phase4/v1/PHASE_4_DOC.md:1910`/`:2131`). Round eighteen's literal PASS verifies the
   changed interface (`docs/phase4/reviews/PHASE_4_REVIEW_18.md:57`–`:70`).

No `ProgramHandle`, source string, or parallel declaration parser is assumed. The provisional
current-byte sampler/retirement grants and remaining authority/verification gates are explicit in §5.2.

### 5.4 Requested governing clarification

RC3 calls the object-like GPU redirect “legal without an AST transformer”
(`docs/design/v2.0-RC3/DESIGN.md:1746`). Against Phase 3's actual macro placement, it also rewrites
the pack's uniform declaration and is not legal without a declaration-safe source operation.
Request the next `DESIGN.md` candidate qualify Candidate B with both prerequisites identified in
§4.8: declaration-safe rewriting and an explicit fixed sampler-unit extension. This document follows
RC3's stated default—Candidate A when the check is inconclusive—without editing the governing file.

---

## 6. Failure modes & degradation

| Failure | Ladder rung | Behavior |
|---|---:|---|
| custom expression evaluation fails | 1 | Phase 11 omits that command or returns `Aborted(diagnosticId, accepted, skippedAbsent, rejected)` for the submitted prefix; accepted earlier commands remain isolated |
| custom command has an invalid name, actual linked type mismatch, or duplicates a valid name | 1/2 | sink returns `Rejected` with a stable diagnostic; first occurrence owns the name for that refresh; no GL call for rejected command |
| custom name is absent from the active layout or has an optimized-out location | normal | sink returns `SkippedAbsent`; no GL call, warning, or diagnostic; later commands continue in definition order |
| custom refresh returns a negative or sink-ledger-mismatched counter | 2 | emit a stable counter-contract diagnostic; discard the whole accepted batch without GL; return Phase 4 `Degraded` scoped to custom uniforms for this activation; carry nothing over and retry only as a fresh refresh at the next activation |
| one built-in/sampler/custom GL upload reproduces an error | 2 | disable only that uniform for the effective program/generation; warning on `.uniforms`; continue program |
| feature provider fails (center-depth source unavailable, matrix inverse singular) | 2a | disable/retain only that feature value; center depth retains prior sample, bad inverse disables only inverse |
| Phase 4 compile/link/validate fails | 3 | Phase 4 backup chain; Phase 6 has no cache for a failed program |
| uniform layout/type conflicts with Appendix D | 2 | disable only the mismatched uniform; attributed diagnostic; do not coerce |
| Phase 5 resolver returns `Invalid(SamplerLayoutValidation)` | 3 | retain complete typed evidence in diagnostic; return existing Phase 4 `Degraded` for that effective program's sampler participant; no sampler upload or replacement map, unrelated uniform scopes unchanged |
| sampler plan maps one location to conflicting units | 3 | sampler participant degrades that program; do not issue ambiguous integer uploads |
| batched GL drain is non-empty but replay is clean | 3→4 | mark unattributable, disable no uniform; rate-limit only ordinary logs, deliver every typed report and hand recurring results through P7 to existing broader escalation |
| replay observer rejects/throws or receiving storage loses evidence | 5 guard / capture failure | §4.11 retains pending originals, latches `phase6.replay.delivery.failed`, stops new uploads; existing barrier Degraded or immediate exception reaches P7 recovery; no guessed disable, silent loss or COMPLETE |
| required capability/texture-unit count fails at init | 4 | pack off through existing capability gate; no Phase 6 GL work |
| provider throws or returns non-finite/out-of-range data | 2/2a | catch at seam, retain last valid affected cell or neutral on first sample, diagnose once; unrelated cells continue |
| immediate signal arrives with no current Phase 4 activity token | normal | replace the cell but issue no GL call; next activation uploads it |
| frame ID repeats on a live runtime | — | return `DUPLICATE`; idempotent no-op; Phase 7 may continue; retirement guard takes precedence |
| frame/world identity regresses or generation mismatches | 5 guard | return the exact §4.6 rejection; draw forbidden; Phase 7 reacquires publication |
| retirement called off-thread or during an active callback | 5 guard | exact `Rejected(WRONG_THREAD\|ACTIVE_CALLBACK)` without mutation; caller retains runtime/services, keeps admission closed and retries only at the legal render-thread boundary |
| operation uses a retired runtime or retained capability | 5 guard | exact §4.14.2 rejection before state mutation, borrowed-service access or GL; never revive through generation equality, duplicate frame, shaders-off or bridge installation |
| matrix capture missing for frame | 2a | disable current gbuffer matrix set for that frame; previous snapshot remains coherent |
| second matrix capture in frame | — | first wins; invariant diagnostic |
| missing later-milestone producer | planned degrade | neutral value plus once-per-pack warning; declaring the uniform never throws |
| center-depth dimensions/FBO unavailable | 2a | retain previous smoothed depth; first unavailable frame leaves cell invalid |
| built-in/sampler location optimized out | normal | cache absent; no upload, no warning |
| unexpected runtime exception crosses uniform entry point | 5 | Phase 1/mod.core boundary disables shaders and resumes vanilla path |

The system never catches a failure by calling raw GL, rebinding a program, or restoring vanilla
state itself. Those would violate Phase 4 and §G4.6 ownership.

---

## 7. Threading & performance notes

### 7.1 Thread ownership

| Component | Thread |
|---|---|
| runtime construction from pure configuration | any caller thread before publication; no GL work |
| injected Phase 5 fixed-sampler resolver | pure; plan resolution inside render-thread `afterBind` on plan-cache miss, with immutable results reused |
| `beginFrame`, all event methods, all barrier participants | render thread only |
| `UniformPlatformProvider` / `CenterDepthSource` production implementation | render thread only |
| `UniformReplayErrorSink.accept` | synchronous render-thread-only under outer-operation guard, no GL/provider/lifecycle/reentrant work; borrowed through final use and successful retirement |
| smoothing, inverse, catalog planning in headless tests | test thread; no affinity |
| custom bridge evaluation | Phase 11 may prepare pure expression plans elsewhere, but activation/evaluation and upload occur on render thread at v0.4 |
| generation adoption for pack/registry replacement or GL-context loss | render thread after accepted publication and authoritative-generation reacquisition, before replacement use |
| direct `reset(WORLD_EPOCH)` | render thread, after final old-world and before first new-world use; non-terminal |
| `retire(UNPUBLISHED_ABORT\|REPLACEMENT\|SHUTDOWN)` | render thread even for worker-created candidates; after final callback; replacement after old-barrier invalidation, shutdown before atomic Phase 4 teardown, every reason before borrowed-service disposal; wrong-thread/in-flight rejection and ordering are exactly §4.14 |

No event is queued asynchronously. A hook observes a value and writes it synchronously before the
draw activation that consumes it. This prevents an entity ID or blend state from crossing draw
scope.

### 7.2 Hot paths and allocations

The hot path is every successful Phase 4 activation. Steady state must allocate nothing:

- program locations and command slots are built once per effective program/generation; sampler
  plans reuse the matching effective sampler-layout/policy and stage/band inputs within that cache;
- value cells reuse primitive storage;
- activation copies values into preallocated command slots, with immutable semantics enforced by
  ownership rather than per-switch record allocation;
- provider sampling occurs once per tick/frame, not per program switch;
- dynamic signals reuse the current cache and issue only changed commands after one token check;
- no stream, iterator allocation, boxing, varargs, matrix copy, or source lookup occurs in the
  activation loop; and
- error replay allocates diagnostics only on failure.

Clean-path GL cost is changed-value scalar/vector uploads plus every declared matrix, followed by
Phase 1's one-query clean drain protocol. Sampler integers usually skip after the first activation
of a linked program, while their plans are still visited every switch.

Location cache cardinality is bounded by:

```text
effective linked programs × (declared Appendix-D built-ins + declared fixed sampler aliases)
```

not by requested fallback slots. A generation replacement drops the old map in one operation.

### 7.3 Synchronous center-depth cost

The v0.1 read is deliberately one pipeline stall per rendered frame **only when declared**. It is
performed before resize/clear so it observes the completed prior image and cannot multiply by
program switches. Phase 14 owns measuring/replacing that stall. Phase 6 records frame time but does
not hide the cost with an uncontracted one-frame queue.

---

## 8. Testability plan

All unit tests live in `:engine` and use scripted providers plus Phase 1's recording facade. No
Minecraft, LWJGL, display, pack source, or image is needed.

### 8.1 Headless unit tests

| Test | Assertions |
|---|---|
| `UniformCatalogCompletenessTest` | every Appendix D name appears once with exact type/provider/cadence/milestone; no extra built-in silently added |
| `SharedSamplerRepointTest` | real Phase 5 resolver plus effective fallback layouts yield exact integer uploads in unit/fixed-name order; direct `watershadow` selects `shadow` at 5 even if its location is optimized out, otherwise 4; `depthtex1` uploads 11; gbuffers/shadow never acquire a unit-12 sampler; unsupported declarations/domains/shapes and conflicting types return Degraded without sampler uploads or local fallback; same-unit distinct locations both upload, duplicate location/unit pairs coalesce, conflicting location units degrade; repeated activation and generation replacement preserve skip/invalidation behavior |
| `TickEmaClosedFormTest` | first sample, 1/2/3 half-lives, irregular \(\Delta t\), zero half-life, zero time, asymmetric wet/dry |
| `EyeBrightnessQuantizationTest` | independent components, truncation toward zero, 0…240 clamp |
| `FrameOrderingTest` | call log proves depth/world sampling completes before synthetic resize/clear callback |
| `FrameIdempotenceTest` | duplicate frame does not resample, rotate previous state, increment counters, or advance EMA |
| `TemporalSnapshotTest` | previous camera/matrices equal exactly the prior accepted frame; two switches do not advance; world reset previous=current |
| `MatrixCaptureTest` | copies mutable input, correct inverse, singular inverse isolates only inverse, matrix uploads always occur |
| `ProgramLocationCacheTest` | one lookup/name/effective program/generation; optimized-out absent cached; fallback children share provider cache; generation invalidates |
| `RedundantUploadTest` | unchanged scalars/vectors skip, changed upload, `-0.0` canonicalized, matrices never skip |
| `BarrierOrderTest` | sampler → built-ins → customs after recorded program use; fixed-function outcome invokes none |
| `DynamicSignalUploadTest` | entity/instance/fog changes upload without rebind while token current; invalidated token performs no GL call and next activation catches up |
| `UploadErrorIsolationTest` | batched failure triggers cached replay, one reproduced name disabled, providers not re-entered, siblings remain |
| `ForeignGlErrorTest` | non-empty batch followed by clean replay disables nothing; every original error remains false in typed delivery even while WARN logging is rate-limited; recurring reports reach existing broader escalation |
| `CenterDepthDecisionTest` | macro contributor always Empty; one read at exact center; disabled when undeclared; no read on unavailable dimensions |
| `NotifierCoverageTest` | every signal enum has a producer-audit row and a non-null sink method; `blendFunc` declaration never needs listener assignment |
| `FixedExpressionInputSchemaTest` | construction-time immutability; catalog-version change on any name/type change; exact positive type for every permitted Appendix D name; all five D.4 names plus `fogMode`/`fogColor` absent; unknown exact name absent |
| `ExpressionViewSchemaConformanceTest` | view/schema versions match; every runtime `Present` maps to the exact closed schema type and every present value variant has that mapping; transient invalid values are runtime-absent without changing schema |
| `CustomBridgeOrderTest` | default no-op; stable permitted view; conservative exclusions absent; custom upload follows built-ins |
| `CustomBridgeLifecycleTest` | null, late, and different-instance installs fail without mutation; same-instance repeat is a no-op while live; non-terminal transitions retain the bridge; retirement releases it and permanently rejects installation and stale sink submissions |
| `CustomSubmissionDispositionTest` | present/type-correct returns `Accepted`; active-layout and optimized-location absence return `SkippedAbsent` with zero GL/warnings; invalid name, actual type mismatch, and second exact-name submission return `Rejected`; skipped/rejected calls do not reorder the accepted batch; `Completed` counts all three outcomes exactly |
| `CustomBoolUploadTest` | `Bool1(false/true)` validates only against GLSL `bool` and records Phase 6 integer uploads 0/1; `Int1` against `bool` and `Bool1` against `int` reject without GL |
| `CustomAbortPrefixTest` | mixed accepted/skipped/rejected prefix then abort returns the exact three sink-ledger counts, uploads only accepted entries in original order, omits the suffix, and carries nothing forward |
| `CustomCounterContractTest` | each negative and each ledger-mismatched completed/aborted counter discards every accepted command with zero GL, emits the stable diagnostic and Phase 4 activation-scoped degradation, carries nothing over, and retries only through a fresh next-activation refresh |
| `ResetLifecycleTest` | initial generation installed; live replacement adoption precedes frame/activation; equality is idempotent only while live; retired generation input rejects; generation/context/world reset scopes match §4.14.1; no stale location use; reset/adoption never retires the runtime |
| `UniformRetirementTest` | unpublished candidate can retire without publication; old runtime retires after replacement invalidation while new runtime adopts and works independently; shutdown retires before atomic teardown; every reason follows final restoration/callback and precedes borrowed-service disposal; no GL/barrier/provider/diagnostic-service call during retirement; wrong-thread and reentrant active-callback rejection preserve usable state until legal retirement; repeat returns AlreadyRetired without cleanup; old sink, all three participants, current/new-generation adoption, duplicate beginFrame and custom install/submission cannot mutate, upload or revive afterward |
| `UniformFrameTiming` observable query | before-first/stale/future/mismatched/reset/adopted/retired returns empty; wrong thread fails even retired; accepted report matches values actually uploaded, duplicate query/begin does not advance, wrap boundaries match real cells, detached record survives but cannot authenticate a new epoch; unqueried path allocates no timing record |
| `UniformReplayEvidenceBoundary` | repeated equal trigger errors survive in exact list/callback order and retain original objects; mixed true/false verdicts correlate per original error, never per batch label; clean/ambiguous/foreign/different-kind replay cannot become true; cleanup drains remain false and probes are not double-counted; barrier and immediate callbacks complete before return; rejecting/throwing/overflowing observer prevents COMPLETE, retains available originals and cannot be swallowed by P4 participant containment; subsequent positions do not upload, retirement rejection retains observer and successful retirement releases it |
| `CelestialPolicyProviderBoundary` | same retained frame sun/sky values feed pre-camera shadowAngle and later actual main-camera computation; day boundary s=0.5 agrees, translated main modelView does not translate w=0 vectors, stale invocation/camera association rejects before compute; existing P6 sample schemas and sky/shadow publication/restoration moments remain unchanged |
| `CelestialWithoutShadowDemand` | v0.2 zero shadow minima and disabled/unavailable shadow estate still deliver correct main eye-space sun/moon/light/up vectors; stale frame/camera association rejects, translation alone leaves w=0 values unchanged; no shadow allocation or fabricated Ready plan |

`RecordingGLDevice` is the assigned mechanism
(`docs/phase1/v14/PHASE_1_DOC.md:3019`/`:5516`/`:5756`, “recorded-GL run”). Scripted depth answers and GL errors
exercise the two otherwise-driver-shaped paths.

### 8.2 Conformance coverage

- **T0:** packs declaring every built-in and sampler link without type/location crashes; BSL-style
  alternate declarations cannot NPE `blendFunc`.
- **T1:** static scenes cover fog, rain/wetness, water/lava eye state, item brightness, sky color,
  and sampler routing.
- **T1 camera-path motion:** required for previous-camera/matrix and center-depth temporal behavior.
  RC3 says static captures miss the hardest temporal failures
  (`docs/design/v2.0-RC3/DESIGN.md:669`).
- **T2 classic packs:** compare wetness/brightness rounding, previous-frame motion, unit-11 depth,
  and CPU center depth against OptiFine G6 within Phase 2's tolerance.
- **v0.2 celestial/shadow scenes:** cover main celestial delivery with zero shadow demand and
  disabled/unavailable shadows, plus real shadow celestial timing and all four shadow matrices.
- **v0.3 ID scenes:** held/entity/block-entity scoped values and reset to 0.
- **v0.4 custom scenes:** compile against the fixed schema before any activation; built-ins are
  visible before custom evaluation; all seven conservative exclusions remain absent; bool values
  encode correctly; a uniform declared in only one linked program accepts there and skips without
  warning in the other.
- **v0.5 atlas/instance scenes:** atlas-size changes and N total prepared copies with IDs
  `0…N−1`; N=1 is exactly one draw with ID0. Outermost fullscreen restores neutral0;
  genuinely nested prepared submissions restore their saved parent value on every exit.

No pack or rendered image enters the repository. Derived artifacts follow the hash/provenance policy
at `docs/design/v2.0-RC3/DESIGN.md:691`.

### 8.3 Implementation gate

The Phase 6 implementation gate is met when:

1. all §8.1 tests are green against at least two recorded capability profiles;
2. the recording log proves documented cadence, location-cache, shared-resolver integer routing, and skip behavior;
3. the error replay test proves providers are evaluated once;
4. the frame log proves center depth precedes resize/clear; and
5. integration compilation consumes the current Phase 3 declaration, Phase 4 effective layout/access,
   and Phase 5 sole-resolver contracts after §5.2's owner gates, without source reopening, a parallel
   parser, a second sampler map, or test-only handle leakage; and
6. the schema/view conformance and accepted/skipped/rejected ordering/count tests prove the Phase 11
   interface without a GL context; and
7. retirement traces prove unpublished abort, accepted/recovered-off versus rejected replacement,
   and shutdown ordering with zero retirement GL/service calls and no post-retirement resurrection.

---

## 9. Milestone staging

| Component | Architected | Implemented/wired | Notes |
|---|---:|---:|---|
| catalog, cadence, providers, frame snapshot, CPU smoothing | v0.1 | v0.1 | Phase 6 core |
| synchronous center depth + empty macro contribution | v0.1 | v0.1 | PBO remains Phase 14 |
| shared Phase 5 resolver injection and existing sampler participant | v0.1 | v0.1, gated by §5.2 owner reviews and fresh Phase 6 PASS | Phase 5 owns unit policy/backing objects; no duplicate map |
| built-in participant, location/value caches, error replay | v0.1 | v0.1 | unchanged P3/P4 mechanics; current owner-review gates in §5.2 |
| non-terminal reset/adoption and terminal `retire(reason)` | v0.1 | v0.1 with Phase 7, gated by fresh Phase 6/4/7 owner verification | R7-11; final use before retirement, borrowed-service disposal after it; no CLOSE alias |
| gbuffer capture/inverse/previous machinery | v0.1 | v0.1 | hook invoked by Phase 7 |
| frame/fog/blend/entityColor producers | v0.1 | v0.1 with Phase 7 | audit required |
| versioned fixed-expression schema, conforming runtime view, typed custom sink/default participant | v0.1 | v0.1 no-op | closed types/outcomes, bool encoding, and normal per-program absence are fixed before Phase 11 plugs in at v0.4 |
| shadow matrices and celestial value producers | v0.1 | v0.2 | Phase 8 |
| held-item/entity/block-entity values | v0.1 | v0.3 | Phase 9 |
| custom-expression evaluation | v0.1 extension | v0.4 | Phase 11 |
| atlas-size producer | v0.1 | v0.5 | Phase 13 |
| composite `instanceId` loop values | v0.1 | v0.5 | Phase 7 execution |
| GPU center-depth alternative | evaluated now | not scheduled | rejected D-P6-1 |

Architecture-now means every later value owner gets a stable typed slot at v0.1. It does not mean a
neutral value is feature completion.

---

## 10. OQ & spike specifications

No RESEARCH open question is assigned to Phase 6
(`docs/design/v2.0-RC3/DESIGN.md:1691`, “**OQs:** —”), so this section is intentionally short.

The GPU-vs-CPU center-depth choice is a required design decision, not an OQ. Its contract check and
fallback are complete in §4.8. Phase 14's OQ-15/OQ-22 work may measure asynchronous alternatives but
does not reopen D-P6-1 without the declaration/unit prerequisites.

---

## 11. Decisions & open items

### 11.1 Phase-local decision log

| ID | Decision | Rationale |
|---|---|---|
| D-P6-1 | select synchronous CPU `centerDepthSmooth`; return empty macro contribution | only candidate expressible by current App D, Phase 1, Phase 3, and fixed-unit contracts; §4.8 |
| D-P6-2 | separate acquisition cadence from every-activation refresh | prevents program switches/replay from resampling world state while preserving observable refresh |
| D-P6-3 | interpret half-lives as ticks and use time-corrected closed-form EMA | governing assignment says per-tick; Pintonium deciseconds are evidence for math only |
| D-P6-4 | truncate smoothed eye brightness toward zero after continuous EMA | deterministic `ivec2` rule, verified working reference mechanic, bounded by T2 evidence |
| D-P6-5 | rotate previous camera/matrices only at frame begin, never on provider read | program switches cannot advance a per-frame contract |
| D-P6-6 | consume Phase 5's sole App B.3 resolver for sampler integers; never maintain another map, allocate or bind units | pack-visible fixed numbers and P5/P6 split; R7-10 adoption in §§0.23/4.9 |
| D-P6-7 | cache by effective linked program + generation, not requested fallback slot | Phase 4 fallback copies the whole provider binding |
| D-P6-8 | use typed always-present event sink rather than assignable nullable notifiers | makes B6 structurally impossible and auditable |
| D-P6-9 | GL-error disable scope is uniform + effective program + generation | rung 2 says one uniform only; avoids cross-program over-degradation |
| D-P6-10 | upload matrices unconditionally; exact-bit skip other types | Appendix D cadence contract |
| D-P6-11 | unused terrain metrics are explicit zero providers | preserves declared types/default behavior without inventing a source |
| D-P6-12 | Historical blend projection before later lock application; superseded by D-P6-33 | Disabled blend still yields zeros; current values come from observed effective state after successful pre-participant acquisition |
| D-P6-13 | per-draw/celestial/fog/atlas signals upload immediately only under a Phase-4-invalidated activity token | satisfies hook-time cadence and `instanceId` between-draw semantics without retaining a program handle or uploading into a different program |
| D-P6-14 | seed each temporal smoother from its first valid target | prevents an invented startup/world-transition fade; subsequent samples use the exact tick-domain EMA |
| D-P6-15 | publish one immutable exact-name fixed-input schema at the Phase 6 catalog version, excluding all D.4 names plus `fogMode`/`fogColor` | gives Phase 11 load-time types without depending on a first activation and preserves the conservative authoritative-source union |
| D-P6-16 | distinguish custom submission as accepted, skipped-absent, or rejected and count all three without reordering accepted commands | per-program declaration absence is normal; only admitted commands may enter the GL batch, while invalid/type/duplicate errors stay visible |
| D-P6-17 | make `Bool1` distinct from `Int1` and encode boolean 0/1 only inside Phase 6 after linked-GLSL validation | keeps expression typing in Phase 11 and GL representation/location ownership in Phase 6 |
| D-P6-18 | replace terminal `reset(CLOSE)` with R7-11's non-GL `retire(reason)`; keep generation adoption non-terminal | unaccepted abort, replaced-instance disposal and shutdown require distinct ordering and permanent capability invalidation, not a teardown-only alias; §§0.24/4.14 |
| D-P6-19 | Separate pre-clear sampling from later post-camera current-matrix capture; keep Phase 7 entityColor at governing v0.1 | IR-11; a later alias-ID owner cannot silently defer non-alias color |
| D-P6-20 | Accept only Phase 7 authenticated current-bind evidence translated through P13 Known/Unknown into existing atlas sink | IR-21; availability is not binding, and P5 remains sole physical binder |
| D-P6-21 | Reconcile R7-10..13 grants and retain permanent retirement after final use with rejection retaining services | IR-04/10; owner-designed is not freshly verified |
| D-P6-22 | Adopt P3 schema18 and its changed configuration fingerprints through the same P4/P7 handoffs | Locale publication, Internal session options and old-light projection change configuration identity, not P6 event/upload ownership; no schema17 reuse |
| D-P6-23 | Consume P7's v0.5 prepared-submission instance sequence and saved-parent restoration through the existing immediate event sink | Per-copy instance uploads do not rotate history, sample providers or refresh expressions; P7/P8 retain traversal and failure ownership (IR-18) |
| D-P6-24 | 2026-09-08: adopt P3 D-P3-69 schema20, matching nested IDs and exact same-load assets/configuration identity in §5.2 | Supersedes older current-version assertions only; no depth/contribution/upload changes, binary acquisition or historical PASS promotion |
| D-P6-25 | Grant actual accepted-frame timing query to P7/P2, 2026-09-08 | No inferred shader counters, no GL, no second clock; only authenticated P7 capture changes supplied seconds, smoothing remains ticks |
| D-P6-26 | Adopt P3 R55 schema21 and payload-free profile metadata, 2026-09-08 | Current equality across configuration/nested ID/inspection/materialization; retained assets/nine-tree meanings unchanged |
| D-P6-27 | 2026-09-08: grant required P7-owned replay sink and immutable per-drain report, synchronous barrier/immediate evidence and failure containment | R25-1; P1 verdict law preserved, no logging loss, inferred attribution, facade verb or extra participant |
| D-P6-28 | 2026-09-08: receive P8 pure angular result through P7 mod provider and later actual main camera | Pre-camera scalar needs no fabricated frame vectors; existing P6 records and publication/restoration remain unchanged |
| D-P6-29 | 2026-09-08: all active instance instructions require N total/0…N−1 including N=1 | R25-2; retain fullscreen neutral-zero versus nested saved-parent restoration |
| D-P6-30 | 2026-09-08: expression union is resolved upstream; verification profiles are retired historical machinery | R25-N1/N2; preserve original claims/receipts while current RESEARCH and §0 govern |
| D-P6-31 | 2026-09-08: receive P2 `/4` envelope for unchanged timing/replay values, superseding prior dated `/3` wire references | P2 owns profile/comparison encoding; no P6 schema, factory, upload or verdict-law change |
| D-P6-32 | 2026-09-08 receive P1 D-P1-57/P4 D-P4-34 effective blend observation through the existing P7 bridge | Suppressed setter arguments are not state changes; lock-owned successful changes precede built-ins, with existing activity/replay/failure law |
| D-P6-33 | R27 C1: synchronize all active traces with P4's pre-participant lease acquisition and actual effective-state receipt | No predictive BlendSpec overlay; predecessor invalidation and failed-acquisition containment remain P4-owned |
| D-P6-34 | R27 N1: qualify historical LGPL input label against observed pinned GPLv3 root LICENSE | No copied implementation; applicable per-file licence and notices must be established before future reuse |
| D-P6-35 | Receive P3 D-P3-72 schema22/current-constant admission and materialization identity | No mapping parser or era selection; other P6 algorithms and historical evidence unchanged |
| D-P6-36 | Receive schema23/current-constant typed-selector identity and MaterializedSource-v23 | No new P6 parser, data provider or sampling algorithm; prior numeric receipts historical |
| D-P6-37 | Receive P8 D-P8-38 pure CelestialMath and P7 shadow-independent main-sky delivery | Same formula/current-frame main camera, no Ready-plan dependency; real-shadow before-activation timing and unchanged P6 event validation retained |
| D-P6-38 | Review32 C-1: synchronize the §5.1 `UniformReplayErrorSink`/`UniformReplayReport` row with D-P6-31's current `/4` envelope | §5.1 ledger text repair: "P2 flattens the values into current `/4` `gl_errors`" replaces the retired `/3` clause; no §4.11 semantic, schema, factory, upload or verdict-law change |

### 11.2 Contradictions and contract gaps found

1. **Pintonium half-life unit vs governing contract.** Its source says deciseconds
   (`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/transforms/SmoothedFloat.java:45`);
   RC3 says ticks. RC3/RESEARCH win; §4.5 records the conversion
   disposition.
2. **Pintonium sampler allocation vs App B.3.** Dynamic allocation is a standing rejection; §4.9
   adopts queue/cache mechanics only.
3. **Shipped documentation inconsistency.** `depthtex1` appears as 12 in one table and 11 in its
   uniform table. RESEARCH's explicit unit-11 ruling wins.
4. **GPU candidate vs current Phase 3 macro contract.** RC3's suggested object macro rewrites the
   declaration too. D-P6-1 follows RC3's default and §5.4 requests clarification.
5. **RESOLVED UPSTREAM — Phase 3 active-uniform publication.** Phase 3 §5 now publishes complete
   final `DeclaredUniformCatalog`s and closed structural types; Phase 4 consumes them without
   reopening source. Phase 3 round twenty verifies the grant.
6. **RESOLVED UPSTREAM — Phase 4 participant lookup/activity.** Phase 4 §5 now publishes the merged
   handle-free layout, callback-only bound lookup, exact cache key, and retainable operation-free
   activity token. Phase 4 round eighteen verifies the grant.
7. **Eye-brightness rounding is under-specified by the published pack document.** D-P6-4 fixes a
   deterministic, reference-supported rule and explicitly leaves T2 able to correct it through a
   Phase 6 fix-up if OptiFine evidence disagrees.
8. **RESOLVED UPSTREAM — historical custom-input discrepancy.** Earlier D.4 and §3.4/F.6
   lists differed; dated §0 records preserve that history. Current RESEARCH §3.4 and F.6
   explicitly exclude all five D.4 dynamics plus `fogMode`/`fogColor` and state that F.6
   does not narrow D.4. The existing seven-name schema/view contract is unchanged.
9. **CLOSED IN CURRENT BYTES, FRESH VERIFICATION OWED — Phase 11 bridge publication.** Phase 11
   identified the absent compile-time schema, boolean command, and normal per-program absence
   outcome (`docs/phase11/v1/PHASE_11_DOC.md:894`–`:910`). Sections 4.13 and 5.1 now publish the
   immutable versioned schema and closed types, schema/view conformance, `Bool1` with Phase-6-owned
   encoding, and ordered accepted/skipped/rejected outcomes and counts. Because this is a §5
   change, it is not a verified grant until the fresh loop returns literal PASS.
10. **ADOPTED IN CURRENT BYTES, FRESH VERIFICATION OWED — R7-10.** §§2.2/4.9/5 replace Phase 6's
    independent map with Phase 5's required pure resolver. Appendix B.3 numbers, conditional
    declaration rule, participant order and cache/error mechanics are preserved. Phase 4/5's
    coordinated owner contracts remain unverified, with Phase 3's provisional input gate intact;
    §5.2 records those blockers. R7-11 is independently adopted by §0.24, not verified by R7-10.
11. **ADOPTED IN CURRENT BYTES, FRESH VERIFICATION OWED — R7-11.** The old teardown-only
    `reset(CLOSE)` rule could not dispose a replaced/unpublished runtime. §§2.2/4.14/5 now remove
    CLOSE, distinguish runtime retirement from generation adoption, and preserve shutdown before
    atomic Phase 4 teardown while placing replacement retirement after old-barrier invalidation.
    All reasons require final use before retirement and borrowed-service disposal afterward.
    Phase 7 owns caller migration/service ordering; ungranted dependencies remain gated in §5.2.
No contradiction with RESEARCH.md's authority was silently resolved.

### 11.3 Items handed onward

**R39-2 handoff (2026-09-08):** P7 consumes `frameTiming(long,long)` and the exact
`UniformFrameTiming` record from §§2.2/4.6/5.1. P7 alone installs/restores the harness-only
time/tick control, supplies real accepted values, validates the current epoch and captures
the report before its frame view expires. P2 owns the current `/4` fields and comparability per
D-P6-31/D-P6-38 (the D-P2-39-era `/3` wording is historical); P6 makes no
claim that a manual G6 oracle has a controlled clock. Fresh owner/receiver reviews remain.

**R25-1 / P8 receiving handoff (2026-09-08, unverified):** P7 supplies the required final
`UniformReplayErrorSink replayErrors` argument after `DiagnosticReporter diagnostics`, copies
every report under §4.11 before return, classifies without positive attribution, and routes
ordinary escalation independently of capture/log suppression. P2 retains all entries in current
`/4` gl_errors with every error failing T0 (the D-P2-39-era `/3` envelope is retired history);
rejected/lost/overflowed capture cannot COMPLETE.
P7 consumes barrier delivery-failure degradation and immediate exceptions before draw success.
P5's §5.5 full factory request receives this exact arity only; P5 never owns the observer.
P7/P8 also consume §4.2's pure angular provider route and actual post-camera association.
Fresh P6 and affected receiver review is owed, including P7 R38; no self-PASS or implementation.

**To Phase 7:** implement frame sampling before resize/clear and distinct post-camera matrix
capture exactly once; supply frame/fog/blend/entityColor (v0.1), celestial (v0.2) and scoped events;
compose the three participants in Phase 4's fixed positions; add actual hook coordinates beside every
§4.12 audit row. Do not resample providers from a hook that merely switches programs.
Inject `FixedSamplerPolicies.resolver()` immediately after configuration in the §2.2 factory call,
paired with compilation's appB3 policy. R7-10 and R7-11 are adopted here but are not verified grants.
Migrate terminal reset callers to `retire(UNPUBLISHED_ABORT|REPLACEMENT|SHUTDOWN)` under §4.14;
never alias CLOSE, retire the newly adopted runtime as if it were the old one, or treat
`Rejected` as disposal. Keep providers/adapters and the replay observer alive until `Retired`/`AlreadyRetired`, even when
their texture/shadow owner retires earlier. Current sibling ledgers adopt these contracts;
fresh whole-document owner verification and §5.2's remaining authority gates still apply.

**To Phase 8/P7:** use the single P8 pure celestial math for main-sky delivery even without
shadow demand. Real shadow work still supplies all four shadow matrices and its celestial
sample before activation through the existing event interface. A singular inverse disables only that inverse.

**To Phase 9:** supply main/off-hand alias IDs/light values plus scoped entity/TE IDs at v0.3,
with exact nested restoration and outer 0. Share Phase 7's already-v0.1 color scope without
claiming color ownership or delaying hurt/flash tint until alias installation.

**To Phase 11:** obtain the immutable fixed schema before compilation; bind only exact-name
`Present(closed type)` inputs at its catalog version; require every runtime `Present` value to
conform to that same schema; and retain the conservative seven-name exclusion. Install one
`CustomUniformBridge`, submit closed typed commands in validated definition order, use `Bool1` for
GLSL `bool`, branch on `Accepted` / `SkippedAbsent` / `Rejected`, and report exact completed or
aborted-prefix counts for all three without reordering. Treat absent active layout/location as normal and let Phase 6 own
type/location validation, duplicate/name rejection, boolean 0/1 GL encoding, and upload replay. Do
not install another Phase 4 participant, resolve GL locations/types, or read per-draw dynamics.
Map P11-owned terminal CLOSE to `retire(UNPUBLISHED_ABORT|REPLACEMENT|SHUTDOWN)` on Phase 6
only after final custom/controller callback use under Phase 7 composition. P11 may retain its own
CLOSE semantics; it must not send CLOSE to P6. Retired/AlreadyRetired complete disposal; Rejected
keeps services and closed admission until a legal retry, preserving §4.14's reason-specific order.

**To Phases 13/7:** consume §4.12's authenticated bind adapter to the existing atlas sink;
Known/Unknown query data alone never proves current binding. Reset/non-atlas becomes `(0,0)`,
stale evidence does not mutate, and old evidence cannot cross reload/retirement.
Texture/normal/specular changes do not change fixed sampler integers.

**To Phase 14:** D-P6-1 leaves the PBO/fence async-center-depth ledger item live. Measure against the
one-read-per-frame synchronous baseline; synchronous fallback remains mandatory.

**To Phase 6 implementers:** consume the declaration/layout/access mechanics directly and the sole
Phase 5 resolver under §5.2's current owner-verification gates. Do not replace them with source
strings, a parallel declaration parser, a local sampler map, retained `BoundProgramUniformAccess`,
or `ProgramHandle`.

### 11.4 Requested upstream changes

- **RESOLVED UPSTREAM — RESEARCH expression exclusions (receipt 2026-09-08):** current
  §3.4/Appendix F.6 explicitly exclude all D.4 values plus fogMode/fogColor and do not narrow
  D.4. Historical discrepancy claims remain historical; no new authority edit or schema change.
- **PENDING, NON-BLOCKING — next DESIGN candidate:** qualify the rejected GPU center-depth
  Candidate B with §5.4's declaration-safe rewrite and fixed sampler-unit prerequisites. D-P6-1
  already selects the fully specified Candidate A, so no current implementation contract depends
  on this future wording.
- **HISTORICALLY GRANTED AND VERIFIED — Phase 3 and Phase 4 dependency contracts:** §5.3 records
  the original adopted surfaces and literal-PASS reviews; §5.2 records the current owner gates.
- **ADOPTED HERE, FRESH VERIFICATION OWED — Phase 7 R7-10 / Phase 5 §5.5 item 4:** the exact
  resolver injection and same-layout/context integer-upload contract now bind §§2.2/4.9/5. There
  is no remaining Phase 6 independent map. Owner review gates remain open; Phase 5/7 status rows
  require separate owner maintenance after this grant is verified, not edits in this session.
- **ADOPTED HERE, FRESH VERIFICATION OWED — Phase 7 R7-11:** §§2.2/4.14/5 publish permanent
  non-GL retirement, remove CLOSE rather than alias it, and bind candidate/replacement/shutdown
  ordering before borrowed services disappear. Phase 7 and Phase 4 request/consumption rows need
  separate owner synchronization; no sibling bytes or ungranted Phase 8/13 capability change here.
- **GRANTED IN CURRENT BYTES, FRESH VERIFICATION OWED — Phase 11 dependency closure:** §§4.13
  and 5.1 publish the immutable versioned fixed-input schema, closed exact-name types, runtime-view
  conformance, `Bool1` with Phase-6-owned GL encoding, and ordered accepted/skipped/rejected
  outcomes and counts. The §5 change cannot be consumed until a fresh literal PASS.
- **HISTORICAL, RETIRED — verification manifest:** `verification/targets/phase-6.json` was
  data-only and pinned to RC3 selectors/this v1 artifact for earlier review rounds. Profiles
  and their loop machinery were retired and removed on 2026-08-08; current governance comes
  from this document's §0, not a recreated profile or executable gate.

This maintenance session does not edit `docs/research/v1/RESEARCH.md`, any `DESIGN.md`, or
`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md`; their bytes and authority remain unchanged.

---

## 12. Implementation checklist

Ordered so each item has one outcome and one test hook.

| # | Work item | Tag | Test hook |
|---:|---|---:|---|
| 1 | satisfy §5.2's current Phase 3/4/5 owner-review gates and fresh Phase 6 whole-document PASS, then consume published declaration/layout/access/resolver contracts; coordinate R7-11 caller migration and current Phase 7 owner verification | v0.1 | literal PASS for current owner surfaces; compile-time API test; no adoption claim for ungranted §5.2 dependencies |
| 2 | create `engine.uniforms` packages and immutable primitive/vector/matrix records under C-1 | v0.1 | seam tests; mutation/finite validation tests |
| 3 | implement the catalog containing every §4.4 built-in row, with no sampler-name/unit table | v0.1 | `UniformCatalogCompletenessTest`; sampler behavior covered through the real shared resolver |
| 4 | adapt Phase 3 configuration without source reopening; inject Phase 5's pure resolver immediately after configuration and required P7-owned replay observer after diagnostics in exact eight-argument §2.2 factory; retain both borrowed services through retirement | v0.1 | fingerprint/schema/invariant and missing required-service failures without GL, callbacks or fallback |
| 5 | implement provider SPIs and scripted test providers | v0.1 | provider exception/range isolation tests |
| 6 | implement `UniformCell`, acquisition revisions, frame/tick/signal buckets | v0.1 | cadence table-driven tests |
| 7 | implement tick-domain scalar/vector EMA exactly as §4.5 | v0.1 | closed-form and quantization tests |
| 8 | implement frame IDs, counters, frame-time modulo, world-epoch reset | v0.1 | idempotence/wrap/reset tests |
| 9 | implement previous camera/matrix rotation at frame begin | v0.1 | camera-path temporal tests |
| 10 | implement immutable matrix copy/inversion and singular isolation | v0.1 | known-matrix/inverse/singular tests |
| 11 | implement conditional synchronous center-depth source and exact center coordinate | v0.1 | `ScriptedResponses.depthPixel`, ordering and undeclared tests |
| 12 | implement empty Phase 3 macro contributor per D-P6-1 | v0.1 | materialization test has no `centerDepthSmooth` define |
| 13 | implement non-terminal generation adoption and effective-provider/generation ProgramCache, plus §4.14 terminal retire algebra/guards; remove CLOSE callers, retire old/unpublished runtimes at the correct boundary and adopt on the new/surviving runtime | v0.1 | ResetLifecycleTest; UniformRetirementTest, including rejected publication not proving invalidation, final callback/service retention and shutdown-before-teardown |
| 14 | build/reuse sampler integer plans from the sole Phase 5 resolver and effective descriptor/context inside unchanged afterBind; retain deterministic order, locations, cache keys, tokens and error isolation | v0.1 | `SharedSamplerRepointTest`; real resolver/fallback/Invalid/alias/order/skip/generation cases; no physical texture binds |
| 15 | implement built-in upload plans, exact skip, and matrix-always rule | v0.1 | recorded GL redundant/matrix tests |
| 16 | implement immutable activation/dynamic attempted batches, P1 replay and §4.11 ordered report delivery before barrier/immediate return; preserve false cleanup evidence, no probe double-counting; contain observer failure through existing P7 paths | v0.1 | reproduced/unattributable/provider-once/token-invalidated and UniformReplayEvidenceBoundary cases |
| 17 | implement typed `UniformEventSink` with scoped reset helpers and no nullable listeners | v0.1 | notifier coverage and B6 regression tests |
| 18 | implement three Phase 4 participants and default custom bridge | v0.1 | barrier order/fixed-terminal tests |
| 19 | implement the immutable catalog-versioned `FixedExpressionInputSchema`, closed exact-name types, conforming `BuiltInExpressionView`, and custom sink with distinct `Bool1`, `Accepted` / `SkippedAbsent` / `Rejected`, three exact completed/aborted-prefix refresh counts, invalid-counter whole-batch discard with activation-scoped Phase 4 degradation, and stable definition-order batches; exclude every D.4 name plus `fogMode`/`fogColor`, skip active layout/location absence without GL or warning, reject invalid/type/duplicate commands, and keep boolean 0/1 encoding in Phase 6 | v0.1 interface | schema immutability/version/map tests; view conformance; bool encoding; disposition/count/mismatch/order/no-warning tests |
| 20 | implement `mod.glue` world/frame/center-depth providers with no MC type crossing C-1 | v0.1 | seam test + scripted integration |
| 21 | wire Phase 7 frame/fog/blend/entityColor/matrix producers against §4.12 | v0.1 | hook audit + recorded frame run |
| 22 | run recorded-profile gate and the §8.3 Phase 6 implementation gate | v0.1 | all headless tests green on two profiles |
| 23 | wire P8 pure celestial math through P7 main-sky delivery independently of shadow demand, plus real shadow producers | v0.2 | no-shadow celestial, shadow-scene and matrix tests |
| 24 | wire Phase 9 held/entity/block-entity producers | v0.3 | scoped-ID conformance tests |
| 25 | install Phase 11 custom bridge against the verified fixed schema/view/typed-command contract | v0.4 | load-time schema before first activation; built-in-before-custom; mixed accepted/skipped/rejected counts/order; rung-1/rung-2 tests |
| 26 | wire Phase 13 atlas-size producer | v0.5 | atlas reload/bind tests |
| 27 | wire Phase 7 composite/prepared instance loop to N total copies, IDs0…N−1; N=1 is one draw; restore outer fullscreen neutral0 or genuine nested saved parent on every exit | v0.5 | recorded multi-draw and nested failure/restoration sequence |
| 28 | run T1 camera-path/weather/water scenes and T2 classic uniform/sampler comparisons | v0.1 onward | Phase 2 manifests/diffs; no committed images |
| 29 | measure synchronous center-depth stall and hand baseline to Phase 14 | v0.5 | profiler ledger with declared/undeclared comparison |

---

*End of PHASE_6_DOC.md. The original §G1.1 build session stopped after this architecture
document; §§0.3–0.24 record the later governed review, fix-up, and dependency-adoption maintenance.
Implementation and any post-loop version roll remain separate work.*
