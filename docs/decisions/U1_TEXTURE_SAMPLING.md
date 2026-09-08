# U1 — custom-texture sampling authority correction

**Date:** 2026-09-07. **Status:** maintainer-approved narrow scope correction; architecture adoption and verification are separate. No implementation or conformance result.

## Decision and exact authorization

After independent published-document investigation and presentation of the evidence, the maintainer selected **“Correct requirement to documented mechanisms”**:

> Authorize a dated, narrowly scoped authority correction adopted by affected phases: retain .0–.9 duplicate discriminators and .mcmeta blur/clamp; remove the requirement for unspecified filter/wrap key suffixes. Preserve historical text and reviews. This does not approve unrelated defaults or modern features.

This decision resolves the authority question in `docs/phase3/v1/PHASE_3_DOC.md` §11.5 item 3 and `docs/phase13/v1/PHASE_13_DOC.md` §11.5 U1 through their expressly requested **design-correction route**, not by supplying a new suffix grammar.

The affected statements in `docs/design/v2.0-RC3/DESIGN.md` Phase 3/Phase 13 and `docs/design/v3/DESIGN.md` Phase 3/Phase 13 concerning filter/wrap property-key suffixes and “ours must honor them” are superseded **only to that extent**. Their original text, the research document, mining reports, prior reviews and previous integration dispositions remain historical evidence and are not rewritten. This does not globally adopt a different design revision for any phase.

## Evidence and limits

1. **Published OptiFine author documentation**, read 2026-09-07: [shaders.properties, custom-texture section](https://raw.githubusercontent.com/sp614x/optifine/master/OptiFineDoc/doc/shaders.properties). The GitHub file-history endpoint reports latest file revision [`07ec2ca62f81aa52cb9ed1a7fd49a4489c131b1e`](https://github.com/sp614x/optifine/commit/07ec2ca62f81aa52cb9ed1a7fd49a4489c131b1e), dated 2026-01-20. The document says: “The suffixes \".0\" to \".9\" can be added to <name> to avoid duplicate property keys.” Separately: “Wrap and filter modes can be configured by adding standard texture \".mcmeta\" files”, giving a raw-texture sidecar example. These statements establish two distinct mechanisms, not a filter/wrap key-suffix grammar.
2. **Published Iris author documentation**, read 2026-09-07: [Custom Textures, .mcmeta File](https://shaders.properties/current/reference/buffers/custom_textures/#mcmeta-file). It defines the sidecar `texture.blur` and `texture.clamp` booleans: bilinear versus nearest filtering and clamping versus wrapping. It separately describes image/raw defaults and excludes shader-provided sidecars for resource-pack/atlas textures. This is modern author documentation, not by itself proof of G6 runtime parity or permission to import modern features.
3. **Governing research**, `docs/research/v1/RESEARCH.md` Appendix F.5: numeric discriminators, three texture-source forms, sidecars and noise are already part of the recorded contract. It supplies no filter/wrap property-key suffix grammar.
4. **Recorded independent bounded investigation:** the shipped G6 author documentation (`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties`, custom-texture section) makes the same duplicate-key/sidecar distinction. No decompiled implementation was consulted. The mining report's “pack PNG with .mcmeta blur/clamp” is corroborating historical evidence, not an executable suffix specification.

Absence of grammar in these sources is not a universal claim that no pack or engine extension has ever used such syntax. It is the evidence presented for the maintainer's explicit correction of this project's requirement. No proprietary implementation code, OSS implementation code, shader-pack contents or images are incorporated here.

## Binding scope after adoption

- Preserve the existing `texture.<stage>.<sampler>[.0–.9]` numeric-discriminator contract and supported source forms. A numeric discriminator is not a filter/wrap instruction.
- Preserve documented `.mcmeta` `blur`/`clamp` support. Phase 3 owns property-key/source parsing and lossless declaration publication; Phase 13 owns sidecar interpretation and effective texture parameters; Phase 5 retains physical-binding ownership.
- Do not invent, parse, advertise or require unspecified filter/wrap property-key suffixes. Extra/unknown key segments retain the current Phase 3 diagnostic/declaration handling; retaining them does not mean they execute.
- Remove U1's demand for a future required suffix parser, typed suffix-sampling API and associated suffix-conformance gate. No new payload, alias, fallback parser or schema upgrade is authorized by this correction alone.
- Do not use this decision to ratify unrelated defaults, malformed-sidecar behavior, source-kind applicability beyond the existing contract, new texture forms or modern-stage support. Those remain subject to their own evidence and review. In particular, modern Iris image/raw defaults must not be silently conflated with the phase-local noise or malformed-sidecar policy.
- Required classic texture behavior and the v0.5 full-classic-matrix T3 gate remain. This is a narrow removal of an unspecified syntax requirement, not removal of documented sampling behavior or a claim of conformance.

## Adoption and verification boundary

Affected owners must record phase-local decisions and update active scope, interfaces, incorporated semantics, acceptance cases and handoff ledgers. Historical addenda and reviews retain their original statements. The integration review receives a dated follow-on rather than a rewritten original finding.

Fresh whole-document reviews and the final integration review remain required. IR-01 is not waived; native-source completion, jcpp admission, vertex/lifecycle grants and applicable runtime experiments remain independent prerequisites. This decision supplies policy authority only.
