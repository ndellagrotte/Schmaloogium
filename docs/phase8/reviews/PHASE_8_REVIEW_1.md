# Phase 8 — Shadow pass — Verification Review 1

## 0. Method and reading order

I independently re-derived all six Gate-surviving candidates against the whole target,
`docs/design/v2.0-RC3/DESIGN.md` Part I and the Phase 8 assignment/doc gate, the binding §5
contracts of Phases 4–7, and the cited permitted evidence. I then checked the prior-review list
last; it was empty because this is the first review.

There were no deviations from the resolved source contract. I did not use the network, invoke the
verification harness, start another Codex process, or fan out to agents. I did not read forbidden
sources. The Gate reported no drops and no candidates were eliminated before adjudication.

## 1. Findings

### candidate-001 — Exposed Phase 8 plan, policy, health, math, and fixture contracts lack implementable public shapes

- **Location:** `docs/phase8/v1/PHASE_8_DOC.md:206`–`:255`, `:925`–`:930`
- **Claim:** Several contracts expressly exposed to Phases 2, 6, and 7 are not specified as
  callable/data contracts sufficiently for those consumers to implement without inventing API.
- **Evidence:** The public-shape block names `ShadowPlan`, `ShadowHookHealth`,
  `ShadowMipmapPolicy`, and `ShadowPcfPolicy` without defining their schemas
  (`docs/phase8/v1/PHASE_8_DOC.md:206`–`:238`). Section 5 additionally exposes
  `ShadowCelestialPolicy`, `ShadowCameraMath`, and `ShadowFrustum` to downstream consumers
  (`docs/phase8/v1/PHASE_8_DOC.md:925`–`:930`), but the document does not give those contracts
  concrete callable or data shapes. Existing prose supplies substantial algorithms, plan identity,
  and mipmap semantics, but cannot tell a consumer what constructors, methods, fields, and closed
  results to compile against. The governing template requires both exact component semantics and
  exposed named interfaces/data contracts (`docs/design/v2.0-RC3/DESIGN.md:809`–`:813`).
- **Severity:** correction. This is a bounded specification omission, not a structural rebuild.
- **Required correction:** Define the genuinely missing downstream public schemas and operations
  and connect them to the already specified algorithms and closed outcomes. Preserve existing plan
  identity, mipmap-policy semantics, and `ShadowTraversalPlan` rather than redundantly redesigning
  them; add equality, fingerprint, ownership, or identity rules where downstream behavior observes
  them.
- **Touches interface/change-trigger region:** yes.

### candidate-002 — R8-1 lacks an interoperable ShadowExecutionView authentication seam

- **Location:** `docs/phase8/v1/PHASE_8_DOC.md:248`–`:255`, `:306`–`:309`, `:993`
- **Claim:** Phase 7 cannot issue, and Phase 8 glue and hooks cannot consistently validate, the
  promised authenticated execution credential from the requested contract as written.
- **Evidence:** `ShadowWorldPort.openState` consumes `ShadowExecutionView`
  (`docs/phase8/v1/PHASE_8_DOC.md:248`–`:255`), and the glue must validate the borrowed view before
  each operation (`:306`–`:309`). R8-1 requests a borrowed authenticated view and bridge open/close
  around invocation, but gives no issuer-backed validation operation or closed validation result
  (`:993`). The target does already specify render-thread/single-entry behavior (`:471`–`:474`),
  identities to validate (`:480`–`:482`), slot-close invalidation (`:883`–`:888`), and
  invocation-only retention (`:913`–`:915`); therefore the defect is narrower than a wholly
  undefined lifetime.
- **Severity:** correction. The missing seam is locally specifiable.
- **Required correction:** Make R8-1 representation-neutral but exact about the issuer, credential
  validation operation/result, the active execution identity and slot epoch it proves, and close
  and nesting/re-entry behavior. Reuse, rather than restate, the existing thread, retention,
  re-entry, and invalidation rules.
