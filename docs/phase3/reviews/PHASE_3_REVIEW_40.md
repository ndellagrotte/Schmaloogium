# Phase 3 verification review — round 40

## 0. Method and reading order

I first re-derived every surviving candidate from the current target and the resolved source
contract, without consulting historical reviews. The decisive independent reads were:

1. `docs/phase3/v1/PHASE_3_DOC.md` as a whole, with focused checks of the header and revision
   ledger, canonical §2.2 declarations, discovery/load and persistence lifecycles, the Appendix F
   conformance map, macro and option semantics, program-state parsing, §5 publication/version
   rules, tests, milestones, and checklist;
2. `docs/design/v3/DESIGN.md` Part I, including §G0.4 adoption, §G1.3 verification, the mandatory
   template, the Phase 3 specification, and the doc gate;
3. `docs/research/v1/RESEARCH.md` as contract ground truth, especially §3.5, §7.5, Appendix A.1,
   and Appendix F.7;
4. `docs/phase1/v14/PHASE_1_DOC.md`'s manifest-selected binding §5 contract; and
5. the data-only Phase 3 verification manifest and the governance ledger where the v3 adoption
   candidate required checking the distinction between review-time selection and completed phase
   adoption.

I searched the complete target for equivalent token issuance, durable candidate identity,
persistence-target derivation, option-model operations, standard option-macro membership,
program-family scale filtering, and flip-key semantics before settling any disposition. The
permitted Pintonium and Oculus reports were not needed to decide the surviving candidates.

Only after those judgments were settled did I read all discovered prior reviews
`PHASE_3_REVIEW_1.md` through `PHASE_3_REVIEW_39.md`, in round order and including their
resolutions. They were used only to identify settled material and changed premises. There were no
source-list deviations, no network use, and no agent fan-out. I did not invoke `$verify-loop`, run
a verification harness, start another session, or read any forbidden transcript, chatlog, or
`*.txt` source. The Gate reported no drops. `candidate-007` had already been eliminated at Refute
and was not revived.

## 1. Findings

### candidate-002 — Persisted filesystem selection has no durable reference-to-current-ID bridge

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:367-385,702-704,2037-2062`.
- **Claim:** Phase 7 cannot reconstruct a valid current `PackSelection.Filesystem` from a persisted
  filesystem-pack selection after restart or discovery supersession.
- **Evidence:** The canonical `PackCandidate` contains only an opaque `PackCandidateId`, kind,
  display name, status, and diagnostics (`PHASE_3_DOC.md:367-385`). The target expressly gives the
  ID and `DiscoveryGeneration` no public constructor, accessor, or serialization form
  (`:702-704`). Binding §5 then invalidates an older ID after a later completed discovery for the
  same directory identity, rejects stale/unknown IDs as `INVALID_SELECTION`, withholds roots and
  paths, and nevertheless requires Phase 7 to obtain a current result before loading a persisted
  filesystem selection (`:2037-2062`). A deterministic list order and a non-unique display string
  are not identities, while `PackIdentity` is available only after the load that already requires
  a current ID and identifies selected content rather than the outer candidate. No stable
  serialized candidate reference or resolver exists elsewhere in the target.
- **Severity:** correction. Publish a stable, validated, canonically serializable filesystem
  candidate reference that does not expose a host-absolute path, and an exact operation that
  resolves it against a fresh discovery snapshot to one current `PackCandidateId`, with explicit
  missing and ambiguous outcomes. Add restart, new-front-end-instance, supersession, reorder,
  duplicate-display-name, removal, and kind-change tests.
- **Touches interface/change-trigger region:** yes. The correction changes the discovery/selection
  contract incorporated into binding §5 and therefore must update the manifest-declared region.

### candidate-003 — Current-revision markers omit the incorporated Round 39 fix-up

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:6,274-277,2641-2644`.
- **Claim:** The header and terminal verification chronology must identify §0.39 as the current
  target surface.
