# Phase 3 verification review — round 43

## 0. Method and reading order

I independently re-derived every surviving candidate before consulting historical reviews. The
source order was:

1. the complete `docs/phase3/v1/PHASE_3_DOC.md`, with focused checks of the canonical §2.2
   declarations, load/discovery and persistence protocols, profile and program-state semantics,
   Appendix F conformance rows, binding §5 region, schema discipline, tests, milestones, hand-offs,
   and checklist;
2. the manifest-selected `docs/design/v3/DESIGN.md` Part I, mandatory template, Phase 3 target
   specification, and document gate;
3. `docs/research/v1/RESEARCH.md` as contract ground truth, especially the profile, custom-texture,
   program-state, and persistence requirements;
4. the binding interface region of `docs/phase1/v14/PHASE_1_DOC.md`; and
5. the permitted Pintonium and Oculus reports as supporting evidence.

I searched the whole target for equivalent profile lookup/evaluation context, evaluation diagnostic
routes, persistence-target rejection outcomes, service-domain construction checks, acquisition of
safe persistence access, and PNG validation. Only after settling each candidate's interpretation,
severity, interface classification, and duplicate relationship did I read all discovered prior
reviews `PHASE_3_REVIEW_1.md` through `PHASE_3_REVIEW_42.md`, including their resolutions, as the
last step.

There were no source-list deviations, no network use, and no agent fan-out. I did not invoke
`$verify-loop`, run a verification harness, start another session, or read any forbidden transcript,
chatlog, or `*.txt` source. No candidate was eliminated before adjudication, and the Gate reported
no drops. Candidate-005 is consolidated with candidate-002 below because both identify the same
`packOptionsTarget` failure-contract omission and order the same repair.

## 1. Findings

### candidate-001 — The exact program-state evaluator lacks the selected profile data it must evaluate

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:567-592,658-664,1905-1913,2163-2191`.
- **Claim:** `ProgramStateModel.evaluate(OptionState, Optional<ProfileName>)` cannot implement its
  binding profile-disable semantics from its receiver and arguments.
- **Evidence:** `OptionConfiguration` owns `List<ProfileModel> profiles`, and each `ProfileModel`
  owns its expanded `List<ProgramDisable>` (`PHASE_3_DOC.md:567-592`). In contrast,
  `ProgramStateModel` contains only the raw program map, and its exact evaluator receives only an
  option state and an optional profile name (`:658-664`). The required result must apply the
  selected profile's expanded qualified and unqualified disables, include profile-only keys in the
  evaluated key union, and warn for unknown profile/program references (`:1905-1913`). A profile
  name is not the corresponding expanded profile. Two configurations can therefore present
  identical evaluator inputs while assigning different disable lists to that name, yet the required
  outputs differ. The exact result also carries no diagnostics and the operation accepts no
  reporter (`:694-704,2177-2187`). The fact that `PackConfiguration` retains options and properties
  as sibling components does not make those siblings reachable from this narrower receiver, and a
  Phase 4 recomputation would bypass the exact API and duplicate Phase-3-owned semantics.
- **Severity:** correction. Move evaluation to an aggregate that has both profile and program-state
  data, or pass an immutable validated profile lookup/resolved profile or disable set. Define the
  diagnostic route and unknown-profile/program behavior at that same boundary. Add tests for a
  profile-only disable entering the evaluated key union, qualified and unqualified disables,
  differing packs with the same profile name, and unknown references; update §2.2 and the §5 row,
  applying §5.3 if the chosen repair changes a published component's shape, meaning, or default.
- **Touches interface/change-trigger region:** yes.

### candidate-002 — `packOptionsTarget` has no observable authentication-failure outcome

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:381-387,399-415,1320-1323,1437-1442,2258-2292`.
- **Claim:** The standalone conversion from `PackCandidateId` to `PackOptionsTarget` specifies only
  success, although invalidity is reachable with legitimately issued values and during ordinary
  concurrent use.
