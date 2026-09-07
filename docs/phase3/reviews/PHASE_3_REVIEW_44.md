# Phase 3 verification review — round 44

## 0. Method and reading order

I independently re-derived every surviving candidate before consulting historical reviews. In
load-bearing order, I read:

1. the complete `docs/phase3/v1/PHASE_3_DOC.md`, including the header and actual-input ledger,
   canonical declarations, conformance maps, detailed discovery, option, preprocessing, texture,
   and evaluator semantics, the complete manifest-declared §5 interface region, tests, decisions,
   hand-offs, checklist, and closing ledger;
2. the resolved authority `docs/design/v3/DESIGN.md`, specifically Part I, the Phase 3 target
   specification, the document gate, and the mandatory template;
3. `docs/research/v1/RESEARCH.md` as contract ground truth, particularly source/fallback,
   preprocessing, option/profile, and Appendix F texture/program-state semantics; and
4. the binding contract in `docs/phase1/v14/PHASE_1_DOC.md` for the inherited architecture and
   package/interface seam.

I searched the whole target for equivalent state-construction operations, catalog/configuration
identity checks, invalid-discovery branches, source-root projections, executable-key definitions,
texture provenance, and preprocessing/test coverage. The permitted Pintonium and Oculus supporting
reports were not needed. Only after settling the candidates independently did I read all discovered
prior reviews `PHASE_3_REVIEW_1.md` through `PHASE_3_REVIEW_43.md`, in order and including their
resolutions, to apply settled material and detect changed premises.

There were no source-list deviations, no network use, and no agent fan-out. I did not invoke
`$verify-loop`, run a verification harness, start another session, or read a forbidden transcript,
chatlog, or `*.txt` source. The Gate reported no drops. `candidate-007` had already been eliminated
at Refute and was not revived or included in the adjudicated disposition set.

## 1. Findings

### candidate-002 — The lowercase-PNG restriction is falsely attributed to Appendix F.5

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1245` (§3.2), `:2437-2440` (§5.3), and
  `:2852` (D-P3-38).
- **Claim:** The conformance map and schema rationale must distinguish authoritative texture syntax
  from a Phase 3 interpretation of an ambiguity.
- **Evidence:** RESEARCH Appendix F.5 specifies a “pack-relative PNG path” but supplies neither an
  extension-case rule nor a non-empty-final-stem predicate (`docs/research/v1/RESEARCH.md:1484-1489`).
  The target's conformance row nevertheless attributes the exact non-empty-stem and literal
  lowercase `.png` restriction only to Appendix F.5 (`PHASE_3_DOC.md:1245`), and §5.3 calls the
  narrowed lexical domain “authority-required” (`:2437-2440`). The decision log correctly describes
  the same restriction as D-P3-38's interpretation of Appendix F.5 (`:2852`). The governing design
  requires exact provenance for pack-visible Appendix F behavior and requires deviations or local
  interpretations to be flagged rather than silently attributed to the contract
  (`docs/design/v3/DESIGN.md:552-558,831-835`).
- **Severity:** correction. Preserve the explicit parser policy and schema-8 compatibility break if
  that remains the chosen design, but cite and label D-P3-38 in the §3.2 row and replace §5.3's
  “authority-required” wording with an explicit D-P3-38 interpretation of Appendix F.5. Do not
  claim that Appendix F.5 literally defines extension case or stem grammar.
- **Touches interface/change-trigger region:** yes. The inaccurate authority claim is repeated in
  §5.3, so the ordered correction changes the manifest-declared interface region and requires a
  fresh verification round even though the executable policy may remain unchanged.

### candidate-004 — The public option-state boundary cannot enforce its catalog-relative invariant

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:595-618,768-783,980-985,1514-1528`, and the
  option/persistence publication in §5.
- **Claim:** Phase 3 must define how a valid candidate `OptionState` is constructed or updated and
  how every receiver establishes that the state belongs to the expected option catalog and pack.
