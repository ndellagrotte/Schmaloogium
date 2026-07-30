# Phase 6 verification review — round 3

## 0. Method and reading order

This adjudication first re-derived every surviving candidate against the whole target,
`docs/phase6/v1/PHASE_6_DOC.md`; the governing Part I, Phase 6 assignment, document gate, and
mandatory template in `docs/design/v2.0-RC3/DESIGN.md`; the contract ground truth in
`docs/research/v1/RESEARCH.md`; the manifest-selected binding interfaces of Phases 1, 3, and 4;
and the listed supporting evidence where relevant. Pintonium material was treated as evidence,
never as contract.

Only after settling those interpretations did adjudication read
`docs/phase6/reviews/PHASE_6_REVIEW_1.md` and
`docs/phase6/reviews/PHASE_6_REVIEW_2.md`, in that order. Their four corrections have resolutions
present in the target and do not settle the three current candidates. There were no reading-order
deviations, no network use, and no agent fan-out. Gate dropped no candidates. All three surviving
candidates were independently re-derived rather than accepted from their incoming labels.

## 1. Findings

### candidate-001 — `Aborted` leaves the accepted custom-command prefix indeterminate

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:1072-1075`,
  `docs/phase6/v1/PHASE_6_DOC.md:1101-1105`, and
  `docs/phase6/v1/PHASE_6_DOC.md:1138-1139`
- **Claim:** The Phase 6-owned custom refresh boundary does not determine what happens to commands
  accepted before Phase 11 returns `Aborted`.
- **Evidence:** `CustomRefreshResult.Aborted` carries only a diagnostic ID
  (`docs/phase6/v1/PHASE_6_DOC.md:1072-1075`). The accompanying rule says accepted commands
  “remain eligible for upload,” but does not say whether Phase 6 uploads that prefix during the
  current activation, discards it, or retains it for a later activation
  (`docs/phase6/v1/PHASE_6_DOC.md:1101-1105`). Phase 11 owns the decision to abort remaining
  evaluation, but Phase 6 owns accepted-command ordering and upload isolation, so that ownership
  split does not determine the observable disposition. Section 5 exports both the closed refresh
  result and accepted/rejected upload sink as the Phase 6-to-Phase 11 contract
  (`docs/phase6/v1/PHASE_6_DOC.md:1138-1139`). The test table covers custom ordering but has no
  accepted-prefix-then-abort case (`docs/phase6/v1/PHASE_6_DOC.md:1334-1335`).
- **Required correction:** Specify in §§4.13 and 5.1 whether an aborted refresh commits the
  accepted prefix during the current activation, discards it, or carries it over. Explicitly
  specify carry-over behavior and add an accepted-prefix-then-abort test. Any deterministic
  disposition is acceptable; the contract need not mandate the candidate's example policy.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

### candidate-002 — Custom bridge installation has no closed lifecycle semantics

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:214-223`,
  `docs/phase6/v1/PHASE_6_DOC.md:1079-1085`, and
  `docs/phase6/v1/PHASE_6_DOC.md:1469-1471`
- **Claim:** Consumers of `UniformRuntime.installCustomUniformBridge` cannot determine the
  permitted installation states or outcomes without inventing Phase 6 lifecycle policy.
- **Evidence:** The public runtime exposes a void installation operation with no declared
  preconditions, cardinality, or result (`docs/phase6/v1/PHASE_6_DOC.md:214-223`). The detailed
  design specifies the default bridge and successful installed behavior, but not null handling,
  installation thread or timing, same-instance repetition, different-instance replacement, or
  late installation (`docs/phase6/v1/PHASE_6_DOC.md:1079-1085`). Reset rules do not state whether
  bridge installation survives reset, returns to the default, or releases the installed reference.
  The Phase 11 handoff requires installation of one bridge
  (`docs/phase6/v1/PHASE_6_DOC.md:1469-1471`), while §5 publishes custom bridge installation on
  `UniformRuntime` to Phase 11 and the composition owner
  (`docs/phase6/v1/PHASE_6_DOC.md:1131-1138`). Thus these are reachable cross-phase lifecycle
  semantics, not an API-style preference or Phase 11-owned expression policy.
- **Required correction:** Close the installation state machine in §4.13 and publish it in §5:
  define null handling, thread and permitted lifecycle timing, first installation, same-instance
  repetition, different-instance replacement or rejection, and reset/close retention or release.
  A closed result type is one valid representation, but fully specified idempotent or fail-fast
  void semantics are also sufficient.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

