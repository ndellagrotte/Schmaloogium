# Phase 3 Adversarial Review — Round 4

## 0. Method and reading order

I independently re-derived every candidate from, in order:

1. `docs/phase3/v1/PHASE_3_DOC.md`;
2. the selected Part I, Phase 3 specification, doc-gate, and mandatory-template material in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the relevant ground truth in `docs/research/v1/RESEARCH.md`;
4. the binding interface region of `docs/phase1/v14/PHASE_1_DOC.md`; and
5. the supplied candidate records and permitted supporting evidence.

Only after settling those judgments did I read
`docs/phase3/reviews/PHASE_3_REVIEW_1.md`,
`docs/phase3/reviews/PHASE_3_REVIEW_2.md`, and
`docs/phase3/reviews/PHASE_3_REVIEW_3.md`, including their resolutions. I made no deviation from
the assigned reading order, used no network access, performed no agent fan-out, and read no
forbidden source. The Gate reported no drops, and no candidate was eliminated before adjudication.

## 1. Findings

### candidate-001 — Macro contribution cardinality and type are inconsistent

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:219-228`

**Claim:** The public macro-contributor flow cannot be implemented as written because its producer
and consumer use incompatible types without a declared bridge.

**Evidence:** `MacroContributor.contribute` returns singular `MacroContribution`, while
`SourceMaterializer.materialize` accepts otherwise undeclared plural `MacroContributions`
(`docs/phase3/v1/PHASE_3_DOC.md:219-228`). The complete §5 publication surface exposes only
singular `MacroContribution` and directs Phase 6 to pass that returned contribution to
`materialize` (`docs/phase3/v1/PHASE_3_DOC.md:761-762`). The design names only one optional
reserved contributor (`docs/phase3/v1/PHASE_3_DOC.md:231-234`), and the document supplies no
aggregate construction or conversion rule.

**Severity:** correction. Make §§2 and 5 use one coherent type. The narrow design can accept the
singular optional contribution; a plural aggregate instead requires a declared shape and exact
empty/conversion semantics.

**Touches interface/change-trigger region:** yes.

### candidate-002 — Failure prose bypasses `PackLoadResult.Failed`

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:130-132`

**Claim:** Two normative descriptions contradict the declared load-result algebra by describing a
raw failure payload as the load return.

**Evidence:** `PackFrontEnd.load` returns `PackLoadResult`
(`docs/phase3/v1/PHASE_3_DOC.md:150-152`), whose failure variant is
`Failed(PackLoadFailure)` (`docs/phase3/v1/PHASE_3_DOC.md:181-184`). The central publication
invariant instead says a load returns `PackLoadFailure`
(`docs/phase3/v1/PHASE_3_DOC.md:130-132`), and the degradation table repeats “return
`PackLoadFailure`” (`docs/phase3/v1/PHASE_3_DOC.md:824-825`).

**Severity:** correction. State all outcomes as `PackLoadResult.Off`,
`PackLoadResult.Loaded(configuration)`, or `PackLoadResult.Failed(failure)`, including the
degradation row.

**Touches interface/change-trigger region:** no.

### candidate-003 — `SourceMaterializer` has no unavailable-source outcome

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:219-224`

**Claim:** Phase 4 and the Phase 2 harness must invent how an unavailable source root is reported
through the published materializer.

**Evidence:** `SourceMaterializer.materialize` unconditionally returns `MaterializedSource` and
declares no failure or exception behavior (`docs/phase3/v1/PHASE_3_DOC.md:219-224`). Yet include
graph failures explicitly make roots unmaterializable
(`docs/phase3/v1/PHASE_3_DOC.md:479-484`), spoofed markers are fatal for a source
(`docs/phase3/v1/PHASE_3_DOC.md:596-599`), and the failure policy requires attributed unavailable
roots to be handed to Phase 4 as rung-3 program failures
(`docs/phase3/v1/PHASE_3_DOC.md:819-821`). Section 5 publishes the materializer directly to Phase 4
and the Phase 2 harness (`docs/phase3/v1/PHASE_3_DOC.md:761`), but exposes no availability query or
equivalent outcome contract.

**Severity:** correction. Define in §§2 and 5 a deterministic success/unavailable outcome,
including attributed diagnostics and whether materialization can throw. A sealed result is one
valid design, not a required implementation choice.

**Touches interface/change-trigger region:** yes.

### candidate-004 — The reserved macro slot has no representable binding shape

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:226-234`

**Claim:** The exposed contract does not let Phase 6 populate, or Phase 4 validate and apply, the
named optional reserved slot without inventing data semantics.