- **Evidence:** The header still says `Last revised: ... (§0.38)` (`PHASE_3_DOC.md:6`), but the
  document contains `### 0.39 Round 39 fix-up` and describes its public contract changes
  (`:274-277`). The closing ledger likewise ends with Round 38 producing §0.38 and omits that Round
  39 reviewed §0.38 and produced §0.39 (`:2641-2644`). The document still correctly says it is
  unverified, so this is stale traceability metadata rather than a false closure claim.
- **Severity:** correction. Advance the header pointer to §0.39 and append the Round 39 transition
  to the closing ledger while retaining the pending-fresh-review status.
- **Touches interface/change-trigger region:** no. This correction changes only header and terminal
  chronology outside the declared interface region.

### candidate-005 — The canonical opaque token classes provide no legal engine issuance path

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:352-385,702-704,1877-1886,2051-2062`.
- **Claim:** `PackFrontEnd.discover` must be able to create the engine-issued candidate and
  generation values that consumers later return, without allowing consumers to forge them.
- **Evidence:** The canonical public shapes declare both token types as `final` classes whose only
  shown constructors are private (`PHASE_3_DOC.md:352-385`). The same block says implementations
  remain under `.internal`, but an ordinary Java subpackage has no private or package-private
  access to the enclosing public package; a final class also cannot have an internal subclass.
  The target expressly binds the exact declarations and method signatures rather than treating
  them as illustrative (`:1877-1886`) and requires the front end to originate and later
  authenticate current, instance-owned IDs (`:2051-2062`). Reflection or unsafe allocation is not
  an architecture contract.
- **Severity:** correction. Define an ordinary Java issuance design that remains opaque to
  consumers—for example sealed public token interfaces with inaccessible final internal
  implementations, or another explicit trusted issuer—and bind equality/hash, owning-instance,
  directory/generation authentication, and non-serialization semantics. Add issuance, forgery,
  foreign-instance, unknown, and supersession tests.
- **Touches interface/change-trigger region:** yes. Correcting the exact token declarations and
  lifecycle semantics changes the discovery/load surface incorporated into §5.

### candidate-006 — Per-pack option persistence has no discovery-to-target identity bridge

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:372-377,595-635,1289-1309,1862-1871,2048-2057`.
- **Claim:** Phase 12 cannot derive the required `shaderpacks/<pack>.txt` target for a discovered
  filesystem candidate without inventing an identity or guessing from display text.
- **Evidence:** `PackCandidate` exposes no stable pack key (`PHASE_3_DOC.md:372-377`), while the
  codec accepts a caller-constructed `PackOptionsTarget(String sanitizedPackId)` (`:595-635`) and
  maps it to `shaderpacks/<sanitized-pack-id>.txt` (`:1289-1309`). Discovery explicitly says display
  names are never accepted back as paths and withholds roots and paths (`:2048-2057`). Binding §5
  separately publishes discovery and persistence to Phase 12 but defines no operation from a
  candidate, persisted candidate reference, or loaded configuration to `PackOptionsTarget`
  (`:1862-1871`). Thus the caller must invent a sanitization/collision convention or independently
  re-enumerate filesystem identity outside the front end. This is distinct from candidate-002's
  load-token remapping defect: even a current live candidate still lacks a specified option-target
  derivation.
- **Severity:** correction. Reuse the durable candidate reference introduced for candidate-002, or
  another collision-free candidate identity, and expose an engine-owned derivation to
  `PackOptionsTarget` with canonical encoding, sanitization/escaping, and collision rules. Retain
  the identity with the successful selection/configuration as needed; never derive the filename
  from display text or a host-absolute path. Add duplicate/sanitization-collision and content-change
  tests.
- **Touches interface/change-trigger region:** yes. The repair changes the published discovery and
  persistence semantics and must update their §5 rows.