- **Evidence:** `OptionState` is a publicly constructible record containing only
  `Map<String,OptionValue>` and exposes only exact-name lookup (`PHASE_3_DOC.md:595-599`); it carries
  no catalog/configuration identity and has no catalog-bound update operation. The detailed design
  nevertheless promises that its map contains only valid known names, while ambiguous definitions
  cannot be rewritten (`:1514-1524`). `SourceMaterializer` accepts a freely supplied state
  (`:980-985`), and standalone persistence requests independently accept a pack target, catalog,
  and baseline/current state (`:768-783`). The existing bundle authentication binds file access and
  targets to a service domain, but it does not establish that independently supplied catalog and
  state values belong to that target's pack. Section 5 publishes lookup and inference but no closed
  state-construction, update, validation, or mismatch result. Phase 3 owns the option model and
  persistence codec; Phase 12 owns GUI interaction timing, not this invariant.
- **Severity:** correction. Publish one coherent Phase-3-owned construction/update and validation
  protocol. Define exact outcomes for unknown or missing names, nulls, Boolean/text kind mismatch,
  `DISABLED_AMBIGUOUS`, and syntactically safe out-of-list text values. Require materialization,
  profile/program evaluation, and persistence to reject or return a closed failure for a state
  incompatible with the receiving configuration/catalog; standalone persistence must also detect
  same-domain cross-pack target/catalog/state pairing before rewrite or I/O. An opaque identity is
  one valid design, but an equally exact enforceable protocol is acceptable. Add invalid-state and
  same-domain cross-pack tests and apply §5.3 only if the published configuration shape or meaning
  becomes incompatible.
- **Touches interface/change-trigger region:** yes. The repair changes incorporated option,
  materialization, and persistence declarations or consumer-visible semantics in §5.

### candidate-005 — Invalid discovery has no coherent generation identity or snapshot effect

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:413-424` (§2.2) and `:2328-2351` (§5 discovery
  binding).
- **Claim:** Every `discover` outcome must have a defined capability identity, resolver result, and
  deterministic interaction with prior valid snapshots, including a request whose directory cannot
  be resolved.
- **Evidence:** `PackDiscoveryResult` always carries a non-optional `DiscoveryGeneration`, and its
  candidate list has non-optional IDs (`PHASE_3_DOC.md:413-424`). Section 5 derives directory
  identity only after `toRealPath()` succeeds and yields a readable real directory (`:2328-2333`),
  but says an invalid path still returns an immutable discovery result with sentinel candidates and
  that every discovery issues tokens whose hidden authentication includes that real-directory
  identity (`:2334-2344`). The resolver is then defined only for the calling instance's latest
  snapshot “for that real directory” (`:2347-2351`). When real-path resolution fails, the required
  token identity does not exist; no other clause defines an unkeyed invalid generation, mandates its
  resolver outcome, or says whether the failed attempt preserves or supersedes any earlier valid
  directory snapshot.
- **Severity:** correction. Preferably retain the current shape by defining an engine-issued,
  domain-authenticated but non-directory-keyed invalid generation, exact sentinel-ID semantics,
  mandatory `InvalidSnapshot` resolution, and the exact rule that such a call supersedes no
  directory-keyed snapshot (or another explicitly chosen deterministic rule). Alternatively,
  introduce a closed invalid-request result variant. Add invalid-before-valid and
  valid-before-invalid tests, including a formerly valid path that becomes unavailable, and state
  concurrency/publication behavior.
- **Touches interface/change-trigger region:** yes. Generation authentication, resolver failure,
  and snapshot lifecycle are incorporated consumer-visible discovery semantics in §5.

### candidate-006 — Staged roots are not deterministically projected to executable program keys

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:691-692,960-977,1105-1110,1398-1403,
  1954-1971,2245-2261,2690-2693`.
- **Claim:** The aggregate evaluator must define one exact source-backed `ProgramKey` universe for
  profile matching, diagnostics, result membership, and ordering.
