# Phase 4 Review 32 — Independent whole-document verification

## 1. Reviewed artifact and review boundary

- **Target:** `docs/phase4/v1/PHASE_4_DOC.md`, complete document, including §0.39 and §§0–12.
- **Assigned review destination:** `docs/phase4/reviews/PHASE_4_REVIEW_32.md`.
- **Supplied frozen SHA-256:** `d8f1ea29f84ca1ec8a1a389a58c29d27901ab6eaa04c4984c903b4ff9d869c5b`. This is the assignment's identity, not an independently computed checksum.
- **Governing design:** `docs/design/v2.0-RC3/DESIGN.md`, as expressly selected by the target header. No global substitution of a later design revision was made.
- **Method:** independent §G1.2 architecture review, not a patch-only review or implementation session. No files were modified; no builds, tests, formatters, runtime validation, or checksum commands were run. Prior review outcomes and integration receipts were treated as historical claims, not proof.

## 2. Authority and read set

### 2.1 Governing and assigned inputs

Read `AGENTS.md` and `docs/MOVES.md`; the selected design's complete Part I and Phase 4 specification at lines 1471–1570; `docs/research/v1/RESEARCH.md` §§0–1, assigned §3.1, §3.6.1, §4.1 steps 4–5, §4.2, §7.3, and complete Appendix A. Supplementary research reads covered the geometry and compatibility constraints in §§3.2/6.1–6.2 and the sampler/per-program state contracts in Appendices B.3 and F.5–F.7 because the target expressly implements those boundaries.

Read the declared dependencies' current §5 contracts and their incorporated semantics:

- `docs/phase1/v14/PHASE_1_DOC.md`: module placement and seam, capability/handle ownership, shader/state/framebuffer facade, native geometry configuration, actual linked primitive requirements, fullscreen submission, recording/error semantics, and §5.
- `docs/phase3/v1/PHASE_3_DOC.md`: configuration and source/materialization declarations, native geometry preservation, resource and per-program state algebras, evaluation/dimension rules, complete canonical fingerprint semantics, source-free inspection/enrichment, and §5.

Read `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` §§3 and 13, and the shipped author-facing program table in `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt`. The permitted behavioral digest was used only for the documented legacy topology, not as source code to inherit.

### 2.2 Integration and receiving-side reads

Read `docs/PHASE_INTEGRATION_REVIEW.md` in full, including original findings, Resolutions, subsequent owner/receiver follow-ons, and final gate language. Read the recorded rulings in `GEOMETRY_PRIMITIVE_COMPATIBILITY.md`, `U1_TEXTURE_SAMPLING.md`, and `TEXTURE_SIDECAR_DEFAULTS.md` under `docs/decisions/`.

Receiving-side checks extended to `docs/phase5/v1/PHASE_5_DOC.md` route realization, `docs/phase7/v1/PHASE_7_DOC.md` build/publication and scope activation paths, hook catalog and §5 receiving contracts, and `docs/phase2/v2/PHASE_2_DOC.md` requested-slot disposition/T3 and manifest predicates. These additional reads were necessary to establish whether the target's values and promised effects actually have receiving mechanisms; they do not certify those entire dependencies.

### 2.3 Reference provenance

Licensing/exclusion rules were read before reference mining. The historical local `pintonium-9c2fcc1` tree was unavailable, so load-bearing reference claims were independently checked at the named upstream commit rather than treating the current checkout as that pin:

- [ProgramFallbackResolver.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/programs/ProgramFallbackResolver.java) and [ProgramId.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/loading/ProgramId.java): recursive cached fallback and explicit parent edges.
- [Program.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/program/Program.java) and [ProgramCreator.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/shader/ProgramCreator.java): bind/refresh shape and the deliberately rejected attribute numbering.
- [WorldRenderingPhase.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/WorldRenderingPhase.java), [CommonIrisRenderingPipeline.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/CommonIrisRenderingPipeline.java), and [CompositeRenderer.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/CompositeRenderer.java): phase override/deferred removal and per-pass inventory.
- [PipelineManager.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/PipelineManager.java) and [IrisChunkProgramOverrides.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/compat/sodium/impl/shader_overrides/IrisChunkProgramOverrides.java): generation change and consuming-side inequality invalidation.
- The pinned README and source license header were also inspected for the LGPL provenance assertion.

