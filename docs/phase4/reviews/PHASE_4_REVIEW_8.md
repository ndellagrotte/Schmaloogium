## 0. Method and reading order

This adjudication independently re-derived every supplied candidate against, in order:

1. the whole Phase 4 target;
2. the governing Part I, Phase 4 specification, document gate, and mandatory template in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the binding contracts in `docs/phase1/v14/PHASE_1_DOC.md:3944-4039` and
   `docs/phase3/v1/PHASE_3_DOC.md:890-1015`; and
5. the listed supporting evidence where it bore on a candidate.

Only after those interpretations were settled were
`docs/phase4/reviews/PHASE_4_REVIEW_1.md` through
`docs/phase4/reviews/PHASE_4_REVIEW_7.md` read, in round order. Their resolutions establish the
evolving registry, barrier, and publication surface but do not settle the two omissions admitted
below. There were no reading-set deviations, no network use, no agent fan-out, no
forbidden-source use, no candidates eliminated before adjudication, and no Gate drops. The
canonical engine had already dispatched this atomic adjudication role, so neither the verification
harness nor another Codex session was invoked.

## 1. Findings

### candidate-001 — Opaque barrier candidate promises caller-driven close but exposes no close operation

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:953-956`,
  `docs/phase4/v1/PHASE_4_DOC.md:968-970`, and
  `docs/phase4/v1/PHASE_4_DOC.md:1089-1091`
- **Claim:** A caller cannot perform the idempotent cleanup that the unpublished and rejected
  barrier-candidate lifecycle assigns to it.
- **Evidence:** The public shape of `BarrierPublicationCandidate` is opaque and declares no
  `AutoCloseable` contract or `close()` operation
  (`docs/phase4/v1/PHASE_4_DOC.md:953-956`). The immediately following lifecycle nevertheless says
  that both the registry product and opaque barrier candidate remain caller-owned until
  publication and that closing either unpublished product is idempotent
  (`docs/phase4/v1/PHASE_4_DOC.md:968-970`). Pre-release rejection likewise leaves the candidate
  with the caller for idempotent close
  (`docs/phase4/v1/PHASE_4_DOC.md:1089-1091`). That an unpublished barrier has no GL handle, lock,
  or participant-owned resource can make cleanup a no-op internally, but it does not provide the
  callable lifecycle transition promised to consumers. The contrast is concrete:
  `CompiledRegistryCandidate` expressly implements `AutoCloseable` and publishes `close()`
  (`docs/phase4/v1/PHASE_4_DOC.md:401-404`), while §5 exports both products with deterministic
  close duties (`docs/phase4/v1/PHASE_4_DOC.md:1157`).
- **Required correction:** Give `BarrierPublicationCandidate` a public idempotent pre-transfer
  cleanup operation, preferably `AutoCloseable.close()` for consistency. Define behavior before
  and after successful ownership transfer, align rejection/recovery duties and lifecycle tests,
  and publish the resulting duty in §5.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

### candidate-002 — §5 omits callable compiler/publisher entry points and required argument contracts

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:373-383`,
  `docs/phase4/v1/PHASE_4_DOC.md:407-422`, and
  `docs/phase4/v1/PHASE_4_DOC.md:1147-1157`
- **Claim:** The binding cross-phase inventory does not name the public entry points and data
  contracts that Phases 7 and 12 must use to build, publish, and activate a registry.
- **Evidence:** The detailed public API declares
  `ProgramRegistryCompiler.compile(RegistryBuildRequest)` and the complete build-request shape
  (`docs/phase4/v1/PHASE_4_DOC.md:373-383`). It separately declares
  `ProgramRegistryPublisher.current()` and `publish(RegistryPublication, BarrierContext)`, while
  published barrier operations require `UseProgramRequest` and `BarrierContext`
  (`docs/phase4/v1/PHASE_4_DOC.md:407-422`,
  `docs/phase4/v1/PHASE_4_DOC.md:890-898`). Section 5 labels its table “Exposed interfaces and data
  contracts,” yet its product-oriented rows omit `ProgramRegistryCompiler`,
  `RegistryBuildRequest`, `ProgramRegistryPublisher`, `UseProgramRequest`, and `BarrierContext`
  (`docs/phase4/v1/PHASE_4_DOC.md:1147-1157`). The governing mandatory template requires §5 itself
  to state what the phase exposes through named interfaces and data contracts
  (`docs/design/v2.0-RC3/DESIGN.md:811-813`). Normative detail elsewhere establishes these as
  Phase-4-owned APIs but does not make the binding §5 inventory complete.
