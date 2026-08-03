## 0. Method and reading order

I independently re-derived all three Gate-surviving candidates from the whole Phase 7 target, the
manifest-selected governing design sections, authoritative RESEARCH material, the binding §5
regions of Phases 2–6, and the cited evidence. I searched the target for equivalent fence,
reload-token/status, and hook-need mapping coverage before settling the candidates. Only after
those judgments did I read `docs/phase7/reviews/PHASE_7_REVIEW_1.md` through
`docs/phase7/reviews/PHASE_7_REVIEW_16.md`, in order and last. No prior review settled these newly
exposed defects.

There were no reading-order deviations, no network use, no forbidden-source use, and no agent
fan-out. This was the canonical engine's already-dispatched atomic adjudication role, so the
supplied `verify-loop` skill required completing only this role without invoking the loop or
delegating. No candidates were eliminated before adjudication, and Gate dropped none.

## 1. Findings

### candidate-001 — Round-16 contract prose is inside the Java interface fence

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1281`–`:1296`
- **Claim:** The binding §5 presentation does not distinguish the newly added normative
  active-world lifecycle prose from illustrative Java declarations.
- **Evidence:** The Java fence opens before `FrameHookSink` at
  `docs/phase7/v1/PHASE_7_DOC.md:1091` and remains open when the prose beginning
  `` `ActiveWorldIdentityPublication` is one atomically replaced, internal composition object ``
  appears after `ResizeLifecycleSink` (`docs/phase7/v1/PHASE_7_DOC.md:1277`–`:1298`). The fence
  closes only at `docs/phase7/v1/PHASE_7_DOC.md:1383`. Markdown therefore renders the normative
  lifecycle paragraph as malformed Java-fence content rather than contract prose. Equivalent
  lifecycle semantics in §4.8 do not repair the binding §5 presentation required by the mandatory
  cross-phase-interface template (`docs/design/v2.0-RC3/DESIGN.md:809`–`:813`).
- **Required correction:** Close the Java fence after `ResizeLifecycleSink`, place the
  `ActiveWorldIdentityPublication` paragraph in normal Markdown, and reopen a Java fence before
  `FullscreenPassExecutor` (or relocate the paragraph outside the existing fence).
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the malformed insertion is inside the
  manifest-selected §5 interface region.

### candidate-002 — `ReloadStatus` has no behavior for an unknown token

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1313`–`:1335`
- **Claim:** The exposed Phase 12 polling contract is not exhaustive for the reachable case where
  `status` receives a token that this controller did not issue.
- **Evidence:** `ReloadToken` is a publicly constructible value record
  (`docs/phase7/v1/PHASE_7_DOC.md:1235`), and `ShaderReloadController.status` accepts it without a
  stated issued-token precondition (`docs/phase7/v1/PHASE_7_DOC.md:1313`–`:1316`). Its closed
  `ReloadStatus` algebra contains only `Queued`, `Building`, `Active`, `Off`, and `Failed`
  (`docs/phase7/v1/PHASE_7_DOC.md:1328`–`:1335`), none of which truthfully describes an unknown
  token. The controller is promised as a closed downstream contract to Phase 12
  (`docs/phase7/v1/PHASE_7_DOC.md:1478`–`:1480`), so implementations and consumers would have to
  invent observable behavior. The candidate's reliance on the generic negative-value validation
  sentence is overstated: that sentence names negative counts, versions, and sequences, not token
  values. Nor does the evidence require expiration or finite history retention.
- **Required correction:** Specify exhaustive `status(ReloadToken)` behavior for tokens not issued
  by the controller, either through a closed unknown/invalid outcome or an explicit typed failure
  contract. Also state how a token returned by `Coalesced` is polled. Define expiration or
  supersession history only if the design permits tokens to cease being queryable.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the missing behavior belongs to the exposed
  §5 `ShaderReloadController` contract.

### candidate-003 — Hook need 4 does not trace first-person overlay

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:426`
- **Claim:** The §3.3 row claiming all per-phase switches does not provide zero-unmapped coverage
  of authoritative hook need 4.
- **Evidence:** RESEARCH hook need 4 enumerates first-person overlay separately from hand
  solid/translucent (`docs/research/v1/RESEARCH.md:807`–`:811`). The target's need-4 row ends with
  `H-HAND` and omits `H-OVERLAY` (`docs/phase7/v1/PHASE_7_DOC.md:426`), while the detailed catalog
  already supplies `H-OVERLAY-01` as the draw-buffers-none route around first-person overlays
  (`docs/phase7/v1/PHASE_7_DOC.md:948`–`:952`). The governing doc gate requires every hook need to
  be traced (`docs/design/v2.0-RC3/DESIGN.md:1941`–`:1942`); detailed substantive coverage does not
  cure the missing conformance-map trace.
- **Required correction:** Add `H-OVERLAY-01` to the §3.3 need-4 disposition and preserve its
  R7-3-gated status. Do not add H-OVERLAY-02/03 merely for this trace; those are separate
  cancellation behaviors rather than the required program route.
- **Severity:** correction
- **touches interface/change-trigger region: no** — the correction is confined to the §3.3
  conformance map.

## 2. Checked and clean

The finder-reported clean areas survived re-derivation. Round-16 world-identity semantics are
otherwise consistent between §4.8 and §5: one publication writer, one frame-begin snapshot, and
authenticated resize handling with mutation-free rejection. Consumed Phase 3–6 contracts,
frame/scope result algebras, shadow handoff, capture lifetime, resize authentication, publication
ordering, ownership boundaries, the remaining RESEARCH mappings, engine flags, seven-row timeline,
and Appendix E ledger yielded no other candidate-backed defect.

No supplied candidate was cleared. Candidate-002 survives only in narrowed form: neither the cited
generic validation rule nor the evidence establishes mandatory expiry, retention, or distinct
supersession history, but the reachable unknown-token case itself remains undefined. Prior reviews
contain no disposition that clears any of the three findings.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

All three findings are bounded corrections; none requires a structural rebuild. Two affect the
manifest-selected interface region, so Phase 7 cannot close without another fresh whole-document
and interface verification round. The recent correction trend is 2, 1, 3 across Rounds 15–17;
corrections have increased and literal convergence has not been reached. The next required action
is a scoped fix-up of all three findings, including a `## Resolutions` record and Phase 7 addendum,
followed by fresh verification. Literal PASS remains required for closure.

## Resolutions

### candidate-001 — applied

Closed the illustrative Java fence after `ResizeLifecycleSink` and reopened it before
`FullscreenPassExecutor`. The active-world lifecycle rules now render as binding Markdown prose
without changing their semantics.

### candidate-002 — applied

Added the closed `ReloadStatus.Unknown` outcome and specified mutation-free handling for tokens
not issued by the controller. Also specified that `Coalesced` returns an already-issued token and
is polled through the same status operation as `Accepted`. No expiry or supersession-history rule
was added because neither the governing sources nor the correction requires one.

### candidate-003 — applied

Added the existing R7-3-gated `H-OVERLAY-01` route to hook need 4 in §3.3. The two overlay
cancellation rows remain outside that program-routing trace.

### Notes deferred

None. The adjudicator admitted no notes.
