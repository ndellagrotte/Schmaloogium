## 0. Method and reading order

This adjudication independently re-derived the sole surviving candidate against, in order:

1. the whole Phase 4 target, with focused inspection of its §3 conformance map;
2. the governing Part I, mandatory template, Phase 4 specification, and document gate in the
   explicitly overridden `docs/design/v3/DESIGN.md` revision;
3. the provenance taxonomy and cited stage/program contracts in
   `docs/research/v1/RESEARCH.md`;
4. the manifest-selected binding contracts in `docs/phase1/v14/PHASE_1_DOC.md` and
   `docs/phase3/v1/PHASE_3_DOC.md`; and
5. the listed supporting evidence where relevant to the two affected contract rows.

Only after independently settling the candidate were
`docs/phase4/reviews/PHASE_4_REVIEW_1.md` through
`docs/phase4/reviews/PHASE_4_REVIEW_29.md` consulted as settled prior material. No prior review
settles or clears these two undefined provenance cells.

There were no reading-set deviations, no network use, no agent fan-out, no forbidden-source use,
no candidates eliminated before adjudication, and no Gate drops. The canonical engine had already
dispatched this atomic adjudication role, so the verification-loop skill, `scripts/verify`, and any
additional Codex session were not invoked.

## 1. Findings

### candidate-001 — Two conformance rows use an undefined provenance tag

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:704` and
  `docs/phase4/v1/PHASE_4_DOC.md:708`
- **Claim:** Two mandatory conformance-map rows use `[V:doc/web]`, which has no meaning in the
  authoritative provenance taxonomy.
- **Evidence:** The mandatory template requires every conformance-map row to carry a provenance
  tag (`docs/design/v3/DESIGN.md:831-834`). The Phase 4 map labels both the 0…99 sparse-array row
  and the compute-companion eligibility row `[V:doc/web]`
  (`docs/phase4/v1/PHASE_4_DOC.md:703-708`). The ground-truth taxonomy defines `[V:doc]` and
  `[V:web]` separately but defines no hybrid `[V:doc/web]` token
  (`docs/research/v1/RESEARCH.md:28-38`). Consequently, the affected cells do not state a defined
  evidence class, and correctly tagged coverage elsewhere cannot repair their own provenance.
- **Required correction:** Replace each `[V:doc/web]` with the applicable defined tag. Use
  `[V:web]` where only web evidence supports the row; list `[V:doc]` and `[V:web]` separately only
  where both evidence classes independently support the claim.
- **Severity:** correction
- **Touches interface/change-trigger region:** no

## 2. Checked and clean

- The finder-reported new surface is clean. The §0.33 publication changes consistently separate
  candidate-build and publication-protocol failures, validate before old-publication mutation,
  preserve the absent-old-barrier branch, and propagate the closed publication-failure model
  through APIs, diagnostics, tests, checklist language, and status.
- The cross-phase interface region is honest and aligned with the detailed design. Phase 4
  distinguishes consumed dependency grants from requested capabilities and consistently exposes
  its publication, barrier, ownership, failure, generation, and registry contracts.
- Apart from the admitted provenance defect, the conformance map covers and supports the modern
  and G6 stage configurations, classic catalog and fallback families, compile/link sequence,
  fixed attributes, program state, failure handling, barriers, and reload generation.
- Candidate-001 is not cleared as harmless local shorthand. The authoritative taxonomy enumerates
  the permitted tag meanings separately, while §G9 requires each row's provenance to be stated;
  no other row or prior resolution defines the hybrid form.
- No candidate was refuted, cleared, or dropped on re-derivation, and no finding was admitted
  beyond the supplied candidate set.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: no

The admitted defect is a bounded provenance correction, not a structural miss requiring rebuild.
Literal `PASS` is unavailable until it is corrected. The finding does not touch the
manifest-declared `cross-phase-interfaces` change-trigger region.

The prior-review trend supplied for this round contains no derived trend entries. Independent
inspection shows that Round 29's two publication findings were resolved and the current round has
one newly surfaced, localized metadata correction. This is not convergence to zero corrections,
so Phase 4 cannot close yet, but it does not indicate a renewed structural or interface failure.

The next required action is a scoped Phase 4 fix-up replacing both undefined provenance tags,
appending this review's `## Resolutions`, and adding the required correction addendum. A fresh
whole-document verification round is then required before Phase 4 can close.

## Resolutions

### candidate-001 — corrected

Re-derived against the confidence taxonomy at `docs/research/v1/RESEARCH.md:28`–`:38` and the
modern-stage source characterization at `docs/research/v1/RESEARCH.md:330`–`:335`. The shipped
G6 pack-author document does not independently establish either modern claim: the 0…99 arrays and
the compute-companion eligibility rule come from the live upstream OptiFine/Iris documentation
summarized in research §3.6. Accordingly, both `[V:doc/web]` cells in
`docs/phase4/v1/PHASE_4_DOC.md` §3.1 now use the single defined tag `[V:web]`; listing `[V:doc]`
separately would overstate the evidence. A compact §0.34 addendum records the correction.

No binding §5 or manifest-declared cross-phase-interface text changed. The correction is limited
to conformance metadata and header/addendum bookkeeping, so the interface change trigger did not
fire. A fresh whole-document verification round remains required before Phase 4 can close.

### Notes deferred

None; Round 30 admitted no notes.
