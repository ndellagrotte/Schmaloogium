# Phase 8 architecture verification — Review 5

## 1. Reviewed artifact and method

- **Reviewed document:** `docs/phase8/v1/PHASE_8_DOC.md`, all 2,021 lines, including all thirteen sections.
- **Assigned frozen SHA-256:** `e8e745c7af487f668d85ee2560089d0dfea32e49f69937414c10c7a2228f86b7`. This identifies the supplied review snapshot; it is not presented as an independently computed checksum.
- **Governing design:** the target header’s `docs/design/v2.0-RC3/DESIGN.md`, not a presumed globally governing v3 document.
- **Method:** fresh whole-document §G1.2 review, including consuming-owner contracts and permitted reference checks. Historical reviews, integration receipts and recorded resolutions were treated as claims to examine, not as substitute verification.
- **Execution boundary:** read-only architectural investigation. No files were changed and no builds, tests, runtime experiments, lint or formatters were run. This report does not certify dependency reviews, final integration, implementation readiness or runtime correctness.

## 2. Authority and evidence read set

### Governing and behavioral authorities

Read RC3 Part I, including §G1.2 and the licensing/provenance rules, and its complete Phase 8 specification and required inputs. Also read `docs/MOVES.md` and the applicable research material in `docs/research/v1/RESEARCH.md`: §§0–1, §4.5, Appendix A.3, B.2–B.3, D.2–D.3, E rows 1–2, and the shadow-translucency entry in F.1. Read Pintonium design §10 and its provenance introduction as a subordinate implementation reference.

Read the original findings in `docs/PHASE_INTEGRATION_REVIEW.md` and all its Resolutions and dated follow-ons. Read the recorded U1 texture-sampling, geometry-primitive compatibility and texture-sidecar-default decisions, together with Phase 4 §11.5’s approved adjacent-submission ruling. These establish current decisions, not runtime evidence.

### Dependencies and actual receivers

Read the complete current §5 interfaces of Phases 4, 5, 6 and 7 and the incorporated semantics relevant to Phase 8: selection and barrier outcomes; buffer/shadow operation and physical-binding algebras; uniform frame/event schemas, notification, reset and retirement; construction order, shadow invocation, scope admission, restoration, publication and teardown.

Additional actual-owner checks covered Phase 1’s package grants; Phase 2’s `/3` nested owner-8 health reporting; Phase 9’s authenticated alternate ID scopes; Phase 10’s prepared geometry/submission adapters; and Phase 13’s texture publication, leases and atlas observation. This included the consuming dispatch or outcome handling rather than stopping at Phase 8’s declaration of an outgoing value.

### Permitted reference checks and limitations

After reading the licensing rules, inspected the available `reference-src/Pintonium-main` shadow-matrix, renderer and celestial-uniform material; the available `reference-src/Cleanroom-0.6.12-alpha` Forge render-pass setter/getter and RenderGlobal patch; permitted G6 published shader documentation; and the bounded behavioral digest. MCP signature checks corroborated the 1.12.2 `setupTerrain` and blob-only `renderShadow` targets, and the framework render-pass setter.

The older version-named reference directories cited by the target were not present. Filename searches located the available replacement trees, which were used only for qualified behavioral cross-checks. Their byte identity with the historical pinned revisions was not re-established. No forbidden pipeline, transformation, relocated-library, transcript or chatlog sources were inspected.

## 3. Substantive adversarial audit

### 3.1 Document gate, research and scope

All thirteen required sections are substantive. The document supplies the camera equations, traversal algorithm, shadow directive and uniform mappings, Appendix E hook rows, milestones, failure policy, test plan and fallbacks required by the Phase 8 specification. It explicitly acknowledges that the referenced design does not supply a usable 1.12.2 traversal implementation.

The research mapping covers the shadow directives and aliases, declaration-driven demand, opaque/translucent depth split, fixed sampler units, comparison and mipmap policies, `shadowAngle`, all four shadow matrices and the celestial vectors. The target preserves the required solid → cutout-mipped → cutout order rather than adopting the differing modern reference order. Clouds remain before the depth split; Forge pass 1 remains independent of the terrain-translucency switch; shadowcomp is not introduced.

No OQ is assigned to this phase. The listed camera/traversal and integration spikes have concrete observations and fallback behavior. Their pending execution is not itself an architecture defect. The callable celestial contract, however, does not provide the inputs and output required by its otherwise explicit equations; this is finding R5-1.

### 3.2 Camera, culling and traversal