No transcripts, agent histories, forbidden Oculus pipeline/transformation sources, `libs`, or `glsl-relocated` sources were consulted. No implementation source was copied.

## 3. Literal document-gate audit

| Required criterion | Independent assessment |
|---|---|
| Both G6 and modern-superset configurations without structural change | Satisfied architecturally. Stage identity is distinct from schedule occurrence; classic and modern configurations use the same sparse representation, with legal indices through 99 and dormant compute/future-family slots. |
| Every Appendix A.1 row mapped | Satisfied at the catalog level. Named raster slots, virtual pre-transitions, fixed terminals and indexed families are explicitly accounted for rather than inferred from a fixed allocation count. |
| Barrier fully specified as an interface | Selection, activation, participants, credentials and result branches are substantially specified. The alpha/blend duration-lock promise lacks a receiving enforcement mechanism: R32-2. |
| Backup table cross-validated against PD §3.1 | Satisfied. The target retains the classic contract's complete-provider inheritance, root-shadow termination and disabled-as-absent semantics while rejecting reference-only extra slots and numbering. |
| Version-counter invalidation exposed in §5 | Satisfied. Accepted ready/off transitions and forced recovery change generation; pre-release rejection does not. Consumers compare inequality, and detached metadata is not live activation authority. |

Catalog row coverage does not establish semantic fidelity for every state value. The positional routing loss in R32-1 is a concrete conformance/interface defect despite the presence of a DRAWBUFFERS conformance-map row.

## 4. Substantive adversarial audit

### 4.1 Representation, conformance and scope

The schedule separates the two gbuffers occurrences from their common stage identity. Sparse family traversal does not terminate at the first absent index. Virtual `deferred_pre` and `composite_pre` descriptors remain flip-control operations and cannot become shader selections. Fixed-function terminals remain distinct from absent indexed passes and the final passthrough operation.

Fallback is an entire effective binding, not merely a program handle: state, declarations, sampler layout, source identity, instance count and geometry metadata travel together. No requested-child state overlay is authorized in Phase 5 or Phase 7. The classic attribute contract remains 10/11/12; the reference's 11/12/13/14 layout is explicitly rejected. FBO realization, uniforms, traversal, shadow camera, and custom-texture ownership remain with their assigned owners.

### 4.2 Compile and geometry transactions

The build path consumes Phase 3's finalized, fingerprint-bound materialization and declaration catalog rather than reparsing source. It validates sampler policy output and structural conflicts before provider GL allocation. Candidate-local failures clean up owned objects and participate in normal fallback rather than silently dropping a source stage.

The present native geometry design is grounded in the coordinated Phase 3/P1 grants: retained native source, pre-link ARB configuration with immediate error handling, actual linked input classification, and agreement before readiness. Core layout sources remain a separate accepted path. The P1 fullscreen operation consumes its cached linked primitive requirement; geometry input other than the permitted fullscreen cases rejects instead of issuing an incompatible draw. The separately approved native-quad conversion and adjacent prepared-submission repetition are not inferred from the old reference or mislabeled as whole-traversal replay.

These are architecture contracts. Their recorded-GL, compiler, adapter and real-context evidence remains independently required.

### 4.3 Selection, lifetime and publication

The selected provider is resolved once after shadow forcing. Phase 5 receives the opaque selection for physical binding, and Phase 7 activates that same selection; copied public descriptors cannot manufacture a credential. Nested scope restoration reuses the original logical selection/context but reacquires physical snapshots and leases. Invalid selection and stale publication authorize no draw.

