# Phase 3 Adversarial Review — Round 28

## 0. Method and reading order

I independently re-derived the sole candidate from the complete Phase 3 target, then the
manifest-selected v3 governing-design regions, the relevant RESEARCH.md contract ground truth,
the Phase 1 binding contract, and the permitted supporting evidence. Only after settling the
candidate's interpretation, severity, and interface classification did I read prior reviews
1–27, in round order and including their resolutions.

There were no deviations from the required reading order, no network use, no agent fan-out, and
no use of forbidden sources. Under the dispatched-role exception in the supplied `verify-loop`
skill, I did not invoke the verification harness or start another session. No candidate was
eliminated before adjudication, and the Gate reported no drops.

## 1. Findings

### candidate-001 — Phase 13's custom-texture publication lacks a closed data contract

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1321`.
- **Claim:** Phase 13 cannot implement consumption of Phase 3's parsed custom/noise texture model
  from the binding publication surface without inventing consumer-visible structure.
- **Evidence:** Section 5 declares itself the complete Phase 3 publication surface
  (`docs/phase3/v1/PHASE_3_DOC.md:1302-1305`), but its texture row names only
  `CustomTextureSpec` and `NoiseTextureSpec` and describes them as “lossless specs only”
  (`docs/phase3/v1/PHASE_3_DOC.md:1320-1322`). The only elaboration identifies three source
  variants and selected retained concepts—stage, sampler, duplicate discriminator, raw texture
  properties, sidecar, and noise override—without defining their exact components, closed value
  domains, optionality, ordering, or associations (`docs/phase3/v1/PHASE_3_DOC.md:671-677`). The
  governing template requires §5 to expose named cross-phase data contracts
  (`docs/design/v3/DESIGN.md:838-840`), and Phase 13 owns texture loading rather than definition of
  Phase 3's parsed representation (`docs/design/v3/DESIGN.md:1457-1459`). RESEARCH confirms that
  this representation must distinguish the three source forms, texture type and raw-format data,
  sampler-type sharing, sidecar settings, stage expansion, and noise override
  (`docs/research/v1/RESEARCH.md:1481-1490`).
- **Severity:** correction. Define in §5 the closed immutable consumer-visible algebra for
  `CustomTextureSpec`, `NoiseTextureSpec`, `TextureSidecarRef`, and required component value
  types. Specify exact fields and variants, domains, absence/default and deterministic ordering
  rules, duplicate-discriminator handling, and sidecar association. Producer parsing algorithms
  need not be duplicated there.
- **Touches interface/change-trigger region:** yes.

## 2. Checked and clean

- The finder-reported new-surface and conformance areas were rechecked. The §0.29 header,
  addendum, and closing status are coherent; schema version 2 remains consistent; and the
  `ProgramStateModel` and `ResourceRequirements` contracts agree with their detailed-design and
  checklist counterparts.
- Phase 3's declared consumption of the Phase 1 module/seam, capability, logging, diagnostics,
  debug-flag, licensing, and conformance-extension contracts is supported by the selected binding
  dependency region.
- The examined Appendix F and Appendix A.3 families retain named conformance coverage, including
  all custom-texture forms and the four mandated Pintonium pitfalls. That parsing coverage does not
  substitute for the missing consumer-visible §5 shape.
- No candidate was refuted or cleared on re-derivation. Prior Round 6 settled raw-format validation
  domains, and Rounds 17–18 settled Phase 13's separate generated-noise requirement; neither
  defines the published custom/noise texture record algebra. Round 27's PASS settled the prior
  candidate set but does not clear this newly admitted interface omission. There were no candidates
  eliminated before adjudication and no findings dropped on derivation.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The admitted finding is a bounded interface-contract completion and does not require rebuilding
the architecture. Round 27 reached zero corrections, but this round exposes one previously
uncleared omission, so the trend regresses from literal PASS and the current surface has not
converged.

The next required action is a scoped fix-up resolving `candidate-001` and appending this review's
`## Resolutions`. Because the correction changes the declared cross-phase interface/change-trigger
region, a fresh whole-document verification round is required before Phase 3 may close.

## Resolutions

### candidate-001 — applied

Re-derived from RESEARCH Appendix F.5 and the governing Phase 3/Phase 13 ownership split. Section
5.1 now publishes closed immutable variants and exact fields for `CustomTextureSpec`,
`NoiseTextureSpec`, `TextureBindingKey`, and `TextureSidecarRef`. It fixes the stage, texture-target,
dimension, discriminator, absence, sidecar-association, duplicate, and deterministic-ordering
semantics, while referring to §4.8's already closed format domains instead of duplicating them.
The model preserves Minecraft resource identities verbatim and leaves opening, decoding, sidecar
interpretation, and GL realization to Phase 13.

This intentionally changes the manifest-declared cross-phase-interface region. A fresh whole-
document verification round is therefore required before Phase 3 can close. The compact §0.30
addendum records only the corrected surface; `PackFrontEnd.CURRENT_SCHEMA_VERSION` remains 2
because no `PackConfiguration` record component changed.

### Notes deferred

None; the adjudicator admitted no notes.
