# Phase 1 — Whole-document verification, round 28

## 1. Review identity and scope

**Document:** `docs/phase1/v14/PHASE_1_DOC.md`  
**Governing authority:** `docs/design/v2.0-RC2/DESIGN.md`, Part I and the complete Phase 1 specification. The phase-header authority remains RC2; this review does not globally adopt RC3 or v3.  
**Review scope:** the complete current Phase 1 document, including historical addenda, active architecture, incorporated §5 contracts, failure semantics, testability, staging, assigned OQs, decisions and implementation handoff. This is a fresh whole-document review, not acceptance of the R27 resolution note as proof.  
**Severity counts:** blocking **0**, correction **0**, note **0**.

No actionable architecture defect was established in the reviewed Phase 1 document. The evidence and remaining execution boundaries below explain that result; they are not additional findings or implementation clearance.

## 2. Inputs and evidence discipline

Read the governing Part I and Phase 1 specification, Research §0–§1 and the assigned platform, seam, licensing and MCP inputs, with targeted reads of the research contract/lifecycle sections and appendices used by Phase 1. Read PD §2 and §16 and its standing §17/§18 do-not-inherit material. Reviewed the actual template build/settings/properties, dependency/extra/publishing scripts, wrapper, all three workflows, README and all eight template source/resource files. The MCP mixin guide and mixin template were checked as platform documentation, not as evidence that the planned project has been implemented.

Bounded source checks covered the available Pintonium service inventory, startup injections and mixin-registration mechanism, plus the available Cleanroom bootstrap layout. The available trees are `reference-src/Pintonium-main` and `reference-src/Cleanroom-0.6.12-alpha`; they were not relabeled as the historical `pintonium-9c2fcc1` or the pinned Cleanroom 0.6.10-alpha runtime. Their license files were inspected. In particular, the current Pintonium root license is GPLv3 text; this review does not re-date the historical LGPL provenance or authorize incorporation based on that historical label. No implementation source was copied and no prohibited transformer, decompile, chatlog or root transcript was used.