Candidate inspection is immutable detached metadata and remains safe after close or ownership transfer without retaining handles. Compiler and barrier provenance are validated before publication release. Pre-release rejection preserves caller ownership and the old publication; failure after old release instead installs empty recovered-off state and does not revive the old registry. Off-to-ready publication has an explicit barrier-absent branch. Accepted resources cannot be destroyed by a later caller close.

Activity tokens are narrower than publication generation: activation invalidates previous uniform activity even within one generation. Same-provider caches can share layout identity without conferring draw permission. Texture leases may defer deletion, but do not preserve stale activation authority.

### 4.4 Current coordinated values and failure evidence

The current schema21 configuration/materialization identity is adopted without upgrading older objects. The same-load ninth inspection section remains asset metadata only; raw source, byte cursors and capability identities do not enter the source-free evidence writer.

The new requested-slot `ownBuild` classification is independent of effective fallback status. In particular, own failure followed by successful ancestor selection remains `CHAIN` plus `FAILED`, whereas intentional profile/property disablement remains `DISABLED`. Phase 7 explicitly copies those values and Phase 2's `/3` receiver rejects own `FAILED` even behind `CHAIN`; it does not reconstruct failure from source presence. Virtual/fixed and genuine source absence have distinct total outcomes. These producer/consumer statements are compatible on inspection, not runtime evidence.

### 4.5 Template, OQs and binding decisions

All thirteen required sections, numbered 0 through 12, are present and substantive. The Phase 4 assignment has no OQ; §10 states that explicitly. Optional Phase 14 async splitting is correctly recorded in §5.7 as an unadopted owner amendment with synchronous compilation remaining the available path. Its pending signatures, cancellation/visibility results and OQ-15 execution evidence are not falsely exposed as implemented APIs, and their absence is not a missing mandatory Phase 4 spike.

The decisions preserve the target platform, scope, compatibility profile, headless seam and fixed contract. No heuristic pack-specific fallback, replacement renderer or transformation-library mechanism was adopted. Current owner grants and historical receipts remain distinct from fresh verification and final integration clearance.

## 5. Findings

### R32-1 — Preserve positional no-output slots across the DRAWBUFFERS boundary

**Severity:** correction.

**Locations:** target §2.2, lines 555–557; §4.7, lines 1281–1283; §4.9, lines 1443–1451; incorporated by §5.1. Receiving evidence: Phase 3 §4.7 at lines 2617–2622 and §5 at lines 3556–3588; Phase 5 §4.5 at lines 1471–1488; Phase 1 §4.7.4 at lines 3234–3237.

**Claim under review:** adaptation from Phase 3 is lossless, and `DrawRouting.Explicit(List<BufferRef>)` plus an empty list for `N` fully represents explicit routing.

**Evidence and impact:** Phase 3 publishes a nonempty positional `List<DrawSlot>` with `Attachment` and payload-free `None` variants. It expressly preserves leading, middle, trailing, repeated and all-`N` slots. Phase 4 has only a list of actual buffer references. For `DRAWBUFFERS:0N2`, the producer supplies attachment 0, a no-output position, then attachment 2. Phase 4 cannot represent the middle element: dropping it shifts the attachment-2 destination from fragment output 2 to output 1; rejecting it turns legal producer data into provider failure/fallback; fabricating a buffer reference violates the published domain. An empty list only expresses a whole-route no-write special case, not the mixed route. Phase 5 merely preserves the received explicit list order, and P1 publishes only attachment indices plus an empty-array whole-route none case, so neither receiver supplies the missing positional mechanism. The target's claim that duplicates are necessarily a Phase 3 invariant breach is also inaccurate: P3 explicitly preserves duplicates, with attachment legality needing its own later validation rather than that producer assertion.

**Required correction:** publish a lossless positional routing algebra and define its adaptation, capacity validation and canonical identity. Preserve each no-output slot without treating it as an attachment or reducing the output-location count. Coordinate Phase 5's route realization and P1's backend representation/encoding through actual owner grants, including repeated/leading/trailing/all-none cases; do not introduce a consumer-local raw GL or magic-index convention. Keep any repeated non-none attachment rejection explicit as backend/program validation, not a nonexistent P3 invariant.

