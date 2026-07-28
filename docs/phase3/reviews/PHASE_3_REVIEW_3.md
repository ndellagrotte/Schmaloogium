# Phase 3 Adversarial Review — Round 3

## 0. Method and reading order

I independently re-derived every surviving candidate from, in order:

1. `docs/phase3/v1/PHASE_3_DOC.md` in full;
2. the selected Part I, Phase 3 specification, doc-gate, and mandatory-template material in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the relevant contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the binding interface region of `docs/phase1/v14/PHASE_1_DOC.md`; and
5. the supplied candidate records and permitted supporting evidence.

Only after settling those judgments did I read
`docs/phase3/reviews/PHASE_3_REVIEW_1.md` and
`docs/phase3/reviews/PHASE_3_REVIEW_2.md`, including their resolutions. I made no deviation from
the assigned reading order, used no network access, performed no agent fan-out, and read no
forbidden source.

The Gate dropped `candidate-001` because its finder quote did not resolve at the cited location.
I did not revive it or derive a finding from it.

## 1. Findings

### candidate-002 — `OFF` has no representable load outcome

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:162-188`

**Claim:** The public load protocol does not completely specify the behavior of a selection that
Phase 3 discovers and claims to own.

**Evidence:** `PackLoadRequest` accepts an unrestricted `PackSelection`, while the sealed result
has only `Loaded(PackConfiguration)` and `Failed(PackLoadFailure)`
(`docs/phase3/v1/PHASE_3_DOC.md:162-174`). The publication invariant likewise makes configuration
and failure exhaustive (`docs/phase3/v1/PHASE_3_DOC.md:126-128`). However, discovery returns
`OFF` as a logical sentinel and says it “produces no load”
(`docs/phase3/v1/PHASE_3_DOC.md:423-425`). Section 5 then exposes the request/result trio as the
atomic entry point for Phase 7 and Phase 12 while also assigning `OFF` selected-source state to
those consumers (`docs/phase3/v1/PHASE_3_DOC.md:732-739`). No precondition excludes `OFF`, no
narrower loadable-selection type enforces interception, and no non-failure result represents it.

**Severity:** correction. Define one coherent protocol: either add an explicit non-failure
`OFF` result and its publication/transition semantics, or exclude `OFF` from load requests and
require Phase 7/12 to intercept it through an enforceable selection contract.

**Touches interface/change-trigger region:** yes.

### candidate-003 — `InternalPackSource` cannot supply the index Phase 3 requires

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:150-153`

**Claim:** The internal-pack provider cannot support Phase 3's specified
discovery-to-configuration pipeline.

**Evidence:** `InternalPackSource` exposes only stable identity and lookup by an already-known
normalized path (`docs/phase3/v1/PHASE_3_DOC.md:150-153`). Phase 3 nevertheless says that it builds
the deterministic normalized-path index (`docs/phase3/v1/PHASE_3_DOC.md:191-198`) and that the
load transaction locates the shaders root and indexes files before constructing source sets
(`docs/phase3/v1/PHASE_3_DOC.md:224-232`). Correct dimension configuration also depends on
discovering physical directories, including a present empty `world<id>` directory that denotes a
successful disabled dimension (`docs/phase3/v1/PHASE_3_DOC.md:447-451`). Lookup of known file paths
cannot enumerate unknown sources, language files, or empty directories. Section 5 publishes only
the same lookup-only protocol to Phase 7 (`docs/phase3/v1/PHASE_3_DOC.md:737-750`).

Round 2's resolution asserted that enumeration was unneeded, but it did not supply a finite probe
universe, fixed internal layout, manifest, directory-presence operation, or distinct pipeline that
would make that assertion executable. It therefore does not settle this candidate.

**Severity:** correction. Add a bounded deterministic snapshot/manifest or equivalent
directory-aware enumeration contract, including normalization, ordering, duplicate handling,
limits, empty-directory representation, failure attribution, and byte ownership; align §2, §5,
the pipeline, and tests.

**Touches interface/change-trigger region:** yes.

### candidate-004 — `MacroContributor` is omitted from the complete publication surface

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:741`

**Claim:** Section 5 does not name every interface Phase 3 exposes to Phase 6.

**Evidence:** The detailed design declares the public
`MacroContributor.contribute(PackConfiguration)` interface and associates it with the reserved
Phase 6 slot (`docs/phase3/v1/PHASE_3_DOC.md:201-220`). The governing template requires §5 to name
interfaces exposed to dependents (`docs/design/v2.0-RC3/DESIGN.md:811-813`). Yet §5.1 calls its
table the complete publication surface and lists `MacroContribution` and
`phase6.centerDepthSmoothRedirect`, but not `MacroContributor`
(`docs/phase3/v1/PHASE_3_DOC.md:730-742`). The later hand-off confirms that Phase 6 decides whether
to populate that slot (`docs/phase3/v1/PHASE_3_DOC.md:1047-1050`).

**Severity:** correction. Add `MacroContributor` to §5.1 and define the minimal mechanism by which
it supplies the reserved contribution consumed during materialization, or remove the unused
abstraction and consistently publish the direct `MacroContribution` contract. The existing rule
that absence is valid need not be duplicated.

**Touches interface/change-trigger region:** yes.

### candidate-005 — Profile inference omits the required `Custom` outcome

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:315`