- **Evidence:** `SourceKey` includes dimension, program name, shader stage, and source identity, and
  `SourceCatalog.roots()` therefore exposes stage/source-level roots (`PHASE_3_DOC.md:960-977,
  1105-1110`). The index can publish `.vsh`, `.fsh`, and `.gsh` roots for one logical base program
  (`:1398-1403`), while `ProgramKey` retains only dimension and program name (`:691-692`). The
  aggregate evaluator repeatedly relies on an undefined set of “executable source keys” for
  qualified/unqualified disables and unknown-program warnings, then defines its evaluated domain as
  the union of all explicit property keys and resolved disable keys (`:1954-1969`). Binding §5
  repeats that dependence while promising Phase 4 only executable keys (`:2245-2261`). The target
  never specifies the root-to-key projection, stage/source de-duplication, source-presence
  qualification, or what happens to an explicit property key with no projected source. Different
  reasonable implementations can therefore publish different state and fallback inputs. The named
  aggregate test does not cover multi-stage collapse, partial roots, or source-absent explicit
  properties (`:2690-2693`); RESEARCH makes source absence and disablement load-bearing for whole
  configuration fallback (`docs/research/v1/RESEARCH.md:1147-1153`).
- **Severity:** correction. Define in §§4.8 and 5 one immutable, distinct, ascending
  `Set<ProgramKey>` derived from an exact source collection. State the projection, de-duplication,
  and whether any directly materializable root or a particular stage-presence combination qualifies;
  distinguish this from Phase 4's later compile/link success. Use the same set for profile matching,
  diagnostics, result membership, and ordering. Reconcile source-absent explicit property keys with
  the “only executable keys” hand-off, and keep virtual-pre flip-only keys separate. Add multi-stage,
  partial-stage, cross-dimension same-name, source-absent property, and qualified/unqualified disable
  assertions.
- **Touches interface/change-trigger region:** yes. This defines the evaluator domain and values
  published through §5 to Phases 4 and 5.

## 2. Checked and clean

The finder-reported clean areas were rechecked against the current bytes. The typed target-acquisition
rejection algebra, ordinary discovery linearization, direct persistence locations and safe-file
lifetime, bundle-domain construction, persistence integration into load, schema-8 repetition and
rejection, ID-map schema/fingerprint treatment, package placement, failure rows, milestone IDs,
checklist traceability, Phase 1 capability/diagnostic/logging/debug/notice seams, thirteen-section
shape, OQ-7 spike, and the remaining Appendix F/A.3 conformance surfaces yielded no additional
finding from the supplied candidate set.

`candidate-001` is dropped on independent re-derivation and against settled rounds 39, 40, and 42.
The v3 §G0.4 adoption protocol expressly says that Phase 3 keeps its declared RC3 governance until
all four migration steps are complete (`docs/design/v3/DESIGN.md:195-220`). The v3 selection override
supports the required dry-run/delta assessment; it does not by itself make the still-current RC3
header or actual-input ledger false, and mechanically relabeling those fields before completion
would contradict the same rule. A maintainer intending formal adoption must complete §G0.4 and its
governance bookkeeping as a separate operation, but this candidate establishes no current target
correction. The similarly framed `candidate-007`, eliminated before adjudication at Refute, remains
excluded and was not revived.

`candidate-003` is dropped as an exact substantive duplicate of admitted `candidate-006`. Both
identify the missing projection from staged `SourceCatalog.roots()` values to unique stage-free
`ProgramKey` values and order the same §4.8/§5 algorithm-and-test repair. Candidate 006 is retained
because it also captures the directly related conflict between unioning raw explicit property keys
and promising Phase 4 only executable keys; counting candidate 003 separately would double-count
one correction.

