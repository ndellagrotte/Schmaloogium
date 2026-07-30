# Phase 6 verification review — round 4

## 0. Method and reading order

This adjudication first re-derived both surviving candidates against the whole target,
`docs/phase6/v1/PHASE_6_DOC.md`; the governing Part I, Phase 6 assignment, document gate, and
mandatory template in `docs/design/v2.0-RC3/DESIGN.md`; the relevant contract ground truth in
`docs/research/v1/RESEARCH.md`; and the manifest-selected binding interfaces of Phases 1, 3, and
4. The listed supporting evidence does not define Phase 6's reset API or lifecycle, so it supplied
no contrary contract and was not used to infer one.

Only after settling those interpretations did adjudication read
`docs/phase6/reviews/PHASE_6_REVIEW_1.md`,
`docs/phase6/reviews/PHASE_6_REVIEW_2.md`, and
`docs/phase6/reviews/PHASE_6_REVIEW_3.md`, in that order. Their resolutions are present in the
target. There were no reading-order deviations, no network use, and no agent fan-out. Gate dropped
no candidates. Both surviving candidates were independently re-derived rather than accepted from
their incoming labels.

## 1. Findings

No candidate was admitted.

## 2. Checked and clean

The finder-reported clean areas survived re-derivation:

- The frame-begin-before-resize/clear rule is consistent across the architecture, conformance map,
  detailed design, exported interface, threading rules, and tests.
- The custom-refresh abort policy deterministically commits only the accepted prefix in the
  current activation and carries nothing forward.
- Custom bridge installation consistently defines null, timing, thread, first-install,
  same-instance, different-instance, late-call, non-close-reset, and close behavior.
- Phase 6 identifies the missing Phase 3 declaration metadata and Phase 4 bound-program
  capabilities as requested dependency changes rather than assuming them. Its use of the selected
  Phase 1, Phase 3, and Phase 4 binding regions remains honest.
- The examined Appendix D inventory, cadence, smoothing, sampler-map, notifier, and frame-ordering
  requirements remain mapped to concrete design and test coverage.

Candidate dispositions on independent derivation:

- **candidate-001 — dropped.** The target expressly says that the Java signatures are
  illustrative while their data contracts are binding
  (`docs/phase6/v1/PHASE_6_DOC.md:261-265`). The binding reset data is nevertheless closed
  semantically: §4.14 enumerates pack replacement, shaders-off, world epoch, GL-context loss, and
  close as the reset reasons (`docs/phase6/v1/PHASE_6_DOC.md:1120-1128`), while §4.13 and §5.1
  distinguish all non-close resets from close for bridge retention and release
  (`docs/phase6/v1/PHASE_6_DOC.md:1085-1091`,
  `docs/phase6/v1/PHASE_6_DOC.md:1142-1145`). Exact Java enum-constant spellings are therefore
  neither promised nor required for a consumer to implement the binding behavior. Adding an
  illustrative enum could improve presentation, but its absence is not a contractual correction.
- **candidate-002 — dropped.** The alleged missing close operation is already the documented
  `close` reset. The runtime shape exposes `reset(UniformResetReason)`
  (`docs/phase6/v1/PHASE_6_DOC.md:219-228`); the lifecycle prose calls the terminal event a
  “`close` reset” and retains borrowed services until it
  (`docs/phase6/v1/PHASE_6_DOC.md:261-265`); and §4.14 expressly includes close among reset reasons
  (`docs/phase6/v1/PHASE_6_DOC.md:1120-1128`). Section 5 transfers the sole runtime lifecycle and
  exports reset plus non-close-retention/close-release behavior to its consumers
  (`docs/phase6/v1/PHASE_6_DOC.md:1142-1145`). A separate `close()` or `AutoCloseable` shape is a
  permissible implementation choice, not a missing callable lifecycle contract.

Reading the prior reviews last did not change either disposition. Round 2 explicitly recognized
that §4.14 already supplied the reset-reason set and principal effects, and Round 3's resolved
bridge-lifecycle correction consistently uses the same non-close-reset versus close distinction.
Neither settled material requires exact enum identifiers or a second terminal method.

## 3. Verdict

# PASS
Counts: blocking=0; corrections=0; notes=0
Interface changed: no

Both surviving candidates are cleared on independent re-derivation. With no blocking findings and
no corrections, literal `PASS` is supported. This adjudication orders no target or interface
change, so the `cross-phase-interfaces` change trigger is not activated.

Trend: prior rounds reported 2, 2, and 3 corrections, all with resolutions. Round 4 reports zero.
The earlier non-decreasing warning is superseded by evidence of convergence in the current
review. No fix-up or further fresh verification round is owed for Phase 6; the verification loop
may close this phase.
