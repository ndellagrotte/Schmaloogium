# Phase 3 verification review — round 41

## 0. Method and reading order

I first re-derived every surviving candidate from the current target and resolved source contract,
without consulting historical reviews. The decisive reads were:

1. `docs/phase3/v1/PHASE_3_DOC.md` as a whole, with focused checks of the canonical §2.2
   declarations, load and persistence flow, Appendix F and remaining-contract conformance rows,
   macro staging, program-state construction, binding §5 semantics, schema discipline, tests,
   milestones, and checklist;
2. `docs/design/v3/DESIGN.md` Part I, the mandatory template, the Phase 3 target specification, and
   the document gate selected by the manifest override;
3. `docs/research/v1/RESEARCH.md` as contract ground truth, especially §§3.1–3.5, §§4.1–4.2 and
   §4.7, Appendix A.1, Appendix B.1, Appendix F.7, and the milestone table;
4. `docs/phase1/v14/PHASE_1_DOC.md`, including its binding §5 module/package seam and the incorporated
   unnamed-module sealing rule; and
5. the permitted Pintonium/Oculus reports and shipped pack-author documentation where cited by the
   candidate evidence.

I searched the target and authorities for equivalent filename bounds, virtual-program family rules,
omitted viewport offsets, milestone gates, legal token issuance, a persisted-option load bridge,
flip-family filtering, and row-local provenance before settling the dispositions. Only after those
independent judgments were complete did I read all discovered prior reviews
`PHASE_3_REVIEW_1.md` through `PHASE_3_REVIEW_40.md`, including their resolutions, as settled
historical material. In particular, Round 40's resolutions explain the changed premises but do not
override defects in the resulting bytes.

There were no source-list deviations, no network use, and no agent fan-out. I did not invoke
`$verify-loop`, run a verification harness, start another session, or read any forbidden transcript,
chatlog, or `*.txt` source. No candidate was eliminated before adjudication, and the Gate reported
no drops. Candidate 007 is consolidated with candidate 003 on independent derivation because both
identify exactly the same omitted-offset defect and order the same repair.

## 1. Findings

### candidate-001 — The doubly encoded persistence filename exceeds a valid host component bound

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1254-1258,1350-1359,1949,1957,1979-1984`.
- **Claim:** The canonical reference-to-target mapping must produce a usable, collision-safe
  persistence location for every filesystem candidate admitted by the discovery contract.
- **Evidence:** A direct-child name of `n` UTF-8 bytes first becomes `d:` or `a:` plus `n`
  `%HH` triplets, for `2 + 3n` ASCII bytes. Section 4.3 then percent-encodes every byte of that
  complete reference and wraps it as the single component `p<encoded-reference>.txt`, yielding
  `1 + 3(2 + 3n) + 4 = 11 + 9n` bytes. An ordinary 28-byte source name therefore requires a
  263-byte target component, exceeding the common 255-byte component limit even though the source
  name itself is valid. `PackInputLimits.maxPathLength` bounds pack input paths, not this generated
  persistence component, and the target specifies neither sharding nor a bounded alternative.
  The persistence test inventory covers collisions, case-insensitive storage, symlinks, failures,
  and round trips, but no component/path-length boundary. Phase 3 owns the per-pack filename/read-
  write model (`docs/design/v3/DESIGN.md:1444-1448`), so this cannot be left to Phase 12.
- **Severity:** correction. Replace the literal single-component double encoding with an explicitly
  bounded collision-safe mapping, such as a verified full-reference record in fixed-length digest
  buckets with a collision protocol, or a reversible sharded encoding with proven per-component
  and total-path bounds. Define migration/collision behavior and add ASCII and multibyte boundary
  tests, including the current 27/28-byte transition and maximum accepted candidate names.
- **Touches interface/change-trigger region:** yes. The correction changes
  `packOptionsTarget`/`PackOptionsTarget` semantics incorporated into §5 and therefore must update
  the monitored row and compatibility accounting.

### candidate-002 — Virtual pre-programs are not excluded from viewport-scale classification

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1568-1575,1771-1794,1959,2041-2058,2447-2450`.
- **Claim:** Viewport scale must be published only for executable deferred/composite passes, while
  `deferred_pre` and `composite_pre` remain flip-control-only virtual positions.