### candidate-003 — Section 3 omits the mandatory before-resize/clear ordering requirement

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:305-335`
- **Claim:** The contract conformance map does not map the governing Phase 6 requirement that
  frame-begin sampling complete before any buffer resize or clear.
- **Evidence:** The governing specification makes this a distinct mandatory Phase 6 requirement
  and requires it to be exported in §5
  (`docs/design/v2.0-RC3/DESIGN.md:1721-1725`). The mandatory template requires Section 3 to map
  every in-scope contract item with zero unmapped rows
  (`docs/design/v2.0-RC3/DESIGN.md:804-808`). The nearest Section 3 row maps only “World state
  sampled at frame begin” to §4.6 and cites the weaker frame-start research statement
  (`docs/phase6/v1/PHASE_6_DOC.md:317`); frame-begin timing alone does not establish completion
  before resize or clear. The actual design and binding interface already specify the correct
  relative ordering (`docs/phase6/v1/PHASE_6_DOC.md:711-718`,
  `docs/phase6/v1/PHASE_6_DOC.md:1133-1134`), so this is a traceability defect rather than an
  architectural or interface omission.
- **Required correction:** Add a Section 3 row for the frame-begin-before-resize/clear constraint,
  map it to §§4.6 and 5.1, and cite
  `docs/design/v2.0-RC3/DESIGN.md:1721-1725`. No §5 change is required for this finding.
- **Severity:** correction
- **touches interface/change-trigger region: no**

## 2. Checked and clean

The finder-reported clean areas survived re-derivation:

- The corrected construction result, frame-begin result, provider/event schemas, and held-light
  mapping are internally consistent. Round 1 and Round 2's resolved findings are not reopened.
- The custom boundary otherwise closes exact-name typed/absent lookup, immutable command variants,
  validation and first-wins duplicate handling, definition order, diagnostic attribution,
  per-command GL-error isolation, and the ownership split between Phase 6 upload policy and Phase
  11 expression policy.
- Phase 6 accurately consumes the selected Phase 1 upload/readback/error contracts. It reports
  missing Phase 3 declaration metadata and Phase 4 activity/location capability as requested
  dependency changes rather than silently assuming them.
- The Appendix D inventory and cadence mapping, smoothing formulas, fixed sampler maps including
  `depthtex1` at unit 11, center-depth decision, notifier audit, provider seam, upload isolation,
  previous-frame snapshots, fixed-function capture, and Phase 4 barrier trace have substantive
  design coverage.
- Frame-begin sampling before resize/clear is correctly implemented in §4.6 and exported in §5.1;
  candidate-003 is confined to the mandatory Section 3 mapping.

No Gate-surviving candidate was refuted, cleared, or dropped on independent derivation.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

All three admitted defects are bounded corrections rather than structural failures requiring a
rebuild, so `FAIL` is not warranted. Literal `PASS` is unavailable while three corrections remain.

The next required action is a fix-up resolving candidate-001, candidate-002, and candidate-003 and
appending the resolution record to this review. Candidate-001 and candidate-002 require updates to
the binding cross-phase interface; candidate-003 requires only a conformance-map row. Because the
interface/change-trigger region must change, a fresh verification round is required before Phase 6
can close.

Trend: Rounds 1 and 2 each found two interface corrections; Round 3 finds two further interface
corrections plus one traceability correction. The correction count has increased from two to three,
so literal convergence has not yet been reached. The findings remain localized and do not justify
escalation to `FAIL`, but closure cannot be claimed.

## Resolutions

### candidate-001 — resolved

Re-derived ownership placed accepted-command disposition in Phase 6: Phase 11 decides when
evaluation aborts, while Phase 6 owns upload order and isolation. Sections 4.13 and 5.1 now require
an aborted refresh to commit its accepted prefix, in order, during the current activation; rejected
commands and the unevaluated suffix do not upload, and nothing carries to another activation.
`CustomAbortPrefixTest` covers accepted-prefix-then-abort behavior.

### candidate-002 — resolved

Sections 4.13 and 5.1 now close bridge installation without adding a result type: null is rejected;
installation is composition-thread-only and must precede the first frame or activation; the first
non-default bridge wins; the same instance is idempotent; different-instance and late calls fail
without mutation. Non-close resets retain the bridge, while close releases it and restores the
default. `CustomBridgeLifecycleTest` covers every transition.

### candidate-003 — resolved

Section 3 now maps the distinct frame-begin-before-resize/clear requirement to §§4.6 and 5.1 and
cites `docs/design/v2.0-RC3/DESIGN.md:1721-1725`. The existing ordering design and interface were
already correct, so no additional architectural change was made for this finding.

### Notes deferred

None. All three admitted corrections were applied; no correction required refusal or a new
upstream design decision.
