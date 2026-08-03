# Phase 5 Verification Review — Round 31

## 0. Method and reading order

I independently re-derived both Gate-surviving candidates before consulting prior reviews. I read
the Phase 5 target's contract-conformance map, shadow-estate signatures and lifecycle, failure
table, test plan, and manifest-declared §5 interface region; the RC3 Part I mandatory-template
rule, Phase 5 specification, and document gate; and the manifest-selected binding regions of
Phases 1, 3, and 4. The declared implementation/reference evidence was not needed to decide these
closed-contract and conformance-map defects.

Only after settling both interpretations did I read Phase 5 reviews 1 through 30 in numeric order,
with the prior dispositions and resolutions considered last. No prior resolution defines the
filter-restoration-failure branch or maps the conditional sfb-creation requirement. Round 21's
ordinary no-shadow result correction is related to, but does not settle, the distinct §3 mapping
omission in candidate-002.

I used no network access, forbidden source, or prior-session transcript. In particular, I did not
open `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt`, because the resolved forbidden-source
rule bars `*.txt` and it was unnecessary. There was no agent fan-out or delegation. In accordance
with the dispatched atomic-role instruction and the verify-loop skill, I did not invoke the loop,
run `scripts/verify`, or start another Codex session. There were no deviations from the resolved
reading contract, no candidates eliminated before adjudication, and no Gate drops.

## 1. Findings