- **Evidence:** RESEARCH Appendix A.1 labels both pre-programs “virtual — flip control only”
  (`docs/research/v1/RESEARCH.md:1135-1140`). The target nevertheless uses a closed classifier with
  only `SHADOW`, `GBUFFERS`, `DEFERRED`, `COMPOSITE`, `FINAL`, and `UNKNOWN`, explicitly says it
  classifies the virtual names, and makes scale eligible for `DEFERRED`/`COMPOSITE`. It never maps
  the recognized virtual names to distinct ineligible outcomes or defines an executable-pass
  predicate. The general `ProgramState` shape can consequently carry scale under either valid
  virtual outer key. The scale test names deferred/composite and wrong/unknown families but does
  not require rejection of either exact virtual name; only the neighboring flip test names them.
  Round 40 introduced the family filter but did not settle this virtual-slot distinction.
- **Severity:** correction. Give the virtual pre-slots distinct classifier outcomes, or use an
  explicit executable deferred/composite-pass predicate. State that these keys are flip-control-only,
  warn and ignore non-flip state on them, and add both exact virtual scale cases in relevant source
  orders. Apply §5.3 schema/version compatibility rules to the restricted published meaning.
- **Touches interface/change-trigger region:** yes. The correction changes the binding
  `ProgramStateModel` semantics consumed by Phases 4 and 5.

### candidate-003 — The valid one-token scale form has no deterministic `ViewportScale` projection

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1144,1771-1794,1959,2041-2048,2447-2450`.
- **Claim:** Every contract-valid `scale.<prog>` spelling must map deterministically to the published
  three-float viewport value.
- **Evidence:** RESEARCH Appendix F.7 and the shipped pack-author grammar both allow either one
  value or a scale plus an offset pair (`docs/research/v1/RESEARCH.md:1514-1521` and
  `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:440-443`). The binding value is
  exactly `(float scale,float offsetX,float offsetY)`, but the target gives no values or formula for
  the two fields when the valid one-token form omits them. Generic malformed/last-valid handling
  cannot resolve a syntactically valid form, and the named scale test states family, range, and
  ordering coverage without an expected one-token expansion. Phase 4 only applies the stored value;
  Phase 3 owns parsing and storage (`docs/design/v3/DESIGN.md:1431-1435`).
- **Severity:** correction. Resolve the omitted-offset semantics from admissible contract evidence
  rather than guessing, then bind the exact expansion in §§4 and 5. Enumerate exactly one- and
  three-token valid arities; route two-token, extra-token, nonnumeric, nonfinite, and out-of-range
  occurrences through the existing warn/ignore/retain-prior rule. Add explicit one-token,
  three-token, partial, extra, and malformed-after-valid assertions and apply §5.3 versioning.
- **Touches interface/change-trigger region:** yes. The repair defines an executable default and
  consumer interpretation for `properties.programStates` in the monitored §5 contract.

### candidate-004 — v0.1 emits normal/specular feature macros before the v0.5 feature exists

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1429-1445,1955,2497-2499,2685-2688`.
- **Claim:** A milestone must not advertise pack-visible renderer capability before the behavior
  selected by that macro is implemented.
- **Evidence:** The target defaults `normalMapEnabled` and `specularMapEnabled` to true, emits
  `MC_NORMAL_MAP 1` and `MC_SPECULAR_MAP 1` into shader source at P3-C08/v0.1, and prevents every
  identity policy or per-pack override from suppressing this option family. RESEARCH's more specific
  schedule places the `_n`/`_s` companion atlases and these two named macros together at v0.5
  (`docs/research/v1/RESEARCH.md:947-952`), while the governing design says milestone tags mean
  “implemented at that milestone” and distinguishes full-subsystem architecture from later
  implementation (`docs/design/v3/DESIGN.md:185-193,567-571`). Phase 13 owns the companion atlas
  system at v0.5 (`docs/design/v3/DESIGN.md:625`). Packs branch on the macros, so this is observable
  selection of unavailable functionality, not merely a future-shaped data model.
- **Severity:** correction. Keep the complete option-macro model architected, but gate effective
  `MC_NORMAL_MAP`/`MC_SPECULAR_MAP` emission on the corresponding implemented/enabled renderer
  capability and stage activation at v0.5. Align §4.4, §5, P3-C08/§9, checklist text, and tests with
  negative v0.1 and positive/negative v0.5 cases; apply interface/schema-version accounting.
- **Touches interface/change-trigger region:** yes. The effective `MacroConfiguration` payload is a
  published Phase 4 materialization contract.