### candidate-008 — Canonical option declarations omit promised operations and contradict the profile-inference receiver

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:519-545,1311-1324,1673-1681,1870,1877-1886`.
- **Claim:** Phases 4 and 12 need one exact callable surface for catalog lookup, state lookup,
  aggregate profile inference, and screen-column resolution.
- **Evidence:** The canonical §2.2 records have empty bodies and therefore declare none of
  `OptionCatalog.find`, `OptionState.value`, `OptionConfiguration.inferProfile`, or
  `ScreenModel.resolvedColumns` (`PHASE_3_DOC.md:519-545`). Detailed §4.3 promises all four named
  operations and assigns cross-profile precedence to `OptionConfiguration`, the aggregate that
  owns `List<ProfileModel>` (`:1311-1324`). Section 4.8 instead assigns list-wide ordering and
  inference to `ProfileModel.infer(OptionState)`, although one `ProfileModel` owns only one
  profile (`:1673-1681`). The binding option row publishes the derived operations to Phases 4 and
  12, and the incorporation rule makes exact method signatures contractual (`:1870,1877-1886`).
  A consumer must currently choose a receiver and silently add absent methods.
- **Severity:** correction. Put exact public signatures for `OptionCatalog.find(String)`,
  `OptionState.value(String)`, `OptionConfiguration.inferProfile(OptionState)`, and
  `ScreenModel.resolvedColumns(int)` into the canonical declarations, including argument and
  absence behavior. Remove or deliberately redesign the contradictory `ProfileModel.infer` claim
  so only the aggregate owning the ordered profile list performs aggregate inference.
- **Touches interface/change-trigger region:** yes. The option-model callable surface is an
  incorporated §5 contract consumed by Phases 4 and 12.

### candidate-009 — The required standard option-macro identity set remains unconstrained

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1181,1334-1379,1869,2257-2263`; authority at
  `docs/design/v3/DESIGN.md:1375-1388` and `docs/research/v1/RESEARCH.md:313-319`.
- **Claim:** The configurable standard-header model must still close the exact legacy option-macro
  membership and value sources that packs can query; OQ-7 may defer identity posture, not erase
  this baseline family.
- **Evidence:** RESEARCH names `MC_NORMAL_MAP`, `MC_SPECULAR_MAP`, `MC_RENDER_QUALITY`,
  `MC_SHADOW_QUALITY`, `MC_HAND_DEPTH`, `MC_OLD_HAND_LIGHT`, `MC_OLD_LIGHTING`, and
  `MC_FXAA_LEVEL` as standard option macros (`RESEARCH.md:313-319`). The governing Phase 3
  specification includes option macros in the full standard header and requires configurable
  identity-set data (`DESIGN.md:1375-1388`). The target maps that obligation only to a generic
  ordered `List<MacroDefinition> optionMacros`, whose validation permits any unique identifiers—or
  an empty list—and nowhere names the eight required macros, their value sources, or their
  presence conditions (`PHASE_3_DOC.md:1181,1334-1379`). The binding row similarly names only an
  option family (`:1869`), and the header test inventory checks version, vendor/renderer,
  extensions, and the Phase 6 slot without exact option-family membership/value assertions
  (`:2257-2263`). A conforming implementation could therefore omit or misspell pack-visible
  macros. OQ-7's alternatives vary Iris/engine identity posture around the OF baseline and do not
  defer these named option macros.
- **Severity:** correction. Enumerate all eight macros in §§3.5/4.4 and binding §5; define each
  macro's source, replacement representation, milestone, condition for presence, interaction with
  policy/overrides, and exact positive/negative membership tests. Keep the final OQ-7 identity
  policy open.
- **Touches interface/change-trigger region:** yes. This closes consumer-visible
  `MacroConfiguration` payload semantics used by materialization and G8/S3.

