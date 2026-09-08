# Phase 4 — Fresh Whole-Document Architecture Review R35

## Frozen artifact and disposition

- **Owner:** `docs/phase4/v1/PHASE_4_DOC.md`
- **Frozen SHA256:** `9a8d779f830dd77dcfd3c94a51278be746a4841f0585507976b7842cea504d69`, as supplied by the assignment and recorded for Phase 4/R35 in `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_5.json:25-29`.
- **Review scope:** the complete current owner, sections 0–12, including historical addenda and their explicit superseding decisions. This was not limited to D-P4-36–39.
- **Required corrections:** 1. **Notes:** 1. **Section 5 impact:** yes.
- No repository write, implementation, build, test, formatter, linter, or runtime validation was performed. The owner was not modified by this review. The checksum above identifies the assigned freeze; this review does not claim an independently executed checksum command.

## Authority and independent evidence

The owner selects **RC3**, not the newest design, at `docs/phase4/v1/PHASE_4_DOC.md:11-13`. I read `docs/design/v2.0-RC3/DESIGN.md` Part I G0–G12 and the complete Phase 4 assignment at lines 1471–1570. Research remains superior authority: the reviewed requirements include `docs/research/v1/RESEARCH.md` §§0–1, 3.1–3.2, 3.6.1 and the compute exclusion, 4.1–4.2, 6.2, 7.3, Appendix A in full, and the load-bearing B.3/F.5/F.7 contracts. `AGENTS.md` and `docs/MOVES.md` were read before reference mining.

I read the complete binding dependency sections in `docs/phase1/v14/PHASE_1_DOC.md:5082-5207` and `docs/phase3/v1/PHASE_3_DOC.md:3231-4238`, including the incorporated source/type/native-geometry and facade contracts needed by this owner. Reciprocal reads covered the concrete P5 sampler-policy and virtual-transition receivers, P6 participant dispatch, P7 main/nested/virtual/publication/evidence consumers, P8 shadow activation dispatch, P10 geometry handoff, P2 own-build serialization, and P14's ungranted compiler proposal. These are seam checks, not sibling certifications. No prior review was used as proof that a current contract was correct.

The shipped author table was checked directly at `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:61-110,340-357`. The permitted digest at `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:150-152` was used solely for its recorded triangle-input/triangle-strip-output behavior, not decompile structure or identifiers. The primitive adapter's authority is the explicit limited approval in `docs/decisions/GEOMETRY_PRIMITIVE_COMPATIBILITY.md`, not inferred reference parity.

## Independent whole-owner checks