The written camera policy distinguishes orthographic and perspective paths, fixes the near/far policy, confines signed-remainder snapping to the orthographic path and explicitly corrects the referenced perspective-matrix defect. The light direction and culling convention agree on direction toward the active light.

The frustum construction defines normalized inward planes and silhouette side planes. Its positive-coefficient combination preserves the retained interior; the AABB test uses the corresponding positive vertex. Degenerate or invalid geometry and traversal arithmetic have conservative handling rather than silently excluding potential casters.

The terrain plan defines the distance-to-chunk conversion, the bounded sun-aligned prism when the shadow distance is below the main view distance, and the full-load alternative. It specifies toroidal chunk-position checks, a separate visited set and restoration of the main terrain snapshot, rather than borrowing vanilla’s mutable frame-index visitation. These are architectural commitments; the proposed oracle/spikes still have to establish implementation behavior.

### 3.3 Program, buffer and texture transactions

The invocation contract carries the same selected program and activation context through binding and forced activation. The Phase 5 physical-binding result is exhaustively handled: only a successful Bound result transfers ownership; incomplete backend work does not authorize activation; and expiration does not eliminate the obligation to close a transferred binding. Abort restoration and terminal neutralization are distinct outcomes.

Construction separates pure shadow planning from the final compiled-registry pairing. Publication identity includes the final registry, while live generation and slot-epoch checks prevent equal content hashes from reviving retired resources. Texture leases are checked against the expected publication, and the actual Phase 13 receiving contract supplies the publication/lease semantics claimed by Phase 8.

### 3.4 Nested execution and consumer routing

The Phase 7 receiving path explicitly avoids normal main-world interception during shadow execution. Alternate entity/tile-entity ID admission reaches Phase 9’s scoped receiver; color restoration remains independently scoped. Atlas updates follow the actual-bind Phase 7 → Phase 13 → Phase 6 path rather than introducing a second texture binder or inferring an atlas merely from a requested name.

The approved prepared-submission path retains adjacent native submissions and per-submission IDs without replaying world traversal, Forge passes, the depth split or mipmap generation. Its current owner contracts preserve the model-list/geometry boundaries. This assessment does not promote future implementation milestones into already verified behavior.

### 3.5 Failure, lifetime and control-plane boundaries

Failure handling distinguishes an isolated inverse-matrix degradation from failures requiring a neutral shadow estate, safe abort or full shaders-off containment. Invocation-local data is borrowed, not retained. Replacement and teardown drain final scopes and callbacks before resource disposal; Phase 6’s retirement contract keeps borrowed adapters alive through successful terminal retirement and distinguishes rejection from completed disposal.

The document preserves resource-only reload semantics, separate configuration and live-generation identities, and independently gated installation of a real shadow slot. Canonical schema-21 reporting remains Phase-7-owned, while owner-8 hook health uses the current `/3` nested protocol and all eight rows. No second frame clock, protocol alias or reporting authority is introduced.

Licensing/provenance boundaries remain explicit. Modern renderer material is used for portable math and behavior checks, not as authority to copy modern traversal or override the research contract. The current coordinated grants can be assessed as declared interfaces; their historical receipts do not prove current dependency acceptance or final integration.

## 4. Findings

### R5-1 — Separate pure angular policy from frame-bound celestial sampling

**Severity:** correction. **Location:** `docs/phase8/v1/PHASE_8_DOC.md:426–433`; supporting target locations `392–406`, `454–463`, `830–834`, `936–947` and `1287–1309`.

**Claim:** the binding celestial API cannot produce the value it promises or supply the shared `shadowAngle` result. `ShadowCelestialPolicy.sample(float sunAngle)` returns Phase 6’s `CelestialSample`, but the policy is constructed solely from pure plan content. Phase 6’s exact record contains current `worldEpoch`, `frameId` and four eye-space vectors, and contains no `shadowAngle`, day indicator or celestial-rotation result. Meanwhile `ShadowCameraMath.compute(ShadowFrameView, ShadowPolicy, Extent2i)` must produce a `ShadowCamera` containing that same celestial sample, but `ShadowFrameView` has no main-camera model-view matrix or orientation.

