# Phase 13 — Fresh whole-document architecture review 4

**Verdict: PASS**  
**Blockers: 0 · Corrections: 0 · Notes: 3**  
**section5Impact: false** — this review requires no owner-interface correction. Existing coordinated §5 amendments retain their separate owner/receiver verification and integration gates.

## Scope and frozen artifact

Reviewed `docs/phase13/v1/PHASE_13_DOC.md` in its entirety, §§0–12, including all historical addenda and current incorporations. The commissioned SHA-256 is `2b69734a87dd3accc88a9ac7e0ca5a4af5c07fd4bfc1d5eb3fc24c3a7fcb6a37`; the coordinator subsequently confirmed that every assigned freeze remained unchanged. This report relies on that supplied freeze confirmation, not on an independently run hash command.

The governing authority is the owner's selected `docs/design/v3/DESIGN.md`, Part I §§G0–G12 and the Phase 13 specification. Review applied §G1.2's document gate, conformance, interface honesty, scope, template, decision and licensing checks, together with §G5.3's dependency and final-integration boundaries. It is a review of this owner architecture, not implementation certification or certification of any sibling.

## Evidence read

- Complete Phase 13 owner document and its working-tree diff.
- `docs/design/v3/DESIGN.md`, all Part I globals and the complete Phase 13 assignment.
- `docs/research/v1/RESEARCH.md`, §§0–1, §4.6 texture portion, §9, Appendices B.3/B.4, D.3, E, F.3's const-option requirements and F.5.
- Complete declared-dependency §5 regions in `docs/phase3/v1/PHASE_3_DOC.md`, `docs/phase5/v1/PHASE_5_DOC.md` and `docs/phase7/v1/PHASE_7_DOC.md`. Additional incorporated reads included P3 §4.1's binary acquisition/sidecar-failure/lifetime rules and exact asset declarations, and P5 §§4.12–4.13's candidate dispatch, binding, preflight and lifetime rules.
- Relevant current grants and receiving contracts in P1 §5 and §4.7.7, P4 §5, P6 §5, P8 §5, and P14 §5.5. These reads assessed P13's consumption and handoffs only.
- `docs/decisions/U1_TEXTURE_SAMPLING.md` and `docs/decisions/TEXTURE_SIDECAR_DEFAULTS.md`, complete; the shipped custom-texture block at `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:78–125`; the permitted texture-only behavioral digest §8; and `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` §§7.4, 7.6, 11 and relevant §§17–18 rows.
- Published [Iris custom-texture documentation, .mcmeta section](https://shaders.properties/current/reference/buffers/custom_textures/#mcmeta-file), independently read for field meanings, source-specific defaults and resource/atlas exclusion. This was not treated as evidence of G6 runtime parity or authority for modern features.
- MCP 1.12.2 class details for `TextureMap` and `TextureAtlasSprite`, and the Forge texture-stitch event catalog. These corroborated the owner-listed sprite counters/metadata, atlas collections/mipmap field and Pre/Post event availability. The available local Cleanroom source tree is `reference-src/Cleanroom-0.6.12-alpha`; its two texture-class patches were read narrowly to check event placement and platform acquisition context. The historical `cleanroom-0.6.6-alpha` path was unavailable, and no old-version equivalence was inferred from that newer tree.

## Assessment

### 1. Mandatory architecture and milestone completeness

All thirteen required template sections are present and substantive. The complete v0.5 owner scope is architected: full companion atlases and mip chains; independent normal/specular macro preferences; post-vanilla animation; reproducible generated noise and pack override; pack PNG, Minecraft asset/live-resource and raw sources; all four raw targets; sidecars; required stage expansion; full-shape shared-unit candidates; actual-bound atlasSize delivery; publication, leases, resize and resource reload.

P13 §3 maps the in-scope contract into 44 explicit rows across source/sidecar/stage requirements, fixed units, texture behavior, uniform/options and hook/do-not-inherit ledgers. The mapped semantics agree with the selected RESEARCH and documented-mechanism authority. In particular, gbuffers expands to gbuffers plus shadow, composite to composite plus final, depthtex1 remains unit 11, generated companions remain world/shadow-only, and custom fullscreen replacements on units 2/3 are not accidentally prohibited.

P13 §§8–9 and §12 retain the full classic-matrix T3 implementation gate and MC_NORMAL_MAP rendering requirement. Earlier-milestone empty publications, unavailable real shadow execution, and optional optimizations are explicitly not substitutes for v0.5 completion. P13 has no assigned OQ and does not claim to resolve another owner's question.

### 2. Source acquisition and failure reachability

P13 §§4.3.2 and 5.2 consume an actual P3-owned same-load asset capability, not a digest standing in for bytes. P3 §4.1/D-P3-69 and §5 publish exactly the required acquisition domain, immutable manifest, independent read-only heap cursors, and Acquired/Missing/Unreadable/InvalidReference outcomes. P13 distinguishes missing primary data, impossible primary unreadability, invalid/cross-load references and positively classified optional-sidecar unreadability; it preserves the primary-before-sidecar ordering and P3's fatal safety/bounds/container duties.

The receiving side is present: P7 §5.2's TS-1 receipt retains the exact configuration/assets; §5.3 step 8 passes those inputs into P13 preparation; and §5.1's resource-NONE branch retains pack bytes while reacquiring resource-owner data under the new resource epoch. Thus the architecture provides a reachable post-archive texture preparation path without reopening a changed selected pack.

Schema21 and nested identity gates are current in P13 §§4.3.2/5.2. The documented TEXTURE_RECTANGLE token reaches P3's typed RECTANGLE, same-load raw acquisition, P13's Rectangle upload, and P5's compatible sampler2DRect selection. Illegal repeat/mipmap or integer-linear parameterization remains entry-local rejection, not hidden coercion.

### 3. Sampling policy, legality and identity

The U1 decision explicitly supersedes only the historical unspecified key-suffix requirement. P13 §§0.8/3.6/4.3.1/5.3 preserve numeric duplicate discriminators and the lossless unresolved declaration stream without inventing a second property parser.

The separate defaults/recovery decision supplies authority for PNG NEAREST/REPEAT, raw LINEAR/CLAMP_TO_EDGE and noise-override LINEAR/REPEAT. P13 §4.3.5 distinguishes omission from explicit false, defines strict bounded JSON and duplicate/type/encoding handling, and discards both overrides atomically on malformed/unreadable input. §4.5.1 specifies deterministic sidecar outcomes and the parameters/v2 cutover; recovery warnings are separate from source-legality failures in §6.

P1 §4.7.7/§5 supplies the complete mandatory synchronous parameter mapping adopted by P13 §5.5/D-P13-29. The six minification modes, both magnification modes, applicable wrap axes, neutral comparison/LOD/border/anisotropy fields, mip extent and color swizzle have a concrete mapping. Foreign objects are never passed to the owned setter. P5 §5.3, P7 §5.1's P13 receipt and P14 §5.5 consume the new effective meaning and identity rather than independently selecting defaults. No missing mandatory parameter grant was found.

### 4. Producer/receiver dispatch and closure

P13's candidate producer was traced into the consuming dispatcher, not merely to a matching type declaration. P5 §4.12.2 filters each exact-name cell by full sampler/storage capability, selects the greatest compatible custom ordinal, retains compatible-fallback diagnostics, detects alias conflicts and returns sixteen BoundObject/Unused rows. This accepts multiple target-specific candidates without silently collapsing them or changing fixed units.

P5 §4.12.3 owns physical binding and all four binding outcomes. P7 §5.2 consumes Bound/Degraded/Rejected/BackendFailed and the same opaque P4 selection; P8 §5.3 receives the equivalent full shadow operation. Bound alone transfers lease closure, non-transferring outcomes leave the caller responsible, and invalidation does not discharge closure duty. P13 §§4.5.2–4.5.3 agree with those rules and distinguish content identity from live generation/owner authority.

The current dependency corrections are received at their actual orchestration owner: P7 §5.2 handles P5's already-frame-aborted PassSnapshotResult.Failed; P7 §5.3 receives P8's minima-derived requested state and required pre-admission neutralization. P13 incorporates the current P7 transaction and P8 planning contract rather than defining a competing orchestration route. No silent-drop defect against this texture owner was established.

### 5. Atlas lifecycle and atlasSize

P13 §§4.1 and 4.6 assign the two deferred App E rows and separate stitch capture from post-vanilla animation snapshots. Immutable metadata preserves sequence order, frame indices, durations and interpolation; validation rejects stale snapshots, while invalid live state restores frame0 rather than freezing the last frame. Off-state behavior and borrowed-object nonmutation remain explicit.

P13 §4.4 distinguishes known atlas dimensions from evidence of current binding. The receiver exists in P7 §5.1's authenticated current-atlas adapter: it checks issuer/composition/resource epoch/serial, queries P13 metadata, maps Unknown/non-atlas to zero and delivers through P6's existing immediate-active/cached-inactive event. P6 §5.1 receives that route. Stitch metadata alone cannot authorize a uniform update, and old evidence cannot restore a retired value.

### 6. Authority and licensing

No required behavior is replaced by Pintonium's dynamic unit allocation, Random(0) noise, per-bound-object companion model, no-op atlasSize notifier, or generated-name source patching. Relevant decisions and contract checks remain visible in P13 §§3/11. Historical reviews and superseded requirement/schema statements are retained as history rather than re-raised as current defects.

Only the permitted texture digest was read from the proprietary behavioral reference; no transformation code was read or copied. No chatlogs, root transcripts or blocked Oculus pipeline/transform, libs or glsl-relocated contents were used.

## Notes — not owner corrections

1. **Current-byte dependency and integration gates remain.** P13 §5.4 accurately distinguishes adopted architecture from fresh verification. This PASS applies only to the frozen P13 owner. It does not certify P1/P3/P4/P5/P6/P7/P8/P14, clear IR-01, or replace the final all-owner integration review required by v3 §G5.3.
2. **Explicit compatibility assumptions remain evidence-limited.** P13 §4.1.4 preserves the mandated 0xFF7F7FFF literal and exposes the C-TX01 byte-order/flat-normal tension. §4.1.3 labels the mip-filter policy as overturnable, and §4.1.7 treats animation synchronization as a design requirement rather than observed behavior. These are disclosed governing-assumption boundaries, not newly discovered owner corrections. No pixel or animation parity is certified here.
3. **Optional execution remains separate.** P13 R4's post-analysis allocation optimization and P14 sampler/async/staging extensions remain genuinely ungranted or execution-gated as documented. The synchronous baseline is architected without them; their absence is not a missing required texture implementation design.

## Required changes and limitations

**Required owner changes: none. Required receiver corrections discovered by this review: none.** No files were edited and no validation commands, tests, builds, formatters, linters, runtime launches or conformance runs were performed. MCP/event evidence establishes available symbols and event contracts, not actual hook application or synchronized rendering. The optional reference-version gap above was disclosed rather than filled by assumption.

Within those limits, the frozen owner supplies a coherent, reachable architecture and satisfies its document gate. This is architecture readiness only, never runtime proof or final implementation clearance.