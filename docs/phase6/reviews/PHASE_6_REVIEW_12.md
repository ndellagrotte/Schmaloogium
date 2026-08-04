# Phase 6 — Uniform & sampler system — Verification Review 12

## 0. Method and reading order

I independently re-derived both surviving candidates before consulting prior Phase 6 reviews. I
read the complete target surface selected by the manifest, with focused checks of the public
runtime shape, lifecycle/reset rules, maintenance addenda, and §5 interface region. I checked the
governing v3 Part I, Phase 6 assignment, doc gate, and mandatory template; the binding §5 regions
of Phases 1, 3, and 4; and the relevant contract/evidence inventory identified by the resolved
target contract. The manifest's explicit verification override selected
`docs/design/v3/DESIGN.md`; the target's historical §0 citations remain anchored to RC3, so I used
v3 as the governing adjudication authority without treating the override as document adoption.

Only after reaching an independent disposition did I read
`docs/phase6/reviews/PHASE_6_REVIEW_1.md` through
`docs/phase6/reviews/PHASE_6_REVIEW_11.md`, in round order, to test the candidates against settled
material. No network access was used. No subagents, agent fan-out, `$verify-loop`,
`scripts/verify`, or nested Codex execution was used. No forbidden source was read. The Gate
reported no dropped candidate, and there were no pre-adjudication eliminations.

## 1. Findings

### candidate-001 — The sole current §G1.3 status marker remains at the Round 6 state

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:138-141`
- **Claim:** The document's expressly current verification-status marker is inconsistent with its
  maintained architecture through §0.11.
- **Evidence:** Lines 138–141 still describe Review 5, Round 6, and §0.8 as the current state. The
  later addenda explicitly record Review-9 through Review-11 maintenance at
  `docs/phase6/v1/PHASE_6_DOC.md:153-165`. Independently, the header says “maintained architecture
  through §0.11” (`docs/phase6/v1/PHASE_6_DOC.md:8`) and the footer says §§0.3–0.11 record the
  governed maintenance (`docs/phase6/v1/PHASE_6_DOC.md:1639-1641`). No later current-status marker
  supersedes lines 138–141. Prior Round 6 settled the then-stale marker, while Round 11 settled
  only the three compact extent markers; neither cleared the newly exposed contradiction in the
  sole paragraph labelled current.
- **Required correction:** Replace the stale current-status paragraph with the actual state
  through §0.11 and state that the current maintained bytes await a fresh literal-PASS review.
  Leave §5 unchanged for this correction.
- **Severity:** correction
- **touches interface/change-trigger region: no**

### candidate-002 — `UniformResetReason` lacks a closed implementable domain

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:268-279`,
  `docs/phase6/v1/PHASE_6_DOC.md:1194-1208`, and
  `docs/phase6/v1/PHASE_6_DOC.md:1230-1232`
- **Claim:** Consumers cannot implement or call the public lifecycle operations without inventing
  the values of their `UniformResetReason` parameter.
- **Evidence:** `UniformRuntime.adoptRegistryGeneration` and `UniformRuntime.reset` both expose
  `UniformResetReason`, but the public shape declares neither that type nor exact variants
  (`docs/phase6/v1/PHASE_6_DOC.md:268-279`). Section 4.14 supplies five semantic prose reasons and
  partitions generation-changing reasons from the two reasons accepted by direct `reset`
  (`docs/phase6/v1/PHASE_6_DOC.md:1194-1208`), but whole-target search finds the identifier only in
  the two parameters. Section 5 exports adoption “plus semantic reset reason” and reset to Phases
  7, 8, 9, 11, and 13 without closing the argument domain
  (`docs/phase6/v1/PHASE_6_DOC.md:1230-1232`). Neighboring public results are declared as exact
  closed types. Prior Round 2 expressly left the already-present semantic set alone while closing
  different result schemas, and Round 9 preserved that semantic set while adding generation
  adoption; neither supplied or settled a concrete public reason domain. The semantics therefore
  exist, but the exposed API remains non-implementable without consumer invention.
- **Required correction:** Declare a closed public `UniformResetReason` domain with exact variant
  identifiers, specify which variants are accepted by `adoptRegistryGeneration` and by `reset`,
  and publish that closed domain in §5.1 without changing the already documented effects.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

## 2. Checked and clean

The conformance map, Appendix D inventory, provider/cadence/milestone mappings, sampler maps,
barrier participation, smoothing formulas, center-depth decision, notifier audit, temporal
snapshots, fixed-function capture, custom bridge, frame-begin ordering, and the Phase 1/3/4
binding consumptions were checked without another admissible candidate. The finder-reported clean
areas are sustained: all compact maintenance extent markers other than the expressly current
status paragraph agree on §0.11, and the other examined cross-phase interfaces are sufficiently
specified.

Neither surviving candidate was refuted or cleared on re-derivation. Prior-review comparison did
not show either defect as settled: candidate-001 is a later recurrence outside the compact markers
fixed in Round 11, and candidate-002 concerns the still-undeclared public type rather than the
previously settled lifecycle meanings or generation handshake. No candidate was dropped.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both findings are bounded fix-ups rather than structural rebuilds. Candidate-002 requires a change
inside the manifest-declared `cross-phase-interfaces` region; therefore the interface trigger fires
and Phase 6 cannot close on this review. Apply both corrections in a governed fix-up, record the
next addendum and resolutions, then run a fresh verification round. The prior correction trend is
`1 -> 3 -> 1`, and this round rises to 2; corrections are not converging strictly, so the next
round should specifically recheck maintenance-marker consistency and the complete closed
`UniformResetReason` declaration before considering literal PASS.

## Resolutions

### candidate-001 — resolved

Replaced the sole current §G1.3 status paragraph with the state after Review 12 and synchronized
the compact document-extent markers through §0.12. The paragraph now says plainly that these
maintained bytes await a fresh literal-PASS review. Section 5 was not changed for this correction.

### candidate-002 — resolved

Declared `UniformResetReason` as a closed public enum with exact variants `PACK_REPLACEMENT`,
`SHADERS_OFF`, `GL_CONTEXT_LOSS`, `WORLD_EPOCH`, and `CLOSE`. Section 4.14 now makes the operation
partition exact: generation adoption accepts only the first three, direct reset only the last two,
and a reason outside an operation's accepted subset fails fast without mutation. Section 5.1 now
publishes both the closed domain and partition to consumers without changing the established reset
effects. This intentionally changes the manifest-declared cross-phase interface region, so a fresh
verify round is required before Phase 6 can close.

### Notes deferred

None.