1. **Catalog, fallback and ownership:** the classic names and explicit fallback edges match Research Appendix A and the author table; the 60 named shader/virtual slots exclude the external sentinel. Missing/disabled/failed providers resolve to one complete ancestor binding rather than overlaying child state (`docs/phase4/v1/PHASE_4_DOC.md:1120-1264`). The own-build matrix distinguishes requested-slot failure from effective CHAIN success. P2 consumes this distinction at `docs/phase2/v2/PHASE_2_DOC.md:2409-2446`; P7 copies it at `docs/phase7/v1/PHASE_7_DOC.md:3518-3521,3552-3561`. Current `/4` receipts supersede historical `/3` wording.
2. **D-P4-36, sole integer policy and candidate validation:** `FixedSamplerLayoutPolicy.initializationAssignments` is an actual declared method (`docs/phase4/v1/PHASE_4_DOC.md:625-631`), incorporated through §5's exact-method contract. P4 freezes and validates its complete output before registry GL allocation (`:1331-1342`); P5 implements it using the sole resolver rather than another table (`docs/phase5/v1/PHASE_5_DOC.md:2277-2292`, incorporated by `:2536`). P1's exact transaction is present at `docs/phase1/v14/PHASE_1_DOC.md:3395-3415` and granted at `:5145`. P4 calls it after link/input agreement and before validate, permits local fallback only after proven restoration, and aborts the registry on unproven restoration (`docs/phase4/v1/PHASE_4_DOC.md:1374-1400`). No unpublished candidate is routed through a runtime barrier or P6 participant.
3. **External validation reason:** the [Khronos glValidateProgram reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glValidateProgram.xhtml), independently read during this review, states that validation depends on current GL state and identifies differently typed active samplers referring to one unit as invalid. The candidate-initialization contract therefore addresses a real validation prerequisite. The specified active sampler2D/1D case remains future real-driver evidence, not an architecture-gate demand to execute it now.
4. **D-P4-37 and duration locks:** P4's acquire/close/notification order and poison handling (`docs/phase4/v1/PHASE_4_DOC.md:1699-1708,1786-1803,2281-2283`) agree with P1's real interception/private-bypass transaction (`docs/phase1/v14/PHASE_1_DOC.md:3449-3486`). P7 receives the ordering and result-checks suspension/restoration at `docs/phase7/v1/PHASE_7_DOC.md:1113-1174,3538-3550`; P6 observes actual effective state rather than suppressed setter arguments at `docs/phase6/v1/PHASE_6_DOC.md:1469-1476`. Optional debug failure is no longer allowed to absorb required-lock failure.
5. **Geometry/source interface:** the current P3 None/PreserveNative request and None/CoreLayout/NativeLegacy result algebra, native API/source-layout precedence, exact same-load language/maps/catalogs, and whole-program failure are consumed without a resurrected translator. P4 compares P1's cached linked input before publication (`docs/phase4/v1/PHASE_4_DOC.md:1408-1467`); P1 supplies that inspection at `docs/phase1/v14/PHASE_1_DOC.md:3795-3868`. P7/P10 receive only effective authenticated geometry metadata, while P8 reuses the supplied root-shadow selection and branches on every activation result (`docs/phase8/v1/PHASE_8_DOC.md:824-827,883-895`; `docs/phase10/v1/PHASE_10_DOC.md:1526-1545,1601`). No metadata-only view gains draw authority.
6. **Schema and async:** D-P4-38 explicitly receives schema22/MaterializedSource-v22 with equal containing/nested/inspection admission (`docs/phase4/v1/PHASE_4_DOC.md:2088-2099`), matching P3's current contract at `docs/phase3/v1/PHASE_3_DOC.md:4047-4107`. Older numeric receipts are superseded historical evidence, not new findings. D-P4-39 preserves the pending status of the compiler split and receives fence→flush→publication, retained ownership, nonblocking admission and acknowledged disposal/quarantine (`docs/phase4/v1/PHASE_4_DOC.md:2251-2271`), matching `docs/phase14/v1/PHASE_14_DOC.md:1738-1777`. A successful OQ-15 spike alone still grants no API.
7. **Remaining sections:** diagnostics, candidate cleanup, detached-view lifetime, opaque composition/publication ownership, generation inequality, stale-selection rejection, source-free enrichment, failure taxonomy, thread ownership, staged test plan, milestone/OQ boundaries and implementation checklist were reviewed together. No additional required correction was established in those contracts. The schedule's virtual/sparse representation does have the concrete defect below.

## Required correction

### C35-1 — Represent virtual preludes in the published sparse population contract

**Severity:** correction, P2. **Owner:** Phase 4. **Section 5 impact:** yes. **Confidence:** high.

**Current evidence:** `docs/phase4/v1/PHASE_4_DOC.md:476-485` gives each `StageStep` exactly one closed `PassPopulation`: Singleton, NamedPrograms, or SparseArray. The traversal/lookup law at `:1026-1033` permits `named` and `indexed` only for their matching population kind and rejects descriptors outside their population. Nevertheless, the required single deferred/composite steps contain a named virtual prelude followed by sparse indexed raster passes (`:1049-1051`, and the full configuration at `:1067-1070`). Splitting the prelude into a second occurrence is not an existing escape hatch: `:1015-1016` currently permits duplicated stage occurrence only for GBUFFERS. These declarations and kind-correct lookup semantics are binding in §5 at `:2023-2024`.

**Trigger and observable breakage:** instantiate the required DEFERRED step with `deferred_pre`, index 0 and index 5. SparseArray has no published named-prelude membership/access rule; its `named(step,deferred_pre)` is the wrong lookup kind. NamedPrograms instead gives no legal indexed sparse lookup. Therefore the required configuration cannot satisfy the owner's own closed population/construction rules while exposing both descriptors through the promised traversal. Omitting or inventing the prelude downstream loses or bypasses the explicit flip transition. The receiving side is load-bearing: P7 invokes the exact pre descriptor at `docs/phase7/v1/PHASE_7_DOC.md:1208-1210,1335-1337`; P5 accepts only the planned exact virtual descriptor, rejects altered/raster substitutes and applies its explicit flips at `docs/phase5/v1/PHASE_5_DOC.md:1511-1521,2527`. Neither receiver supplies an alternative sanctioned population model.

