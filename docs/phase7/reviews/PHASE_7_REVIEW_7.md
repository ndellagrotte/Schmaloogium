## 0. Method and reading order

I independently re-derived both Gate-surviving candidates from the whole Phase 7 target, the
manifest-selected governing design sections, authoritative RESEARCH material, the binding §5
regions of Phases 2–6, and the cited dependency evidence. Only after settling those judgments did I
read `docs/phase7/reviews/PHASE_7_REVIEW_1.md` through
`docs/phase7/reviews/PHASE_7_REVIEW_6.md`, in order and last. Round 6 explains the newly changed
multi-valued hook-report surface but does not settle either Round 7 defect; the earlier addition of
`InternalPackContent` likewise did not define its relationship to Phase 3's binding protocol.

There were no reading-order deviations, no network use, no forbidden source use, and no agent
fan-out. This was the canonical engine's already-dispatched atomic adjudication role, so the
`verify-loop` instructions required completing only this role without invoking the loop or
delegating. No candidates were eliminated before adjudication, and Gate dropped none.

## 1. Findings

### candidate-001 — `DEFERRED` application policy contradicts the mixed H-ENTITY-03 row

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:812`
- **Claim:** The class-wide `DEFERRED(Pn)` application policy is inconsistent with the catalog's
  mixed-class H-ENTITY-03 row and the newly binding unsplit, multi-valued report model.
- **Evidence:** The policy says a `DEFERRED(Pn)` mixin is absent from Phase 7 until its owner phase
  lands (`docs/phase7/v1/PHASE_7_DOC.md:807`–`:812`). H-ENTITY-03 nevertheless specifies an active
  `TileEntityRendererDispatcher` HEAD/RETURN injection for the `gbuffers_block` program scope,
  classified `FEATURE` for that scope and `DEFERRED(P9)` only for the later block-entity ID
  augmentation (`docs/phase7/v1/PHASE_7_DOC.md:860`–`:864`). The owner ledger confirms that the
  program-scope hook is implemented at v0.1 and only the ID augmentation is deferred
  (`docs/phase7/v1/PHASE_7_DOC.md:951`–`:952`). Section 5 now requires one unsplit report row to
  retain every class, including its deferred owner (`docs/phase7/v1/PHASE_7_DOC.md:1304`–`:1307`),
  so that interface does not supply an implicit responsibility-specific meaning that cures the
  unqualified absence policy.
- **Required correction:** Qualify the `DEFERRED(Pn)` application policy. A wholly deferred row has
  no Phase 7 mixin; on a mixed row, only the owner-phase capability or augmentation is absent while
  the row's active Phase 7 hook remains. Preserve the multi-valued report and owner-phase field.
- **Severity:** correction
- **touches interface/change-trigger region: no** — the contradictory policy is in §4.10.1; the
  existing §5 report schema can remain unchanged.

### candidate-002 — `InternalPackContent` does not satisfy the binding Phase 3 source protocol

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1189`–`:1193`
- **Claim:** The consumer-visible internal-pack contract does not define how it supplies content
  through Phase 3's required `InternalPackSource` protocol, forcing bootstrap and golden consumers
  to invent identity and failure adaptation.
- **Evidence:** Phase 7 exposes `InternalPackContent` with only `snapshot(PackInputLimits)` and
  `manifest()`, with no inheritance, adapter, dual-implementation rule, or attributed failure
  contract (`docs/phase7/v1/PHASE_7_DOC.md:1189`–`:1193`). Phase 3's binding interface instead
  requires `identity()` and `snapshot(limits) throws InternalPackReadException`
  (`docs/phase3/v1/PHASE_3_DOC.md:252`–`:255`), and binds stable identity, limit termination, and
  provider-failure mapping to `INTERNAL_SOURCE_INVALID`
  (`docs/phase3/v1/PHASE_3_DOC.md:1168`–`:1174`). Phase 7 says `BuiltInPassthroughPack` implements
  `InternalPackSource` (`docs/phase7/v1/PHASE_7_DOC.md:712`–`:714`) and lists that dependency-owned
  protocol as consumed (`docs/phase7/v1/PHASE_7_DOC.md:1364`), but its exact exposed-contract table
  separately promises `InternalPackContent` as the Phase 3 snapshot supplier
  (`docs/phase7/v1/PHASE_7_DOC.md:1329`–`:1331`). Nothing connects those two surfaces, while the
  implementation checklist assigns `InternalPackContent` to both Phase 3 loading and golden input
  (`docs/phase7/v1/PHASE_7_DOC.md:1739`–`:1742`).
