# Phase 3 — Independent Architecture Review 56

## 1. Reviewed artifact and review boundary

- **Target:** `docs/phase3/v1/PHASE_3_DOC.md`, complete document, including §§0–12, amendment history, incorporated §5 semantics and terminal notices.
- **Frozen SHA-256 supplied for this assignment:** `a5c2ea02a4817890ffcbf7a896f8db3ae9b022388def857a54962bef8962f158`. This identifies the assigned frozen artifact; no hash-validation command was run.
- **Report destination:** `docs/phase3/reviews/PHASE_3_REVIEW_56.md`.
- **Authority:** the target header explicitly selects `docs/design/v2.0-RC3/DESIGN.md`. Its historical v3 reading does not globally adopt v3 or replace the declared governing design.
- **Method:** independent whole-document §G1.2 review, not a patch-only check or confirmation of previous receipts. No source/document edits, builds, tests, formatters, linters, runtime experiments or validation commands were performed. No author transcripts, agent histories, chatlogs, repository-root text files or prohibited transformation sources were used.

## 2. Authority and evidence read set

The governing read set comprised RC3 Part I §§G0–G12, the complete Phase 3 specification and literal doc gate; `docs/research/v1/RESEARCH.md` §§0–1, 3.1–3.3, 3.5, 3.6.8, 3.7, 4.1 steps 2–3, 4.7, 7.5, the OQ-7 assignment, Appendix A.3, all of Appendix F and Appendix H; and `docs/MOVES.md` for path/version interpretation.

The direct dependency audit read `docs/phase1/v14/PHASE_1_DOC.md` §5 and its incorporated module/package, pure-engine, jcpp admission, capability, logging, diagnostic and debug semantics. In particular, D-P1-49 supplies an architectural jcpp dependency/closure/packaging grant with implementation pinning obligations; an old Phase 1 review is not proof that its current document or eventual dependency closure is verified.

The reference read set included `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` §7, §12 and §17 B1–B3/B12; the applicable licensing, retained findings and conflict dispositions in `docs/reference/oculus/v1.0/OCULUS_DESIGN.md`; all 489 lines of the shipped G6 `doc/shaders.properties`; and the relevant source-file, directive, macro, option, ID and texture-format sections of the shipped `doc/shaders.txt`.

Permitted source corroboration used the available `reference-src/Pintonium-main/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/` implementation: `JcppProcessor`, `PropertiesPreprocessor`, `IncludeGraph`, `ShaderPackOptions`, `OptionAnnotatedSource`, `ProfileSet`, `LanguageMap`, `DispatchingDirectiveHolder`, and the directive registration in `PackDirectives`. The historically cited `pintonium-9c2fcc1` checkout is not present at its recorded path; the current checkout was not represented as that historical revision. Its actual GPL-v3 license header was checked, consistent with the target's explicit D-P3-63 qualification. This review copies no implementation.