### candidate-005 — Cross-package sealed implementations are illegal in Phase 1's unnamed module

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:358,396-399,635-640,745-750,1952,1963-1984`.
- **Claim:** The canonical capability declarations must be legal Java under the binding Phase 1
  module/package architecture while preserving engine-only issuance.
- **Evidence:** `PackCandidateId`, `DiscoveryGeneration`, and `PackOptionsTarget` are sealed public
  interfaces in `com.schmaloogium.engine.pack` whose direct permitted implementations are declared
  in `com.schmaloogium.engine.pack.internal`. Phase 1 expressly rejects JPMS and runs `:engine` in
  the unnamed module, where a sealed type's permitted direct subtypes must share its package
  (`docs/phase1/v14/PHASE_1_DOC.md:2203-2220,4765`). A Gradle project and the `.internal` bytecode
  convention are not a named JPMS module. Constructor visibility, origin authentication, and seam
  scans happen after source legality and cannot make the `permits` clauses compile. Round 40's
  resolution introduced this exact arrangement while repairing the earlier private-constructor
  issue, so prior settled material confirms the changed premise rather than clearing it.
- **Severity:** correction. Either retain sealing and place package-private final permitted
  implementations in `com.schmaloogium.engine.pack`, with a legal package-local issuance path that
  delegates internals as needed, or use an unsealed/non-sealed marker plus mandatory issuer/origin
  authentication at every acceptance boundary. Apply the same coherent design to the persistence
  target and retain issuance/forgery/foreign-origin tests.
- **Touches interface/change-trigger region:** yes. The exact canonical declarations and package/
  issuance promises are incorporated into §5 and must change.

### candidate-006 — Persisted per-pack option values cannot enter an atomic load

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:430-437,667-683,1023-1047,1350-1373,1380-1382,1905-1908,1949,1973-1984,2192-2195`.
- **Claim:** The v0.1 load path must produce one coherent `PackConfiguration` whose option state,
  option-dependent analysis, and fingerprint include persisted pack values using published inputs.
- **Evidence:** The governing Phase 3 specification requires parsing and applying persisted options
  at v0.1 (`docs/design/v3/DESIGN.md:1444-1448`). The target's load pipeline decodes per-pack changed
  values before finalizing options and option-sensitive analysis, and says
  `OptionConfiguration.state()` is the load-time default-plus-persistence snapshot. Yet
  `PackLoadRequest` carries neither persisted bytes/state nor a configured
  `PersistenceFileAccess`. The only declared pack read requires a target, catalog, and baseline—the
  latter two produced during loading—and §4.3 says Phase 12 supplies `PersistenceFileAccess` per
  standalone codec operation. Its returned immutable `OptionState` has no route back into a new
  load request or the already published configuration. Post-load materialization cannot repair the
  stored option snapshot, resource analysis, or configuration fingerprint, and consumers are
  forbidden to rescan/reinterpret the pack.
- **Severity:** correction. Bind one executable integration path. A one-stage design can provide a
  configured persistence capability to the load transaction (or explicitly bind construction from
  the host directory), derive the authenticated target after discovery, build the catalog/baseline,
  read persisted values, and use the result for every option-dependent step and fingerprint. A
  two-stage inspect/finalize protocol is also viable if only its final stage publishes
  `PackConfiguration`. Define `Internal` and failure/absence behavior, and add initial-load/reload
  tests proving a persisted non-default changes state, source/resource analysis, and fingerprints.
- **Touches interface/change-trigger region:** yes. Closing the consumer-visible load/persistence
  orchestration changes an incorporated request/operation or its binding §5 semantics.

### candidate-008 — Flip overrides lack the contract's deferred/composite family filter

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1145,1568-1575,1771-1809,1959,2041-2058,2447-2450`.
- **Claim:** `flip.<program>.<buffer>` must be accepted only for deferred/composite program
  positions, including the two virtual pre-programs, before Phase 5 receives the explicit map.
- **Evidence:** The shipped pack-author contract says flip is for a buffer in a specific composite
  or deferred program
  (`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:445-451`), and RESEARCH ties
  ping-pong flips to deferred/composite passes while explicitly preserving virtual pre-flips
  (`docs/research/v1/RESEARCH.md:1210-1214,1518-1521`). The target's conformance and binding rules
  constrain only the outer/buffer key positions. Its stated exhaustive family predicates cover
  clear, clear color, mipmap, and scale, but omit flip. Thus an out-of-family value such as
  `flip.gbuffers_terrain.colortex0=true` can enter the map sent directly to Phase 5, whose consumers
  may not reinterpret properties. This is separate from candidate 002: that finding excludes
  virtual keys from scale, whereas this one admits the virtual keys for flip but excludes all
  non-deferred/composite families.
- **Severity:** correction. Add flip to the exact-name family applicability mechanism: accept
  `DEFERRED` and `COMPOSITE`, including `deferred_pre` and `composite_pre`; warn and ignore
  `SHADOW`, `GBUFFERS`, `FINAL`, and `UNKNOWN` before state creation/mutation; preserve prior valid
  values on ineligible duplicates. Update the conformance row, producer and binding semantics,
  tests, checklist, and schema/version compatibility.
- **Touches interface/change-trigger region:** yes. Filtering published `explicitFlips` changes the
  meaning of `properties.programStates` consumed by Phase 5.

### candidate-009 — Three conformance rows carry incomplete or incorrect provenance

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1224-1226`.
- **Claim:** Each mandatory conformance-map row must cite the authority that actually establishes
  all mapped behavior and any adopted reference mechanism.
