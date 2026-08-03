## 0. Method and reading order

I independently re-derived all three Gate-surviving candidates first from the whole Phase 7
target, the governing RC3 design selectors, RESEARCH, the relevant Phase 3 binding contract, and
the manifest-listed evidence and dependency contracts. Only after fixing those judgments did I
read `docs/phase7/reviews/PHASE_7_REVIEW_1.md` through `PHASE_7_REVIEW_11.md`, in round order, to
check settled material and regression history. There were no reading-list deviations, no network
use, no agent fan-out, no forbidden-source use, no candidates eliminated before adjudication, and
no Gate drops.

## 1. Findings

### candidate-001 — Ordinary internal-pack loading cannot satisfy the mandatory gated manifest contract

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1217`–`:1224`, `:1303`–`:1306`
- **Claim:** Phase 7 says ordinary internal-pack supply/loading remains implementable before R7-9,
  but its concrete built-in provider must implement an interface whose unconditional `manifest()`
  result cannot be produced until R7-9 is granted and reverified.
- **Evidence:** `InternalPackContent extends InternalPackSource` and unconditionally declares
  `InternalPackManifest manifest()` (`docs/phase7/v1/PHASE_7_DOC.md:1217`–`:1224`). The detailed
  design requires `BuiltInPassthroughPack` to implement that stronger subtype
  (`docs/phase7/v1/PHASE_7_DOC.md:737`–`:740`), while the same document forbids internal-pack
  manifest/digest production before R7-9 (`docs/phase7/v1/PHASE_7_DOC.md:1303`–`:1306`) and stages
  supply/load before the gated production (`docs/phase7/v1/PHASE_7_DOC.md:1618`–`:1620`). Phase 3's
  actual loading protocol requires only `identity()` and `snapshot(...)`
  (`docs/phase3/v1/PHASE_3_DOC.md:252`–`:255`). No pre-grant provider type, typed unavailable
  result, or staged implementation rule reconciles those contracts. Prior Round 8 settled that
  the built-in content must connect to `InternalPackSource`; it did not settle this later-created
  conflict between that subtype and the Round 9 R7-9 gate.
- **Required correction:** Separate the always-available Phase 3 `InternalPackSource` provider
  from the R7-9-dependent manifest producer, or give manifest access an explicit typed pre-grant
  unavailable result with exact behavior. Synchronize §4.7, §5, §8.1, §9.1, and checklist item 5.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the required repair changes the exposed
  `InternalPackContent` contract in the manifest-selected §5 region.

### candidate-002 — The public reload request requires the configuration that Phase 7 must load

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1226`–`:1232`
- **Claim:** The only exposed Phase 12 reload entry point cannot honestly express a pack-selection
  change because it requires the post-load `PackConfiguration` alongside the pre-load selection.
- **Evidence:** The manager owns the selected `PackConfiguration`, and its state machine begins a
  selection request with Phase 3 discovery/loading (`docs/phase7/v1/PHASE_7_DOC.md:476`–`:488`).
  Nevertheless, `ReloadRequest` requires both `PackSelection selection` and `PackConfiguration
  configuration` (`docs/phase7/v1/PHASE_7_DOC.md:1226`–`:1232`). Phase 3 makes configuration the
  validated product of loading a selection and rejects stale or unknown selections
  (`docs/phase3/v1/PHASE_3_DOC.md:1149`–`:1156`). Phase 7 identifies this controller as Phase 12's
  entry point (`docs/phase7/v1/PHASE_7_DOC.md:1379`) and Phase 3 discovery/load as its reload input
  (`docs/phase7/v1/PHASE_7_DOC.md:1408`–`:1409`), but provides no alternative selection-intent API
  or controller-side derivation rule. Prior reviews expanded the reload schema but did not settle
  this circular ownership requirement.
- **Required correction:** Replace the single request with closed intent variants. A
  selection-change variant must carry only the stable inputs or opaque load intent needed for
  Phase 7 to invoke Phase 3 and own the resulting configuration. If an already-loaded rebuild is
  needed, model it separately with explicit provenance and selection/configuration validation.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — `ReloadRequest` is an exposed §5 contract.