**Evidence:** Phase 6 defines `FrameUniformSample.shadowAngle` separately at `docs/phase6/v1/PHASE_6_DOC.md:689–699`, the exact `CelestialSample` shape at `721–725`, and current-frame identity requirements at `738–753`. Target §4.3 requires the Phase 6 provider and shadow camera to call the same pure function for `shadowAngle`. Target §4.5.4 requires multiplication by the current gbuffer model-view matrix for `sunPosition`, `moonPosition`, `shadowLightPosition` and `upPosition`. Phase 7 does provide the missing camera as a separate member of `ShadowInvocationContext` at `docs/phase7/v1/PHASE_7_DOC.md:2163–2178`, but neither published math signature accepts it. Target §5.1 expressly incorporates the exact §2.2 shapes, so this is not merely nonbinding illustrative code.

**Trigger and impact:** rotating the main camera without changing its position or the celestial angles requires different eye-space celestial vectors, while the declared math inputs remain unchanged. The pure policy also has no input from which to obtain a current frame identity, and its declared return type gives the Phase 6 provider no shared angular output to read. Implementation must therefore either add an undeclared dependency/state channel, duplicate the angular calculation, or submit stale/incorrect data. Each conflicts with an explicit architectural requirement.

**Required correction:** define a Phase-8-owned pure angular result exposing the required day/angle/rotation information, and explicitly separate frame-bound `CelestialSample` construction using current frame identity and the main `CameraSnapshot`. Either pass those inputs into the camera/sampling API or assign sample construction to the invocation layer with an explicit contract. Update the exact §2.2 and §5 shapes and the Phase 7/Phase 6-provider handoff consistently. Phase 6’s existing event record need not be enlarged merely to serve as an unrelated pure-policy return type.

This is a bounded dataflow/interface correction, not a structural rebuild and not an unexecuted-runtime-spike finding.

## 5. Counts, §5 impact and disposition

- **Blocking findings:** 0
- **Corrections:** 1
- **Notes:** 0
- **§5 impact:** yes. R5-1 changes the exact exported celestial-policy/math contract and its composition/provider consumers. The corrected Phase 8 §5 surface and affected Phase 7/provider handoff require coordinated re-verification; current grants must not be treated as acceptance of a replacement signature.

The remaining reviewed architecture is sufficiently specified to retain its design rather than rebuild it. No corrections or Resolutions were applied during this review. Dependency acceptance, final integration, implementation gates and the required runtime/spike evidence remain independent obligations.

**Final verdict: PASS-WITH-CORRECTIONS**

## Resolutions

### R5-1 — Applied architecture correction (2026-09-08; §5 changed, unverified)

Authorized separate fix-up in `docs/phase8/v1/PHASE_8_DOC.md` §§0.11/2.2/4.2–4.5/4.10/5/8/9/11/12,
recorded as D-P8-22. P8 now owns immutable
`ShadowCelestialAngles(boolean day,float shadowAngle,double thetaRadians)` returned by the pure
`ShadowCelestialPolicy.sample(float sunAngle)`. The policy rejects nonfinite/non-normalized
input, retains exactly the existing inclusive `s <= 0.5` day boundary, shadowAngle/a equations
and radian theta, and cannot obtain or mint world/frame/camera identity.

The sole camera entry point is
`compute(ShadowFrameView frame,CameraSnapshot mainCamera,ShadowPlan plan,Extent2i shadowExtent)`.
It uses the plan policy and celestialPolicy, current frame worldEpoch/frameId/skyAngle and the
actual main-camera modelView to build P6's unchanged CelestialSample with the existing w=0
equations. Rotation-only changes can alter eye vectors while leaving shared shadowAngle equal.
P7's mod.glue provider obtains angular shadowAngle before camera capture, retains the same
sun/sky frame sample and later supplies the actual post-camera snapshot under its existing live
invocation association. No camera identity field, current-GL lookup, pre-camera eye-vector
synthesis, P6 engine-to-shadow dependency or old callable overload is retained.

The transaction sends the same returned immutable celestial event before first activation;
later sky observation cannot introduce another formula or time basis. Future behavioral fixtures
cover boundary rejection, rotation/translation sensitivity, current identity, actual provider-to-
invocation delivery and stale association rejection. Publication, restoration, traversal,
physical binding, schema21 and current reporting laws are unchanged.

**Disposition:** R5-1 is addressed in architecture prose, not independently verified. §5 changed;
fresh whole-document P8 verification and coordinated P7 receiving verification (R38 after the
preserved R37 PASS) are required before consumption. The original review, finding, counts and
PASS-WITH-CORRECTIONS verdict above remain preserved history, not a self-PASS for this fix-up.
No source/build/runtime changes, builds, tests, lint, formatters or validation commands were run.