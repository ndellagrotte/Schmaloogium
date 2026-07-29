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
`docs/phase4/reviews/PHASE_4_REVIEW_6.md` read, in round order. Their resolutions establish the
evolving registry, barrier, and publication surface, but do not settle the two distinct defects
admitted below. There were no reading-set deviations, no network use, no agent fan-out, no
forbidden-source use, no candidates eliminated before adjudication, and no Gate drops. The
canonical engine had already dispatched this atomic adjudication role, so neither the verification
harness nor another Codex session was invoked.

## 1. Findings

### candidate-001 — Production participant assembly has no implementable cross-package wiring entry point

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:937-943` and
  `docs/phase4/v1/PHASE_4_DOC.md:1119-1122`
- **Claim:** The package-private production assembler cannot receive the three Phase 6
  implementations from the application composition root through any declared callable route.
- **Evidence:** The target says a Phase-4-owned package-private assembler receives three participant
  interfaces from the controlled application composition root and alone mints the opaque
  `ProductionBarrierParticipants` bundle
  (`docs/phase4/v1/PHASE_4_DOC.md:937-943`). Section 5 repeats that package-private assembly is the
  production route, but exposes no facade, installation operation, callback, signature, or
  co-location rule through which an external caller supplies those values
  (`docs/phase4/v1/PHASE_4_DOC.md:1119-1122`). The binding package layout assigns Phase 4 to
  `com.schmaloogium.engine.registry`, Phase 6 to
  `com.schmaloogium.engine.uniforms`, and the Phase 7 application bootstrap to
  `com.schmaloogium.mod.core` (`docs/phase1/v14/PHASE_1_DOC.md:1400-1412`). The dependency's
  binding package-placement rule also makes a phase's assigned package mandatory and keeps
  `.internal` packages private (`docs/phase1/v14/PHASE_1_DOC.md:3964-3967`). None of those callers
  can invoke an `engine.registry` package-private assembler. Because the governing template
  requires §5 to name what dependents consume
  (`docs/design/v2.0-RC3/DESIGN.md:811-813`), leaving the bridge to implementation would require
  downstream invention of a cross-phase API.
- **Required correction:** Define a concrete Phase-4-owned cross-package composition route and
  publish it in §5. Name its owner, package, callable signature, Phase 7 caller, participant
  ordering, invalid/null and repeated-call behavior, ownership lifecycle, and result. Keep the
  credential and opaque-bundle construction package-private; the exposed operation should return
  only a safe construction/publication result rather than a publicly mintable bundle.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

### candidate-002 — Exact object identity authenticates pairing but not registry provenance

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:385-416`,
  `docs/phase4/v1/PHASE_4_DOC.md:920-956`, and
  `docs/phase4/v1/PHASE_4_DOC.md:1048-1054`
- **Claim:** A genuine factory candidate paired with the same registry object does not prove that
  the registry was produced by Phase 4's compiler.
- **Evidence:** `ProgramRegistryView` and `CompiledProgramRegistry` are publicly implementable
  interfaces, and public `RegistryPublication.Ready` accepts any `CompiledProgramRegistry`
  implementation (`docs/phase4/v1/PHASE_4_DOC.md:385-416`). The genuine
  `ProgramStateBarrierFactory` accepts the broader `ProgramRegistryView`, so an external
  `CompiledProgramRegistry` implementation can be supplied to it
  (`docs/phase4/v1/PHASE_4_DOC.md:920-934`). The resulting candidate proves authenticated
  participant composition and pairing with the exact registry identity, but its stated proof does
  not include compiler origin (`docs/phase4/v1/PHASE_4_DOC.md:952-956`). Publication likewise
  validates candidate provenance, production composition, and exact identity, but specifies no
  independent compiler-issued registry credential
  (`docs/phase4/v1/PHASE_4_DOC.md:1048-1054`). A caller can therefore obtain a genuine candidate
  for its own registry implementation and submit that identical object through `Ready`; identity
  establishes pairing, not provenance.