- **Evidence:** Row 1224 cites only RESEARCH §3.1 for OFF/internal sentinels and nested-root
  discovery, although §3.1 states folder/zip layout while RESEARCH §4.1 supplies sentinels,
  nested-root tolerance, sanitization, and lifecycle behavior
  (`docs/research/v1/RESEARCH.md:210-222,474-485`). Row 1225 attributes archive closure and the
  complete containment guard solely to §3.1 even though the governing Phase 3 scope cites both
  §§3.1/4.1 and identifies PD §7.1 as the zip-lifecycle mechanism
  (`docs/design/v3/DESIGN.md:1360-1368`); the target itself gives that PD citation only later in
  §4.1. Row 1226 cites only §3.2 for compiler attribution, while `#line` bookkeeping appears in the
  behavioral compile flow at RESEARCH §4.2 and in the governing source-model requirement
  (`docs/research/v1/RESEARCH.md:497-500`; `docs/design/v3/DESIGN.md:1369-1374`). The mandatory
  template requires row-local provenance and PD citations for adopted mechanisms
  (`docs/design/v3/DESIGN.md:829-835`); correct prose elsewhere does not repair these cells.
- **Severity:** correction. Correct or split the cells: use RESEARCH §§3.1 and 4.1 for layout and
  discovery behavior; cite PD §7.1 for the adopted archive-lifecycle discipline while identifying
  stricter guards as Phase 3 safety design where appropriate; and cite RESEARCH §§3.2/4.2 plus the
  governing source-model requirement for include/source attribution, adding PD only for mechanisms
  actually adopted.
- **Touches interface/change-trigger region:** no. This orders a row-local provenance correction,
  not a change to the published discovery or source contracts.

## 2. Checked and clean

- Candidate 007 is dropped as an exact duplicate of admitted candidate 003. Both cite the same
  one-or-three-token scale grammar, the same mandatory three-float `ViewportScale`, the same absence
  of omitted-offset semantics, the same test gap, and the same interface/version repair. Its
  substance is represented once rather than double-counted.
- The finder-reported clean areas were rechecked. No additional defect survives in token
  authentication apart from the Java package legality in candidate 005; durable-reference NFC
  collision handling apart from target-length bounds; exact option lookup/profile/screen operations;
  persistence status/failure algebra; flip buffer-position grammar; dimension/source/materializer,
  ID-map, texture, resource, and declared-uniform closures; or schema-6 chronology and rejection
  references.
- Phase 3's consumption of Phase 1's `GLCapabilityProfile`, fixed log channels, loader-neutral
  diagnostics, debug flag, pure-`:engine` placement, and notice mechanism matches the binding
  dependency contract. The jcpp build pin remains requested rather than silently assumed.
- The complete F.1 ownership map, the other Appendix F and Appendix A.3 rows, all four mandated
  Pintonium pitfall dispositions, OQ-7 spike shape, thirteen mandatory sections, failure ladder,
  milestones, hand-offs, and checklist are otherwise substantive. Candidate 009 is limited to
  provenance in three existing conformance cells; it does not allege missing discovery/source
  architecture.
- Prior settled material does not clear the admitted findings. Round 40 created the persistence
  mapping, cross-package sealing, exact option-macro emission, and scale filtering now under review;
  its resolution did not bound the filename, satisfy unnamed-module sealing, align the two macros
  with v0.5, distinguish virtual scale slots, define one-token offsets, or connect persisted state
  to load. Earlier broad clean statements did not specifically settle the flip-family filter or the
  three row-local provenance errors.
- No candidates were eliminated before adjudication, there were no Gate drops, and no admitted
  candidate was softened to a note.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=8; notes=0
Interface changed: yes

