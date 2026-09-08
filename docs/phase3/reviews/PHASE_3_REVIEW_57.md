# Phase 3 Architecture Review — Round 57 — Attempt 5

## Frozen owner and disposition

- **Owner:** `docs/phase3/v1/PHASE_3_DOC.md`.
- **Frozen SHA256:** `c38a18d613248765716a4fae3dd7d9b8b319eb29cebe98d655acd0a097562608`, as recorded in `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_5.json:15–19`.
- **Scope:** fresh independent review of the entire current owner, sections 0–12, including incorporated declarations, failure semantics, fingerprints, testability, staging and handoffs—not merely the schema22 amendment.
- **Result:** one required, locally repairable contract correction; one nonblocking evidence note. **Section 5 is affected.** No structural rebuild is indicated.

## Authority and independent reading

The selected governing design remains `docs/design/v2.0-RC3/DESIGN.md`, Part I and Phase 3 specification, as stated at owner lines 7–9. I read those sections and the separately recorded `docs/design/v3/DESIGN.md` verification selectors without treating that read as governance adoption. `docs/MOVES.md` was used for historical citation resolution. Governing research reviewed was `docs/research/v1/RESEARCH.md` §§0–1, 3.1–3.3, 3.5, 3.6.8, 3.7, 4.1, 4.7, 7.5, OQ-7, Appendix A.3, Appendix F and Appendix H; modern compute/routing passages were also checked for staging.

Dependency review covered `docs/phase1/v14/PHASE_1_DOC.md` §5 and the incorporated package/seam, capability-profile, jcpp admission, logging and diagnostic declarations. Reciprocal checks followed actual consuming algorithms in the current versioned Phase 2, 4, 5, 6, 7, 9, 11, 12 and 13 owners, with current-schema receipts checked for the other affected receivers. These checks establish evidence for this owner's contracts, not certification of those siblings.

Independent reference checks included the permitted Pintonium report sections and narrow available source corroboration, `docs/reference/oculus/v1.0/OCULUS_DESIGN.md` §§3–4 and 12–17, the shipped G6 `doc/shaders.properties`, and the assigned directive/macro/ID portions of `doc/shaders.txt`. Its explicit block-matching reference required additionally reading shipped `doc/properties_files.txt`; that exposed the correction below. No OptiFine decompile or prohibited transformation implementation was read. The approved U1 and separate sidecar-default decisions were read independently.

## Independent checks

