# Schmaloogium — Phase 2: Conformance harness — Review Round 18

## 0. Method and reading order

I independently re-derived both gated candidates before reading any prior review. I read the
manifest-selected target, `docs/phase2/v1/PHASE_2_DOC.md`; the governing Part I, Phase 2 target
specification, document gate, and mandatory template in `docs/design/v1.1/DESIGN.md`; the contract
ground truth in `docs/research/v1/RESEARCH.md`; the selected binding dependency in
`docs/phase1/v14/PHASE_1_DOC.md`; and the supporting CI evidence under `.github/workflows/`. Only
after settling both candidates did I read `docs/phase2/reviews/PHASE_2_REVIEW_1.md` through
`PHASE_2_REVIEW_17.md`, in round order.

There were no reading-list deviations and no network use. This already-dispatched atomic role did
not invoke the verification harness, start another Codex session, or use agent fan-out. Forbidden
sources were not read. The Gate reported no drops, and no candidate was eliminated before
adjudication.

The prior reviews were considered only after independent judgment. Round 17's resolution is the
new surface summarized by §0.18. It does not settle either present defect: its fix-up concerned the
accepted Phase 1 package and replay-aware GL-error grants, not Phase 2's separate diagnostic-type
consumption, and no prior review updates the closing status after that fix-up.

## 1. Findings

### candidate-001 — Closing verification history stops before the round-17 fix-up

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:2210–2212`
- **Claim:** The closing status accurately identifies the latest reviewed surface and the reason
  Phase 2 remains unverified.
- **Evidence:** Section 0.18 records that the round-17 fix-up removed obsolete gates, changed
  binding §5, and requires a fresh whole-document review
  (`docs/phase2/v1/PHASE_2_DOC.md:163–166`). The closing status instead still says that round 16
  passed the §0.16 surface and that only the §0.17 amendment subsequently changed §5
  (`docs/phase2/v1/PHASE_2_DOC.md:2210–2212`). It therefore omits both round 17 and the §0.18
  fix-up when stating the current verification history and next-session obligation.
- **Disposition:** Admitted. Update the closing status to state that round 17 reviewed the §0.17
  surface, the §0.18 round-17 fix-up then changed binding §5, and Phase 2 remains unverified pending
  this fresh whole-document review.
- **Severity:** correction
- **touches interface/change-trigger region: no**

### candidate-002 — Phase 2 claims diagnostic types absent from its Phase 1 consumer grant

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:1545`
- **Claim:** Every Phase 1 interface consumed by Phase 2 is granted to Phase 2 in the dependency's
  binding interface.
- **Evidence:** Phase 2 lists `EngineDiagnostic`, `DiagnosticSeverity`, and `UserChannel` as
  consumed from Phase 1 for the run manifest's `diagnostics` block
  (`docs/phase2/v1/PHASE_2_DOC.md:1544–1546`). Phase 1's binding conventions table exposes those
  diagnostic types only to Phases 3, 4, 5, 6, 7, 11, and 12
  (`docs/phase1/v14/PHASE_1_DOC.md:4246–4251`). That consumer list is selective rather than an
  accidental omission: the adjacent rows say “all phases” for common logging contracts and
  expressly name Phase 2 for the debug flags it receives. Phase 2 therefore presents a dependency
  type grant as existing when the selected binding contract does not grant it.
- **Disposition:** Admitted. Revise §5 so Phase 2 does not claim these types as an existing Phase 1
  grant. Either flag and await a Phase 1 binding grant to Phase 2, or define a complete wire
  diagnostic projection produced by an authorized consumer and read by Phase 2 without depending
  on the Phase 1 domain types.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported round-17 fix-up is otherwise internally consistent: the accepted Phase 1 v14
package slot and replay-aware GL-error result are treated as consumable, Phase 6 retains replay
ownership, and Phase 7 serializes the owner-defined attribution boolean verbatim. The conformance
audit found all Phase 2 scope requirements, T0–T3 tiers, Appendix G packs, §8 harness requirements,
scene families, §9 exit criteria, the before-renderer subset, and OQ-10 mapped to substantive
design elements or named runs. The CI evidence introduced no contrary candidate.

Both candidates survived independent re-derivation. Section 0.18's accurate statement does not
cure the contradictory designated closing status in candidate-001. For candidate-002, describing
the manifest field as serialized data does not supply the missing type-level consumer grant, and
the target does not define an intervening wire projection that removes that dependency. No prior
review settled either issue. The Gate dropped none, no candidate was cleared, and no finding is
created from candidate-free clean areas.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both findings are bounded fix-up work rather than structural misses requiring a rebuild.
Candidate-001 corrects verification-state bookkeeping outside §5. Candidate-002 repairs a
cross-phase consumption claim inside the declared interface/change-trigger region.

Rounds 15 and 16 converged to literal PASS, but the §0.17 amendment reopened review and round 17
introduced a correction-bearing interface fix-up. This round admits two further corrections, one
interface-affecting, so the loop is not currently converged. The next required action is a scoped
fix-up resolving both findings and recording their resolutions in this review. Because §5 must
change, a fresh whole-document verify round is required before Phase 2 can close.

## Resolutions

### candidate-001 — resolved

Updated the closing status in `docs/phase2/v1/PHASE_2_DOC.md` to record the actual sequence: round
17 reviewed the §0.17 surface, §0.18 then changed binding §5, and this round required §0.19. The
status continues to mark Phase 2 unverified and forbids a version roll while the loop remains open.

### candidate-002 — resolved

Removed `EngineDiagnostic`, `DiagnosticSeverity`, and `UserChannel` from §5.2's claimed Phase 1
consumptions. Added R4B as an explicit, unaccepted request for the missing Phase 1 grant and stated
that, until acceptance in Phase 1's binding §5, Phase 2 neither consumes those domain types nor
treats them as the source of its independently specified diagnostic wire records. This preserves
the existing Phase-2-owned wire schema without inventing an unauthorized dependency grant.

### Notes deferred

None; the adjudicator admitted no notes.

### Interface and verification status

Binding §5 changed. The `cross-phase-interfaces` selector changed from
`3fa5bd2831c47d3abcbfc95b9fd9beb8b948dd925253e8c7762d8ae25145df17` to
`6a7fb46c781fb1e9b092b857c02688481c49b366d6aaed4393629ac0c69c2c7a`. The manifest change trigger
therefore fires: a fresh whole-document verify round is required before Phase 2 can close.