### candidate-003 — Engine-flag provenance does not support the mapped runtime semantics

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:429`–`:438`
- **Claim:** The §3.5 rows label detailed cancellation, GL-state, and visibility behavior as
  `[V:doc]` even though the cited sources establish only key shape, ownership, and limited
  precedence.
- **Evidence:** Phase 3 says the behavior owner must wire the render-visible effect but supplies
  only types, owners, and parser tests (`docs/phase3/v1/PHASE_3_DOC.md:470`–`:493`). RESEARCH
  Appendix F.1 catalogs the flags, tri-state shape, clouds values, and video-setting precedence
  (`docs/research/v1/RESEARCH.md:1439`–`:1445`); it does not establish the exact hook cancellation,
  depth/cull manipulation, or frustum-query behavior asserted by Phase 7. The target itself
  acknowledges that the inputs otherwise provide only field names
  (`docs/phase7/v1/PHASE_7_DOC.md:80`–`:83`). The governing provenance rule requires behavioral
  claims to cite §4 evidence (`docs/design/v2.0-RC3/DESIGN.md:111`–`:113`) and the mandatory map
  requires deviations to be flagged decisions (`docs/design/v2.0-RC3/DESIGN.md:804`–`:808`).
  Round 11 added `[V:doc]` to cure missing tags; it did not establish behavioral provenance and
  therefore introduced the present overclaim.
- **Required correction:** Separate the documented field/type/owner/precedence facts from the
  exact runtime semantics. Cite authoritative §4 behavioral evidence where it exists; otherwise
  identify the semantics explicitly as Phase 7 design decisions with rationale.
- **Severity:** correction
- **touches interface/change-trigger region: no** — the repair is confined to §3.5 provenance and
  decision treatment.

## 2. Checked and clean

The frame-flow, Appendix A.1 program, §7.1 hook-need, and seven-row reference-timeline maps remain
complete on re-derivation. No additional defect was found in the Phase 2 capture hand-off, Phase
4/5 coordinated publication, Phase 6 frame-begin ordering, shadow invocation seam, hook result
algebras, or the feature-specific R7-1, R7-2, R7-3, and R7-8 gates. The finders likewise reported
the edited staging/checklist synchronization and the remaining dependency interfaces clean.

No candidate was refuted or cleared on independent re-derivation. Prior-review comparison did not
show any candidate already settled: candidate-001 arises from the interaction of the earlier
source-subtype correction with the later R7-9 gate; candidate-002 remains an unresolved semantic
gap in the expanded reload schema; candidate-003 is a provenance overclaim created by Round 11's
tag-only correction.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

Two admitted corrections affect the declared cross-phase interface region, so its change trigger
applies. The correction trend is not converging strictly (recent rounds: 1, 3, 3), and two findings
are regressions or interactions created by prior fix-ups; this does not justify softening them or
escalating the structurally repairable document to FAIL. Apply all three corrections in a scoped
fix-up, record resolutions, and then run a fresh whole-document verification round before Phase 7
can close.

## Resolutions

### candidate-001 — applied

Re-derived from Phase 3's binding `InternalPackSource` protocol and the target's R7-9 gate. The
always-available `BuiltInPassthroughPack` now implements only the Phase 3 source contract. A separate
`InternalPackManifestProducer` capability is explicitly unavailable until R7-9 is granted and Phase
3 reverified. Section 4.7, the §5 declarations and contract table, §8.1, §9.1, and checklist item 5
now distinguish ordinary pre-grant loading from post-grant manifest/digest production.

### candidate-002 — applied

Re-derived from Phase 3's selection validation and configuration ownership. `ReloadRequest` now
carries a closed `ReloadIntent`: `Select` supplies only `PackSelection`, while `RebuildActive`
supplies an expected active `PipelineIdentity`. The controller owns Phase 3 discovery/loading and
the resulting configuration; an active rebuild must match the manager-owned active identity and
cannot claim `PACK_SELECTION`. Coalescing and provenance validation are stated alongside the API.

### candidate-003 — applied

Re-derived against Phase 3's ownership table and RESEARCH Appendix F.1. Section 3.5 now labels only
the documented field, owner, and applicable precedence facts `[V:doc]`; each exact cancellation,
GL-scope, or visibility-query mapping is identified as local decision `D-P7-11`, whose rationale is
recorded in §11.1.

### Notes deferred

None. The adjudication admitted no notes.