**§5 impact:** yes. This changes an exported Phase 4 state value and requires a complete receiving route through Phase 5 to P1.

### R32-2 — Specify an enforceable alpha/blend lock for admitted vanilla rendering

**Severity:** correction.

**Locations:** target §4.10, lines 1665–1672 and 1757–1760; §5.2, line 2029. Receiving evidence: Phase 1 `StateService` at lines 3312–3328 and its implementation discipline at lines 3411–3436; Phase 7 §4.4 at lines 1061–1089 and H-BLEND-01 at line 1551.

**Claim under review:** successful activation holds the effective program's alpha/blend overrides as locks until transition/release, including explicit OFF.

**Evidence and impact:** the only specified acquisition mechanism is snapshot, apply the two immediate P1 setters, and restore the saved snapshot later. P1 expressly routes cached state through `GlStateManager`; its interface provides no duration lock or interception control. Phase 7 then executes admitted vanilla render scopes after activation. Its blend hooks run at RETURN and publish the changed state to Phase 6; they neither prevent the setter nor reassert the program override. No alpha setter interception is granted. Consequently, an admitted body calling blend-enable or changing blend factors after a `blend.<prog>=off` or explicit-factor activation can overwrite the purported lock before its draw. The analogous alpha enable/function change defeats the alpha override. Restoring at the next program transition cannot enforce the promised state during that draw. This is a missing architectural mechanism, not a demand to prove runtime hooks before their scheduled implementation.

**Required correction:** define the lock's concrete engine/backend/adapter boundary and obtain the necessary owner grant. Specify how ordinary vanilla setters interact with a held override, how lock-owned writes bypass interception without recursion, what underlying state is retained, how nested transitions and release restore the documented snapshot semantics, how Phase 6 observes effective blend state, and how partial acquisition/restoration failure disables or contains rendering. P1 immediate setters and P7 observation callbacks must not be presented as an already sufficient lock implementation.

**§5 impact:** yes. The existing consumption row asserts a capability not supplied by those verbs. The corrected contract must name the real lock service/adapter and its owner and receiver responsibilities.

## 6. Counts, gate impact and disposition

- **Blocking findings:** 0.
- **Corrections:** 2 — R32-1 and R32-2.
- **Notes:** 0.
- **§5 impact:** **yes**, for both findings.

Neither defect requires rebuilding the registry architecture. Both require discrete owner/receiver contract corrections and fresh verification of the changed §5 boundaries. The present report grants no dependency-wide verification, runtime conformance, implementation clearance, or final integration approval. Pending required runtime evidence remains gated independently and is not counted as a defect here.

## 7. Final verdict

**PASS-WITH-CORRECTIONS**

## Resolutions — 2026-09-08 (architecture amendment, unverified)

- R32-1: P4 D-P4-33 publishes DrawRoutingSlot.Attachment/None and a nonempty positional
  Explicit list, preserving every hole. Domain/capacity/duplicate checks distinguish output
  locations from packed physical attachments; positional-route-v2 identity includes tags and
  positions. P5 realizes the list through P1 D-P1-57 FramebufferDrawSlot without raw sentinels.
- R32-2: P1 D-P1-57 supplies lockAlphaBlend and opaque AutoCloseable AlphaBlendOverride,
  concrete pre-mutation vanilla interception, guarded cache-coherent bypass, exact snapshot
  restoration, effective blend observation and partial-failure poisoning. P4 D-P4-34 closes
  predecessor before acquisition, locks before participants and invalidates activity on all
  release/failure paths. P7 receiving integration remains the main integration owner's task.
- §§5/8/11/checklists are amended in the owners. No validation, build, test, runtime or fresh
  review was performed. Original findings and PASS-WITH-CORRECTIONS verdict above are preserved;
  changed §5 awaits fresh whole-document review. IR-01 and final G5.3 remain open.