- **Required correction:** In §5.1, explicitly connect `InternalPackContent` to
  `InternalPackSource` through inheritance, a stated dual implementation, or an exact adapter.
  Preserve stable identity, `PackInputLimits` enforcement, and Phase 3's attributed
  `InternalPackReadException`/`INTERNAL_SOURCE_INVALID` semantics, then align the exposed-contract
  table and checklist with the chosen relationship.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the missing relationship is part of the
  manifest-selected §5 cross-phase contract.

## 2. Checked and clean

The finder-reported clean areas survived re-derivation. Apart from candidate-001's policy wording,
the Round 6 `Set<HookHealthClass>` and `deferredOwnerPhase` correction faithfully represents the
catalog's current single- and mixed-class rows without splitting or collapsing them. No other
catalog identifier, class set, or deferred owner mismatch survived review.

The selected Phase 2 capture, Phase 3 pack/configuration, Phase 4 registry/barrier, Phase 5
estate/frame/draw-target, and Phase 6 runtime/event contracts are otherwise honestly consumed or
explicitly gated. Ownership and publication lifetimes, closed frame/capture outcomes, downstream
hand-offs, and dependency-change requests yielded no additional candidate-backed interface defect.

The conformance map provides row-level coverage for the governing frame flow, every Appendix A.1
program family, hook needs 1–11, the seven-row reference timeline, Phase 7 engine flags, and
Appendix E dispositions. The mandatory thirteen sections, assigned OQ-3/OQ-4 spikes, reference-free
hook treatment, milestone staging, and implementation checklist yielded no additional
candidate-backed defect. Both supplied candidates were confirmed; none was refuted or cleared on
independent re-derivation.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both admitted defects are correction-level and can be repaired without rebuilding the Phase 7
architecture. Candidate-002 requires a change in the manifest-declared cross-phase interface
region, so a fresh whole-document/interface verification round is required after fix-up before
Phase 7 can close.

The correction trend is 3, 3, 4, 2, 2, 1, 2 across Rounds 1–7. Round 7 reverses the recent numerical
improvement, and one new interface correction remains, so literal convergence has not been reached
and the result cannot be softened to PASS. The next required action is a scoped fix-up of this
review, including its `## Resolutions` record and Phase 7 addendum, followed by fresh verification
of the changed interface and corrected whole document.

## Resolutions

### candidate-001 — applied

Re-derived from the class table and H-ENTITY-03 rather than treating the finding as authority. The
§4.10.1 policy now distinguishes a wholly deferred row from a mixed row: only the deferred
owner-phase portion is absent on the latter, while its active Phase 7 hook remains. The unsplit
multi-class report and `deferredOwnerPhase` contract are preserved.

### candidate-002 — applied

`InternalPackContent` now extends Phase 3's `InternalPackSource` and explicitly retains its
`identity()` and checked `snapshot(PackInputLimits)` signatures. Thus Phase 3 continues to own
stable-identity validation, finite limit enforcement, and attribution of provider/limit failures as
`InternalPackReadException` / `INTERNAL_SOURCE_INVALID`; Phase 7 adds only the deterministic
manifest. The §4.7 implementation statement, §5.1 exposed-contract row, and §12 checklist now name
the same relationship.

### Notes deferred

None. The adjudicator admitted no notes, and neither correction required a new design decision or
contradicted governing authority.
