# Phase 6 verification review — round 1

## 0. Method and reading order

This adjudication independently re-derived the surviving candidates against the whole target,
`docs/phase6/v1/PHASE_6_DOC.md`, then the governing Phase 6 specification and document gate in
`docs/design/v2.0-RC3/DESIGN.md`, the contract ground truth in
`docs/research/v1/RESEARCH.md`, and the manifest-selected binding interfaces of Phases 1, 3, and 4.
The relevant supporting evidence was used only as evidence, never as contract. There were no prior
Phase 6 reviews to read last.

There were no reading-order deviations, no network use, and no agent fan-out. Candidate-003 had
already been eliminated by Refute. Gate dropped candidate-002 because its finder quotation did not
resolve at the cited target coordinates; it is not admitted or independently revived here. Both
Gate-surviving candidates were re-derived rather than accepted from their incoming labels.

## 1. Findings

### candidate-001 — Published provider and event records lack implementable schemas

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:220-237`,
  `docs/phase6/v1/PHASE_6_DOC.md:353-380`, and
  `docs/phase6/v1/PHASE_6_DOC.md:975-986`
- **Claim:** The binding provider and event seams do not specify enough data and construction
  semantics for `mod.glue` and later-phase producers to implement their request/sample records
  without guessing.
- **Evidence:** The target declares `UniformEventSink` methods using `CelestialSample`,
  `ShadowMatrixSample`, `FogSample`, `BlendSample`, and `HeldItemSample`, then states that their data
  contracts and construction-time finite/range validation are binding
  (`docs/phase6/v1/PHASE_6_DOC.md:220-237`). The provider SPI similarly returns
  `OnceUniformSample`, `TickUniformSample`, and `FrameUniformSample`, and accepts
  `FrameSampleRequest` and `CenterDepthRequest`
  (`docs/phase6/v1/PHASE_6_DOC.md:353-371`). The following prose gives only categories of values
  rather than exact fields, types, units, coordinate spaces, optionality, ranges, and failure
  behavior (`docs/phase6/v1/PHASE_6_DOC.md:374-380`). Whole-target search found no declarations or
  equivalent normative schemas for these records. This is not downstream-owned: the governing
  design assigns value-provider interfaces in `engine.uniforms` to Phase 6 and their implementation
  to `mod.glue` (`docs/design/v2.0-RC3/DESIGN.md:1771-1773`). Section 5 exports both the immutable
  event samples and provider SPI to Phases 7–13 and `mod.glue`
  (`docs/phase6/v1/PHASE_6_DOC.md:975-986`).
- **Required correction:** Define every externally constructed request/sample/event record, or
  provide precise normative references to complete definitions. Include exact fields and types,
  units and coordinate spaces, timing identity, validation, absence/fallback behavior, and
  ownership/copy rules wherever applicable. Do not invent restrictions where the full type domain
  is intentionally valid.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

### candidate-004 — Held-light mapping omits the required brighter-hand result

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:442-445`
- **Claim:** Phase 6 delegates old-hand-light policy to Phase 9 without defining the observable
  two-uniform transformation or the binding held-item input that carries it.
- **Evidence:** Contract ground truth specifies that `heldBlockLightValue` and
  `heldBlockLightValue2` use held-item light values and that in old-hand-light mode the brighter
  hand wins (`docs/research/v1/RESEARCH.md:1327-1329`). The Phase 6 inventory instead assigns
  independent main/off-hand values and says only that the old-hand-light policy is “already
  resolved by Phase 9 input” (`docs/phase6/v1/PHASE_6_DOC.md:442-445`). Whole-target search found no
  other definition of that policy or of `HeldItemSample`. The governing specification requires
  Phase 6 to give every Appendix D uniform its exact semantics and provider
  (`docs/design/v2.0-RC3/DESIGN.md:1698-1706`); Phase 9's scope exclusion is limited to
  alias-derived ID values, while observable built-in semantics remain Phase 6's responsibility
  (`docs/design/v2.0-RC3/DESIGN.md:1776-1789`). `HeldItemSample` is expressly a binding event data
  contract (`docs/phase6/v1/PHASE_6_DOC.md:228-237`) exported to Phase 9 through Section 5
  (`docs/phase6/v1/PHASE_6_DOC.md:979-983`).