### candidate-001 — Shadow mipmap restoration failure has no closed or observable outcome

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:1463`–`:1472`,
`docs/phase5/v1/PHASE_5_DOC.md:1509`–`:1518`,
`docs/phase5/v1/PHASE_5_DOC.md:1791`–`:1792`, and
`docs/phase5/v1/PHASE_5_DOC.md:1882`–`:1883`

**Claim.** The public shadow-mipmap protocol does not define the reachable secondary failure in
which mipmap generation fails and restoring that texture's configured non-mipmap min filter also
fails. The only per-buffer degraded outcome promises that restoration already succeeded and that
later buffers continue, while the adjacent pass-wide neutralization protocol cannot be selected
by Phase 8 because `generateShadowMipmaps` exposes no result identifying this pass-wide failure.

**Evidence.** `ShadowMipmapResult` permits only `Generated(outcomes)` or protocol `Rejected`, and
each requested buffer permits exactly `Generated`, `NotAllocated`, or `Degraded`
(`docs/phase5/v1/PHASE_5_DOC.md:1463`–`:1472`). The operational contract says a backend generation
failure restores the base filter before returning `Degraded` and does not stop later buffers
(`docs/phase5/v1/PHASE_5_DOC.md:1509`–`:1518`); binding §5 repeats that guarantee and exposes no
restoration-failure discriminator (`docs/phase5/v1/PHASE_5_DOC.md:1791`–`:1792`). The failure table
separately assigns ordinary mipmap failure to continued per-buffer degradation and pass-wide
backend failure to Phase-8-initiated `degradeToNeutral`, without defining how restoration failure
enters or communicates the latter transition (`docs/phase5/v1/PHASE_5_DOC.md:1882`–`:1883`). The
test plan scripts generation failures and proves successful restoration, but does not script the
restoration operation's own failure (`docs/phase5/v1/PHASE_5_DOC.md:2024`–`:2028`).

**Required correction.** Define this restoration-failure transition in the public binding
contract. Either expose a pass-wide backend-failure result that requires Phase 8 to abort and
generation-check neutralization, or perform an explicit atomic containment transition and return
a typed outcome for it. Specify token/snapshot state, remaining-buffer behavior, stable diagnostic
propagation, binding invalidation, and a conformance case that scripts restoration failure.

**Severity:** correction

**touches interface/change-trigger region: yes**

### candidate-002 — Conditional shadow-FBO creation is absent from the conformance map

**Location:** `docs/design/v2.0-RC3/DESIGN.md:1630`–`:1633`,
`docs/phase5/v1/PHASE_5_DOC.md:730`–`:740`, and
`docs/phase5/v1/PHASE_5_DOC.md:1353`–`:1358`

**Claim.** The governing Phase 5 assignment explicitly requires the sfb to be created only when
Phase 3 reports shadow-buffer use, but §3.2 does not map that lifecycle condition to a design
element and provenance as required by the mandatory zero-unmapped-rows conformance map.

**Evidence.** RC3 assigns conditional sfb creation to Phase 5
(`docs/design/v2.0-RC3/DESIGN.md:1630`–`:1633`). The detailed design implements the rule by making
the sfb absent unless Phase 3 reports at least one shadow depth buffer
(`docs/phase5/v1/PHASE_5_DOC.md:1353`–`:1358`). The shadow rows in §3.2 map attachment contents,
color counts, policy, swizzle, flip, sizing, resize, and Final handoff, but none maps the distinct
creation/absence gate (`docs/phase5/v1/PHASE_5_DOC.md:730`–`:740`). Detailed coverage elsewhere
does not replace §G9's required contract-item-to-design-and-provenance row.

**Required correction.** Add a §3.2 row mapping “sfb created only when Phase 3 reports shadow
depth use” to §4.10, citing the RC3 assignment and the applicable Phase 3 input provenance.

**Severity:** correction

**touches interface/change-trigger region: no**

## 2. Checked and clean

The finder-reported clean areas remain clean on re-derivation. The new shadow surface is otherwise
internally consistent: typed names, units 4/5/13/14, generation/frame/snapshot validation order,
neutralization idempotence, snapshot invalidation, later `shadow()` behavior, consumers,
milestones, and §0.32 status agree. The selected Phase 1, Phase 3, and Phase 4 binding contracts
support the consumed architecture, and pending versus granted dependency changes remain honestly
distinguished. Outside the conditional-creation omission, the conformance map covers the stated
Appendix B color, depth, shadow, fixed-unit, format, transfer, resize, sizing, reconciliation, and
Final-handoff requirements.

Neither candidate is cleared by prior settled material. Round 21 distinguished normal no-shadow
planning from creation failure in the result carrier, but did not add the missing §3.2 mapping.
Rounds 23 and 30 reached PASS on their supplied candidate sets; they did not define the newly
identified secondary filter-restoration failure. No candidate was dropped on independent
re-derivation, and there were no Gate drops.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both admitted defects are localized corrections; neither requires rebuilding the Phase 5
architecture, so `FAIL` is not warranted. The supplied trend summary is empty. Direct prior-review
comparison shows Round 30 was a literal PASS, while this round identifies two previously
unsettled defects; convergence is therefore interrupted until the corrections are applied and
freshly verified.

The next required action is a scoped fix-up resolving candidates 001 and 002 and appending their
resolutions to this review. Candidate-001 changes the binding §5 cross-phase interface, so the
`cross-phase-interfaces` change trigger applies and Phase 5 owes a fresh verification round before
it can close.

## Resolutions

### candidate-001 — resolved

Re-derived the secondary failure from the public result algebra and filter sequence. Section 4.10
now gives `ShadowMipmapResult` a result-level `Neutralized` outcome and adds
`MIPMAP_FILTER_RESTORE_FAILURE`. Successful restoration still produces per-buffer `Degraded` and
continues; restoration failure stops later generation, atomically aborts the open snapshot without
flips, invalidates shadow snapshots, installs neutral bindings, propagates the stable failure
diagnostic, and requires Phase 8 to stop without another lifecycle or neutralization call. The
branch is also bound in §5, the failure table, conformance matrix, and implementation checklist.
This changes the declared cross-phase interface and requires a fresh verify round.

### candidate-002 — resolved

Added the missing §3.2 row mapping conditional sfb creation/absence to §4.10, with the RC3
assignment and Phase 3's published shadow-depth requirement as provenance. The detailed lifecycle
was already correct and was not otherwise changed.

### Notes deferred

None. The adjudicator admitted no notes, and neither correction required a new upstream decision.
