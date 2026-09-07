## 0. Method and reading order

I independently re-derived every surviving candidate from the current target and resolved source
contract before consulting historical reviews. The decisive sources were:

1. `docs/phase3/v1/PHASE_3_DOC.md` as a whole, with focused checks of the canonical §2.2
   declarations, source/index/include model, discovery lifecycle and limits, §3 conformance map,
   resource and texture models, binding §5 region, schema discipline, tests, milestones, decisions,
   and implementation checklist;
2. the manifest-selected `docs/design/v3/DESIGN.md` Part I, mandatory template, Phase 3 target
   specification, and document gate;
3. `docs/research/v1/RESEARCH.md` as contract ground truth, especially §§3.1–3.2, §3.6.2,
   Appendix A.3, Appendix B.4, and Appendix F.5;
4. the binding interface region of `docs/phase1/v14/PHASE_1_DOC.md`; and
5. the supplied candidate, refutation, and finder-clean-area records. The permitted Pintonium and
   Oculus reports were not needed to decide the surviving candidates.

I searched the complete target for equivalent file-level source identities, discovery retention or
eviction rules, public declarations for the questioned resource leaves and texture sidecar, and a
`.csh` conformance row. Only after settling every interpretation, severity, and interface
classification did I read all discovered prior reviews `PHASE_3_REVIEW_1.md` through
`PHASE_3_REVIEW_44.md`, in round order and including their resolutions, as historical settled
material.

There were no source-list deviations, no network use, and no agent fan-out. I did not invoke
`$verify-loop`, run a verification harness, start another session, or read a forbidden transcript,
chatlog, or `*.txt` source. The Gate reported no drops. `candidate-005` had already been eliminated
at Refute and was not revived or included in the adjudicated disposition set.

## 1. Findings

### candidate-001 — Include-only and shared-file `SourceKey` semantics are under-specified

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md` §2.2 and §4.2, especially lines 977–1000,
  1127–1135, and 1430–1445; binding publication at lines 2163 and 2359–2366.
- **Claim:** The public catalog does not determine how an include-only physical file, especially
  one shared by roots with different programs or stages, is represented through the mandatory
  dimension/program/stage fields of `SourceKey`.
- **Evidence:** `SourceId` supplies path-level physical identity, but `SourceDocument`, both
  resolved `IncludeEdge` endpoints, and `source(...)` are keyed by `SourceKey`, whose components
  necessarily include `DimensionKey`, non-empty `programName`, stage, and `SourceId`
  (`PHASE_3_DOC.md:981-999`). The catalog simultaneously requires `sources()` to contain every
  indexed document, including include-only files, and requires all four `SourceKey` fields to agree
  with the indexed source (`:1127-1135`). Includes are path-based and need not have a stage-bearing
  extension (`RESEARCH.md:256`), while the internal graph has one node per physical file and one
  edge per include location (`PHASE_3_DOC.md:1440-1445`). For one library included by unlike roots,
  the target never says whether the document and edges are duplicated under contextual keys, how
  their program/stage/dimension values are derived, or how such duplication remains consistent with
  the one-file/one-location graph rules. `roots()` membership can distinguish compile roots in
  principle, so the type algebra is not proven incapable; the surviving defect is the absence of a
  determinate public multiplicity and identity rule.
- **Severity:** correction. Prefer canonical `SourceId` identity for documents, resolved include
  endpoints, and graph nodes while reserving `SourceKey` for roots; alternatively, completely
  specify contextual alias cardinality, derivation, ordering, lookup, and edge duplication while
  requiring graph canonicalization by `SourceId`. Define root discovery/program-name extraction and
  add cross-program, cross-stage, and cross-dimension shared-include tests. Update the source row in
  §5 and apply §5.3 if record components or their published meanings change.
- **Touches interface/change-trigger region:** yes. The correction changes the source-catalog
  declarations or consumer-visible identity/multiplicity semantics incorporated into §5.

### candidate-002 — Directory-keyed discovery retention has no finite aggregate policy

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md` §2.2, §4.1, §5.1, §6, and §7, especially lines
  404–420, 2174–2179, 2393–2407, 2543–2545, and 2565–2575.
- **Claim:** The service bundle promises bounded latest-discovery retention, but its state machine
  permits successful discovery of arbitrarily many distinct real-directory keys without a table
  bound, eviction, expiry, or reset rule.
