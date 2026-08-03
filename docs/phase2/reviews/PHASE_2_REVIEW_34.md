# Phase 2 verification review — round 34

## 0. Method and reading order

I first independently re-derived the supplied candidate from the complete target in
`docs/phase2/v1/PHASE_2_DOC.md`, the resolved governing selectors in
`docs/design/v1.1/DESIGN.md`, the contract ground truth in
`docs/research/v1/RESEARCH.md`, the selected Phase 1 binding contract in
`docs/phase1/v14/PHASE_1_DOC.md`, and the resolved target contract. Only after settling that
judgment did I read discovered prior reviews 1–33, last, and compare the candidate with their
settled findings, resolutions, and clean-area conclusions.

There were no reading-order deviations and no network use. This already-dispatched atomic role did
not invoke the verification harness, start another Codex session, or use agent fan-out. The
canonical engine supplied the finder, refuter, and Gate material. The Gate dropped no candidates,
there were no pre-adjudication eliminations, and forbidden sources were not read.

Prior round 17 described the then-new runner-owned provenance path as internally consistent, but it
did not test the distinct scene-domain values `internal` and `OFF` against that path's closed
`MODRINTH|MANUAL` domain. No prior finding or resolution supplies a sentinel, conditional schema,
or validation restriction for those values. The present candidate therefore is not cleared by the
earlier general clean statement.

## 1. Findings

### candidate-001 — Capture provenance cannot encode valid `internal` and `OFF` scene selections

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:715,819–832,894–899`
- **Claim:** Every shader-pack selection accepted by the scene wire format must have a canonical,
  implementable representation in the capture-plan and run-manifest provenance contracts.
- **Evidence:** The scene field catalogue accepts a pack id and version, `internal`, or `OFF` as
  `[pack] shaderpack` (`docs/phase2/v1/PHASE_2_DOC.md:715`). The capture plan nevertheless requires
  `pack.id`, `pack.version`, `pack.acquisitionMode`, `pack.archiveSha512`, and `pack.licence` for
  every plan (`docs/phase2/v1/PHASE_2_DOC.md:819–825`), restricts acquisition mode to exactly
  `MODRINTH|MANUAL`, requires an archive SHA-512, and derives all three provenance facts from a
  resolved `PackFixture` and verified archive (`docs/phase2/v1/PHASE_2_DOC.md:828–832`). The run
  manifest repeats the same unconditional required fields and domains
  (`docs/phase2/v1/PHASE_2_DOC.md:894–899`). The fixture registry and acquisition modes define only
  the seven external matrix packs; no target text limits `internal` or `OFF` to a non-capture run,
  rejects them during client-capture validation, or defines canonical provenance values for them.
- **Disposition:** Admitted. Define aligned scene-validation, capture-plan, and run-manifest rules
  for `internal` and `OFF`, including exact acquisition-mode and archive-hash/licence presence or
  sentinel semantics. Alternatively, explicitly reject those selections for client-capture scenes.
  The fix must preserve one canonical representation across runner, agent transport, validation,
  and failure-manifest publication.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the defect and required correction affect the
  declared `scene-wire-format`, `capture-agent-plan-and-frame-grab`, and
  `run-manifest-wire-schema` regions.

## 2. Checked and clean

The finder-reported new-surface checks hold: the §0.34 header/addendum and closing lifecycle marker
consistently describe the round-33 fix-up and current unverified surface. The selected Phase 1
binding contract supports the examined module/seam, capability-profile, recording/replay,
diagnostic, debug-capture, and CI consumptions. The conformance audit found no additional
candidate-backed defect in the mapped tiers, harness requirements, all seven Appendix G fixtures,
milestone gates, initial scene families, OQ-10 plan, or mandatory thirteen-section structure.

No candidate was refuted or cleared on independent re-derivation. Candidate-001 is not duplicate
settled material: prior provenance review established ownership and byte-exact transport for
registry-backed archives, while this candidate tests two additional values explicitly admitted by
the scene contract. Both refuters confirmed that no alternate branch, sentinel, or capture-only
restriction reconciles those domains. The Gate dropped none, and no finding is created from
candidate-free clean areas.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The admitted schema-domain mismatch is bounded fix-up work rather than a structural miss requiring
a rebuild. It is consumer-visible across three declared interface/change-trigger regions, so the
interfaces are treated as changed for this review.

Rounds 31–34 each contain one correction; the count is not strictly decreasing and the loop is not
yet converged. The prior three corrections concerned recurring lifecycle bookkeeping, whereas this
round's independently supported correction is a newly exposed wire-domain defect, so the trend does
not justify dropping or softening it. Apply candidate-001's correction and record its resolution in
this review. Because interface regions must change, run a fresh whole-document verification round
before Phase 2 can close or its interface can be consumed as verified. Do not roll the version while
the loop remains open.

## Resolutions

### candidate-001 — applied

Re-derived the mismatch from the live scene, plan, and manifest domains. I chose the adjudicator's
explicit-rejection alternative rather than inventing provenance sentinels: `internal` and `OFF`
remain valid scene selections for headless consumers, while client-capture preflight now rejects
both before cache or client-process work and admits only an id + version resolving to one registry
`PackFixture` and verified archive. The capture-plan and run-manifest `/1` contracts now state that
they have no sentinel representation and apply only after that preflight, preserving the existing
closed `MODRINTH|MANUAL`, archive-hash, and licence domains and one canonical runner/agent/failure-
manifest representation for every admitted capture. The exposed §5 wire-contract rows were aligned
with the same restriction, and the closing lifecycle marker now requires round 35.

Intentional interface edits: `scene-wire-format`, `capture-agent-plan-and-frame-grab`,
`run-manifest-wire-schema`, and `cross-phase-interfaces`. Their change trigger requires a fresh
whole-document verification round before Phase 2 closes.

### Notes deferred

None; the adjudicator admitted no notes.
