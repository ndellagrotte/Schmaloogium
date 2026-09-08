# Phase 5 — Fresh whole-document architecture review 41

## Verdict

**PASS** — the frozen Phase 5 owner architecture is ready for BUILD.md architecture integration. This is not implementation certification, runtime conformance evidence, certification of a sibling phase, or closure of final IR-01.

- **Owner:** `docs/phase5/v1/PHASE_5_DOC.md`, all 3,364 lines.
- **Frozen SHA-256:** `8b2ce09adbb986dc7eb753db4754999c162947575a82334fce555a4e90aeb862` — identity supplied and reconfirmed by the coordinator; no hashing or validation command was run by this reviewer.
- **Blockers:** 0.
- **Required owner corrections:** 0.
- **Notes:** 3, below.
- **section5Impact:** **false**. This review requests no addition, deletion, or change to the current binding §5 contracts.

## Review scope and evidence

This was a fresh whole-owner review, not a verification limited to recent corrections. I read the complete owner, including historical amendments, public declarations, detailed behavior, binding §5, failure policy, testability specification, staging, decision ledger, and implementation checklist. Boundary ranges were recovered rather than inferred. The working-tree diff was inspected as supporting change evidence, not treated as the review boundary.

The governing inputs examined were RC3 Part I and the complete Phase 5 assignment (`docs/design/v2.0-RC3/DESIGN.md:92-1109,1572-1685`); RESEARCH §§0–1, §3.6.3, §§4.1/4.3, Appendix B, and the incorporated clear/flip authority; Pintonium design §5 and its relevant sampler, historical-bug, and frame-end dispositions; the current dependency §5 surfaces of Phases 1, 3, and 4 with their incorporated handle, operation, selection, publication, lifetime, and failure rules; and the applicable current resolutions in `docs/PHASE_INTEGRATION_REVIEW.md`.