**Evidence:** `MacroContributor` returns an undefined `MacroContribution`; the surrounding prose
names `phase6.centerDepthSmoothRedirect` and says absence is valid, but the signature has no slot
identity or absence representation (`docs/phase3/v1/PHASE_3_DOC.md:226-234`).
`MacroConfiguration` lists `reservedContributors` without defining their shape
(`docs/phase3/v1/PHASE_3_DOC.md:536-546`). Section 5 declares this the complete publication surface
and promises the handoff and valid absence, but defines neither permitted contribution payload nor
validation and application semantics (`docs/phase3/v1/PHASE_3_DOC.md:758-763`). The general
collection-order rule (`docs/phase3/v1/PHASE_3_DOC.md:791-800`) does not cure those omissions.

**Severity:** correction. Define the minimal typed slot in §§2 and 5: its explicit empty form,
permitted macro operation/value, validation, and deterministic application at the already stated
location. Collision/precedence rules are needed only if the chosen shape permits multiple or
overlapping contributions.

**Touches interface/change-trigger region:** yes.

### candidate-005 — Option application and persistence are absent from the conformance map

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:312-354`

**Claim:** Section 3 does not achieve the mandatory zero-unmapped contract rule for two explicit
Phase 3 scope items.

**Evidence:** The mandatory template requires every in-scope contract item to map through §3 with
zero unmapped rows (`docs/design/v2.0-RC3/DESIGN.md:804-808`). The governing Phase 3 scope includes
compile-time option application by source-line rewrite
(`docs/design/v2.0-RC3/DESIGN.md:1386-1389`) and both persistence formats plus their read/apply
model (`docs/design/v2.0-RC3/DESIGN.md:1417-1421`), matching the authoritative combined options
contract (`docs/research/v1/RESEARCH.md:601-604`). The §3.2 table maps discovery and organization
but has no row for either requirement (`docs/phase3/v1/PHASE_3_DOC.md:312-354`). Both designs exist
elsewhere—`OptionLineRewriter` and its named behavior
(`docs/phase3/v1/PHASE_3_DOC.md:514-520`), and the persistence codecs/reload merge
(`docs/phase3/v1/PHASE_3_DOC.md:522-532`)—so this is a traceability omission rather than a missing
architecture.

**Severity:** correction. Add §3 conformance coverage mapping source-line option application and
per-pack/global persistence parsing, application, and formats to their design elements and named
rewrite and round-trip/application tests.

**Touches interface/change-trigger region:** no.

## 2. Checked and clean

- The Round 3 internal-snapshot protocol and profile-inference correction are consistent across
  architecture, interfaces, hand-off, and tests.
- The explicit `OFF` result is consistently represented apart from candidate-002's raw-payload
  wording. Schema publication and compatibility rules remain coherent.
- Phase 3's consumption of Phase 1's module/seam, capability profile, logging, diagnostics, debug
  flag, notice mechanism, and conformance extension point matches the dependency's binding
  contract. The jcpp dependency is requested rather than silently assumed.
- Appendix F and Appendix A.3 spot checks, the complete engine-flag ownership map, the four
  mandated Pintonium pitfalls, discovery, preprocessing, source attribution, macro families, and
  ID mapping yielded no additional finding.
- No candidate was refuted or cleared on independent re-derivation. Candidates 001 and 004 overlap
  in the same macro surface but survive separately: candidate-001 is the singular/plural call-site
  incompatibility, while candidate-004 is the missing executable shape of the named optional slot.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=5; notes=0
Interface changed: yes

All five findings are bounded fix-up work; none requires rebuilding the architecture. Prior rounds
resolved their admitted findings, but Round 4 exposes three defects in the binding interface plus
two non-interface consistency/traceability defects, so convergence has not reached literal PASS.
The next required action is a scoped fix-up resolving all five corrections, followed by a fresh
verification round because the §5 interface/change-trigger region must change. Phase 3 may close
only after a later round returns literal `PASS` with zero blocking findings and zero corrections.

## Resolutions

### candidate-001 — resolved

Sections 2 and 5 now use the singular `MacroContribution` consistently from contributor through
`SourceMaterializer.materialize`; no aggregate or implicit conversion remains.

### candidate-002 — resolved

The publication invariant and structural-failure row now name the declared algebra exactly:
`PackLoadResult.Off`, `PackLoadResult.Loaded(configuration)`, and
`PackLoadResult.Failed(failure)`.

### candidate-003 — resolved

`SourceMaterializer` now returns sealed `MaterializationResult.Available` or
`Unavailable(root, diagnostics)`. The contract makes source unavailability non-throwing and
requires stable source-attributed diagnostics, so Phase 4 and the Phase 2 harness need not invent
failure semantics.

### candidate-004 — resolved

`MacroContribution` now has explicit `Empty` and `DefineCenterDepthSmooth(replacementTokens)`
variants. Only the object-like `centerDepthSmooth` definition is permitted; its validation,
failure outcome, and deterministic insertion point are specified without deciding Phase 6's
pending smoothing policy.

### candidate-005 — resolved

Section 3 now maps compile-time captured-span rewriting and both persistence formats through their
read/apply/write models, downstream owner, and named rewrite/round-trip/application tests. The
testability plan names the same tests.

### Notes deferred

None.