- **Required correction:** State the exact two pack-facing held-light values produced when
  old-hand-light mode is enabled, including which uniform receives the brighter-hand value and how
  the other is populated, and encode that result in the binding `HeldItemSample` contract. Phase 9
  may retain ownership of source acquisition and alias-derived item IDs.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

## 2. Checked and clean

The re-derivation confirmed the finder-reported clean areas:

- The document contains all thirteen mandatory sections and otherwise satisfies the Phase 6 doc
  gate: the Appendix D mapping, cadence model, smoothing formulas, Phase 4 barrier trace,
  center-depth decision, notifier-to-producer audit, and frame-begin ordering export are present.
- The fixed sampler maps, including `depthtex1` at unit 11, activation cadence, unconditional matrix
  uploads, center-depth disposition, frame ordering, and pure provider seam are substantively
  traced.
- Phase 6 honestly requests the missing Phase 3 declared-uniform catalog and Phase 4 bound-program
  lookup/activity-token capabilities instead of assuming them. Its use of the selected Phase 1
  upload/readback/error contracts and the selected Phase 4 ordering, generation, provider, and
  instance-count contracts is consistent with those dependencies.
- Candidate-003 remains cleared: Phase 6 identifies the relevant smoothing inputs through the
  dependency surface and explicitly requests missing publication where required.
- Candidate-002 remains a Gate drop, not a finding: its supplied quotation was unverifiable at the
  cited target coordinates, and adjudication does not revive candidates absent from the surviving
  set.

No Gate-surviving candidate was dropped on independent derivation.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both admitted defects are bounded contract corrections rather than structural failures, so `FAIL`
is not warranted. Literal `PASS` is unavailable because two corrections remain.

Both corrections affect the cross-phase sample contracts exported in Section 5. The fix-up must
resolve candidate-001 and candidate-004, append the required resolution record, and update the
binding interface definitions. Because the interface/change-trigger region is touched, a fresh
verification round is required before Phase 6 can close.

This is the first review, so there is no prior trend. The round establishes a two-correction
baseline; convergence cannot yet be claimed.

## Resolutions

### candidate-001 — resolved

Re-derived against the Phase 6 assignment's ownership of `engine.uniforms` provider interfaces and
the target's complete built-in inventory. Section 4.2 now gives exact fields and engine types for
all externally constructed provider requests/results and celestial, shadow, fog, blend, and held
event records. It binds world/frame/tick identity, units and coordinate space, finite/range
validation, explicit optional/unavailable behavior, last-valid-or-neutral failure handling, and
immutable copy ownership. Integer domains are restricted only where the governing inventory
actually bounds them; alias IDs, fog enum encodings, and blend factors retain the full `int`
domain. Section 2.2 now points to that normative schema, and §5 publishes it as part of the
cross-phase contract.

### candidate-004 — resolved

Re-derived from Appendix D's brighter-hand requirement and the shipped main/off-hand meanings.
Section 4.2 now makes `HeldItemSample` carry the four final pack-facing values and binds its
construction: normal mode publishes `(mainLight, offHandLight)`; old-hand-light mode publishes
`(max(mainLight, offHandLight), offHandLight)`. Section 4.4.1 repeats the observable result:
`heldBlockLightValue` is the legacy/brighter-hand channel and `heldBlockLightValue2` remains the
actual off-hand channel. Phase 9 still owns source acquisition and alias-derived item IDs.

### Interface change and next status

The `cross-phase-interfaces` region changed because §5 now normatively exports the completed §4.2
schemas and held-light mapping. Per the manifest trigger, Phase 6 requires a fresh verification
round before it can close.

### Notes deferred

None. The adjudication admitted no notes.