**Minimal owner fix:** publish one precise combined population rule—e.g. an optional named virtual-prelude component of SparseArray, with `named` accepting only that prelude, `indexed` addressing raster indices, and `passes` returning the prelude first followed by populated indices in ascending order. Define the prelude's empty index/no-program invariants and illegal combinations, and update §§2.2/4.1/4.2/5.1 consistently. An equally explicit owner-defined prelude exception can avoid a new record component, but it must close construction, traversal and both lookup semantics rather than leave receivers to invent them. Add the architecture acceptance case for prelude plus indices 0/5/99 and unchanged descriptor delivery to P5. This is a local contract repair, not a subsystem rebuild.

## Note

### N35-1 — Historical pinned source evidence is unavailable; present corroboration is narrower

**Severity:** note. **No required owner correction.** `docs/phase4/v1/PHASE_4_DOC.md:32-62` cites `reference-src/pintonium-9c2fcc1/...`; that pinned tree is absent, including a direct failed read of its ProgramFallbackResolver path. `docs/MOVES.md` resolves the historical spelling but does not establish that the available `reference-src/Pintonium-main` is byte-identical to commit 9c2fcc1.

Permitted current corroboration was read independently: `reference-src/Pintonium-main/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/programs/ProgramFallbackResolver.java:28-44`, `shaderpack/loading/ProgramId.java:11-46`, `gl/program/Program.java:28-34`, `gl/shader/ProgramCreator.java:21-25`, `pipeline/CompositeRenderer.java:156-163,431-442`, `pipeline/PipelineManager.java:84-94`, and `reference-src/Pintonium-main/forge122/src/shaders/java/net/irisshaders/iris/compat/sodium/impl/shader_overrides/IrisChunkProgramOverrides.java:136-140`. These corroborate recursive fallback, bind/refresh shape, rejected attribute numbering, pass metadata and generation invalidation. They do not re-verify the historical pinned bytes or supply runtime compatibility. The numbered correction above rests on current owner/receiver contracts, not on missing historical evidence.

## Final verdict and disposition

**PASS-WITH-CORRECTIONS**

The frozen Phase 4 document has one locally repairable current interface defect: its closed population model cannot express the virtual-prelude-plus-sparse configurations that it promises and its receivers consume. D-P4-36–39 are otherwise substantively integrated at the checked producer/receiver seams. Because C35-1 changes binding §5 semantics, apply it through the owner and obtain a fresh whole-document review before verified downstream consumption. No sibling approval, implementation clearance, runtime/pack-tier certification, or final integration clearance is granted.

## Resolutions

### C35-1 — Owner contract amended, fresh verification pending (2026-09-08)

`docs/phase4/v1/PHASE_4_DOC.md` §0.40/D-P4-40 extends the existing SparseArray with
`Optional<ProgramSlotId> virtualPrelude`. §§2.2/4.1/4.2/5.1 now require exact
deferred_pre/composite_pre membership within one respective stage occurrence; pre has empty
index/source/fallback/compute/read/write/mipmap data, exact explicit flips and no program.
Named lookup addresses only that stage's legal prelude; indexed lookup addresses sparse members.
Traversal returns pre first, then populated indices ascending. Illegal combinations reject;
neither P5 nor P7 may synthesize omitted or altered descriptors.

§8.1 plans the same-descriptor pre/0/5/99 trace through P7 to P5, holes, pre-only/empty and
illegal-member boundaries. §§11.4.2/12 record receiver obligations and acceptance staging.
D-P4-41 also receives the coordinated schema23/MaterializedSource-v23 cutover with exact-current
containing/nested/inspection equality; nine metadata-only trees/projectionVersion1 are unchanged.

The original review body, freeze, verdict and N35-1 note remain historical and unaltered.
No historical pinned source was re-verified. Documentation changes are owner-designed/unverified;
P5/P7 receipts and fresh whole-document owner/receiver verification remain required. No validation
commands, builds, tests, formatting, runtime checks, fresh PASS or implementation/integration
clearance are claimed by this resolution.