Cross-boundary checks read the relevant producing and receiving contracts in P3, P7, P10, P13 and P14 rather than stopping at P1’s publication rows. The published [Khronos ARB_vertex_shader specification](https://raw.githubusercontent.com/KhronosGroup/OpenGL-Registry/main/extensions/ARB/ARB_vertex_shader.txt) was checked for vertex-array/current-state and display-list rules. The dated geometry, U1 and texture-sidecar decisions were treated as narrow maintainer authority, not measured reference parity.

This review made no file changes and ran no build, test, formatter, linter, validation command or client. Existing execution evidence is attributed to `docs/build/READINESS.md`, not claimed as a new run.

## 3. Literal Phase 1 doc gate

| Gate criterion | Result and evidence |
|---|---|
| Final module/package layout and testable dependency rules | Satisfied. §§2.1–2.2 and §4.3 specify `:engine`, `:mod`, `:conformance`, their allowed edges and C-1 through C-4. §8 supplies enforcement for both forbidden platform dependencies and engine-internal access, including the conformance-module edge. New vertex, texture and modernization homes preserve these constraints. |
| Every D-1 through D-10 satisfied or explicitly deferred to a named owner | Satisfied by §11.2, read together with §1.2, §3 and §11.4. Foundation mechanisms are distinguished from later renderer, pack, hook and policy implementation. |
| Complete pin table and re-verification procedure | Satisfied by §4.2.6 and its current-checkout correction, D-P1-51, §10.1 and the implementation handoff. Current executable pins are distinguished from July observations; preserving the wrapper pin and aligning CI setup inputs is explicit. Conditional jcpp admission requires an exact verified implementation-time pin, closure, notices and packaging before production use; it is not an unversioned production dependency grant. |
| Glue-seam completeness check against PD §2 | Satisfied by §4.12. The version-facing and glsm services are bucketed into facade operations, named later-owner providers and deliberate exclusions. Foreign textures and Minecraft’s framebuffer are not misrepresented as ordinary owned allocations or framebuffer zero. |
| Adopted or justified bootstrap sequence | Satisfied by §4.13 and the mixin/implementation sections: early options initialization, post-vanilla-texture capability/engine initialization and loading completion have distinct responsibilities. The class-scan plugin is evaluated rather than silently inherited. |

The literal doc gate is met. The separate implementation gate—real empty-module build, architecture test and green CI—is not met by an architecture document or by the unchanged template build.

## 4. Whole-phase adversarial checks

### 4.1 Conformance-map fidelity

The in-scope §3 rows were checked against their research meanings, including capability probes, identity/extension macros, fixed sampler ownership, depth-copy/readback roles, source directives and prepared-submission semantics. No omitted in-scope foundation contract or semantic inversion was established. Texture-object binding remains P5’s responsibility, sampler-uniform assignment P6’s, source processing P3’s, and traversal/execution P7/P8’s. P1 does not acquire these policies merely because it supplies a facade verb.

Native geometry legality is not inferred from successful source processing or linking. D-P1-48’s cached effective linked input controls fullscreen dispatch, including the triangle-strip route when TRIANGLES input is active on a QUADS-capable context. Incompatible or unknown geometry state cannot silently take the old QUADS preference. The approved native conversion and adjacent prepared-submission repetition remain separately owned and do not authorize a renderer rewrite or repeated world traversal.

### 4.2 Interface honesty and consumer routing

§5 incorporates the substantive contracts instead of publishing names alone. The vertex source/result/mode boundaries, texture parameter value, debug activity gate, recording semantics and failure paths have corresponding receiving responsibilities. P10’s D-P10-20 receives the complete D-P1-55 operation; its capture staging and replay paths do not assume that a pointer bind inside a display list can implement P1’s pre-list transaction. P7’s D-P7-32/R10-6 retains the lifecycle and hook-coverage responsibility for the expanded cached-model path rather than leaving the new path outside quiescence.

P3’s current binding contract is schema20. P1’s active §5.2 and §11.4 receive the native `None/PreserveNative` request and `None/CoreLayout/NativeLegacy` result semantics retained from D-P3-68, without reviving `Translate`. The historical schema19 receipt remains dated history. D-P3-69’s required `assets` component immediately after `sources`, same-load provenance and post-archive lifetime are P3-owned acquisition—not a new P1 I/O service.

The actual asset receiver distinguishes `Acquired`, `Missing`, `Unreadable` and `InvalidReference`; it does not collapse invalid pairing or primary failure into optional recovery. Only P3-proven optional-owned-sidecar-only read errors take P13’s baseline/one-warning path. Source, configuration, bounds, index, container and primary duties remain fatal where specified. P7 retains the exact configuration/capability for resource-only NONE; metadata-only inspection is not a replacement preparation input. The ninth `assets` inspection section adds canonical manifest metadata, not bytes, cursors or providers, and projectionVersion remains 1 under the schema20 gate.

### 4.3 Scope, completeness and decisions

All thirteen §G9 sections are present and substantive. OQ-2, OQ-12, OQ-20’s assigned seam-hardness share and OQ-21 have question, procedure, criteria and fallback/disposition material. Accepting the licensing note does not settle the later GUI arrangement or grant reuse from an unverified license region.

D-5/D-6/D-7 are explicit. The v0.1 geometry-only input/early-veto requirement is not postponed behind v0.3 CLASSIC56 work. Complete synchronous texture parameters are not confused with optional sampler-object or async execution grants. P14’s supported-backend debug activity requires the stated capability and flag, not an unnecessary debug-context prerequisite; unrelated worker/context operations remain separate requests. No new binding-decision violation or scope substitution was established.

### 4.4 Pintonium compliance

The document retains provenance and the distinction between portability evidence and headless-testability proof. The service inventory is a completeness checklist, not a copied multi-backend architecture. Relevant standing bug/divergence handling is present; reference stubs and excluded transformation machinery are not treated as implemented capability. The historical source and template observations remain dated rather than being silently promoted to current execution evidence.

## 5. Independent re-derivation of R27-1

R27-1’s trigger remains a valid adversarial case: an unrelated enabled generic attribute array zero can defeat an authenticated conventional position source, and list capture can consume unrelated enabled arrays even when they are not part of the intended shader-input plan. Restoring state only afterward would not repair either captured or submitted values.

Current §4.7.6, especially lines 4035–4108, now closes those paths. LIVE_DRAW disables generic zero before conventional-position submission without overwriting its foreign descriptor. LIST_CAPTURE uses the complete plan as an enabled-array allowlist over capability-legal conventional arrays, every legal client texture unit and every generic location, with rejection before mutation if a supported family cannot be safely isolated. Admission is based on authenticated ranges, not apparent shader usage.

The restoration contract covers actual per-pointer buffer associations, changed descriptors, enables, global array-buffer binding, client selector and legal current-value side effects. It does not query nonexistent current position/generic-zero state. Partial setup rolls back internally without issuing Bound; failed rollback invalidates admission and reaches P7 containment. Setup precedes `glNewList`, closure precedes restoration, and replay is a separate guard that never reconstructs expired pointers.

P10’s receiving capture design provides the required pre-list prepared input rather than demanding an impossible inside-list setup. Its cached-model path is connected to P7’s expanded invalidation/quiescence grant. Thus the owner correction is not merely a stronger sentence with an unchanged incompatible consumer.

§8’s value-observing A/B positions, unrelated-array capture, complete predecessor comparison, nested scopes, capability absence, partial-failure injection and retired-source replay cases target the original failure mechanisms. They remain planned proof. This review closes the architectural correction, not driver behavior or primitive parity.

## 6. §5 impact and review applicability

**§5 impact: yes.** The current document incorporates changed contracts for linked primitive dispatch, vertex input/isolation/restoration, complete owned-texture parameters, debug activity/package placement, early compatibility evaluation, jcpp admission and the current P3 source/schema receipt. Their incorporated semantics—not merely the table rows—were within this whole-document review.

No new owner correction is requested by round 28. This result applies to the reviewed P1 document only. It is not a fresh PASS for P3, P7, P10, P13, P14 or any other receiver, does not make historical reviews certify their changed bytes, and does not replace §G5.3 final integration. A subsequent substantive P1 §5 change requires the applicable fresh review again.

## 7. Readiness and verification limits

`docs/build/READINESS.md` records a successful unchanged-template Java-25/Gradle-9.7.0 build, with `compileTestJava` and `test` NO-SOURCE. That demonstrates neither the planned three-module architecture nor a renderer or architecture-test pass.

The same evidence records a working direct NVIDIA compatibility context, followed by a pre-menu JEI/HEI 4.33.0 startup failure. The recorded classloading investigation identifies split `SidedProxy`/`Side` identities across AppClassLoader and LaunchClassLoader in the forced-parent `modLibrary` dependency path. This is a known current-template runtime blocker, not an absence of GPU capability and not a new successful client run. This reviewer did not rerun it or infer that architecture amendments fixed it.

No menu/frame/pack result, T0–T3 conformance, native client/VBO/list restoration proof, classic-pack parity or completed OQ runtime experiment is supplied here. IR-01, fresh affected owner/receiver verification, final integration and explicit implementation clearance remain independent gates. The historical readiness inventory is not used as the current schema/grant authority.

## 8. Verdict

**PASS**

The current Phase 1 architecture meets its literal doc gate and the whole-phase verification checks. R27-1 is resolved at the owner-contract level with an explicit compatible receiving path, and no additional correction was established. Counts: **0 blocking, 0 correction, 0 note**. This is documentation verification only; implementation and final-integration clearance remain withheld.