- **Evidence:** The method returns a bare `PackOptionsTarget` (`:381-387`), while every discovered
  candidate carries a `PackCandidateId` and the closed kind set includes `OFF` and `INTERNAL`
  (`:399-407`). Discovery publishes those two sentinels as normal candidates (`:1320-1323`). The
  target says the method authenticates a current filesystem ID and describes only the target it
  returns on success (`:1437-1442`); it defines no result variant, exception, diagnostic route, or
  null policy for a sentinel, foreign-bundle, unknown, superseded, or otherwise non-filesystem ID.
  This is not merely forgery or caller misuse: immutable old results survive later calls, receivers
  are thread-safe, and authentication retains only latest-discovery snapshots, so a separately
  resolved valid ID can be superseded before acquisition (`:2075-2085,2277-2286`). `load`'s
  `INVALID_SELECTION` and the resolver's `InvalidSnapshot` govern different operations and cannot
  define this method's outcome.
- **Severity:** correction. Prefer a closed, non-throwing acquisition result with a resolved target
  and typed invalid-candidate outcome, and define its linearization against discovery. At minimum,
  state an exact caller-observable policy for foreign, unknown, superseded, sentinel/non-filesystem,
  unavailable, and null inputs, with rejection before persistence I/O. Add focused cases including
  resolve/supersede/acquire interleaving, then update the incorporated §5 operation row.
- **Touches interface/change-trigger region:** yes.

### candidate-003 — The public service-bundle record does not enforce one authentication domain

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:366-388,735-738,817-825,2058,2074-2085`.
- **Claim:** The canonical public construction surface contradicts the promise that one
  `PackFrontEndServices` value owns one coherent front-end/token/target authentication domain.
- **Evidence:** The canonical declaration is a public record with four components
  (`:375-379`), so Java exposes a public canonical constructor. Its `PackFrontEnd` and
  `OptionPersistenceCodec` components are open public interfaces (`:381-388,735-738`). A consumer
  can therefore construct a bundle from the front end of factory domain A and the codec of domain B,
  or from arbitrary implementations. That bundle's own front end can issue a target that its own
  option codec rejects as foreign. Opaque target issuance does not constrain receiver combinations.
  This directly conflicts with the binding statement that one bundle owns one authentication domain
  and that its option codec accepts only that domain's targets (`:2058,2074-2083`). The Phase 12
  convention to call `PackFrontEnds.create()` does not remove the contradictory public constructor
  from the canonical API.
- **Severity:** correction. Replace the record with a publicly readable but non-publicly
  constructible final class or sealed interface/package-private implementation returned only by
  `PackFrontEnds.create()`, or define a real shared-owner check that rejects null, arbitrary, and
  mixed-domain receivers during construction. Test mixed receivers from two factory bundles as well
  as own-target acceptance and foreign-target rejection, and update §2.2 and the §5 acquisition row.
- **Touches interface/change-trigger region:** yes.

### candidate-004 — Phase-3-owned safe persistence access has no public acquisition route

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:366-380,714-717,1432-1435,2058,2068,2074-2102,2647-2649,2794-2796,2854-2857`.
- **Claim:** Phases 7 and 12 cannot obtain the implementation of the Phase-3-owned safe file-access
  protocol used by filesystem loads and persistence codecs without implementing that protocol
  themselves or inventing an undocumented constructor.
- **Evidence:** `PackFrontEnds.create()` exposes exactly the front end, two codecs, and ID parser;
  it exposes no `PersistenceFileAccess` receiver or factory (`:366-380`). The latter is only a
  public provider interface (`:714-717`), and both standalone codecs and filesystem load require a
  caller-supplied instance (`:1432-1435`). Yet §5 assigns that provider the configured roots,
  direct-child containment, symlink/path-escape rejection, byte snapshots, handle closure, and safe
  temporary/atomic replacement (`:2095-2102`), while also promising dependency-free acquisition
  with no consumer implementation required (`:2074-2085`). The component register and checklist
  place safe file access and safe atomic access in Phase 3's P3-C07 implementation
  (`:2647-2649,2854-2857`), but the hand-off instead tells Phase 7 to supply it (`:2794-2796`). No
  selected dependency supplies an equivalent service. `GlobalOptionsTarget` is empty, so an
  external implementer would additionally have to guess the exact configured root mapping for
  `optionsshaders.txt`.