### candidate-010 — `scale.<prog>` loses the composite/deferred applicability rule

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1096-1100,1486-1511,1688-1723,1873,1953-1970`;
  authority at `docs/research/v1/RESEARCH.md:1514-1521`.
- **Claim:** Phase 3 must filter viewport scale properties to composite and deferred programs before
  publishing the typed program-state projection.
- **Evidence:** Appendix F.7 defines `scale.<prog>` as a composite/deferred sub-viewport feature
  (`RESEARCH.md:1514-1521`). The target's conformance row records only numeric shape/range
  (`PHASE_3_DOC.md:1096-1100`). It already has an exact private `ProgramFamily` classifier and
  wrong-family no-mutation behavior, but applies those predicates only to three Appendix A.3
  directives (`:1486-1511`). `ProgramStateModel` and `EvaluatedProgramState` then expose
  `Optional<ViewportScale>` under an unrestricted generic `ProgramKey`, with no scale eligibility
  filter (`:1688-1723,1953-1970`). Phase 4 therefore receives an out-of-family typed value with no
  contractual basis to discard it. The generic §6 failure row supplies the warn/ignore
  disposition once a property is classified out of family, but the missing scale predicate never
  makes that classification.
- **Severity:** correction. Apply exact `{DEFERRED, COMPOSITE}` eligibility to `scale.<prog>` using
  the existing classifier; wrong/unknown-family occurrences warn, are ignored, and cannot create
  or replace a scale value. Add eligible, wrong-family, unknown-family, and valid/invalid ordering
  tests, and bind absence of ineligible scale in §5's program-state/evaluated projection. Apply the
  existing schema-compatibility discipline because this changes a published component meaning.
- **Touches interface/change-trigger region:** yes. The correction changes the consumer-visible
  meaning of the §5 program-state projection.

### candidate-011 — Virtual `*_pre` program names are admitted into the buffer-key domain

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1099,1583-1586,1688-1695,1719-1723,1953-1965`;
  authority at `docs/research/v1/RESEARCH.md:1135-1140,1519-1520`.
- **Claim:** `flip.<prog>.<buf>` must retain `deferred_pre`/`composite_pre` in the program position
  and keep the nested key restricted to a buffer identifier.
- **Evidence:** RESEARCH classifies `deferred_pre` and `composite_pre` as virtual program slots
  (`RESEARCH.md:1135-1140`) and says virtual `*_pre` programs are accepted in the `<prog>` position
  of `flip.<prog>.<buf>` (`:1519-1520`). The target's outer `ProgramKey` already accepts exact
  non-empty pack-facing program names (`PHASE_3_DOC.md:1583-1586,1688-1690`). It nevertheless
  defines `FlipBufferKey` as either `ColorAttachmentKey` or an exact virtual `*_pre` name
  (`:1691-1695`) and repeats that extra variant in binding §5 (`:1953-1965`), allowing an impossible
  state in which a virtual program name occupies `<buf>`. Phase 5 receives this nested key directly
  (`:1719-1723`) and must otherwise invent how to handle it.
- **Severity:** correction. Keep the existing broad `ProgramKey`, explicitly parse virtual-pre
  names there, and restrict `FlipBufferKey` to the normalized contract buffer domain. Test
  `flip.deferred_pre.<valid-buffer>` and `flip.composite_pre.<valid-buffer>` as outer virtual program
  keys, and diagnose/ignore a virtual program name in the buffer position. Apply the existing
  schema/compatibility rule for the changed published component meaning.
- **Touches interface/change-trigger region:** yes. Removing the invalid nested-key variant changes
  the Phase 5-facing §5 contract.

## 2. Checked and clean

- `candidate-001` is dropped on independent re-derivation and against specifically settled prior
  material. The v3 adoption rule says that promotion/selection is not adoption, expressly records
  Phases 3–9 as RC3-governed, and requires all four ordered adoption steps before the phase changes
  its declared governing design (`docs/design/v3/DESIGN.md:195-220`). The repository governance
  ledger likewise records Phase 3 as RC3 and v3 as adopted only by other named phases
  (`docs/MOVES.md:80-103,121-130`). The review-time v3 override permits the delta review; it does
  not make the existing RC3 header false or authorize relabeling before migration. Rounds 37 and 39
  specifically reached the same disposition on the same unchanged premise. No target edit is
  ordered; a maintainer who intends adoption must separately complete §G0.4.