Cross-boundary gap checking additionally read the relevant receiving algorithms in current P2, P4, P7, P12 and P13 documents. The complete `docs/PHASE_INTEGRATION_REVIEW.md`, including original findings, Resolutions and follow-ons, was read alongside the recorded `U1_TEXTURE_SAMPLING.md`, `GEOMETRY_PRIMITIVE_COMPATIBILITY.md` and `TEXTURE_SIDECAR_DEFAULTS.md` decisions. Their policy rulings were distinguished from historical or unverified implementation claims. The native mixed-layout rule was independently checked against the published [ARB_geometry_shader4 specification](https://registry.khronos.org/OpenGL/extensions/ARB/ARB_geometry_shader4.txt), especially its OpenGL 3.2 dependencies section.

## 3. Literal doc-gate audit

| Gate | Independent assessment |
|---|---|
| Every Appendix F key and Appendix A.3 directive mapped | §§3.1–3.4 give producer meaning, receiving owner and test hooks. The detailed model and §5 supply actual typed projections rather than substituting unknown-key retention for implementation. Documented alias/family restrictions, texture forms, profile/screens, ID inputs and debug behavior remain represented. |
| Complete flag ownership | §3.1 assigns all seventeen F.1 flags: twelve to P7, two to P9, two to P10 and one to P8. The map does not misassign parsing as runtime execution. |
| Option-3-shaped identity with decision open | §4.4 separates standard, option, supported-feature, engine-identity, override and singular contributor families. §10 can exercise all three identity policies without replacing the preprocessor. OQ-7 is not declared resolved by Pintonium's identity choices. |
| Pure engine and jcpp | §§1–2, 4.5 and 5 place the frontend in the P1-authorized pure packages. Minecraft acquisition, GL compilation and runtime state remain outside it. jcpp is explicitly selected, with no public third-party API leak or invented platform seam exception. |
| B1/B2/B3/B12 each have a map row and named test | §3.4 explicitly names `directive_drynessWritesDryness`, `directive_legacyCommentFormsReachFields`, `optionRefsDoNotCrossWcc` and `propertyHashRoundTrip`; §§8/12 retain their intended behavioral coverage. The current permitted reference independently exhibits the corresponding miswired field, dead handlers, singleton component stub and hash stripping. These are designed tests, not claimed executions. |
| Reserved center-depth injection point | The singular `phase6.centerDepthSmoothRedirect` contribution is retained. P6's current Empty/CPU choice does not delete the extension point or create a second companion-macro producer. |

## 4. Substantive adversarial audit

### 4.1 Ingestion, physical identity and failure isolation

I traced discovery credentials through filesystem resolution, persistence-target acquisition and load, including invalid generations, supersession, completion-LRU eviction, direct-child names and same-bundle authentication. The contracts distinguish durable references from live candidate capabilities and reject mismatches before I/O. Off deliberately short-circuits unused request fields.

The source model separates one physical `SourceId` from contextual compilation roots. Include expansion precedes option rewriting and conditional processing; depth/cycle checks remain root-local and cannot be bypassed by include guards. Real component analysis does not broaden Appendix F.3's physical same-file switch confirmation.

The schema21 usability correction is active in the load algorithm and failure ladder: a usable base **or explicit override** can establish structural usability. Absent dimensions use base, overrides do not merge, and explicitly empty dimensions disable. This does not waive unsafe paths, bounds, required decode failures or include validation. I found no remaining base-only acceptance rule in the active contract.

### 4.2 Finalized options, persistence and locale lifetime

Catalog-issued complete states, exact catalog authentication and fresh-load application prevent GUI previews from silently changing requirements or already-issued materializations. Filesystem persistence has explicit acquisition/read/write outcomes and changed-only encoding; global persistence has a separate validated baseline-overlay matrix.

For Internal packs, the opaque snapshot is a session capability, not a forged filesystem target. P7 §§5.3 and the Internal acceptance algorithm consume both capture outcomes, map every failure, write changed globals before accepting the queue/session preference, retain accepted preference after a failed reload, and clear it at shutdown/bundle replacement. P12's committer route follows that same contract. No incomplete or foreign preview reaches runtime materialization.

The complete locale catalog has deterministic normalization, collision and per-key fallback rules. Explicit empty translations remain distinct from missing values. The payload-free `ScreenProfileEntry()` is routed by P12 §4.3.2 to `ProfileCycle`; profiles and preview inference, not an invented entry field, supply selection. Expanded column calculation includes retained layout slots and treats configured columns as a floor.

### 4.3 Preprocessing, geometry and declaration integrity

The jcpp wrapper specifies protected directive markers, first-significant version handling, final source attribution and lossless properties preprocessing. Its properties path does not inherit the inspected reference's hash/backslash corruption. Standard-only properties processing precedes the ratified user→pack→true old-light resolution, so effective shader macros require neither a runtime callback nor option-macro preprocessing of properties.

D-P3-68 provides a genuine native-preserving route, not a two-edit translator advertised as compatibility. The closed request/result forms, active pair cardinality, extension-state folding, layout classification, source witnesses and contribution rediscovery are specified. ARB's published mixed-mode rule independently supports source-per-property precedence over program parameters. P4 §§4.7–4.8 consume None/PreserveNative and None/CoreLayout/NativeLegacy explicitly, preserve all stages, require the actual extension on the native route, configure the API triple rather than replacing it with source-effective values, and fail the complete program on unavailable geometry. Linked-input agreement and draw-conversion authority remain separate owner contracts.

Final materialized text, source maps, language/geometry metadata and complete declared-uniform catalogs are paired by the current materialization fingerprint. Unsupported/unclassifiable active declarations do not become fabricated successful metadata, and declared-but-optimized-out uniforms are not lost through post-link-only inspection.

### 4.4 Directives, binary assets and downstream routing

I checked distinct wetness/dryness destinations, mandatory gdepth RGBA32F treatment, plain-versus-explicit attachment formats, positional DRAWBUFFERS `N` slots, clear/mipmap program-family rules, exact dimension/name requirement joins and unresolved ID/expression ownership. P3 publishes requirements rather than allocating buffers, resolving registries or evaluating custom expressions.

The documented raw token `TEXTURE_RECTANGLE` maps to the existing RECTANGLE type; P13 §4.3.3 follows that typed value through same-load byte acquisition, validation and Rectangle upload. Bare `RECTANGLE` is not silently accepted as a pack-language alias. Lossless texture declarations precede last-valid reduction, and unresolved keys never alias a valid base texture.

D-P3-69 closes acquisition and lifetime rather than merely publishing file paths: surviving owned primary assets and their adjacent-sidecar domain are snapshotted; acquired bytes have immutable shared storage and independent read-only cursors; available/missing/unreadable metadata participates in identity without capability cycles. P13 §4.3.2 explicitly handles Acquired, Missing, Unreadable and InvalidReference. Optional-sidecar-only I/O recovery cannot swallow primary, shader/include/configuration, safety, bounds or container failures. P13 owns the single recovery warning and effective sampling identity; P7 retains the exact configuration/assets for resource-only NONE. Neither consumer may reopen the host pack from an inspection manifest.

### 4.5 Inspection and integration evidence

Inspection is one real load with source-free projection and same-read archive provenance. The nine-tree codec excludes binary buffers and shader text. P2's actual receiving algorithm retains the source-bearing configuration only for same-request P4 enrichment, closes candidates in finally, obtains P5's pure resource projection and distinguishes incomplete evidence from complete negative capability evidence.

The latest P4 `ownBuild` disposition is consumed separately from effective fallback status. Its closed matrix preserves the distinction between intentional disablement and own compilation failure hidden by CHAIN; P2/P7 copy the required classification instead of inferring it from `sourcePresent`. This changes the separate evidence receiver, not P3's nine-tree snapshot meaning.

## 5. Scope, completeness, decisions and provenance

All thirteen required sections are present and substantive. §§6–7 give failure/thread/lifetime rules; §8 supplies observable boundary and conformance plans; §9 assigns every registered component a milestone; §12 provides ordered implementation work with test hooks. No mandatory v0.1 parser contract is replaced by a future UI/runtime task. Modern compute/RENDERTARGETS work remains explicitly staged.

The OQ-7 spike includes the question, fixed matrix inputs, three configurations, queried-macro/branch/compile evidence, classic and motion checks, unsupported-feature rejection, override-isolation checks, success/failure criteria and a conservative fallback. Its pending execution is not a missing architecture mechanism.

The session-only Internal, option-only supersampling, effective old-light and documented texture-mechanism changes have recorded authority. They are not silently attributed to Research or to unrelated default approvals. The provisional tag-selector and edition-comparison decisions publish concrete behavior and explicitly request upstream clarification; the inspected authority does not supply a contrary grammar that this review could substitute. No unstated renderer identity decision was inferred from these narrower rulings.

Licensing/provenance boundaries remain intact: jcpp is not justified using Oculus's inactive dependency stanza; retained Oculus findings do not establish 1.12.2 hooks; prohibited transformer and unverified expression code are not adopted; historical Pintonium licensing is not applied to the current checkout without qualification; and matrix/source-free/debug artifact policies remain distinct.

## 6. Findings and §5 impact

**Findings:** none established by this independent review.

- **Blocking:** 0
- **Correction:** 0
- **Note:** 0
- **New §5 correction required by this review:** no.

This review covers the target's current §5 and its incorporated semantics. It does not certify every sibling document, turn receiver receipts into independent reviews, verify the eventual jcpp pin/runtime closure, close IR-01 or authorize implementation. Separate current producer/receiver reviews, final integration and all runtime/OQ/implementation evidence gates remain independently applicable. Those existing gates are not counted as defects in a complete Phase 3 architecture contract.

## 7. Final verdict

**PASS**

The reviewed Phase 3 document satisfies its declared RC3 §G1.2 architecture gate. No actionable missing mechanism, unresolved required authority, contradictory receiving route or structural rebuild defect was established at the reviewed artifact.