1. **Acquisition and lifetime.** Reviewed selection-first Off behavior, bounded discovery/authentication, durable candidate references, safe persistence, atomic load failures, base/explicit-override structural success and snapshot closure. Same-load binary capabilities remain separate from paths, hashes and inspection metadata. P13's actual acquisition dispatch at `docs/phase13/v1/PHASE_13_DOC.md:896–946` handles Acquired/Missing/Unreadable/InvalidReference without reopening; P7 retains the exact configuration across resource-only NONE at `docs/phase7/v1/PHASE_7_DOC.md:3487–3505`.
2. **Preprocessing and options.** Checked include-before-preprocess ordering, same-file switch confirmation plus real component isolation, source attribution, marker protection, A–G-only properties/ID environments, once-only properties interpretation, immutable catalog-bound option state and the empty Phase 6 contribution. Available Pintonium source independently corroborates the WCC stub, comment-handler no-ops, half-life misassignment and destructive properties preprocessing that P3 explicitly avoids. The owner preserves the named B1/B2/B3/B12 evidence plans rather than inheriting those behaviors.
3. **Presentation and approved scope.** The full locale catalog, empty-preserving per-key fallback, payload-free profile selector, session-only Internal state, ratified old-light resolution and option-only supersampling remain distinct contracts. P12's entry dispatch maps `ScreenProfileEntry()` to its existing ProfileCycle and derives inference separately (`docs/phase12/v1/PHASE_12_DOC.md:545–623`). P7's Internal acceptance/load path authenticates capture and rebinds through a fresh catalog (`docs/phase7/v1/PHASE_7_DOC.md:3257–3289`). The U1 decision does not invent filter/wrap suffix syntax or ratify unrelated defaults.
4. **Source/resource publication.** Reviewed the complete directive/resource algebra, positional draw-routing holes, format defaults and gdepth force, final structural uniform declarations, lossless custom-expression/texture occurrences, and acyclic configuration/materialization identities. P4 materializes through the owner, merges final catalogs and preserves direct resource projections (`docs/phase4/v1/PHASE_4_DOC.md:1269–1310`). P11 consumes ordered declarations without another properties parser (`docs/phase11/v1/PHASE_11_DOC.md:1135–1150`).
5. **Native geometry.** P3's None/PreserveNative requests have explicit receiver handling; unsuccessful geometry does not become a vertex/fragment-only retry. P4's dispatch, extension gate, unchanged native API triple, per-property source precedence and linked-input agreement are explicit at `docs/phase4/v1/PHASE_4_DOC.md:1410–1461`. The source-precedence distinction was independently corroborated against the published [ARB_geometry_shader4 specification](https://registry.khronos.org/OpenGL/extensions/ARB/ARB_geometry_shader4.txt), “Dependencies on OpenGL 3.2”; source preservation is not runtime/link success.
6. **Schema22 dual-era flow.** Owner `docs/phase3/v1/PHASE_3_DOC.md:2974–3032` publishes two independently bounded parses of the same copied bytes, replacing only MC_VERSION for the alternate. Ordinary state/CLASSIC provenance is unchanged; alternate membership alone means MODERN; ITEM/LAYER alternates remain empty. P9's actual dispatch at `docs/phase9/v1/PHASE_9_DOC.md:603–619` selects an alternate only for PRESENT_EMPTY plus nonempty alternate, independently per contribution, without merging or registry-match heuristics. Its modern grass/lamp handling at lines 567–575 consumes that explicit provenance. The owner's concrete conditional fixture and controls at lines 4317–4350 trace genuine parser production to resolution, while remaining honestly unexecuted plans.
7. **Inspection and exact-current admission.** P2 explicitly dispatches all nine owner trees and keeps assets metadata-only (`docs/phase2/v2/PHASE_2_DOC.md:1982–2093`). Containing configuration, nested ID input and inspection identity are checked against current schema22 before derivation/reuse. MaterializedSource-v22 remains separate from unchanged projectionVersion1 and the nine-tree inventory. Superseded numeric receipts were not reported as current defects.
8. **Whole-document gate.** All thirteen sections are substantive. Scope/ownership, App A.3/App F mapping, failure ladder, threading/bounds, named test plans, implementation milestones and the OQ-7 procedure/criteria/fallback were reviewed. OQ-7 remains open; modern parser/runtime work and optional APIs are not silently granted. The remaining defect is in an existing ID grammar publication boundary, not in the new alternate selection mechanism.

## Required correction

### C57-1 — Publish range-capable block selector constraints

**Severity:** correction, medium/P2. **Owner:** Phase 3. **Section 5 impact:** yes.

**Current evidence:** `docs/phase3/v1/PHASE_3_DOC.md:1284–1306` gives both IdRule and LayerRule only `OptionalInt legacyMetadata`. Lines 3034–3037 require one rule per selector occurrence. Lines 3063–3069 define acceptedValues as comma-ordered value tokens and require exact equality with one listed token; no integer-range normalization or range predicate is supplied. The complete algebra is incorporated into the exported contract at §5.1, line 3249.

The shipped author contract explicitly connects shader block mappings to the shared matcher grammar: `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:544`. That referenced grammar includes `minecraft:leaves:3,7,11,15` at `doc/properties_files.txt:102`, `minecraft:reeds:age=0-3` at line 115, and the combined `minecraft:reeds:0-3:age=0-3` at line 122. Correspondingly, the actual P9 consuming algorithm requires metadata-set membership, inclusive ascending integer ranges, and metadata/property conjunction at `docs/phase9/v1/PHASE_9_DOC.md:530–537`.

**Observable breakage:** the published P3 model cannot carry the four metadata alternatives in the leaves example while retaining the mandated single rule per selector. For the reeds example, a producer following the literal token/equality contract publishes `0-3`, which cannot equal canonical state values `0`, `1`, `2` or `3`. An implementation must therefore reject valid selectors, match no states, or invent unpublished expansion/interpretation rules. P9 cannot repair the loss by reopening/reparsing the original selector without violating the ownership boundary. The same defect applies to ordinary and forced BLOCK parses and to shared layer constraints; schema22's alternate mechanism does not remedy it.

**Minimal owner fix:** close the existing selector algebra over metadata alternatives/ranges and integer property ranges. Publish an immutable metadata constraint capable of preserving the author's set, or explicitly specify a bounded, provenance-preserving expansion that replaces the current one-rule-per-occurrence rule. Define property-range normalization or a typed range representation with exact ordering, ascending/overflow validation, conjunction and failure semantics. Keep registry membership resolution in P9 and retain the prohibition on dropping invalid predicates. Amend the incorporated §5 contract, canonical fingerprint/schema discipline, and planned pack/mod/ordinary/alternate fixture cases together; coordinate the consuming P9 contract and current-schema/inspection receivers. Do not add a second parser in P9 or claim runtime proof as part of this architecture correction.

## Nonblocking note

### N57-1 — Preserve historical-source and license confidence

The historical `reference-src/pintonium-9c2fcc1/` spelling recorded through `docs/MOVES.md` was not available at that path. The readable `reference-src/Pintonium-main/` sources corroborate the specific preprocessing/option pitfalls and locale mechanism, but do not independently establish the historical pinned checkout. Its `LICENSE:1–4` says GPL v3; owner D-P3-63 at `docs/phase3/v1/PHASE_3_DOC.md:4915` already distinguishes that observation from historical blanket LGPL labels. This is a limitation, not another required correction or permission to reuse files under an assumed license. P1's explicit implementation-time jcpp pin/closure/notice/once-only packaging grant remains the binding dependency gate (`docs/phase1/v14/PHASE_1_DOC.md:2259–2287`). No optional API or source-license certification follows from this review.

## Limitations and final verdict

This was read-only architecture review. No repository/report files were changed; no build, tests, formatter, linter, runtime experiment, pack-tier run or GPU validation was performed or claimed. The frozen digest above is the inventory identity, not a newly computed checksum. Historical reviews and receiver receipts were not treated as executable evidence. Forbidden transcripts, Oculus transformation boundaries, relocated libraries and glsl-transformer implementation were not read.

**Final verdict: PASS-WITH-CORRECTIONS.** Resolve C57-1 through the P3-owned contract fix and coordinated receiving amendments. Because §5 is affected, fresh owner/receiver review is required before dependency consumption. Final integration and implementation clearance remain separate.

## Resolutions

### C57-1 — Owner contract corrected; receiving adoption and fresh review pending

2026-09-08: Phase 3 §0.65/D-P3-73 replaces both rules' scalar `OptionalInt legacyMetadata`
with `Optional<MetadataConstraint>`, whose immutable authored-order alternatives are
`IntegerRange(lowerInclusive,upperInclusive)` records. `PropertyPredicate.acceptedValues`
is now `List<PropertyValueConstraint>`, with closed `Literal(value)` and
`IntegerInterval(range)` variants. §§2.2/4.9/5.1 bind the exact grammar, checked endpoints,
ascending ranges, normalization, conjunction, whole-selector invalid-predicate rejection,
defensive immutability and finite bounds without range-member or rule expansion.
The leaves, reeds and combined examples each have exact one-rule payloads; pack/mod,
ordinary/forced BLOCK and ordinary LAYER all use the same constraint algebra and retain
actual era/origin/line/ordinal. P9 alone resolves finite live registry values; no second
selector parser or altered alternate-selection policy is granted.

§§4.10/5.3 advance containing/nested/inspection schema to **23** and the materialization
domain to `MaterializedSource-v23`, canonically encoding all typed variants/endpoints/order.
§5.1.1 keeps `projectionVersion=1`, all nine metadata-only trees and existing String-leaf
hash restrictions. §§3.5/8.1/9/12 add named producer-to-consumer boundary plans, including
integer limits, no-overmatch and rejection before stale-state reuse. §11.4 publishes exact
P9/P2 receiver needs and coordinated schema23 receipts for Main integration.

This is documentation-only owner remediation, not fresh independent verification or proof
of parser/runtime behavior. No validation command, build, test, formatter or linter ran.
The original review body, frozen identity, PASS-WITH-CORRECTIONS verdict and N57-1 historical
checkout/license confidence limit are preserved. Changed §5 requires fresh owner/receiver
review; receiving amendments, final integration and implementation clearance are not claimed.
