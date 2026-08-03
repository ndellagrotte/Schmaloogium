## 0. Method and reading order

This adjudication independently re-derived both surviving candidates against, in order:

1. the whole Phase 4 target, with focused checks of its header/addenda, compiler request,
   candidate/private-registry ownership, cleanup, barrier construction and activation, and the
   manifest-declared cross-phase interface region;
2. the governing Part I, Phase 4 specification, document gate, and mandatory template in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the manifest-selected binding contracts in
   `docs/phase1/v14/PHASE_1_DOC.md:4160-4265` and
   `docs/phase3/v1/PHASE_3_DOC.md:1300-1478`; and
5. the listed supporting evidence where it bore on a candidate.

Only after settling those interpretations were
`docs/phase4/reviews/PHASE_4_REVIEW_1.md` through
`docs/phase4/reviews/PHASE_4_REVIEW_22.md` consulted as settled prior material. None resolves the
new stale-header defect or establishes the compiler/product conflation alleged by candidate-002.

There were no reading-set deviations, no network use, no agent fan-out, no forbidden-source use,
no candidates eliminated before adjudication, and no Gate drops. The canonical engine had already
dispatched this atomic adjudication role, so neither the verification harness nor another Codex
session was invoked.

## 1. Findings

### candidate-001 — Header identifies §0.25 as the latest revision after §0.26 was added

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:7` and
  `docs/phase4/v1/PHASE_4_DOC.md:266-269`
- **Claim:** The document's unique latest-revision marker is internally inconsistent with its
  correction-addendum sequence.
- **Evidence:** The header states `Last revised: 2026-08-03 (§0.25)`
  (`docs/phase4/v1/PHASE_4_DOC.md:7`), but the same document contains the later
  `0.26 Review 22 correction addendum` and says that correction was integrated into §§2–5
  (`docs/phase4/v1/PHASE_4_DOC.md:266-269`). The date can remain unchanged, but the section
  reference cannot truthfully identify §0.25 as the last revision while §0.26 exists.
- **Required correction:** Change the header's last-revised reference from `§0.25` to `§0.26`,
  retaining the existing date.
- **Severity:** correction
- **Touches interface/change-trigger region:** no

## 2. Checked and clean

- The viewport-scale correction introduced by Review 22 is otherwise coherent. The public bundle,
  conformance map, detailed adapter, and binding §5 all preserve
  `Optional<ViewportScale>` and distinguish absence from a present identity value.
- The stage/program registry, catalog and fallback, compilation, fixed attributes, geometry,
  publication, barrier, generation, projection, and per-program-state surfaces examined by the
  finder lenses produced no additional admitted finding.
- The manifest-selected Phase 1 and Phase 3 binding surfaces remain represented without an
  invented dependency capability.
- **candidate-002 is dropped on re-derivation.** Its impossibility claim conflates the compiler
  instance with its separate caller-owned return product. The statement that “the compiler
  retains none after return” has the compiler as its subject; successful program ownership is
  expressly placed in the unpublished candidate and later private registry
  (`docs/phase4/v1/PHASE_4_DOC.md:1034-1037`), and only the private registry/barrier implementation
  obtains the operational handle (`docs/phase4/v1/PHASE_4_DOC.md:620-621`). Phase 1 exposes
  independently typed services through `GLDevice`, so Phase 4 can capture the specific non-owning
  service references its returned product needs without the compiler retaining the request or its
  listed input objects. A device-lifetime sentence could be editorially helpful, but the candidate
  does not establish an inconsistent or unimplementable contract and therefore does not warrant a
  finding or interface-change disposition.
- Prior reviews establish the evolving candidate, registry, barrier, and ownership model but do
  not contain an equivalent unresolved retention finding. Review 22's settled correction is the
  source of the later §0.26 addendum and does not clear the stale unique header marker.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: no

The sole admitted defect is a bounded document-metadata correction. It does not require a rebuild
and does not touch the manifest-declared cross-phase interface region. Literal `PASS` is
unavailable while that correction remains.

Round 22 also had one correction, and this round again has one, although the present defect is only
the stale revision marker introduced by the prior fix-up rather than another substantive contract
defect. The artifact has not yet produced the zero-correction round required for convergence and
closure.

The next required action is a scoped Phase 4 fix-up resolving candidate-001, appending this
review's `## Resolutions`, and adding the required compact Phase 4 fix-up addendum. Because this
correction does not change the `cross-phase-interfaces` change-trigger region, it does not itself
trigger the interface rule; the verification loop still requires a fresh review after fix-up, and
Phase 4 can close only when that round returns literal `PASS` with zero blocking findings and zero
corrections.

## Resolutions

### candidate-001 — Resolved

Re-derived against the target's ordered §0 sequence, the unique latest-revision marker was stale:
§0.26 followed the header's former §0.25 reference. The header and compact mandatory fix-up
addendum are one atomic correction, so the header now names §0.27, the new latest subsection,
rather than stopping at the intermediate §0.26 and immediately recreating the same defect. The
date remains 2026-08-03. No contract prose or manifest-declared cross-phase interface region
changed; a fresh verification round remains required by the loop, not by the interface trigger.

### Notes deferred

None. The adjudication admitted no notes.
