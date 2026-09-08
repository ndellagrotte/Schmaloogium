# Phase 3 Verification Review — Round 55

## 1. Review identity, authority, and scope

**Target:** `docs/phase3/v1/PHASE_3_DOC.md`, current through §0.62 / D-P3-69, schema 20.

**Governing design:** `docs/design/v2.0-RC3/DESIGN.md`, Part I and the complete Phase 3 specification, as declared by the target header. The additional v3 Part I, mandatory-template, Phase 3, and doc-gate selectors recorded in the target’s required-input history were read as verification context; this review does not adopt v3 for Phase 3 or change another phase’s governance.

This is an independent, read-only, whole-document architecture review, not a review limited to the latest amendment. The complete target, Round 54 and its Resolutions, the required Research sections and appendices, PD §7/§12/§17 B1–B3/B12, OD §§3–4/§§12–17, and the shipped pack-author documentation were read. The current Phase 1 §5 producer contracts and their relevant module, capability, diagnostics, and jcpp supporting sections were checked. **Phase 1 Review 28’s literal PASS is accepted as current**, not reopened by this review.

Actual receiving contracts were followed beyond P3’s outbound table: P2 inspection; P4 materialization and native/core dispatch; P5 resource planning; P6 declaration/contribution consumption; P7 load, Internal-session and resource-refresh composition; P8/P10 indirect projections; P9 ID parsing; P11 expression declarations; P12 screens/options; and P13 binary acquisition and texture preparation. These boundary checks are not whole-document certifications of those receivers.

No documents were modified. No builds, tests, linters, validation commands, or runtime experiments were run.

## 2. Findings

### R55-1 — Preserve usable dimension overrides in structural-load classification

**Severity:** correction. **Priority:** P2.

**Locations:** `docs/phase3/v1/PHASE_3_DOC.md:3766`; repeated at `:4144–4146`. Contradictory producer rule: `:3010–3015`; receiving dimension/source contract: `:3600–3614`.

**Claim under review:** A safely read input is `STRUCTURALLY_UNUSABLE` when no **base** shader source configuration survives mandatory decode/include validation.

**Evidence and impact:** Section 4.10 explicitly admits a pack with “at least one usable base **or explicit dimension mode**.” The binding dimension contract gives a nonempty world folder its own `OVERRIDE` roots, explicitly forbids merging those roots with base sources, and permits absent world keys to select the separate base entry. Thus a safe pack containing valid `shaders/world-1/gbuffers_basic.vsh` and `.fsh`, but no base shader roots, satisfies the producer’s structural condition and has a usable override. The exhaustive public failure matrix nevertheless classifies that same input as `STRUCTURALLY_UNUSABLE`; the following failure-order contract returns no configuration and leaves shaders off. P4 §4.7 selects an exact `DimensionConfiguration`, and P7 receives the same no-merge dimension model, but neither can reach the valid override after that whole-load failure. The contradiction is therefore at the producer’s public success/failure boundary, not an unavailable-program fallback policy.

**Required correction:** Define structural usability consistently across §§4.10/5/6 so surviving valid override source configurations are not rejected merely because the base source set is empty. Retain the ordered unsafe/bounds/unreadable failure precedence and the existing source-local failure policy. Include a dimension-only valid pack and a genuinely unusable pack in the specified boundary cases.

**§5 impact:** **Yes.** The exhaustive `PackLoadFailureCode` cause matrix is a binding cross-phase contract. Update the receiver-facing success/failure meaning and its incorporated references together; do not patch only the explanatory §4.10 sentence.

### R55-2 — Define the documented rectangle token’s producer mapping

**Severity:** correction. **Priority:** P2.

**Locations:** `docs/phase3/v1/PHASE_3_DOC.md:870` and `:3446–3448`; parser/disposition rules at `:2790–2818` and §5.1’s lossless declaration reducer.