- **Touches interface/change-trigger region:** yes.

### candidate-003 — R8-2 does not define implementable Phase 5 operations and result algebra

- **Location:** `docs/phase8/v1/PHASE_8_DOC.md:827`–`:835`, `:951`–`:958`, `:994`
- **Claim:** The requested fixed shadow bindings, post-shadow mipmap generation, and coherent
  neutralization transition require API and outcome choices that R8-2 leaves to Phase 5 to guess.
- **Evidence:** Section 4.9 establishes timing and per-buffer degradation
  (`docs/phase8/v1/PHASE_8_DOC.md:827`–`:835`), while §5.3 accurately records that the current Phase
  5 surface lacks the requested operations (`:951`–`:958`). R8-2 lists three capabilities but does
  not state receivers, typed parameters, binding-view lifetime, rejection precedence, per-buffer
  mipmap outcomes, or a closed result for `degradeToNeutral` (`:994`). Because this is a requested
  change to a binding dependency contract consumed by Phase 8, prose policy and ordering do not
  substitute for an implementable cross-phase contract.
- **Severity:** correction. The architecture remains intact, but the dependency extension is not
  actionable as written.
- **Required correction:** Specify only the Phase-8-observable Phase 5 contract: callable
  signatures and typed inputs; generation/frame/snapshot checks and rejection ordering; binding
  ownership/lifetime; aggregate and per-buffer mipmap outcomes with failure restoration; and an
  idempotent neutralization result defining open-snapshot abort/invalidation and subsequent
  `shadow()` and fixed-unit views.
- **Touches interface/change-trigger region:** yes.

### candidate-004 — The force-shadow-program requirement is missing from the conformance map

- **Location:** `docs/phase8/v1/PHASE_8_DOC.md:370`–`:388`
- **Claim:** An explicitly assigned Phase 8 requirement has design coverage but no contract-to-design
  mapping and provenance row.
- **Evidence:** The governing assignment requires Phase 8 to define the interval during which Phase
  4 forces the shadow program (`docs/design/v2.0-RC3/DESIGN.md:2000`–`:2001`). Section 4.10 defines
  the barrier request and says the interval starts immediately before the first shadow draw and ends
  before fixed-function/state restoration (`docs/phase8/v1/PHASE_8_DOC.md:857`–`:862`). The complete
  §3.1 behavior table (`:370`–`:388`) contains no corresponding row.
- **Severity:** correction. This is a traceability omission; the architecture already exists.
- **Required correction:** Add a §3.1 row mapping the force-shadow rule and exact interval to §4.10,
  with the governing assignment and Phase 4 barrier contract as provenance.
- **Touches interface/change-trigger region:** no.

### candidate-006 — Appendix conformance rows omit mandatory provenance tags

- **Location:** `docs/phase8/v1/PHASE_8_DOC.md:392`–`:430`
- **Claim:** The Appendix A.3, B.2/B.3, and D conformance mappings cite coordinates but do not carry
  the confidence/provenance tags required per row.
- **Evidence:** The mandatory template requires each conformance row to map to a design element and
  a provenance tag (`docs/design/v2.0-RC3/DESIGN.md:804`–`:806`), and confidence tags carry over
  unchanged (`:111`–`:113`). The appendix rows beginning at
  `docs/phase8/v1/PHASE_8_DOC.md:392` provide source coordinates but omit tags such as `[V:doc]` or
  `[V:observed]`; tagged §3.1 rows do not establish row-level provenance for these distinct maps.
- **Severity:** correction. This is a systematic but mechanical document-gate defect.
- **Required correction:** Add the correct RESEARCH-defined tag to every affected row in §§3.2–3.4,
  preserving the coordinates and selecting tags from each claim's actual evidentiary basis.
- **Touches interface/change-trigger region:** no.

## 2. Checked and clean

