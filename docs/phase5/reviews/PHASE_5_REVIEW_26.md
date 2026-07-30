# Phase 5 Verification Review — Round 26

## 0. Method and reading order

I independently re-derived both Gate-surviving candidates against the complete Phase 5 target
before consulting prior reviews. I read the target's fixed texture-unit table, overlay validation
and lease lifecycle, `TextureBindingSnapshot` payload, test oracle, hand-offs, implementation
checklist, and manifest-declared §5 interface region; the RC3 Part I rules, mandatory template,
Phase 5 specification, and document gate; and the manifest-selected binding regions of Phases 1,
3, and 4. The candidates concern the internal representability of a Phase-5-owned cross-phase
result, so the listed supporting reference implementations were not needed.

Only after settling that interpretation did I read Phase 5 reviews 1 through 25 in numeric order
and compare the candidates with their findings and resolutions. I used no network access,
forbidden source, or prior-session transcript. In particular, I did not open
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt`, because the resolved forbidden-source
rule bars `*.txt` and it was unnecessary. There was no agent fan-out or delegation. In accordance
with the dispatched atomic-role instruction and the verify-loop skill, I did not invoke the loop,
run `scripts/verify`, or start another Codex session. There were no deviations from the resolved
reading contract, no candidates eliminated before adjudication, and no Gate drops.

## 1. Findings

### candidate-001 / candidate-002 — `TextureBindingSnapshot` cannot represent all fixed-unit outcomes

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:1506`–`:1514`,
`docs/phase5/v1/PHASE_5_DOC.md:1555`–`:1575`,
`docs/phase5/v1/PHASE_5_DOC.md:1632`, and
`docs/phase5/v1/PHASE_5_DOC.md:1846`–`:1851`

**Claim.** The successful binding plan has three semantically distinct fixed-unit outcomes: a
bindable texture handle, an explicit `MissingTextureBinding`, and `Absent`/no object. The exported
snapshot is nevertheless defined only as immutable unit-to-handle rows. It defines no closed row
value or accessor capable of carrying `MissingTextureBinding`, and no omission or explicit
no-object rule for `Absent`. Phases 6 and 7 therefore cannot implement or observe every valid
successful table resolution without inventing part of Phase 5's public contract.

**Evidence.** The table vocabulary expressly says `Missing` reports a
`MissingTextureBinding`, while `Absent` means the binding plan reports no object
(`docs/phase5/v1/PHASE_5_DOC.md:1506`–`:1514`). Unit 12 uses the real `Absent` outcome in
GBUFFERS/SHADOW, so this is not unused taxonomy
(`docs/phase5/v1/PHASE_5_DOC.md:1555`–`:1556`). Optional-buffer and unit-15 overlay absence
resolution must yield an explicit `MissingTextureBinding`
(`docs/phase5/v1/PHASE_5_DOC.md:1561`–`:1568`). Yet the successful snapshot contains only
*"immutable unit→handle rows"* and is the object from which binding occurs before the draw
(`docs/phase5/v1/PHASE_5_DOC.md:1570`–`:1575`). Binding §5 publishes both the snapshot and
per-unit missing outcome to Phases 6, 7, and 13 without defining a row union, accessor, or
`Absent` omission rule (`docs/phase5/v1/PHASE_5_DOC.md:1632`). The test oracle exercises overlay
absence reasons but does not require the successful snapshot to expose their per-unit
`MissingTextureBinding` results or distinguish the fixed table's no-object outcome
(`docs/phase5/v1/PHASE_5_DOC.md:1846`–`:1851`).

**Required correction.** Define the immutable public `TextureBindingSnapshot` row/accessor
contract with deterministic fixed-unit ordering and a closed value that distinguishes a bindable
`TextureHandle` from `MissingTextureBinding`. Specify exactly whether a fixed-table `Absent` cell
is represented by a third no-object value or omitted, with no bind performed and fixed sampler-unit
identity preserved. State which component binds or skips each outcome, and mirror the exact shape
in binding §5 and the conformance oracle.

**Severity:** correction

**touches interface/change-trigger region: yes**

Candidates candidate-001 and candidate-002 are admitted as one finding because they identify the
same snapshot-row representability defect. Candidate-002 supplies the additional, correct
observation that the table's `Absent`/no-object outcome also needs an explicit representation or
omission rule; it does not constitute a second independent correction.

## 2. Checked and clean

The finder-reported new-surface area is otherwise clean on re-derivation. The Round-25 correction
consistently removes the inapplicable per-unit missing payload from whole-call validation
rejection: §4.12 and binding §5 now expose exactly `Bound(snapshot)` or `Rejected(reason)`,
preserve publication-ID-first priority, reject before binding, suppress the draw, and leave the
lease caller-owned on rejection. The present finding concerns only successful per-unit table
resolution and does not reopen that settled correction.

The interface and conformance clean areas remain clean. The manifest-selected Phase 1, Phase 3,
and Phase 4 binding contracts match their consumption in the target. Publication, resize,
frame/pass, depth, shadow, clear, flip, sizing, format, and Final-framebuffer contracts otherwise
have equivalent detailed-design and test coverage. The conformance map covers the governing
Appendix B requirements and Phase 5 document gate, including the authoritative unit-11 ruling.

The strongest clearing interpretation was that §5's phrase *"unit→texture objects"* implicitly
includes `MissingTextureBinding` and that `Absent` can simply be omitted. It fails because the
detailed payload is explicitly narrowed to unit-to-handle rows, while no public row type,
accessor, iteration order, or omission rule connects either non-handle outcome to the snapshot.
Prior Round 24 noticed that existing per-unit missing rows could not solve whole-call rejection,
but its fix introduced only the `TextureBindingResult` algebra. Round 25 then removed the
untruthful rejection payload. Neither resolution defined the successful snapshot's row shape, so
settled material does not clear either candidate. Both candidates survive, consolidated into the
single finding above. There were no Gate drops.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The single admitted defect is a localized dependent-facing data-contract correction and does not
require rebuilding the Phase 5 architecture, so `FAIL` is not warranted. Although the supplied
trend data is empty, direct prior-review comparison shows Round 23 passed, while Rounds 24 and 25
each corrected a different part of the same texture-binding result surface. This round finds its
successful row shape still incomplete, so convergence is not established.

The next required action is a scoped fix-up resolving the consolidated candidate-001/candidate-002
finding and appending its resolution to this review. Because the correction must define and mirror
the public snapshot payload in binding §5, the `cross-phase-interfaces` change trigger applies:
Phase 5 owes a fresh verification round before it can close.

## Resolutions

### candidate-001 / candidate-002 — resolved

Re-derived the successful fixed-unit table as a total sixteen-unit result rather than treating the
review argument as authority. Section 4.12 now defines immutable ascending
`TextureBindingRow(unit,outcome)` rows for units 0 through 15 and closes
`TextureBindingOutcome` as `Bindable(TextureHandle)`, `Missing(MissingTextureBinding)`, or
`Absent`, with total lookup and out-of-range rejection. This preserves fixed sampler identity while
representing both non-handle outcomes without overloading omission.

Phase 7 is now the single component that iterates the snapshot and binds only `Bindable` rows;
`Missing` enters the existing affected-program degradation policy and `Absent` performs no bind.
Phase 6 only uploads the sampler integers for the fixed units. Binding §5 mirrors the public row
types, exact cardinality/order, closed outcome algebra, and ownership split. The conformance oracle
now checks every stage's sixteen ordered outcomes, total lookup, binds, degradation, skip behavior,
and unchanged unit identity. Compact §0.28 records the fix and its interface-trigger consequence.

### Notes deferred

None.