- **Evidence:** `discover` accepts an arbitrary host directory and returns an immutable generation
  and candidate list (`PHASE_3_DOC.md:404-420`). The binding lifecycle expressly says one bundle
  “retains only bounded latest-discovery snapshots” (`:2174-2179`). Yet each successful completion
  replaces state only for its own real-directory key, an invalid completion changes no keyed state,
  and distinct keys remain independent (`:2393-2399`). Resolution is then defined against the
  latest retained generation for that key (`:2401-2407`), while invalid discovery preserves every
  prior keyed snapshot (`:2543-2545`). The finite input/cache limits govern candidate traversal,
  pack bytes, graphs, macros, diagnostics, and processing caches; they state no maximum key count,
  aggregate snapshot bytes, deterministic eviction order, or result for an evicted generation
  (`:2565-2575`). One latest value per key is not a finite bound over unbounded keys.
- **Severity:** correction. Define finite limits for one discovery result and for the aggregate
  directory-keyed table (by key count and/or retained bytes), deterministic completion-linearized
  eviction or expiry, and exact resolver/target-acquisition behavior for evicted generations.
  Preserve or deliberately revise the validity of already acquired persistence targets. Add
  many-directory, candidate/byte overflow, concurrent eviction, and evicted-generation tests and
  align §§4–8 and the §5 discovery/bundle rows. If lifetime-unbounded retention is intentional,
  remove the bounded claim and explicitly address the resource-exhaustion posture instead.
- **Touches interface/change-trigger region:** yes. Any executable retention policy changes the
  published generation, resolver, acquisition, or bundle-lifecycle semantics in §5.

### candidate-003 — `ResourceRequirements` still exposes undeclared leaf APIs

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md` §4.7 and §5.1, especially lines 1807–1855,
  2192–2194, and 2316–2325.
- **Claim:** The aggregate resource graph is not a closed compile-time contract because several
  leaf types used in public record components have domains described in prose but no exact public
  declarations or inspection surface.
- **Evidence:** The canonical aggregate records expose `ColorInternalFormat`, `Vec4f`,
  `VertexRequirements`, `ShadowTextureKey`, and `ShadowDepthKey` in component signatures
  (`PHASE_3_DOC.md:1807-1842`). The following prose states only that the shadow keys cover depth or
  color indices, `VertexRequirements` is a set over three names, `ColorInternalFormat` is the
  Appendix-B.4 domain, and `Vec4f` contains four finite floats (`:1844-1850`). Binding §5 repeats
  those value domains but supplies no variants/components/accessors for those leaves
  (`:2316-2325`), even though it makes exact signatures, components, and variants binding
  (`:2192-2194`). The conformance map specifically expects a
  `VertexRequirements.attributes` projection (`:1304`), but no attribute enum type or accessor is
  declared. Likewise, consumers cannot derive shadow kind/index accessors or the four `Vec4f`
  component names from prose alone. Appendix B.4's 37 tokens constrain accepted values but do not
  declare the nominal Java API.
- **Severity:** correction. Add exact canonical and binding declarations for the vertex-attribute
  enum and `VertexRequirements`, both shadow key types, `Vec4f`, and `ColorInternalFormat`, including
  concrete constants/variants, components or accessors, validation, equality, and deterministic
  ordering. Add producer/consumer constructor-and-accessor compatibility tests and apply §5.3 only
  if the repair changes rather than documents the intended schema-9 meaning.
- **Touches interface/change-trigger region:** yes. The missing leaves occur in the public resource
  graph incorporated into the monitored §5 contract.

### candidate-004 — `TextureSidecarRef` has no public path projection

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md` §3.2 and §5.1, especially lines 1274–1278 and
  2264–2286; canonical-public-shape rule at line 374.
- **Claim:** Phase 13 cannot compile against a specified operation that retrieves the normalized
  `.mcmeta` path retained in a present `TextureSidecarRef`.
- **Evidence:** The custom `PackPath` and `Raw` variants and noise `Override` all carry
  `Optional<TextureSidecarRef>` (`PHASE_3_DOC.md:2264-2268,2280-2282`). The binding prose says the
  reference “is” the associated normalized `<image-or-bytes>.mcmeta` pack path (`:2276-2278`), and
  the conformance map requires it to be retained for Phase 13 (`:1274-1278`). But neither the
  canonical declaration block nor §5 declares a record component, accessor, or equivalent
  projection from `TextureSidecarRef` to `NormalizedPackPath`. Java has no transparent nominal
  alias, and Phase 13 owns loading/interpretation rather than reinvention of Phase 3's lossless
  parsed model. The target's own `NormalizedPackPath.canonicalString()` repair demonstrates why a
  named path value needs an exact consumer projection (`:184-187`).