**Claim:** The profile model does not specify the authoritative result when current option values
match no profile.

**Evidence:** RESEARCH requires current-profile inference from option values and otherwise the
pack-facing result `"Custom"` (`docs/research/v1/RESEARCH.md:1471-1474`). Phase 3's Appendix F.4
row specifies token forms, cycle handling, and descending-constraint match order, but neither the
no-match result nor a test for it (`docs/phase3/v1/PHASE_3_DOC.md:315`). Its detailed profile
discussion covers cyclic includes but not inference fallback
(`docs/phase3/v1/PHASE_3_DOC.md:670-673`). Phase 3 publishes profiles and immutable option state to
Phase 12 (`docs/phase3/v1/PHASE_3_DOC.md:742`), so Phase 12's GUI ownership does not supply the
missing model semantics.

**Severity:** correction. Specify deterministic matching and the explicit `Custom`/no-profile
outcome in the detailed model and conformance map, and add a named test covering both matching and
no-match cases.

**Touches interface/change-trigger region:** no.

## 2. Checked and clean

- The Round 2 directive stage restrictions are consistent across the conformance table, scanner
  design, named tests, and implementation checklist.
- The schema constant, exact-version support predicate, publication tests, and fingerprint
  retention rule are now consistent.
- Appendix F.1 ownership, the remaining Appendix F rows, Appendix A.3 mappings, discovery,
  includes, macro families, preprocessing, ID mappings, textures, expressions, render state, and
  dimension handling yielded no additional conformance finding.
- Phase 3's consumption of the selected Phase 1 module/seam, diagnostics, logging, debug flags,
  notices, conformance extension, and `GLCapabilityProfile` contracts is supported. It claims no
  unsupported GL service or handle.
- The identity model remains option-3-shaped, the four required Pintonium pitfall gates remain
  mapped, and the document retains all thirteen mandatory sections.
- No surviving candidate was refuted or cleared on independent re-derivation. Gate-dropped
  `candidate-001` remains excluded solely because its evidence was unverifiable.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=4; notes=0
Interface changed: yes

All four findings are bounded fix-up work; none requires rebuilding the Phase 3 architecture.
Rounds 1 and 2 resolved their admitted items, but Round 3 establishes four distinct remaining
defects, including three in the binding interface region, so the document has not converged to
literal PASS. The next required action is a scoped fix-up resolving all four corrections, followed
by a fresh verification round because §5 must change. Phase 3 may not close until that round
returns literal `PASS` with zero blocking findings and zero corrections.

## Resolutions

### candidate-002 — resolved

Added `PackLoadResult.Off` and made it the successful, non-configuration result for an `OFF`
request. Sections 2, 4, 5, and 8 now agree that the request short-circuits without opening input,
publishing configuration, or reporting failure, while Phase 7/12 own replacement of prior state
with shaders off.

### candidate-003 — resolved

Replaced lookup-only internal content with a finite `InternalPackSnapshot` manifest containing
ordered file and directory entries. The provider receives the common input limits; Phase 3
revalidates normalization, rejects duplicate and file/directory collisions, enforces all limits,
represents empty directories, defensively copies bytes, and attributes provider violations as
pack-level failures. The pipeline, §5 publication, Phase 7 hand-off, and named test now use this
directory-aware contract.

### candidate-004 — resolved

Added `MacroContributor` to the complete §5 publication surface. The row now states the minimal
flow: Phase 6 invokes its contributor with the published configuration and passes the resulting
`MacroContribution` to `SourceMaterializer.materialize`; the already-specified empty contribution
remains valid.

### candidate-005 — resolved

Defined `ProfileModel.infer(OptionState)` to test valid profiles by descending expanded constraint
count with source order as the tie-break, return the first exact option-state match, and return the
pack-facing `Custom` outcome on no match. The Appendix F map and test plan now require
`profiles_inferenceMatchAndCustom`.

### Notes deferred

None.

### §G1.3 status

All four admitted corrections were applied. Because §5 changed, the manifest change trigger fires:
a fresh verify round is required before Phase 3 can close.