Primary reference inspection included the shipped G6 `doc/shaders.txt` tables and the pinned Pintonium `9c2fcc1` target directory, including all thirteen Java files across its root and `backed/` subdirectory. Relevant additional permitted primary evidence was the pinned `ShadowRenderTargets`, framebuffer mixin, and actual depth-copy strategy. No transformation code was copied or used. The target reference was obtained from its pinned upstream URLs, including [RenderTargets](https://github.com/Xplodin/Pintonium/blob/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/RenderTargets.java), [RenderTarget](https://github.com/Xplodin/Pintonium/blob/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/RenderTarget.java), [ClearPassCreator](https://github.com/Xplodin/Pintonium/blob/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/ClearPassCreator.java), [BufferFlipper](https://github.com/Xplodin/Pintonium/blob/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/BufferFlipper.java), and [ShadowRenderTargets](https://github.com/Xplodin/Pintonium/blob/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shadows/ShadowRenderTargets.java).

## Whole-owner assessment

### Resource planning, allocation, and evidence

The owner separates immutable planning, candidate creation, acceptance, and accepted estate observation. Its PLANNED/REALIZED model does not promote requested values into allocation facts: `DefaultRgba` remains distinguishable from explicit `RGBA8`, successful RGBA fallback retains the request and records allocation origin, and the fog clear policy is declarative rather than an invented frame-color sample (`§2.2`, `§4.1.1`, `§4.2`, `§5.1`). Equal-input planned equality and exact candidate/accepted-estate realized equality are compatible; no cross-stage full-value equality is required. `BufferSizing` has exactly the current main and optional shadow extents.

The plan retains positional routes, explicit None holes, effective-provider requirements, virtual transitions, capability shortfalls, and logical-versus-physical attachment identity. Allocation, complete-FBO validation, whole-estate fallback, and cleanup are specified as bounded ownership transitions rather than partial successful publication (`§§4.1-4.3`, `§4.5`, `§4.7`). The thirty-seven pack-facing formats remain separate from the private plain-RGBA allocation representation.

### Main pass, clear, and mipmap lifecycle

The main-side model preserves logical carried-role identity instead of assuming role A is permanently one concrete texture. Successful frame-end rebase and abort normalization are distinct; neither introduces a hidden copy-back operation or an extra flip (`§4.4`). Full clear, ordinary clears, retained buffers, integer clears, and fog-alpha behavior have explicit policies (`§4.6`).

Snapshot preparation now has a total result contract. Pure protocol rejection occurs before mutation; successful preparation alone exposes `Acquired`; mutation-bearing preparation failure returns `Failed(failure,diagnosticId,true)`, consumes the frame, invalidates its snapshots, requires full clear, and marks the estate stale (`lines 857-861,901-914`; `§4.2.1`; `§5.1`; `§6`). Main mipmap generation uses the frozen effective provider and readable side/revision, validates before mutation, distinguishes successful generation, freshness reuse, and recoverable base-level degradation, and treats failed restoration as terminal. These contracts no longer leave backend preparation failure masquerading as rejection or an uncaught ordinary acquisition exception.

### Depth, shadow, resize, and ownership

The sampleable main-depth bridge remains Phase-1-owned and borrowed by Phase 5; auxiliary copies and engine-created attachments retain explicit ownership. Same-extent depth reattachment, invalidated pass snapshots, resize-required outcomes, failed copies, and safe continuation/replacement have distinct dispositions (`§§4.8-4.9`, `§4.11`).

Shadow allocation, live pass behavior, two-sided color state, compare/filter policy, mipmap freshness, and neutral-cache recovery are explicitly modeled (`§4.10`). Requested demand and provider minima do not become fictitious allocated availability. The neutral family preserves required actual formats and applicable policies and has a pre-admission terminal-failure path, rather than permitting a frame to proceed with missing or old texture bindings. The implementation milestones separate v0.1 structure from v0.2 shadow execution without treating later execution as already proven.

The shared texture protocol retains one fixed-unit authority, the same authenticated effective program selection, complete preflight, deterministic compatible-candidate resolution, and exactly-once lease transfer only after successful binding (`§2.4`, `§4.12`). Publication and teardown distinguish caller-owned candidates, accepted owner-managed objects, borrowed handles, independently closeable scopes, and terminal frame state. The current coordinated lock order is consistent with the examined dependency publication/lifetime rules.

## Cross-boundary receiving checks

These checks establish that Phase 5's current exported values have documented receiving dispatch, not that the entire receivers pass review:

| Export or handoff | Receiving evidence and result |
|---|---|
| PLANNED/REALIZED resource projection; requested/allocated format; allocation origin; fog/constant variants | P2 `§4.5.4`, `lines 1204-1243,2009-2027,2560-2565` explicitly preserve, require, or forbid fields by variant. Both valid and invalid planning results are handled; unavailable evidence cannot become complete. P7's accepted-estate resource export retains REALIZED evidence rather than reconstructing a plan. |
| `PassSnapshotResult.Failed(...,true)` | P7 `§4.4` and `§4.6`, particularly `lines 1000-1055,1110-1270`, apply the already-consumed terminal marker to ordinary, nested/resumed, and fullscreen paths and outer cleanup. Later draw, mipmap, activation, flip, resume, commit, and abort are prohibited while independent owned scopes still close. |
| `MainMipmapResult` | The same P7 fullscreen dispatch permits drawing after completed generation/freshness/base-level degradation, rejects invalid preconditions without drawing, and handles already-aborted failure as terminal. |
| `FixedSamplerPlanResult.Ready/Invalid` | P6 `§4.9`, `lines 1195-1250`, consumes both branches. Invalid retains diagnostics and degrades the sampler participant without becoming an empty successful plan or a replacement unit map. |
| Full overlay lease and texture binding outcomes | P13 `§5.2`, `lines 1510-1563`, receives the mandatory selection, full main/shadow snapshot domain, closeable binding result, exact rejection set, and suppression behavior. Its coherent transaction retains acceptance and compensation ownership (`§5.3`). |
| Shadow demand, actual availability, neutral recovery | P8 `lines 675-760,815-933` distinguishes requested demand from actual estate availability and receives the required neutral family and fail-closed pre-admission outcome. P7 `§4.5.1` receives that admission gate. |

No currently introduced Phase-5 export examined here is silently dropped, routed through an obsolete success-only branch, or treated as a drawable result when its owner forbids drawing.

## Required changes

**None.** There is no evidenced current owner defect requiring a §5 amendment or correction before this frozen architecture can receive a literal PASS.

## Notes

1. **Historical reference qualification is correct and must remain historical.** At pinned `9c2fcc1`, `ShadowRenderTargets.flip(int)` toggles the tracked state and its color accessor selects main versus alternate accordingly, despite the stale TODO/comment attribution carried by the older reference digest. Phase 5's current `D-P5-35` and ruling qualify that evidence; the superseded blanket stub claim is not an active owner defect. Likewise, historical review findings and superseded SSAA/frame-end interpretations are preserved as evidence, not competing current instructions.
2. **Receiver observations are not sibling certification.** The receiving paths above were inspected only to assess Phase 5's contract completeness and routing. This PASS does not certify Phases 1–4, 6–8, 13, or 14, does not change their independent review outcomes, and does not close the final integration review.
3. **Runtime proof remains outstanding by scope.** The owner’s testability, fault-injection, recorded-GL, live-conformance, and implementation-checklist items remain future execution requirements. I ran no builds, tests, formatters, linters, smoke tests, or runtime validation, and changed no files. No real driver completeness, clear/flip image equivalence, mipmap restoration, resource-retirement behavior, performance, or shader-pack compatibility is certified by this architecture review.

**Final literal verdict: PASS.**