**Claim under review:** The closed raw texture target vocabulary is `TEXTURE_1D|TEXTURE_2D|TEXTURE_3D|RECTANGLE`, while the document claims all four documented raw source forms are parsed.

**Evidence and impact:** The authoritative shipped author specification, `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:103–109`, names the fourth external token **`TEXTURE_RECTANGLE`**, not `RECTANGLE`. The current P3 document never names that external token or gives a mapping from it to `TextureTarget.RECTANGLE`. Its executable grammar is closed, and rejected known-key source values become `INVALID_VALUE`, which the reducer never publishes as a `CustomTextureSpec`. For example, the documented source form `textures/lut.dat TEXTURE_RECTANGLE RGBA8 16 16 RGBA UNSIGNED_BYTE` therefore has no specified valid producer route under the published vocabulary. This is not repairable by P13: P13 §§4.3.2–4.3.3 consume already-typed `Raw` values and their target, and explicitly leave source grammar to P3. Under D-P3-69, omission from the surviving executable declaration also omits the primary path from the declared asset domain. U1’s approved removal of unspecified sampling-key suffix requirements does not alter raw target spellings.

**Required correction:** Specify the exact case-sensitive external raw target tokens and explicitly map `TEXTURE_RECTANGLE` to the existing `RECTANGLE` representation, just as the other three tokens map to their existing variants. State whether bare `RECTANGLE` is invalid rather than leaving an undocumented alias. Cover the documented spelling, arity, and rejection boundary in the raw-source vectors. No new suffix grammar is needed.

**§5 impact:** **Yes for the published lexical clarification**, because §5.1 currently gives the complete target domain used by the source-form contract. The existing enum and P13 `Rectangle` upload variant can remain unchanged. A spelling-to-existing-value clarification should not be turned into an unnecessary record redesign; apply §5.3’s schema rule if the chosen repair changes an already-established public meaning instead.

### R55-3 — Represent the profile selector without an invented profile identity

**Severity:** correction. **Priority:** P2.

**Locations:** `docs/phase3/v1/PHASE_3_DOC.md:980–985`; incorporated options/screens contract at `:3172` and `:3182`; producer interpretation at `:2845–2856`.

**Claim under review:** A parsed profile-selection screen entry is `ScreenProfileEntry(ProfileName profile)`.

**Evidence and impact:** RESEARCH Appendix F.4 and `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:290–297` define the literal `<profile>` entry as a selector, with no profile-name operand. Current selection is inferred from option values and may be `Custom`. The actual receiver confirms this: P12 §4.3.2 maps the entry to `ProfileCycle`, §4.5.1 gets its current value from `OptionConfiguration.inferProfile(previewState)`, and §4.5.2 cycles the complete declaration-ordered profile list. P3 nevertheless requires one non-null `ProfileName` in the frozen screen entry and specifies no source or meaning for it. A screen containing `<profile>` with two declared profiles and a Custom option state cannot supply a selected profile from the input; choosing the first profile, an inferred load-time profile, or a fabricated `Custom` name would each add semantics not present in the contract. P12 has no consuming use for that extra identity, while P3’s generic configuration fingerprint and inspection codec retain record components, making an arbitrary producer choice observable in otherwise equivalent configurations.

**Required correction:** Make the selector entry payload-free, leaving profile definitions in `OptionConfiguration.profiles()` and selected/custom state in `ProfileInference`. Update the incorporated §5 model and the P12/P2 receiving description so there is one unambiguous producer-to-selector route. Do not fix this with a fabricated profile name or by freezing the UI’s current selection into the screen declaration.

**§5 impact:** **Yes.** Section 5 explicitly incorporates the §2.2 declarations as binding, not illustrative. Removing the component changes the nested configuration graph and requires the next schema under §5.3, matching nested ID and inspection schemas, and a coordinated receiver cutover. The existing shape-generic inspection codec does not waive that configuration-schema change.

## 3. D-P3-69 / TS-1 seam audit

