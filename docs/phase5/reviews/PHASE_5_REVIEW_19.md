# Phase 5 Verification Review — Round 19

## 0. Method and reading order

I independently re-derived both surviving candidates before consulting prior reviews. I read the
whole Phase 5 target where necessary, the RC3 governing Phase 5 specification and document gate,
the mandatory template, and the manifest-selected binding regions of Phases 1, 3, and 4. I checked
the relevant detailed lifecycle, public API, cross-phase interface, failure, testability, and
handoff passages against those contracts. The supplied supporting evidence was not needed to
decide either candidate: both concern completeness and testability of interfaces defined by the
target itself.

Only after settling those interpretations did I read Phase 5 reviews 1 through 18, in order, to
check for prior disposition and settled material. I did not use the network, forbidden sources,
or prior-session transcripts. There was no agent fan-out or delegation. Candidate-003 had already
been eliminated before adjudication. The Gate reported no drops. There were no deviations from
the resolved reading contract.

## 1. Findings

### candidate-001 — Pass acquisition/completion rejection contracts lack corresponding tests

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:568`–`:577` and
`docs/phase5/v1/PHASE_5_DOC.md:1644`–`:1657`

**Claim.** The newly closed pass acquisition and completion outcomes are observable state-machine
contracts, but the test plan does not require exercising their rejection matrix or proving the
promised mutation-free behavior.

**Evidence.** The lifecycle specifies exact acquisition rejections, distinguishes completion
generation/epoch/frame mismatches from foreign, duplicate, or consumed snapshots, excludes
`WRONG_FRAME_ID` from acquisition, and promises that every rejection performs no GL and preserves
the open token and flip state (`docs/phase5/v1/PHASE_5_DOC.md:568`–`:577`). The pure
state-machine plan tests arbitrary writes and **successful** completion, commit, and abort, but
contains no table-driven acquisition/completion rejection coverage
(`docs/phase5/v1/PHASE_5_DOC.md:1644`–`:1657`). The recorded-GL plan likewise covers other
rejections without covering these pass-result paths
(`docs/phase5/v1/PHASE_5_DOC.md:1669`–`:1711`). This omission matters because RC3 requires
`engine.buffers` policy to be pure and testable as a state machine
(`docs/design/v2.0-RC3/DESIGN.md:1671`–`:1673`).

**Required correction.** Extend §8 with table-driven tests for every applicable acquisition and
completion rejection, including foreign, duplicate, and already-consumed snapshots. Assert the
exact result reason, zero GL calls, unchanged open-frame/open-pass state, unchanged flip state,
and that acquisition never returns `WRONG_FRAME_ID`.

**Severity:** correction

**touches interface/change-trigger region: no**

### candidate-002 — Shadow pass operations lack closed, implementable result contracts

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:1256`–`:1266` and
`docs/phase5/v1/PHASE_5_DOC.md:1493`

**Claim.** Phase 5 exposes a checked shadow lifecycle to Phase 8 but does not define closed outcomes
for its operations, leaving Phase 8 unable to distinguish acceptance, validation rejection,
backend failure, and token consumption without inventing Phase-5-owned policy.

**Evidence.** `ShadowEstateView` names `ShadowBindResult` and `ShadowDepthCopyResult`, but supplies
no variants for them; `beginPass` returns an unconditional snapshot, while `clearPlan`,
`completePass`, and `abortPass` have no stated return contracts
(`docs/phase5/v1/PHASE_5_DOC.md:1256`–`:1266`). The same passage nevertheless promises rejection
of stale, foreign, duplicate, or closed snapshots before GL, so those outcomes are load-bearing
rather than incidental. Binding §5 publishes generation/epoch/token-checked bind, clear,
split-copy, complete, and abort operations to Phase 8 without making their observable outcome
model any more precise (`docs/phase5/v1/PHASE_5_DOC.md:1493`). By contrast, the analogous main
estate publishes closed success, protocol-rejection, and backend-failure results
(`docs/phase5/v1/PHASE_5_DOC.md:504`–`:549`). RC3 assigns shadow structure, lifecycle, and real
flip semantics to Phase 5 while leaving pass wiring to Phase 8
(`docs/design/v2.0-RC3/DESIGN.md:1630`–`:1637`), and the target's handoff confirms Phase 8 merely
uses this view and owns camera, traversal, pass ordering, and the copy moment
(`docs/phase5/v1/PHASE_5_DOC.md:1831`–`:1834`).

