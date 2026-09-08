# Phase 4 — Whole-owner Architecture Review R36

## Frozen artifact and disposition

- **Attempt:** 6.
- **Round:** R36.
- **Owner:** `docs/phase4/v1/PHASE_4_DOC.md`.
- **Frozen SHA256:** `c66ffe7c798326e8fb60db62b9060127c3d7df9e2d2f6981e9741f137dfb76c2` — the identity supplied by the assignment and `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_6.json`.
- **Required corrections:** 1.
- **Notes:** 1.
- **§5 impact:** **Yes**: the required correction changes the published compiler request and its receiving contract.
- **Verdict:** **PASS-WITH-CORRECTIONS**.

This was a fresh read-only architecture review of the entire current Phase 4 owner, §§0–12, not a confirmation of earlier resolutions. No repository bytes were edited. No validation commands, builds, tests, formatters, linters, implementation, or native execution were run; the SHA above is the frozen inventory identity, not a newly claimed checksum-command result.

## Scope and authority

The governing revision is **`docs/design/v2.0-RC3/DESIGN.md`**, selected by Phase 4’s own header at lines 11–13. I read its global Part I and complete Phase 4 assignment at lines 1471–1570. I did not substitute v3 because a receiver adopts it or because a historical review used an override. `AGENTS.md` and `docs/MOVES.md` supplied the repository restrictions and moved-citation rules.

The governing research checks covered `docs/research/v1/RESEARCH.md` §§0–1, program/source/stage contracts, modern array shape, lifecycle/compiler/barrier behavior, §7.3, all of Appendix A, and the fixed-unit/shared-texture and per-program-state provisions in Appendices B and F. In particular, Appendix A.1–A.2 at lines 1107–1153 remains controlling for classic rows and entire-provider fallback. Modern virtual preludes remain required by lines 337–355. The dated conditional geometry decision in `docs/decisions/GEOMETRY_PRIMITIVE_COMPATIBILITY.md` is a narrow authorization, not a general renderer rewrite or parity result.

I read the complete current §5 publication regions of Phase 1 and Phase 3, their load-bearing incorporated compiler/native-source contracts, and the applicable receiving dispatch and lifecycle surfaces in Phases 2, 5, 6, 7, 8, 10, 12 and 14. Those reads establish interface compatibility or the correction below; they do **not** award sibling verdicts. Historical PASS receipts, old schema numbers and superseded `/3` receipts were not treated as current acceptance gates.

## Independent checks