Eight distinct correction-sized findings remain after consolidating candidate 007 into candidate
003. None requires rebuilding Phase 3's publication architecture, so `FAIL` is not warranted, but
nonzero corrections make literal `PASS` unavailable. Seven corrections change incorporated
consumer-visible declarations or semantics in the manifest-declared §5 region; candidate 009 is the
sole non-interface provenance repair.

Round 40 also had eight corrections, and Round 41 remains at eight after one duplicate is removed.
Several current defects arise directly from Round 40's new persistence, token, macro, and
program-state surface, while the load orchestration, flip-family, and provenance gaps remain
independently live. The correction count is therefore not decreasing, and the document has not
converged despite prior literal-PASS rounds.

The next required action is a scoped fix-up resolving candidates 001, 002, 003, 004, 005, 006, 008,
and 009, with resolutions recorded in this review. The fix-up must update every affected §5 row,
apply §5.3 schema/version and producer-consumer compatibility rules where component meaning or an
executable default changes, align milestones and tests, and keep the provenance-only repair
shape-stable. Because at least one ordered correction changes the monitored cross-phase region, a
fresh whole-document verification round is required before Phase 3 can close or be consumed as a
verified dependency.

## Resolutions

### Applied corrections

1. **candidate-001:** Replaced the unbounded double-encoded component with a fixed SHA-256 bucket
   key and four-hex-digit slots. Every slot filename is 74 ASCII bytes, every file carries an exact
   canonical-reference identity line, reads reject duplicate matches, writes select the lowest free
   slot, and exhaustion fails without aliasing. Added ASCII/multibyte boundary, maximum-name,
   collision, malformed-record, exhaustion, case-insensitive, and legacy-no-probe tests. The former
   proposal was never released, so silently probing it would create ambiguity rather than migration.
2. **candidate-002:** Split the private family classifier into executable `DEFERRED`/`COMPOSITE`
   and virtual `DEFERRED_PRE`/`COMPOSITE_PRE` outcomes. Virtual slots are now flip-only; non-flip
   state warns and is ignored, and both exact virtual scale cases are tested.
3. **candidate-003 (including duplicate 007):** The documented one-or-three-token grammar now has
   an exact projection: one token means `(scale,0,0)`, the origin-preserving interpretation of
   omitted offsets, while three tokens supply both offsets. Every other arity and malformed,
   nonfinite, or out-of-range input warns and retains prior valid state; tests enumerate all forms.
4. **candidate-004:** Added independent runtime renderer-availability bits and made each companion
   macro require both its engine option and active companion-atlas capability. Both are absent at
   v0.1; P3-C23 activates them at v0.5. The six other effective option macros remain P3-C08/v0.1,
   with negative v0.1 and full positive/negative v0.5 tests.
5. **candidate-005:** Kept sealing but moved each package-private final permitted implementation
   and the package-private issuing front end into `com.schmaloogium.engine.pack`. Helpers may remain
   under `.internal`; this compiles in Phase 1's unnamed module while preserving authenticated,
   nonserializable, engine-only capability issuance.
6. **candidate-006:** Added selection-dependent `PackLoadRequest.persistenceFiles`. A filesystem
   transaction now derives the target from its authenticated candidate, builds catalog/baseline,
   reads persisted values, and uses one finalized state for properties, rewriting, analysis,
   publication, and fingerprinting. Absence or failure uses the baseline (failure diagnosed);
   internal loads ignore the capability. Initial-load and reload effects are asserted.
7. **candidate-008:** Flip applicability now accepts only executable and virtual-pre deferred/
   composite keys. Shadow, gbuffers, final, and unknown keys warn and cannot create, mutate, or
   replace state; tests cover every family, both virtual names, source orders, and invalid buffers.
8. **candidate-009:** Corrected the three row-local provenance cells: discovery now cites RESEARCH
   §§3.1/4.1; archive lifecycle cites those sections and PD §7.1 while identifying stricter guards
   as local safety design; source attribution cites RESEARCH §§3.2/4.2 and the governing source-model
   requirement without claiming an unadopted PD mechanism.

### Interface and compatibility accounting

The monitored §5 region intentionally changed for candidates 001–008: load/request, persistence
target, legal capability declarations, renderer-feature/macro semantics, and program-state
projections are all incorporated there. Program-state defaults/filtering and effective macro
defaults change existing `PackConfiguration` component meanings, so the schema advances from 6 to
7; schema-6 consumers reject 7 and all current ID-map/version references and compatibility tests
were aligned. Candidate 009 changes provenance only. A fresh whole-document verification round is
therefore required before Phase 3 can close.

### Notes deferred

None. The adjudicator admitted no notes, and no correction was refused.