- **Severity:** correction. Expose a Phase-3-owned acquisition/configuration operation for
  `PersistenceFileAccess`, either as an appropriately configured service receiver or a factory with
  exact validated root inputs. Bind its lifetime, thread safety, authentication relationship,
  per-pack root, and global `optionsshaders.txt` location in §5. If downstream implementation is
  truly intended, explicitly transfer ownership, fully specify both roots and all provider duties,
  and narrow the contradictory no-consumer-implementation and P3-C07 commitments.
- **Touches interface/change-trigger region:** yes.

### candidate-006 — The ordinary App F.5 pack-path texture form omits the PNG restriction

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1210-1212,1800-1836,2072,2141-2159`; authority at
  `docs/research/v1/RESEARCH.md:1482-1490` and `docs/design/v3/DESIGN.md:1431-1435`.
- **Claim:** The conformance map and published `CustomTextureSpec.PackPath` domain do not faithfully
  model the authoritative ordinary custom-texture source form.
- **Evidence:** RESEARCH defines that form as a pack-relative PNG path, distinct from a
  `minecraft:` asset and the typed raw form (`RESEARCH.md:1482-1487`). The governing Phase 3 scope
  assigns parsing all three forms into the model to Phase 3 and defers only loading to Phase 13
  (`DESIGN.md:1431-1435`). The target's conformance row instead says only “pack path” and names a
  discriminator test (`PHASE_3_DOC.md:1210`); detailed texture validation covers generic normalized
  paths and raw format domains but never PNG (`:1800-1836`); and binding §5 defines
  `PackPath(key, NormalizedPackPath image, ...)` without an additional predicate (`:2141-2151`).
  `NormalizedPackPath` enforces path safety, not file format. A one-token non-PNG value can thus be
  published as an ordinary pack image even though it is not one of Appendix F.5's forms.
- **Severity:** correction. Define an exact lexical PNG-path predicate, including extension case
  policy, in the §3.2 row, §4.8 parser semantics, and §5 `PackPath` contract. Diagnose and ignore
  only a nonconforming occurrence under the existing malformed-texture rule, while leaving file
  opening and decoding to Phase 13. Add positive/negative and last-valid tests. Because this narrows
  the accepted meaning of a published `properties.textures` component, apply §5.3's schema-version
  and producer/consumer compatibility requirements.
- **Touches interface/change-trigger region:** yes.

## 2. Checked and clean

- Candidate-005 is dropped as an exact duplicate of admitted candidate-002. Both concern the same
  bare-return `packOptionsTarget(PackCandidateId)` operation, the same stale/foreign/non-filesystem
  states, the same resolve-to-acquire supersession race, and the same need for a defined observable
  failure policy in §5. Candidate-002 is the broader representative because it also expressly covers
  sentinel and null handling; counting candidate-005 would double-count one repair.
- The finder-reported clean areas were rechecked. Include-first expansion, numeric `#line`
  attribution, expanded-stream option rewriting, and later conditional evaluation are consistently
  ordered across the pipeline, conformance map, detailed design, tests, and checklist.
- Apart from the admitted acquisition and rejection defects, direct-name per-pack persistence,
  exact archive-extension preservation, changed-only ISO-8859-1 content, containment and symlink
  policy, load-time persisted-option application, and no digest/slot probe are internally aligned.
- Profile parsing, cycle handling, constraint-count/source-order inference, and `Custom` are
  otherwise consistent. The admitted program evaluator defect is specifically the loss of profile
  data and diagnostics at the narrowed exact operation boundary.
- The alpha/blend/scale/flip domains, virtual-pre filtering, resource-requirement projections,
  source/materialization contracts, schema-7 statements, ID-map model, and declared-uniform
  fingerprint construction are otherwise aligned under the supplied lenses.