The current binary acquisition amendment is substantively connected to its actual receivers; no additional demonstrated TS-1 finding is raised.

1. **Owned immutable acquisition:** P3 §2.2 and §4.1’s D-P3-69 block (`:1833` onward), incorporated by §5.1 (`:3166`), publish required same-load `assets`, canonical metadata, and closed `Acquired|Missing|Unreadable|InvalidReference` results. Acquired bytes expose independent read-only heap cursors, not reopened paths, archive streams, or mutable arrays. Empty readable content remains distinct from missing/unreadable content.
2. **Bounded, narrowly deferred failures:** Deferred unreadable markers require safe individual indexing, trustworthy enumeration/container state, and accounted bounds. Classification occurs after source/include/configuration and surviving asset duties are known. Only an optional owned-sidecar-only failure survives as `UNREADABLE`; primary, shader, include, configuration, locale, ID, unsafe, bounds, and container failures retain fatal handling. The amendment does not grant generic per-file I/O recovery.
3. **P13 consuming dispatch:** `docs/phase13/v1/PHASE_13_DOC.md:888–931` explicitly handles every acquisition variant. Primary bytes are validated before sidecar interpretation. Missing primary is source-local unavailable; an impossible primary unreadable result or invalid reference is identity failure. A present sidecar accepts Acquired or authenticated Unreadable; present-reference Missing is identity failure. Unreadable sidecar recovery is atomic, emits one P13 warning, and retains the prescribed outcome without inventing a partial-byte digest.
4. **Retained-load lifetime:** P7 D-P7-33 and §5.2 retain the exact containing configuration/assets through preparation, dimension metadata, and resource-only `NONE` refresh. A resource epoch does not relabel pack bytes. Only a full pack load replaces the retained pack snapshot. The P13 receiver independently states that host deletion or replacement cannot change prepared content.
5. **Identity and inspection:** P3’s acyclic bytes → pack identity/manifest → configuration → materialization chain hashes canonical asset metadata once without hashing capability identity or a fingerprint back-reference. P2 §4.11.4, D-P2-32, receives all **nine** source-free trees, mapping `assets` to `[properties] owner.assets`. Its explicit metadata-field dispatch preserves zero/absence and availability distinctions; digest strings use the owner’s generic TextHash rule. No binary cursor, provider, source text, or reconstructed capability reaches the golden writer. Schema 20, nested ID schema, and snapshot schema agree; projectionVersion remains 1.

## 4. Whole-frontend and receiver audit

- **Discovery and ingestion:** The authenticated bundle/discovery/selection model, deterministic ordering, no-follow containment, archive lease closure, bounded eager snapshot, durable external references, and engine-only Internal provider remain distinct from downstream resource-manager access. Finding R55-1 is the demonstrated structural-usability exception.
- **Options and persistence:** Same-file switch confirmation, real weak-component merging, ambiguity handling, source-span rewriting, safe codec access, finalized-state-only materialization, locale normalization and empty-preserving per-key fallback, and Internal session capture/rebinding are designed as one atomic load. Preview state is not passed to runtime materialization. Finding R55-3 is the demonstrated profile-screen publication gap.
- **Preprocessing and directives:** The target retains separate shader and Properties adapters; the latter protects Properties values rather than inheriting Pintonium’s `#` stripping. Appendix A.3’s syntactic forms and field mappings, B1/B2/B3/B12 dispositions, the corrected buffer-name normalizer, canonical requirements, and full final uniform-catalog publication remain represented. Geometry-source preservation is not treated as a successful native draw.
- **Macro ownership:** Required independent normal/specular inputs precede same-build shader preprocessing. A–G Properties/ID environments remain separate from shader option macros and GLSL intrinsics. OQ-7 remains configurable architecture with final identity policy deferred. The Phase 6 reserved contributor remains singular, and its current receiver deliberately supplies Empty.
- **Native/core receiving path:** P4 §§4.7–4.8 and §5.3 consume `None|PreserveNative` requests and all materialized geometry forms, merge final uniform catalogs before GL, retain source-local unavailability as whole-program failure, and compare owner-provided actual linked geometry input before publication. No missing branch or geometry-only retry was found at that boundary.
- **Remaining published models:** P5 consumes typed resource requirements, P9 consumes the exact nested current-schema ID model with ordinary/forced-11300 branches, and P11 consumes ordered duplicate-preserving custom-expression declarations without source reopening. P8/P10 receive their designated resolved projections through P7. Schema20 receipts do not grant those phases binary-decoding authority. Finding R55-2 remains producer grammar, not a request for P13 to parse raw property text.
- **Template and staging:** Sections 0–12 are substantive. Appendix F flag ownership, milestone tags, failure ladder, headless plans, the OQ-7 procedure/criteria/fallback, and the named PD pitfall tests are present. These are architectural plans, not executed tests or matrix evidence.