`candidate-008` is dropped. The abbreviated conformance-row word “properties” does not erase the
ID-mapping workload: the load algorithm explicitly preprocesses each ID-map input with standard A–G
macros only (`PHASE_3_DOC.md:1158-1159`), the macro contract explicitly excludes option macros from
ID maps (`:1601-1603`), and the shared properties-safe preprocessing/parser path is fixed in
§§4.5/4.9 (`:1675-1685,1988-1996`). Section 8 decomposes grammar coverage into all conditional forms,
both `defined` syntaxes, and substitution (`:2567-2570`), supplies ID-map integration cases
(`:2637-2649`), and the checklist requires all `preprocess_*` and `idMap_*` tests. Repeating the
umbrella test identifier in §8.1 or spelling out every ID file in the map would be optional catalog
clarity, not a missing contract or ordered correction.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=4; notes=0
Interface changed: yes

Four distinct correction-sized findings remain. None requires rebuilding the Phase 3 architecture,
so `FAIL` is not warranted; the nonzero correction count makes literal `PASS` unavailable. Every
admitted correction changes wording, declarations, or consumer-visible semantics in the monitored
§5 region, so the derived interface disposition is `yes`.

The supplied recent trend is 8 → 8 → 5 → 5 corrections for rounds 40–43; this round lowers the count
to 4, but the five-round sequence is still not strictly decreasing because of the earlier plateaus,
and Phase 3 has not converged to PASS. The drop is genuine after duplicate consolidation and
re-derivation, but it does not permit closure.

The next required action is a scoped fix-up for candidates 002, 004, 005, and 006, with each
resolution recorded in this review, all affected §5 rows updated, and §5.3 compatibility accounting
applied where a published shape or meaning changes. Because those repairs alter the
manifest-declared cross-phase interface region, a fresh whole-document verification round is
required before Phase 3 can close or be consumed as a verified dependency.

## Resolutions

### candidate-002 — applied

Re-reading Appendix F.5 establishes only “pack-relative PNG path”; it does not establish extension
case or a non-empty-stem grammar. The parser policy remains a permissible local interpretation.
The §3.2 conformance row, §5 texture row, and §5.3 history now attribute that narrowing to
D-P3-38, while preserving the schema-8 compatibility break it originally caused.

### candidate-004 — applied

The public map record was replaced by sealed, engine-issued `OptionCatalog` and `OptionState`
interfaces. A catalog now owns complete default construction, exact-map construction, single-name
update, and validation, with deterministic closed outcomes for null, foreign catalog, unknown or
missing name, null value, Boolean/text mismatch, ambiguity, and unsafe text. Safe out-of-list text
continues to warn and succeed under D-P3-14. Profile inference, program evaluation, materialization,
and both persistence operations validate the receiving catalog; persistence also compares the
target's exact candidate credential, not merely its durable reference, so a same-domain cross-pack
pair fails before I/O. The nested `options` meaning is incompatible and advances the schema to 9.

### candidate-005 — applied

Failed real-directory resolution now issues a domain-authenticated, non-directory-keyed invalid
generation containing exactly fresh Off/Internal sentinels. Its resolver result is always
`InvalidSnapshot`; sentinel target acquisition is `NON_FILESYSTEM`. Invalid discovery never
publishes or supersedes a directory-keyed snapshot, including when a formerly valid path vanishes,
and completion-linearized concurrency plus invalid-before/after-valid tests make that rule exact.

### candidate-006 — applied

`SourceCatalog.executablePrograms()` is now the immutable distinct ascending dimension/name
projection of indexed `.vsh`, `.fsh`, and `.gsh` roots, excluding virtual-pre slots. Any one stage
establishes source presence; stage multiplicity collapses, dimensions remain distinct, include-only
files do not qualify, and compile/link success remains Phase 4. Profile disables, diagnostics, and
evaluated membership use exactly that set; every projected key appears once, source-absent raw
properties warn and are omitted, and virtual-pre keys remain confined to the separate flip map.

### Interface and compatibility disposition

All four resolutions intentionally edit the manifest-declared §5 interface region. Candidate 004
and candidate 006 change existing `PackConfiguration` component meaning, so schema 9 and a
schema-8 rejection test are required. The declared change trigger fires: a fresh whole-document
verification round is required before Phase 3 can close.

### Notes deferred

None. The adjudication admitted zero notes, so no note was applied or deferred.