The declared consumption of the existing Phase 4–7 binding contracts is otherwise honest: the
document distinguishes granted surfaces from R8-1/R8-2/R8-4 and requires owner fix-up plus fresh
verification before consumption. The enumerated shadow directives, buffers, samplers, uniforms,
camera behavior, traversal, draw order, depth split, PCF, mipmaps, blob suppression, and cloud
behavior otherwise have corresponding design coverage. The mandatory thirteen sections, written
camera/snap math, traversal algorithm, shadow appendix maps, App-E-format hook ledger, and explicit
absence-of-reference risk are present and substantive. No assigned open-question structure is due.

**candidate-005 is dropped on re-derivation.** The Phase 8 doc gate separately requires (1) every
shadow-related App A.3/B.2/D.3 row in the conformance map and (2) added hook sites in App E format
(`docs/design/v2.0-RC3/DESIGN.md:2024`–`:2028`). Section 4.13 supplies the latter ledger, while §3
maps the underlying shadow behaviors, including traversal, terrain order, Forge entity passes,
clouds, and blob suppression. Requiring a further meta-row for the hook ledger itself is not stated
by that gate and is not an independently unmapped RESEARCH §3/App contract item. Final disposition:
dropped, severity none, interface impact no.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=5; notes=0
Interface changed: yes

Five candidates are admitted as corrections; none is blocking and none is a note. candidate-005 is
dropped. Candidates 001–003 require changes in the declared §5 cross-phase-interface region, so a
fresh verify round is required before Phase 8 can close. There is no prior-round trend on this
first review. The document is substantial and repairable, so FAIL is not warranted; it has not
converged because literal PASS requires zero corrections.

Next action: perform the governed fix-up for this review, append its Resolutions, add the required
§0.1 addendum to the Phase 8 document, and then run a fresh verification round because the binding
interface region changes.

## Resolutions

### candidate-001 — resolved

Added binding public shapes for `ShadowPlan`, plan/hook fingerprints, mipmap and PCF policies,
celestial and camera operations, finite frustum fixtures/predicate, and hook-health rows. The
neighboring semantics now fix collection ordering/immutability, structural equality, fingerprint
inputs and use, health ownership, and the links from the callables to §§4.5–4.6. Existing
`ShadowPolicy`, plan outcomes, and `ShadowTraversalPlan` remain unchanged.

### candidate-002 — resolved

Expanded R8-1 without choosing a representation: Phase 7 is the sole bridge issuer; open, validate,
and close are specified; validation has a closed ordered rejection algebra and proves the active
execution identity plus slot epoch. Close invalidates before invocation return, a second open/nested
use issues no credential, and the existing §4.3 thread, retention, re-entry, and invalidation rules
remain authoritative.

### candidate-003 — resolved

Expanded R8-2 with Phase-8-observable signatures for fixed shadow bindings, per-buffer mipmap
generation, and idempotent neutralization. The request now fixes generation/frame/snapshot check
precedence, pre-GL rejection, borrowed binding lifetime and exact fixed units, ordered per-buffer
mipmap results and filter restoration, plus open-snapshot abort/invalidation and subsequent
`shadow()`/fixed-unit neutral behavior. These remain requested Phase 5 changes, not assumed grants.

### candidate-004 — resolved

Added a §3.1 row mapping the forced root-shadow program and its exact begin/end interval to §4.10,
with the governing Phase 8 assignment and verified Phase 4 barrier contract as provenance.

### candidate-006 — resolved

Added `[V:doc]` to every Appendix A.3, B.2/B.3, and D conformance row. These mappings restate
documented inventory/contracts (including Phase 6-derived inverse behavior), so no observed or
decision tag was substituted. Existing coordinates and dispositions were preserved.

### Notes deferred

None. The adjudicator admitted no notes. candidate-005 remains dropped and was not applied.

### Fix-up status

All five admitted corrections were applied. Section 5 changed, so the manifest's interface-change
trigger fires: a fresh verify round is required before Phase 8 can close.