- `candidate-004` is dropped as settled and unsupported by a changed premise. Round 23 admitted the
  original selector-coverage defect and its resolution made §5 the sole binding consumer contract;
  Round 36 then admitted the remaining public-declaration incorporation gap. The current target now
  expressly incorporates the exact §2.2 declarations and detailed value families into §5 and
  requires every edit to an incorporated declaration or consumer-visible semantic to update its
  §5 row in the same revision, thereby changing the monitored region
  (`PHASE_3_DOC.md:1877-1897`). An unpaired outside edit would violate that binding revision rule;
  neither v3 nor the manifest requires the interface hash to independently validate an invalid
  edit while ignoring the document's mandatory companion update. The candidate therefore reopens
  the already resolved monitoring theory rather than identifying a regression.
- `candidate-007`, concerning an include-only source shared across programs or stages, was
  eliminated at Refute before adjudication and remains outside the disposition set.
- The finder-reported clean areas were independently rechecked. The current compatibility
  aggregation, texture-stage expansion, schema-5 ID-map values, macro-family naming (apart from the
  exact option-family membership admitted above), failure-code/value bounds, dimension/source and
  geometry closure, materialization text/source-map pairing, persistence failure lifecycle,
  resource baselines, and consumed Phase 1 runtime contracts remain coherent.
- All thirteen mandatory sections are present and substantive. Pure-`:engine` placement, the
  complete F.1 ownership map, jcpp adoption, the four mandated Pintonium pitfall dispositions,
  reserved Phase 6 contribution slot, and OQ-7 spike structure remain covered. No finding outside
  the surviving candidate set was created.

## 3. Verdict
# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=8; notes=0
Interface changed: yes

Eight correction-sized findings remain. None requires rebuilding the Phase 3 architecture, so
`FAIL` is not warranted, but the nonzero correction count makes literal `PASS` unavailable. Seven
of the eight corrections alter discovery, persistence, option, macro, or program-state semantics
incorporated into the manifest-declared §5 region; only the revision-ledger correction is
non-interface.

The supplied trend is empty. The actual history has repeatedly reached literal PASS and then
reopened on later interface additions; Round 39's fix-up introduced or closed several declarations
that expose the token-issuance, durable-selection, and option-operation problems, while the
remaining conformance issues have not been specifically settled. This round therefore does not
establish convergence.

The next required action is a scoped fix-up resolving candidates 002, 003, 005, 006, 008, 009, 010,
and 011, recording the resolutions in this review. The fix-up must update the affected §5 rows and
apply §5.3's schema/compatibility rule where a published `PackConfiguration` component meaning
changes. Because the ordered corrections change the manifest-declared interface region, a fresh
whole-document verification round is required before Phase 3 can close or be consumed as a
verified dependency.

## Resolutions

### candidate-002 — applied

The target now separates durable selection identity from live authorization. Every filesystem
candidate publishes a validated `FilesystemCandidateReference`: kind plus an uppercase
percent-encoding of the NFC direct-child entry name, with no host root, display text, content hash,
or generation. `resolveFilesystemCandidate` authenticates a fresh result against the calling
front end's private latest snapshot and returns exactly resolved, missing, ambiguous, kind-changed,
or invalid-snapshot. Only resolved yields that snapshot's current ID. The conformance map, binding
§5, failure table, Phase 7 hand-off, milestone/checklist, and tests cover restart through a new
instance, supersession, reorder, duplicate display names, content change, removal, normalization
ambiguity, kind change, and foreign/stale snapshots.

### candidate-003 — applied

Because this fix-up itself creates the next current surface, the header now points to §0.40 rather
than stopping at the requested intermediate §0.39. The terminal chronology records both omitted
transitions: Round 39 reviewed §0.38 and produced §0.39, and Round 40 reviewed §0.39 and produced
§0.40. The document remains explicitly unverified pending a fresh whole-document review.

