## 0. Method and reading order

I independently re-derived the candidate first from the whole Phase 7 target, the manifest-selected
RC3 governing sections (including the mandatory §G9 template and Phase 7 doc gate), RESEARCH.md,
and the binding §5 contracts of Phases 2–6. I then checked the supplied supporting evidence relevant
to the candidate and read Rounds 1–17 last, as required. There were no reading-list deviations, no
network use, no agent fan-out, no eliminated candidates, and no Gate drops. Forbidden transcript and
`*.txt` sources were not read.

## 1. Findings

### candidate-001 — The exposed-contract inventory omits the reload polling surface

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1493`
- **Claim:** The Round-17 reload polling addition is not represented consistently throughout §5's
  binding interface inventory.
- **Evidence:** `ShaderReloadController` publicly declares both `request(ReloadRequest)` and the
  distinct `status(ReloadToken)` operation, with `ReloadStatus` as a closed result algebra
  (`docs/phase7/v1/PHASE_7_DOC.md:1320`–`:1343`). Its binding semantics make a coalesced token
  pollable like an accepted token, return mutation-free `Unknown` for a token the controller did
  not issue, and keep issued tokens queryable through their current closed status
  (`docs/phase7/v1/PHASE_7_DOC.md:1393`–`:1396`). The document then labels the table as the “exact
  exposed contracts” (`docs/phase7/v1/PHASE_7_DOC.md:1440`–`:1444`), but the Phase 12 row names only
  `ShaderReloadController`, `ReloadRequest`, `ReloadIntent`, and `ReloadResult` and describes only
  request/commit behavior (`docs/phase7/v1/PHASE_7_DOC.md:1493`). This omits the named token/status
  data contracts and the separate consumer-facing polling operation from the exact inventory,
  contrary to §G9's requirement that §5 identify exposed named interfaces and data contracts
  (`docs/design/v2.0-RC3/DESIGN.md:811`–`:813`). Adjacent detailed prose makes the behavior
  recoverable but does not make the declared inventory internally complete.
- **Required correction:** Extend the existing Phase 12 inventory row to name `ReloadToken` and
  `ReloadStatus` and briefly state that accepted and coalesced issued tokens are polled through
  stable closed statuses, with mutation-free `Unknown` for unissued tokens and continued
  queryability of issued tokens. Keep the full variant semantics in the adjacent prose.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the correction changes the manifest-selected
  §5 exposed-contract inventory, so a fresh verify round is required before Phase 7 can close.

## 2. Checked and clean

The governing Phase 7 specification, mandatory template, doc gate, Round-17 addendum and edited
§3.3/§5 sites, reload token/status/coalescing occurrences, active-world publication boundary,
first-person overlay trace, Appendix E ledger, milestones, checklist, and the selected dependency
binding regions were checked. No additional candidate was available to admit, and independent
re-derivation did not clear candidate-001. In particular, the active-world lifecycle prose is
outside the illustrative Java fence; H-OVERLAY-01 alone supplies the required draw-buffers-none
program route; the polling prose is internally coherent and introduces neither token expiry nor a
separate supersession history; dependency APIs remain represented or explicitly gated as R7-1
through R7-9; and the conformance map covers the required hook needs, program families, reference
timeline rows, engine flags, and Appendix E dispositions.

Prior-review comparison found no settled disposition that supersedes this defect. Round 17 added
the polling surface and corrected its local semantics, but its resolution did not synchronize the
exact exposed-contract inventory.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The single finding is a localized interface-inventory correction, not a structural miss requiring
rebuild. The correction count decreases from three in Round 17 to one in Round 18, but literal
convergence has not been reached. The next required action is a scoped fix-up of candidate-001,
including the review resolution and Phase 7 addendum; because §5 changes, a fresh whole-document
and interface verification round is then required before Phase 7 can close. Literal PASS remains
required for closure.

## Resolutions

### candidate-001 — applied

Independently re-derived from the target and the governing §G9 interface requirement. The §5.1
table calls itself the exact exposed-contract inventory, but its Phase 12 row omitted the named
`ReloadToken` and `ReloadStatus` data contracts and did not summarize the distinct polling surface
already declared by `ShaderReloadController.status`. The row now names both contracts and states
that accepted and coalesced issued tokens are polled through stable closed statuses, remain
queryable, and yield mutation-free `Unknown` for unissued tokens. The adjacent prose retains the
full closed-variant, no-expiry, and no-separate-history semantics.

The target also gains compact §0.22. Because the edit changes §5, the manifest change trigger
fires: a fresh verification round is required before Phase 7 can close.

### Notes deferred

None; the adjudicator admitted no notes.