1. **Registry shape and required configurations.** The nine identities, repeated gbuffers occurrence, classic catalog, deferred/composite singleton occurrences, sparse index domain, dormant families and compute exclusion were checked against RC3 and Research. The revised `SparseArray` at `docs/phase4/v1/PHASE_4_DOC.md:476-490,530-555` can contain its optional named virtual prelude without converting it to index zero or adding a stage occurrence. The construction and lookup rules at lines 1032–1075 close membership, legal omission, invalid lookup kinds, greatest populated index and descriptor identity. The G6/full-shape tables at lines 1087–1123 are representable under that model.
2. **Prelude delivery, including the receiver dispatch.** Phase 4’s current §5 at lines 2071–2072 incorporates pre-first traversal and unchanged contained-descriptor delivery. Phase 5 receives the same candidate-registry population at `docs/phase5/v1/PHASE_5_DOC.md:2681-2687`; its actual transition branch at lines 1576–1589 performs only explicit true flips, consumes once per frame and never resolves or draws a program. Phase 7’s traversal branch at `docs/phase7/v1/PHASE_7_DOC.md:1208-1222` explicitly makes prelude/raster handling two parts of one traversal; its frame-end branch at lines 1351–1360 does the same for composite. The owner’s pre/0/5/99, pre-only, omitted-pre and invalid-member acceptance cases at `docs/phase4/v1/PHASE_4_DOC.md:2423-2442` remain planned evidence, not executed tests. No current sparse-prelude correction is required.
3. **Classic catalog and permitted source corroboration.** The shipped author table at `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:61-108` independently supplies the named classic rows, backup edges and virtual slots; its vertex/geometry declarations at lines 342–356 support the relevant source-facing inputs. The limited geometry digest at `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:150-153` supports only the observed native API topology, not copied implementation structure. Permitted available Pintonium sources independently corroborate recursive memoization, the relevant explicit fallback edges, bind/refresh ordering, the incompatible reference attribute numbering, per-pass field bundles and generation-consumer invalidation. The historical pin limitation is recorded separately below.
4. **Compiler, geometry and sampler initialization.** Phase 4’s pre-GL declaration merge/policy checks, candidate-local fallback and complete cleanup were compared with P3’s source publication and P1’s facade. The native request/classification at `docs/phase3/v1/PHASE_3_DOC.md:2417-2578`, linked-input acquisition at `docs/phase1/v14/PHASE_1_DOC.md:3806-3880`, and P4’s comparison at `docs/phase4/v1/PHASE_4_DOC.md:1450-1513` agree on preservation, API/source distinction and failure before drawable publication. P5’s actual `initializationAssignments` implementation contract at `docs/phase5/v1/PHASE_5_DOC.md:2343-2358` matches P4’s complete pre-GL frozen plan; P1’s transaction at `docs/phase1/v14/PHASE_1_DOC.md:3406-3426` restores the prior selection before validation and distinguishes restored local failure from poisoned admission. No runtime participant is improperly used to initialize an unpublished candidate.
5. **Activation and lifecycle.** Select-once credentials, forced shadow before fallback, matching physical-bind/activation context, effective-provider state, duration-lock ordering, callback-scoped lookup, retainable activity-token expiry and publication ownership were traced through the current P5/P6/P7 receivers. In particular, P6’s dispatch at `docs/phase6/v1/PHASE_6_DOC.md:1225-1252` consumes the effective sampler layout and issued context rather than resolving the requested child again. Accepted, rejected and recovered-off publications remain distinct; detached evidence does not become teardown or draw authority.
6. **Ordinary fixed terminal.** D-P4-42 at `docs/phase4/v1/PHASE_4_DOC.md:2137-2142` receives the complete object-baseline law. This is backed by P1’s normalization at `docs/phase1/v14/PHASE_1_DOC.md:3428-3438` and baseline grant at line 5269, and P14’s complete latest parameter establishment and ordinary FINAL transition at `docs/phase14/v1/PHASE_14_DOC.md:626-645,723-735`. P7 receives the unchanged P5-bind → P4-activate → draw sequence at `docs/phase7/v1/PHASE_7_DOC.md:3430-3434`. Clearing samplers therefore does not require a demotion, row rebinding or newly invented callback. This is architectural agreement, not native sampler-equivalence proof.
7. **Schema and evidence receivers.** D-P4-41 at `docs/phase4/v1/PHASE_4_DOC.md:2148-2155` agrees with P3’s current schema23 gate and `MaterializedSource-v23` identity at `docs/phase3/v1/PHASE_3_DOC.md:4154-4210`. Containing/nested/inspection equality precedes derivation; old numbers remain historical. P4’s own-build classification and detached same-request enrichment match the required P2/P7 `/4` field grammar (`docs/phase2/v2/PHASE_2_DOC.md:2429,2462-2465,2721-2725`; `docs/phase7/v1/PHASE_7_DOC.md:3586-3587`). The missing selected-profile ingress below prevents one important intended classification from being supplied correctly.
8. **Scope and future gates.** §§6–12 retain the degradation ladder, render-thread ownership, future test/fixture obligations and milestone separation. The optional P14 split remains a request at `docs/phase4/v1/PHASE_4_DOC.md:2308-2328`, not a callable `prepare` API or worker grant. No runtime evidence was demanded merely to pass an architecture gate.

## Required correction

### C36-1 — Carry explicit profile selection into the compiler request

**Severity:** correction; functional cross-phase contract omission. **Owner:** Phase 4. **§5 impact:** yes.

**Current owner evidence:** `docs/phase4/v1/PHASE_4_DOC.md:778-785` publishes the exhaustive `RegistryBuildRequest(configuration, dimension, macroContribution, samplerPolicy, capabilities, device, diagnostics)`, repeated as exact binding content at line 2074. It has neither a selected-profile input nor an evaluated-program-state input. Nevertheless, planning requires “Phase 3’s already-evaluated enabled/profile-disabled state” at line 1320, and lines 1221–1222 require an intentionally disabled present source to become `ownBuild=DISABLED` without materialization/build.

**Producer/receiver evidence:** `docs/phase3/v1/PHASE_3_DOC.md:851-870` shows that `PackConfiguration` contains immutable configuration data, not a retained selected profile or evaluated result. Its `evaluateProgramStates(Optional<ProfileName>, DiagnosticReporter)` returns a result; it does not mutate that configuration. The binding contract at lines 3675–3686 incorporates that operation, and lines 2965–2980 explicitly say an absent profile means **no disables**. Meanwhile `docs/phase12/v1/PHASE_12_DOC.md:1292` publishes independent committed/pending profile selection, and `docs/phase7/v1/PHASE_7_DOC.md:3191-3197,3334-3341` now preserves and evaluates accepted explicit profile intent even when option values do not change. Phase 7’s actual compiler call remains the Phase 4 request at lines 879–883; there is no route for that evaluation result or selection to enter it.

**Observable failure:** Consider two valid profiles with identical option constraints but different `disabledPrograms`, with a present `gbuffers_water` source and a valid terrain ancestor. Selecting the disabling profile must suppress the water build and yield `DISABLED` plus effective `CHAIN`; selecting the other must permit the own build. Both selections can present the same immutable configuration and all the same fields of the published P4 request. Evaluating the profile in P7 cannot change those fields. P4 therefore cannot implement both outcomes through its stated interface. Defaulting to absent profile builds a deliberately disabled program; inferring a profile from equal option values also loses the independently accepted choice. This affects rendering, fallback and the owner-issued evidence consumed by P2/P7, not merely UI presentation.