- The complete Appendix F.1 ownership map, remaining Appendix F.2–F.8 rows, Appendix A.3 mapping,
  four required Pintonium pitfall dispositions, OQ-7 spike, pure-`:engine` placement, and all
  thirteen mandatory sections remain substantive. Candidate-006 is the sole surviving conformance
  exception in this round.
- Phase 3's named Phase 1 module/seam, `GLCapabilityProfile`, diagnostics, logging, debug flag, and
  notice inputs exist in the selected binding dependency. No undeclared GL service is consumed.
- Prior settled material does not clear the five admitted defects. Round 42 introduced the exact
  program evaluator and four-receiver public bundle now under review; its acquisition correction did
  not constrain the public record constructor, expose safe file access, or define target-acquisition
  rejection. Earlier texture-algebra reviews closed variants and raw domains but never specifically
  established the PNG predicate. There were no candidates eliminated before adjudication and no
  Gate drops to discuss further.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=5; notes=0
Interface changed: yes

Five distinct correction-sized findings remain after consolidating candidate-005 into
candidate-002. None requires rebuilding the Phase 3 architecture, so `FAIL` is not warranted, but
nonzero corrections make literal `PASS` unavailable. Every admitted correction changes an exact
consumer-visible declaration or semantic incorporated into the manifest-declared §5 region.

The prior three-round correction trend is not strictly decreasing (`8 -> 8 -> 5`), and this round
remains at five after duplicate consolidation. The surface has therefore not converged. The next
required action is a scoped fix-up resolving candidates 001, 002, 003, 004, and 006, updating all
affected §5 rows and schema/compatibility rules, and recording resolutions in this review. Because
the `cross-phase-interfaces` change trigger is implicated, a fresh whole-document verification
round is required before Phase 3 can close or be consumed as a verified dependency.

## Resolutions

### candidate-001 — applied
Re-derivation confirmed that a profile name cannot recover expanded sibling profile data.
Evaluation now belongs only to `PackConfiguration`, which supplies its profiles, executable source
keys, raw states, and `DiagnosticReporter`; tests cover profile-only keys, qualified/unqualified
disables, same-named profiles in different packs, unknown references, and expression failures.

### candidate-002 — applied (including duplicate candidate-005)
`packOptionsTarget` now returns closed `PackOptionsTargetAcquisition` outcomes. Six rejection
reasons cover null, foreign, unknown, superseded, sentinel/non-filesystem, and unavailable
candidates before persistence I/O. Acquisition-first target survival and supersession-first
`SUPERSEDED_GENERATION` rejection are binding and tested.

### candidate-003 — applied
`PackFrontEndServices` is now a final readable class with no public constructor, created only by
`PackFrontEnds.create()`. Its receivers and issued capabilities share one domain; tests require
own-domain target/access acceptance and cross-bundle rejection before I/O.

### candidate-004 — applied
The bundle now owns the sole typed acquisition route for sealed `PersistenceFileAccess`.
`PersistenceRootConfiguration` fixes validated roots and locations to
`<shaderpacks>/<pack>.txt` and `<game>/optionsshaders.txt`. Lifetime, thread safety, closure,
same-domain checks, root failures/mismatch, and Phase 7/12 duties are binding and tested.

### candidate-006 — applied
RESEARCH Appendix F.5 and the governing target require the ordinary form to be a PNG path.
The conformance, parser, and binding contracts require a non-empty final stem plus literal
lowercase `.png`; invalid case/bare/trailing/non-PNG forms warn and omit only that occurrence
without erasing a prior valid value, and no image is opened or decoded. This narrows
`properties.textures`, so schema 8 is current and schema-7 consumers must reject it.

### Interface and follow-up
The manifest-declared `cross-phase-interfaces` region was intentionally changed for all five
corrections. Its trigger requires fresh whole-document verification before Phase 3 closes or is
consumed as verified. No cryptographic hash was calculated here.

### Notes deferred
None. The adjudicator admitted zero notes, so no note-only proposal exists to apply or defer.