**Required correction.** Define a closed return contract for each fallible shadow acquisition,
bind, clear, split-copy, completion, and abort operation, including validation rejection,
backend failure where applicable, token closure/consumption, mutation guarantees, and Phase 8's
branching duties. If an operation is intentionally infallible after an earlier checked step,
state and justify that invariant instead of adding an artificial failure variant. Summarize the
completed outcome contract in §5.1.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported clean areas remain clean on re-derivation. The main lifecycle descriptions
consistently exclude caller-supplied frame identity from acquisition, apply the same completion
protocol to SCREEN, preserve flip and protocol state on rejection, and direct Phase 7 to abort
after completion rejection. Phase 3 and Phase 4 consumption matches their binding regions, and
Phase 5 records rather than silently assumes the three requested Phase 1 additions. The
conformance map covers the in-scope App B.1, B.2, B.3, and B.4 surface, including the unit-11
ruling, formats and transfers, flip/clear behavior, depth copying, shadow structure, sizing, and
fallbacks. All thirteen mandatory sections are present and there are no assigned open questions.

Candidate-003, the alleged undefined `STALE_ESTATE` result, was eliminated before adjudication and
is not admitted. No other candidate was available to turn into a finding.

Prior settled material does not clear either surviving candidate. Round 18's resolution created
the main pass result contracts but did not add tests for their new rejection surface. Round 1's
shadow resolution says it added operation inputs/results and completion/abort outcomes, but the
current target only names two result types without variants and leaves four operation return
contracts unspecified. The present finding therefore identifies an incomplete settled correction,
not a demand that Phase 8 own Phase 5's lifecycle.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both admitted defects are localized contract/test-plan corrections and do not require rebuilding
the Phase 5 architecture, so `FAIL` is not warranted. The correction count rises from one in Round
18 to two in Round 19. That is a convergence regression in count, and candidate-002 also exposes
that Round 1's shadow-interface resolution was incomplete; the result must not be softened to
`PASS`.

The next required action is a scoped fix-up resolving candidate-001 and candidate-002 and appending
their resolutions to this review. Because candidate-002 requires changing the binding §5
cross-phase interface region, the interface change trigger applies: Phase 5 owes a fresh
verification round before it can close.

## Resolutions

### candidate-001 — resolved

Added table-driven pure-state tests covering every applicable main-pass acquisition and completion
rejection, including foreign, duplicate, and consumed snapshots. The oracle now requires the exact
reason, zero GL calls, unchanged open frame/pass and flip state, and an explicit proof that
acquisition cannot return `WRONG_FRAME_ID`. This completes the test surface of the existing closed
result contracts without changing their semantics.

### candidate-002 — resolved

Re-derived the shadow lifecycle from Phase 5's ownership of sfb structure, lifecycle, and real flip
semantics and Phase 8's ownership of scheduling. §4.10 now defines closed acquisition, operation,
completion, and abort results plus exact protocol-rejection reasons. Validation rejection is
pre-GL and mutation-free; only bind, clear, and split-copy can report backend failure, which keeps
the token open and flip unchanged so Phase 8 can perform the already-required abort. Completion
alone consumes the token and applies flip; abort consumes it without flip and marks full clear.
Phase 8's required branch for every outcome is explicit. §5.1 publishes the same contract, and §8
adds the corresponding state-machine and recorded-backend oracle.

This correction changes §5.1, so the manifest's cross-phase-interface trigger fires and Phase 5
requires a fresh verification round.

### Notes deferred

None.