**Minimal owner fix:** Add an explicit non-null `Optional<ProfileName>` input to P4’s compiler request and binding §5, and have P4 obtain the P3 evaluation from that exact configuration and selection before availability/build planning. Alternatively, an explicitly associated same-configuration evaluated-state input could serve the seam, but an unspecified side channel cannot. Preserve the resulting availability in registry identity/evidence and define the closed evaluation-failure handling. Coordinate P7’s forwarding of the accepted frozen profile; P2’s current inspection path must supply the same absent-profile choice used by its P3 inspection snapshot unless that inspection contract is separately extended. Add a planned acceptance case with equal option values and different profile disable sets, checking build suppression, entire-provider fallback and exact ownBuild outcomes. Do not add preview OptionState to materialization or mutate/relabel P3 configuration.

This is a local owner-interface correction with reciprocal receiving updates, not grounds to rebuild the registry architecture or issue a sibling verdict.

## Note

### N36-1 — Historical pinned Pintonium evidence remains unavailable; narrower current corroboration exists

The pinned path `reference-src/pintonium-9c2fcc1/.../ProgramFallbackResolver.java` is absent in the current checkout, and `docs/MOVES.md` does not establish that `reference-src/Pintonium-main` is that historical revision. I therefore do not claim to have reverified the historical 9c2fcc1 bytes or their exact line coordinates.

Independent permitted corroboration is available in `reference-src/Pintonium-main/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/programs/ProgramFallbackResolver.java:27-44`, `shaderpack/loading/ProgramId.java:11-46`, `gl/program/Program.java:28-35`, `gl/shader/ProgramCreator.java:18-28`, `pipeline/CompositeRenderer.java:156-163,431-443`, `pipeline/PipelineManager.java:78-90`, and the consuming `reference-src/Pintonium-main/forge122/src/shaders/java/net/irisshaders/iris/compat/sodium/impl/shader_overrides/IrisChunkProgramOverrides.java:135-139`. These support the bounded mechanisms described above; they are not silently relabeled pinned evidence. The shipped author table and governing Research remain available and authoritative for contract values. This limitation requires no current Phase 4 contract correction and does not justify reopening historical receipts.

## Final disposition and limitations

**PASS-WITH-CORRECTIONS — 1 required correction, 1 note; §5 impact: yes.** D-P4-40’s sparse-prelude model and reciprocal dispatch, D-P4-41’s schema23 receipt and D-P4-42’s ordinary fixed-terminal baseline receipt are internally and reciprocally accounted for at this architecture gate. The complete owner nevertheless needs C36-1 because selected-profile disablement cannot cross its current compiler interface.

Owner bytes remain unchanged. No forbidden chatlogs, root transcripts, Oculus blocked paths, glsl-transformer implementation or decompile implementation mining was used. No historical review was used as proof of correctness. Correcting and freshly reviewing the amended owner/receivers remains separate from final integration, implementation clearance, runtime conformance and any pack-tier or parity certification.

## Resolutions

### C36-1 — Addressed by D-P4-43 (2026-09-08; architecture-only, unverified)

The owner now adds non-null `Optional<ProfileName> profileSelection` immediately after
`PackConfiguration configuration` in §2.2's exhaustive request and §5.1's exact construction
contract. §4.7 evaluates precisely that immutable configuration/selection pair once before
availability, materialization, compilation or GL allocation. It exhaustively consumes P3's
`Evaluated`/`InvalidState` result, preserving absent/unknown-profile and property-false semantics;
§4.12 reduces InvalidState to registry-wide `INVALID_PROGRAM_STATE`, with no fabricated rows.
No configuration mutation, inferred selection, preview OptionState or external evaluation
side channel is admitted.

§4.11 advances only P4's registry domain to `RegistryFingerprint/profile-selection-v3`,
committing exact selection and ordered evaluated state alongside existing availability evidence.
§5.6 requires P2's exact Inspected configuration with explicit empty selection, matching its
unchanged P3 inspection snapshot; §5.1 requires P7 to forward accepted frozen selection even
when options do not change. P3 schema23, nine trees and existing source-free row grammar remain
unchanged. Main owns the reciprocal P2/P7 edits.

§8.1's planned profileSelection cases distinguish Keep/NoWater/NoWaterOrTerrain with identical
option constraints, disabled backup ancestors, full provider provenance, absent/unknown/invalid
inputs and same-request inspection association. §12 item 5 incorporates these obligations.
Virtual preludes and historical decisions/confidence remain intact; D-P4-43 and §0.41 record
the correction. N36-1 remains a historical pinned-source limitation, not a new evidence claim.

No validation commands, implementation, tests, formatters, linters or checksum/structure checks
were run. This resolution records owner edits, not a replacement verdict or fresh PASS;
consolidated integration checks and fresh owner/receiver review remain separate.
