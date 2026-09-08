# Geometry-stage primitive compatibility — narrow scope approval

**Date:** 2026-09-07. **Status:** maintainer-approved scope correction; exact owner contracts and runtime conformance still require verification.

## Problem and evidence

`docs/phase1/reviews/PHASE_1_REVIEW_26.md` R26-1 identifies a native geometry path configured with `TRIANGLES` input whose fullscreen receiver still prefers `QUADS`. Successful source processing, compilation and linking do not make that draw legal.

The published [Khronos ARB_geometry_shader4 specification](https://raw.githubusercontent.com/KhronosGroup/OpenGL-Registry/main/extensions/ARB/ARB_geometry_shader4.txt), read 2026-09-07, specifies that TRIANGLES-input geometry accepts `TRIANGLES`, `TRIANGLE_STRIP` or `TRIANGLE_FAN`. Issue 30 expressly says `QUADS`, `QUAD_STRIP` and `POLYGON` do not match any geometry input primitive and produce `INVALID_OPERATION`. This is API legality evidence, not an observed Minecraft run or an OptiFine topology/parity result.

Phase 1 already owns a triangle-compatible fullscreen route. Native vanilla quad submissions expose a separate scope conflict: `docs/phase10/v1/PHASE_10_DOC.md` §1 excludes triangle conversion. Leaving that exclusion unconditional would not permit a complete adapter for the affected geometry-stage draws.

## Exact maintainer authorization

After the evidence and alternatives were presented, the maintainer selected **“Authorize conditional submission conversion”**:

> Permit only the geometry-compatibility adapter, at the earliest milestone claiming the affected .gsh support. Preserve per-quad attribute computation, winding, source data, draw ordering, state restoration and adjacent instance repetition; no general chunk-renderer rewrite. Require explicit topology/provoking-vertex/primitive-ID policy and real conformance evidence before claiming parity.

This overrides the Phase 10 blanket triangle-conversion exclusion **only for conditional geometry-stage primitive compatibility**. It does not authorize a general triangulated terrain backend, core-profile migration, performance rewrite, shader-source transformer, new renderer replacement API or changed modern-feature scope. Historical design/research/review text remains evidence; affected phases must explicitly adopt the dated correction without globally changing their governing design revision.

## Required owner boundaries

- Phase 1 owns legal facade/backend dispatch and its linked-program metadata/state restoration. Fullscreen compatibility remains inside the existing facade operation, without a per-draw raw-GL query or source rescan.
- Phase 3 owns complete source processing and source-derived geometry information. Source availability is not draw compatibility.
- Phase 4 owns effective linked-program/provider metadata and must grant the exact input requirement consumed by the draw adapter; no consumer may infer it from a requested fallback child or a private registry.
- Phase 7 owns authenticated active main/shadow orchestration and the private prepared-submission boundary. Phase 10 owns the relevant native client-array/VBO/display-list adapters and preserves quad-level vertex semantics before any necessary submission adaptation. Phase 8 retains traversal and shadow ordering.
- If these interfaces require a new operation or metadata projection, its owner must publish it and receivers must adopt it before use. This approval is not an invented API grant.

The architecture must specify legal conversions and unsupported cases, triangle ordering/diagonal and winding, provoking-vertex and primitive-ID behavior, source storage lifetime, list capture/playback and unknown mixed-state lists, nested admission/state restoration, failure containment and the placement of adjacent instance repetition. These are required contract decisions and checks, **not values silently selected by this approval**. A scope approval does not establish OptiFine's diagonal or primitive-ID behavior.

Required geometry support cannot be replaced by silently omitting `.gsh`, drawing incompatible primitives, treating a failed native draw as supported, forcing one instance, repeating world traversal, or claiming the whole feature from fullscreen-only evidence. The adapter lands no later than the earliest milestone that claims its affected native geometry behavior; unrelated extended attributes retain their existing milestone assignment.

## Verification boundary

R26 must be fixed in a separate session and freshly reviewed. All affected producer/receiver contracts require their applicable independent verification and final integration under §G5.3. Actual compile/link/draw checks must exercise a compatibility context with QUADS available and the geometry path active, including native client-array, VBO and display-list paths as claimed, state restoration, fallback-provider metadata, failures and adjacent-instance ordering. No such runtime result is supplied by this document.

IR-01 remains open. No original authority or historical review is rewritten, no implementation clearance is granted, and no classic-pack tier or pixel parity is claimed.