### candidate-005 — applied

The impossible private constructors were replaced by sealed public token interfaces whose only
permitted final implementations live in the unexported `.pack.internal` package and have
package-private constructors. The binding contract fixes object-identity equality/hash behavior,
hidden owning-instance/directory/generation/reference authentication, fresh issuance on discovery,
and nonserialization. Tests require ordinary issuance and reject consumer implementation/forgery,
foreign-instance, unknown, and superseded tokens before pack input is accessed.

### candidate-006 — applied

`PackOptionsTarget` is no longer a caller-constructed record. It is a sealed target implemented
internally and obtained through `PackFrontEnd.packOptionsTarget(reference)`. The operation encodes
every byte of the complete canonical candidate reference as uppercase `%HH`, prefixes `p`, and maps
only to `shaderpacks/p<encoded-reference>.txt`. This is injective on case-insensitive filesystems
and stable across discovery order and content changes; it never uses display text or an absolute
path. Binding, persistence detail, milestones, hand-offs, checklist, and collision/content-change
tests were updated.

### candidate-008 — applied

The canonical records now contain callable public implementations of
`OptionCatalog.find(String)`, `OptionState.value(String)`,
`OptionConfiguration.inferProfile(OptionState)`, and
`ScreenModel.resolvedColumns(int)`. Exact-name misses return empty, null arguments are programmer
errors, and negative option counts are rejected. Aggregate inference exists only on
`OptionConfiguration`, whose ordered list supplies descending constraint-count/source-order
precedence; the contradictory `ProfileModel.infer` claim was removed. Section 5 and operation tests
bind those receivers and outcomes.

### candidate-009 — applied

The conformance map, detailed macro design, and §5 now enumerate all eight RESEARCH §3.5 option
macros. Effective `EngineOptionData` keys provide the values: normal/specular and old-lighting
booleans conditionally emit `1`; render/shadow quality and hand depth always emit canonical finite
decimals; positive antialiasing level emits canonical integer `MC_FXAA_LEVEL`. The exact key-order
defaults are true, true, 1.0, 1.0, 0.125, false, false, and 0; malformed inputs warn before using
them. Every identity policy retains this shader-only family, and
identity/capability overrides are forbidden from targeting it. P3-C08 remains the v0.1 milestone;
the named test covers exact positive/negative membership, values, defaults, policies, and override
rejection, without resolving OQ-7.

### candidate-010 — applied

The shared exact `ProgramFamily` classifier now applies `{DEFERRED, COMPOSITE}` eligibility to
`scale.<prog>`. Wrong and unknown families warn, are ignored, and cannot create or replace scale
regardless of ordering; the evaluated Phase 4 projection therefore cannot contain an ineligible
scale. The conformance row, detailed parser semantics, §5 row/algebra, tests, milestone, and
checklist were updated. Because this changes the published
`ShaderPropertiesModel.programStates` component meaning, `CURRENT_SCHEMA_VERSION` and the nested
ID-map/current compatibility surface advance from 5 to 6; versions 1 through 5 are rejected.

### candidate-011 — applied

Virtual `deferred_pre` and `composite_pre` are now explicitly accepted only as the outer
`ProgramKey` in `flip.<prog>.<buf>`. `FlipBufferKey` is exactly a normalized
`ColorAttachmentKey`, with no program-name variant; a virtual name in `<buf>` warns and is ignored.
The conformance row, detailed and binding models, Phase 5 projection, tests, milestone, checklist,
and schema-6 compatibility rule were updated.

### Interface-change accounting

The manifest-declared cross-phase region changed intentionally. It now binds the durable candidate
reference/resolver, sealed issuance and target derivation, exact option operations, exact option
macro payload, filtered scale projection, buffer-only flip key, and schema 6. Per the declared
trigger, a fresh whole-document verification round is required before Phase 3 can close.

### Notes deferred

None. The adjudication admitted zero notes, and no correction was refused.
