# Phase 10 — Fresh whole-document architecture review 1

**Verdict: PASS-WITH-CORRECTIONS**

**Counts:** 0 structural blockers; 1 required owner correction; 3 notes. **section5Impact: true.**

## Scope and authority

Reviewed all 1,899 lines of `docs/phase10/v1/PHASE_10_DOC.md`, including §§0–12, its incorporated contracts, milestone staging, planned behavioral verification and historical/current adoption distinctions. The assigned frozen SHA-256 is `6c4fd1729e1c41c186e72445d8ddfe6d30fa651ba24825d883a5217effd22065`; Main independently confirmed that the current document matches that freeze. This reviewer did not run a hash or validation command.

The governing authority is `docs/design/v3/DESIGN.md`, Part I §§G0–G12 and the Phase 10 specification, together with `docs/research/v1/RESEARCH.md` §§0–1, §4.6, §7.4 and Appendix C, Appendix E rows 3–9, and the additional lighting, lifecycle and attribute contract sections necessary for the current owner. The narrow topology authorization is `docs/decisions/GEOMETRY_PRIMITIVE_COMPATIBILITY.md`. Dependency assessment followed P1 §§4.7.6/4.10/5, P4 §§4.8–4.9/5, P7 §§4.6/4.10/5.1/5.3 and P9 §§2.2–2.3/4.1/4.10/5, including their receiving dispatch, issuance, failure and retirement laws. Dependency inspection is not certification of those owners.

## Required correction

### R1-1 — Include vanilla sky/star cached lists in native submission coverage

**Severity:** correction. **Owner:** Phase 10. **Locations:** `docs/phase10/v1/PHASE_10_DOC.md` §4.6, §4.11 lines 1220–1226, and the incorporating §5.1/§5.3 health/lifetime contracts.

The new early geometry adapter and its explicit health subset cover chunk `RenderList` playback and cached `ModelRenderer` playback, but omit the independently cached vanilla sky and star lists. This is a concrete uncovered native route, not an unknown third-party renderer.

**Evidence and trace:**

1. In the [Minecraft 1.12.2 RenderGlobal source](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/renderer/RenderGlobal.java), `generateSky`, `generateSky2` and `generateStars` compile `glSkyList`, `glSkyList2` and `starGLCallList` when VBOs are disabled. The source ranges around lines 299–447 show `begin(7, DefaultVertexFormats.POSITION)` and separate `GL_COMPILE` envelopes. These are QUADS products. They are built from the RenderGlobal constructor and retained for subsequent frames.
2. The same source's `renderSky(float,int)` directly invokes these cached names: the upper-sky, star and lower-sky branches, plus the final lower-sky call, are visible around lines 1336–1515. Later calls do not invoke a Java uploader, `RenderList.renderChunkLayer`, or `ModelRenderer.render/renderWithRotation`.
3. P10 §4.11 identifies only `RenderList.func_178001_a` as H10-LIST-REPLAY and the explicit ModelRenderer methods as model playback receivers. Its newly enumerated v0.1/full health subset has no RenderGlobal sky-list producer or replay row.
4. `docs/phase7/v1/PHASE_7_DOC.md` §4.10.3 H-SKY-01 selects `gbuffers_skybasic`; H-SKY-03 wraps only sun/moon Tessellator draws. Section 4.10.8 expressly says the sky redirects are program/cancellation wrappers, not vertex interception. P7's §4.6 submission policy delegates native repetition to P10 adapters rather than supplying a catch-all list receiver.
5. Therefore, with VBOs disabled and an effective sky-basic provider whose linked geometry input is TRIANGLES, the cached QUADS call reaches GL without P10's compatibility selection or replay guard. The [ARB_geometry_shader4 specification](https://raw.githubusercontent.com/KhronosGroup/OpenGL-Registry/main/extensions/ARB/ARB_geometry_shader4.txt), Errors and issue 30, requires INVALID_OPERATION for QUADS under an active geometry shader. At v0.5 the same bypass also misses the promised adjacent countInstances submission boundary.
6. MCP independently resolves `RenderGlobal.generateSky` as `func_174980_p()V`, `generateStars` as `func_174963_q()V`, and live `renderSky` as `func_174976_a(FI)V`. The available Cleanroom RenderGlobal patch retains this vanilla surface while adding a separate custom-sky override branch; no patch shown introduces the missing native receiver.

**Required change:** Extend the bounded cached-product architecture to the three canonical RenderGlobal sky/star products and every live call site. Specify authenticated original/corresponding converted capture, source incarnation and off-first compilation, current effective geometry selection before playback, safe rebuild or containment before any incompatible call, deletion/replacement/resource/VBO-mode retirement, external restoration, and adjacent repetition of the prepared native call rather than sky traversal. Add exact milestone health rows and behavioral scenarios for these producers and receivers. Reuse the existing P1 facade and P7 authority; do not invent a raw-name adoption or alternate renderer API. An unproven route must close affected admission before GL, but permanent omission or forcing VBOs cannot complete the required two-path support.

**Receiver note:** P7 must receive the expanded owner10 health fingerprint and associated lifecycle obligations through its existing §5.1/§5.3 incorporation, just as it received R10-6. This is a receiving action consequent on the P10 correction, not a separate finding or certification of P7.