- **Severity:** correction. Declare the exact public sidecar shape in §2.2 and §5, for example
  `TextureSidecarRef(NormalizedPackPath path)`, with non-null validation, structural equality, and
  the adjacent-sidecar producer invariant. Make the surrounding sealed texture variants concrete
  where needed so the access path is unambiguous, and add a Phase-13 compatibility test that reads
  the sidecar path from custom and noise variants. Apply §5.3 according to whether this closes an
  intended representation or changes a published component meaning.
- **Touches interface/change-trigger region:** yes. The correction adds or closes a Phase-13-facing
  declaration incorporated into the monitored texture row.

### candidate-006 — Owned post-v0.5 `.csh` recognition is unmapped in §3

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md` §3.5, §9 P3-C19, D-P3-21, and checklist item 20,
  especially lines 1361–1375, 2810, 2893, and 3069–3070.
- **Claim:** The mandatory conformance map omits the modern `.csh` source-recognition component that
  the target itself assigns to Phase 3.
- **Evidence:** RESEARCH §3.1 names `.csh` as a shader-source extension and preserves the
  `.vsh`/`.fsh`-only world-folder restriction (`RESEARCH.md:210-218`); §3.6.2 gives the global
  compute naming/applicability forms (`:358-367`). The governing structural rule requires a phase
  to architect later-milestone parts of its whole subsystem now (`DESIGN.md:185-193`), and the
  mandatory template requires every in-scope RESEARCH §3/App item in the §3 conformance table with
  zero unmapped rows (`DESIGN.md:831-835`). The target reserves `ShaderSourceStage.COMPUTE`, records
  P3-C19 as global `.csh` recognition/materialization at `post-v0.5`, and repeats that implementation
  duty in D-P3-21 and checklist item 20. Nevertheless, the complete remaining-contract table at
  `PHASE_3_DOC.md:1361-1375` contains no `.csh`, compute-source, or §3.6.2 row. The generic source
  identity row cites §§3.2/4.2 and tests legacy stage roots; it does not map global compute
  recognition, its milestone, dimension exclusion, or a compute-specific test. The existing
  post-v0.5 `RENDERTARGETS` row confirms later staging does not excuse §3 omission.
- **Severity:** correction. Add a §3 row citing RESEARCH §§3.1 and 3.6.2, mapping global `.csh`
  recognition/materialization to `ShaderSourceStage.COMPUTE` and P3-C19 at `post-v0.5`, while
  retaining `.vsh`/`.fsh`-only dimension folders. Name a headless future test for global program and
  `_a`…`_z` compute roots, non-gbuffers applicability, COMPUTE materialization, and rejection of
  dimension-folder `.csh` roots.
- **Touches interface/change-trigger region:** no. The existing COMPUTE-shaped source model can
  support this map/test correction without changing §5 or another manifest-declared interface
  region.

## 2. Checked and clean

- The finder-reported new-surface checks were rechecked. Schema-9 publication and rejection,
  catalog-bound option-state validation, persistence authentication, invalid-generation
  non-supersession, source-presence versus Phase 4 compile/link separation, profile invalid-state
  handling, and the D-P3-39 through D-P3-41 test/checklist propagation remain coherent apart from
  the source-key multiplicity and discovery-retention findings above.
- The consumed Phase 1 contracts are honest: module/package seam constraints,
  `GLCapabilityProfile`, fixed log channels, loader-neutral diagnostics, `saveSources`, and the
  SPDX/`THIRD-PARTY.md` mechanism exist in the binding dependency region. The jcpp build allowance
  remains requested rather than assumed.
- The remaining interface publication was checked across discovery/load, option/persistence,
  program-state, source/materialization, resource, texture, ID-map, macro, and internal-source
  contracts. No additional candidate-backed interface defect survives beyond candidates 001–004.
- The Appendix F.1 ownership map, the other Appendix F.2–F.8 and Appendix A.3 dispositions, all four
  required Pintonium-pitfall gates, OQ-7's option-3-shaped architecture, pure-`:engine` placement,
  thirteen-section template, failure ladder, milestones, and checklist remain substantive. The
  `.csh` row in candidate-006 is the conformance exception admitted here.
- Prior settled material does not clear the admitted findings. The similar shared-include proposal
  in Round 42 was eliminated before adjudication rather than decided on the merits, and Round 44's
  new executable-program projection sharpened the roots/include-only distinction. Round 42
  introduced the service bundle's bounded-retention promise without specifying aggregate
  directory-key eviction. Earlier broad resource and texture closures do not supply the leaf
  declarations or `TextureSidecarRef` path accessor still absent from the current bytes; Round 28's
  resolution assertion that the sidecar fields were closed is not borne out by the resulting
  canonical declarations. General prior clean statements about remaining §3 coverage did not
  specifically map P3-C19 `.csh` recognition.
- `candidate-005`, “The preprocessing conformance row omits ID-mapping properties,” remains excluded
  after its pre-adjudication Refute disposition. The current pipeline, macro rules, ID-map parser
  path, tests, and checklist already provide equivalent ID-mapping preprocessing coverage; it is
  not revived as a finding.
- No surviving candidate was consolidated or dropped on independent derivation, and no finding was
  created outside the supplied candidate set.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=5; notes=0
Interface changed: yes

All five admitted findings are bounded specification, interface-closure, lifecycle, or
traceability corrections; none requires rebuilding the Phase 3 architecture, so `FAIL` is not
warranted. Four corrections require edits to declarations or semantics incorporated into the
manifest-declared `cross-phase-interfaces` region. Candidate-006 can remain a §3/test correction.
The nonzero correction count makes literal `PASS` unavailable.

The supplied trend is 8 → 8 → 5 → 5 → 4 corrections for Rounds 40–44; this round rises to 5. The
recent sequence is not strictly decreasing and the document has not converged despite the prior
decline. The next required action is a scoped fix-up resolving candidates 001, 002, 003, 004, and
006, updating every affected §5 row and applying §5.3 compatibility discipline where shapes or
meanings change. Because at least one correction changes the monitored interface region, a fresh
whole-document verification round is required before Phase 3 may close or be consumed as a verified
dependency.

## Resolutions

I re-derived the five corrections from the selected DESIGN v3 contract, RESEARCH, and the binding
Phase 1 interface, then applied them to `PHASE_3_DOC.md` under compact §0.45.

### candidate-001 — resolved
`SourceId(NormalizedPackPath)` now keys each physical document, graph node, resolved include
endpoint, and lookup exactly once; only compile roots carry `SourceKey` dimension/program/stage
context. Root filename derivation, ordering, shared-file cardinality, and cross-context tests are explicit.
### candidate-002 — resolved
The public `DiscoveryLimits` policy bounds candidates, canonical snapshot bytes, and retained
directory keys. Oversize enumeration returns one bounded keyed sentinel result; successful
completion-LRU eviction has exact resolver/acquisition/load outcomes, while acquired targets survive.
### candidate-003 — resolved
Canonical and binding declarations now close `VertexAttribute`/`VertexRequirements`, both shadow
key enums, named-component `Vec4f`, and all 37 `ColorInternalFormat` constants, including
validation, structural equality, enum ordering, constructor/accessor tests, and rejection tests.
### candidate-004 — resolved
`TextureSidecarRef` is now the exact public record `(NormalizedPackPath path)`, with non-null
validation, structural equality, `path()` projection, and the adjacent-file producer invariant for
custom pack-path, raw, and noise variants; the Phase-13-facing accessor test covers all three.
### candidate-006 — resolved
Section 3 now maps RESEARCH §§3.1/3.6.2 to post-v0.5 P3-C19 recognition of each known non-gbuffers
program's base and `_a`…`_z` `.csh` roots, associated program name, distinct physical identity,
`COMPUTE` materialization, and dimension-folder rejection, with a named future headless test.
### Interface and compatibility disposition
The source declarations, discovery lifecycle/API, resource leaves, and sidecar record intentionally
change the monitored `cross-phase-interfaces` region. Binding §5 was updated in the same revision;
the nested source shape advances `CURRENT_SCHEMA_VERSION` from 9 to 10 and adds schema-9 rejection.
The manifest change trigger therefore requires a fresh whole-document verification round.
### Notes deferred
None. The adjudication admitted zero notes, so no non-correction proposal was applied or deferred.