- **Required correction:** Add §5 rows naming
  `ProgramRegistryCompiler`/`RegistryBuildRequest` and `ProgramRegistryPublisher`, and explicitly
  include `UseProgramRequest` and `BarrierContext` in the published-barrier contract. Identify
  their consumers and consumer-visible fields, thread rules, ownership, and lifecycle duties;
  reference the detailed declarations rather than duplicating internal mechanics.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

## 2. Checked and clean

The following examined areas produced no additional admitted finding:

- Review 7's compiler-origin authentication, exact registry/product pairing, public cross-package
  composition facade, fixed participant ordering, one-success composition, and rejection of
  external registries, arbitrary barriers, and bootstrap candidates are consistent across the
  detailed prose, §5, tests, and checklist.
- The consumed Phase 1 and Phase 3 contracts are represented honestly, including the unresolved
  legacy-geometry dependency request. The stage traversal, immutable view, opaque ownership
  products, closed barrier outcomes, generation/fingerprint contracts, diagnostics, fixed
  attributes, and instance-count handoffs otherwise agree with the detailed design.
- The classic catalog, fallback edges and whole-provider inheritance, fixed attribute locations,
  compile/link sequence, barrier ordering, generation invalidation, G6 population, and sparse
  0…99 family mapping were checked without another surviving defect.
- **candidate-003 is dropped on re-derivation.** `PassDescriptor` owns
  `Set<ComputeDispatchSlot>` directly and has no raster-kind precondition
  (`docs/phase4/v1/PHASE_4_DOC.md:267-272`); `ComputeDispatchSlot` independently represents the
  primary and every `a`–`z` companion (`docs/phase4/v1/PHASE_4_DOC.md:289-296`). The phrase “A
  non-gbuffers raster pass may own” is permissive, not an exhaustive “only” rule. The surrounding
  full-shape configuration expressly requires compute-only `SETUP` companion constructibility to
  be tested (`docs/phase4/v1/PHASE_4_DOC.md:647-664`), and §5 states the exhaustive restriction as
  companions “only outside gbuffers” (`docs/phase4/v1/PHASE_4_DOC.md:1152`). Together those
  provisions coherently include `SETUP`; a clarifying rephrase is optional, not a correction.
- Prior round 7's resolutions added the public composition facade and compiler-issued opaque
  registry product. They do not supply the missing barrier-candidate close operation or complete
  §5's inventory of compiler, publisher, request, and context types.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both admitted defects are bounded corrections to consumer-visible lifecycle and interface
documentation, not structural misses requiring a rebuild. Literal `PASS` is unavailable while two
corrections remain.

The correction trend is 5, 3, 1, 3, 2, 2, 2, then 2. The count has plateaued for four rounds, and
the remaining findings still concern the repeatedly revised publication interface. The loop has
not demonstrated convergence, so no closure inference is warranted.

The next required action is a scoped Phase 4 fix-up resolving candidates 001 and 002, appending
this review's `## Resolutions`, and adding the next compact Phase 4 fix-up addendum. Because both
corrections require changes to the binding §5 `cross-phase-interfaces` region, a fresh
verification round is required before Phase 4 can close.

## Resolutions

### candidate-001 — resolved

`BarrierPublicationCandidate` now implements `AutoCloseable` and exposes an idempotent `close()`.
The lifecycle states that pre-transfer close drops the private barrier and participant references
without GL work, while close after accepted ownership transfer is harmless and cannot affect the
publication. Section 5 assigns rejected/recovered-off close to the caller and accepted teardown to
the publisher. Two API/lifecycle tests cover close on each side of transfer, and checklist item 14
requires both behaviors.

### candidate-002 — resolved

Section 5 now names `ProgramRegistryCompiler.compile` with every `RegistryBuildRequest` field,
render-thread and retention rules, and its Phase 7/12 consumers. It also names
`ProgramRegistryPublisher.current`/`publish`, their arguments, thread rule, snapshot/transfer
semantics, and teardown ownership. The published-barrier row now includes `UseProgramRequest` and
all `BarrierContext` fields alongside the existing activation results and consumers. Detailed
mechanics remain in §4 rather than being duplicated in the binding inventory.

### Notes deferred

None.