**Why §5 changes:** P10 §5 incorporates the native product/lifetime and exact hook-report contracts. Adding this missing canonical product family changes that monitored surface and its P7 receiving requirement; a fresh owner review is required after correction.

## Whole-owner assessment

The original Phase 10 doc gate is substantively represented: Appendix C has explicit conformance rows; CLASSIC_56 has numeric offsets, storage/delivery and all three identity components; the normal/tangent/midpoint formulas have distinguishing square and mirrored-UV examples; both required draw families and chunk-list capture are designed; both assigned OQs have procedures, success/failure criteria and fallback designs; and modern growth is descriptor-driven and not advertised prematurely. All thirteen template sections are present.

The builder/task design freezes layout and epoch, separates each builder's mutable stack, authenticates source formats instead of inferring stride from divisibility, preserves saved-state identity through translucent sorting, and rejects stale queued work before upload. P9's Present/Absent/Unrepresentable payloads and matched lookup/ordinal lifetime are explicitly consumed. P7's lifecycle start, Pending/Drained/Failed, prepare, activation and recovery branches have receiving actions, including retaining owners on incomplete drain and creating a new vanilla transition after an already-consumed activation token. None of those closed-result paths was found to have an additional current silent-drop defect.

The current R10-1, R10-2, R10-3, R10-5 and R10-6 grants and receipts are present. The P4 effective-provider geometry category reaches the P7 authenticated adapter and the P1 expected-versus-actual native comparison. The current schema21 receipt supersedes dated schema20/schema19 assertions; the earlier pending-P4 text and older schema decisions are historical, not additional active findings. P1's complete capture-array isolation and generic-zero rule are received rather than reimplemented as an independent P10 API. The remaining defect is coverage of another concrete canonical cached receiver, not an absence of those settled grants.

## Notes and limitations

1. **Architecture only.** No build, test, formatter, linter, runtime validation or native experiment was run; no file was edited. This review does not resolve OQ-5/OQ-14, establish transformed hook cardinalities, prove cache-toggle safety, prove driver list behavior or certify T2 parity. Their unrun implementation procedures are not architecture defects.
2. **Primary evidence versions remain distinct.** Historical paths `reference-src/cleanroom-0.6.6-alpha` and `reference-src/pintonium-9c2fcc1` are unavailable in this checkout. Available `reference-src/Cleanroom-0.6.12-alpha` patches and `reference-src/Pintonium-main` numeric source were read as separately identified corroboration, never relabeled as the historical snapshots or as the configured 0.6.10-alpha transformed runtime. The available tangent source confirms the T×N discrepancy that P10 explicitly rejects; the available LightUtil/BakedQuad sources corroborate retained format references and both static/default and pair-map cache domains. Historical evidence has not been rewritten. The vanilla source and MCP evidence above establish the missing architecture route, not the exact eventual injection cardinalities.
3. **Authority and final integration remain bounded.** The provisional dependency exception permits this architecture assessment, not implementation approval. No forbidden transcript, Oculus transformation boundary or transformation-library source was read or adopted. The constant-attribute decision and Pintonium numeric divergences retain their contract checks and licensing boundaries. Even after R1-1 is corrected and freshly reviewed, a Phase 10 PASS would certify only that frozen owner architecture; sibling reviews, applicable owner/receiver gates and final IR-01 clearance remain independent.

## Conclusion

The document has a coherent, fixable architecture rather than a structural miss requiring rebuild, so FAIL is not warranted. It cannot receive a fresh PASS while its native geometry health model omits vanilla RenderGlobal cached sky/star replay. Resolve R1-1, update the incorporated §5 receiver contract, and obtain fresh review without claiming implementation or integration success.

## Resolutions

### R1-1 — Canonical sky/star coverage (2026-09-08)

Resolved in the owner architecture by D-P10-24, §§0.8/4.6/4.11/5/6/8/9/11/12.
The existing authenticated bounded-product architecture now covers upper sky, lower
sky and stars from their exact generation methods, including constructor/off-first
creation, complete staging before list opening or VBO reset, paired original/converted
native products and final-use retirement. Every four list and three VBO playback
anchor is named, including both lower-list placements and the final unconditional
list site's known absent sentinel in VBO mode. Current effective geometry preflight,
external restoration and v0.5 adjacent native copies occur without a second sky
traversal, source-discovery playback or raw-name authority.

Seventeen explicit CORE owner10 rows cover generation, seven playback sites and
single/range/VBO deletion, VBO mode, resource, world and delete-all boundaries.
R10-7 publishes exact P7 receiving fingerprint and step1/4/9/10 obligations; this
owner-only correction does not claim P7 has received them. Planned behavioral/native
scenarios and milestone/checklist rows cover all products, off-first reuse, topology,
count ordering, failures, deletion/reuse and restoration. D-P10-25 also receives
schema22/current-constant admission and qualifies earlier numeric receipts as historical.

Grounding: canonical RenderGlobal source, MCP 1.12.2 exact method descriptors and the
separately identified Cleanroom 0.6.12-alpha custom-sky patch; no copied transformation
code. No implementation, validation commands, tests, builds, formatters or linters ran.
The original review body/verdict is preserved. **§5 changed; fresh owner/receiver
review remains required.** No PASS, runtime parity or implementation clearance claim.