- **Required correction:** Make production registry origin unforgeable. Narrow construction and
  publication to an opaque compiler-issued registry product, or attach a private Phase-4
  compiler-origin credential checked by both the barrier factory and publisher. Reflect the
  corrected contract in §5 and add a negative test proving an external
  `CompiledProgramRegistry` implementation cannot form or publish a ready candidate even with
  genuine production participants.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

## 2. Checked and clean

The following examined areas produced no additional admitted finding:

- The handle-free published resolution and barrier results, bootstrap-marker rejection,
  arbitrary-barrier rejection, candidate-to-registry identity comparison, registry/view
  inheritance relationship, lifecycle, and ownership rules are internally consistent apart from
  the two precise provenance/wiring gaps above.
- The governing scope and conformance map were checked for stage shape, classic catalog coverage,
  fallback behavior, attribute binding, compile/link ordering, routing, per-program state, and
  Pintonium mechanism dispositions. No additional unmapped or unsupported in-scope requirement
  survived.
- Phase 1's package and module constraints and Phase 3's materialization/configuration publication
  contracts otherwise match the target's declared consumption.
- **candidate-003 is dropped as a duplicate of candidate-001.** It identifies the same missing
  route from the cross-package Phase 6/Phase 7 callers into the package-private Phase 4 assembler
  and requires the same §5 correction. Its additional requested lifecycle details are incorporated
  into candidate-001's required correction rather than counted as a second defect.
- Prior round 6 introduced opaque participant and publication products to close participant and
  arbitrary-barrier forgery. Its resolutions did not define an accessible composition-root bridge
  and did not authenticate compiler origin for the separately supplied registry, so neither
  admitted candidate is cleared by settled prior material.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both admitted defects are bounded corrections to the production composition and publication
contracts, not structural misses requiring a rebuild. Literal `PASS` is unavailable while two
corrections remain.

The correction trend is 5, 3, 1, 3, 2, 2, then 2. The count has plateaued for three rounds, and
both current findings arise in the repeatedly revised barrier/publication interface. The loop has
not demonstrated convergence, so no closure inference is warranted.

The next required action is a scoped Phase 4 fix-up resolving candidates 001 and 002, appending
this review's `## Resolutions`, and adding the next compact Phase 4 fix-up addendum. Because both
corrections require changes to the binding §5 `cross-phase-interfaces` change-trigger region, a
fresh verification round is required before Phase 4 can close.

## Resolutions

### candidate-001 — resolved

Added the public Phase-4-owned
`com.schmaloogium.engine.registry.ProductionBarrierComposer.compose` facade. Phase 7's
`com.schmaloogium.mod.core` composition root calls it with the compiler product and the Phase 6
sampler, built-in, and custom participants in signature order. The facade alone crosses into the
package-private assembler and factory; neither the credential nor
`ProductionBarrierParticipants` becomes public. The contract now defines null, closed, missing,
and repeated-call rejection, no-retention failure, one successful composition per registry
product, caller ownership before acceptance, idempotent unpublished close, and the closed
`BarrierConstructionResult`. Section 5 publishes the route and its consumers.

### candidate-002 — resolved

Re-derived registry origin separately from object identity and introduced opaque
`CompiledRegistryCandidate`, minted only by `ProgramRegistryCompiler` around its private registry
and compiler-origin credential. Factory composition now accepts that product rather than the
publicly implementable view. Ready publication accepts the same product paired with its opaque
barrier candidate, and the publisher independently checks compiler origin, open/unpublished state,
factory provenance, production composition, and exact product/registry pairing before release.
Public `CompiledProgramRegistry` implementations have no conversion path. Section 5 publishes the
compiler-product contract, and §8 adds the required genuine-participant/external-registry negative
test plus facade invalid/repeated-call coverage.

### Notes deferred

None.