## 5. Evidence limits and historical resolutions

Round 54’s four recorded repairs were checked against current content: the review ledger update; fixed load-failure diagnostic payloads; closed, type-tagged diagnostic argument encoding and overflow behavior; and exact canonical/legacy color-buffer normalization. They are present. Its historical verdict and reconciled writer checkpoint do not certify the later schemas 15–20.

The reference snapshot available here is `reference-src/Pintonium-main`, with project metadata `2.4.1-dev`; it is not the absent revision-named `pintonium-9c2fcc1` tree. It has no local Git revision metadata available for establishing that historical commit, and its root GPLv3 text and LGPL-3.0 project/file notices require care rather than a blanket historical-license assertion. Bounded permitted frontend files were surveyed for mechanics only—include graph, shader/Properties preprocessing, locale loading, and profile-menu structure. No code was copied, no current snapshot behavior was relabeled as historical 9c2fcc1 proof, and no forbidden transformer/Oculus boundary, chatlog, root transcript, or OptiFine decompile was read.

Published jcpp 1.4.14 [POM](https://repo.maven.apache.org/maven2/org/anarres/jcpp/1.4.14/jcpp-1.4.14.pom), [Preprocessor API](https://javadoc.io/static/org.anarres/jcpp/1.4.14/org/anarres/cpp/Preprocessor.html), [Token API](https://javadoc.io/static/org.anarres/jcpp/1.4.14/org/anarres/cpp/Token.html), and bounded [published implementation](https://javadoc.io/static/org.anarres/jcpp/1.4.14/src-html/org/anarres/cpp/Preprocessor.html) were inspected. They do not themselves implement P3’s GLSL numeric-file/line and full expansion-provenance promises: the candidate’s native `__FILE__` is a string, `__LINE__` uses its token cursor, and its ordinary PP_LINE handling skips the directive. P3 explicitly assigns adaptation to its wrapper and retains an implementation admission gate, so this is not counted as a proven architecture defect or silently declared impossible. The exact selected release, dependency closure, packaging, containment, intrinsic adaptation, and origin-mapping behavior still require implementation evidence; API inspection is not that evidence.

`docs/build/READINESS.md` remains the runtime/build evidence ledger. Its successful template tasks with NO-SOURCE tests are not a Phase 3 parser result. Its client launch created an NVIDIA GL context and then failed before the menu at the recorded JEI/HEI classloader split. Nothing in this review upgrades those observations to renderer, native geometry, texture, G6 parity, or seven-pack golden proof. The explicit U1, sidecar-default, and conditional geometry-primitive decisions are applied only within their recorded scope.

## 6. Verdict and gate status

# PASS-WITH-CORRECTIONS

**Counts:** blocking=0; corrections=3; notes=0.

**§5 impact:** yes. R55-1 changes the public structural-load classification; R55-2 closes the published raw target spelling route; R55-3 changes an incorporated nested record and therefore requires the next configuration schema and coordinated receiving cutover. These are localized architectural repairs, not structural misses requiring a rebuild, so FAIL is not warranted. They prevent literal PASS for the current target.

The schema20 asset/sidecar/inspection amendment has a complete producer-to-receiver design path, but it does not erase the independently demonstrated frontend defects. Phase 1 R28 remains current PASS. Phase 3 remains unverified for dependent implementation until these corrections are recorded and a fresh whole-document review covers the changed §5 surface. Current receiver receipts are adoption evidence, not their individual certifications. **All fourteen applicable phase verifications, IR-01 closure, and final §G5.3 integration review remain required. This report grants no implementation clearance.**

## Resolutions

### R55-1 — Structural-load classification (2026-09-08)

Architecture fix-up D-P3-70 / §0.63 changes §§4.10/5.1/6 to accept a shader source
configuration surviving mandatory decode/include validation in the base **or any explicit
OVERRIDE**. A valid dimension-only pack retains its empty base plus usable override; absent
world keys still select the base, overrides never merge, and empty disabled folders alone are
not usable sources. Existing unsafe → bounds → unreadable → structurally-unusable priority,
source-local failures and D-P3-69's narrow optional-sidecar exception remain intact. §8.1
specifies dimension-only, surviving-unrelated-source, genuinely unusable and competing-failure
boundaries. P7/P4 receiving obligations are explicit in §§5/11.

### R55-2 — Documented rectangle spelling (2026-09-08)

§§4.8/5.1 map exact case-sensitive external `TEXTURE_1D`, `TEXTURE_2D`, `TEXTURE_3D`,
`TEXTURE_RECTANGLE` to existing `TextureTarget.TEXTURE_1D`, `.TEXTURE_2D`, `.TEXTURE_3D`,
`.RECTANGLE`, respectively. Dimensions have exact positive-integer arities 1/2/3/2;
total source token counts are 6/7/8/7. Bare `RECTANGLE`, case variants, unknown targets and
missing/extra operands are invalid, preserving `INVALID_VALUE` and last-valid reduction.
A surviving documented rectangle `Raw` retains its D-P3-69 declared asset-domain route to
same-load acquisition by P13. The enum/upload algebra and suffix grammar do not change.
§8.1 specifies the documented rectangle acquisition success and lexical rejection boundaries.

### R55-3 — Payload-free profile selector and coordinated schema (2026-09-08)

§2.2 now declares `public record ScreenProfileEntry() implements ScreenEntry {}`.
§§4.8/5.1 retain literal `<profile>` independently of definitions or inferred Custom state;
definitions remain in `OptionConfiguration.profiles()`, and current/preview selection uses
the existing `inferProfile` / `ProfileInference` contract only. P12's `ProfileCycle` handoff
and P2's zero-component `$type`-only record projection are explicit. No name, selected state,
old constructor/accessor or decoder shim is permitted.

Under §5.3 the nested shape requires schema21, matching containing configuration,
`IdMappingInput` and `PackDecisionSnapshot`; active gates use exact `CURRENT_SCHEMA_VERSION`
equality before derivation/retention/enrichment/serialization. §4.10 moves materialization
identity to `MaterializedSource-v21`. D-P3-69 assets, all nine source-free trees and generic
`projectionVersion=1` encoding retain their meanings. §§5.3/11.4 publish each consumer's
dated schema21 receipt obligations, without claiming sibling adoption complete. §8.1 includes
zero/multiple-definition, named/Custom inference, source-free projection and incompatible-schema
boundary plans.

These are architecture resolutions, not executed tests or independent verification. Only the
owned Phase 3 architecture and this append-only section were changed; the original review,
evidence and **PASS-WITH-CORRECTIONS** verdict above remain intact. No build, test, linter,
formatter or validation command ran. Changed §5 requires a fresh independent whole-document
review; consumer receipts/reviews, IR-01, all applicable phase gates and final integration
remain required. No self-PASS or implementation clearance is granted.
