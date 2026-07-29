# Phase 5 Verification Review — Round 20

## 0. Method and reading order

I independently re-derived both Gate-surviving candidates before consulting prior reviews. I read
the Phase 5 target's main and shadow lifecycle declarations, public API, binding §5 interface,
testability plan, and Phase 8 handoff; the RC3 mandatory template, Phase 5 specification, and
document gate; the relevant RESEARCH contract; and the manifest-selected binding regions of
Phases 1, 3, and 4. Supporting material was not needed to decide either candidate because both
concern the completeness of Phase 5's own closed shadow protocol.

Only after settling both interpretations did I read Phase 5 reviews 1 through 19, in order, to
check the candidates against prior dispositions and settled resolutions. I used no network
access, forbidden source, or prior-session transcript. In particular, I did not open the supplied
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt`, because the resolved forbidden-source
rule bars `*.txt` and it was unnecessary here. There was no agent fan-out or delegation. In
accordance with the dispatched atomic-role instruction, I did not invoke the verify-loop skill,
run `scripts/verify`, or start another Codex session. There were no deviations from the resolved
reading contract, no candidates eliminated before adjudication, and no Gate drops.

## 1. Findings

### candidate-001 — Shadow acquisition has no exact rejection for an already-open pass

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:1265`–`:1283` and
`docs/phase5/v1/PHASE_5_DOC.md:1517`

**Claim.** The shadow lifecycle permits only one open pass and promises an exact, closed
validation rejection before GL, but `ShadowProtocolRejection` has no value for calling
`beginPass` while that sole pass is already open.

**Evidence.** The closed vocabulary is `STALE_GENERATION`,
`STALE_DEPTH_ATTACHMENT_EPOCH`, `NO_OPEN_PASS`, `WRONG_FRAME_ID`, `FOREIGN_SNAPSHOT`, and
`CLOSED_SNAPSHOT` (`docs/phase5/v1/PHASE_5_DOC.md:1265`–`:1268`). The acquisition contract says
`beginPass` acquires the *"sole open"* snapshot or returns `Rejected`
(`docs/phase5/v1/PHASE_5_DOC.md:1275`–`:1277`), and all validation promises the exact
`ShadowProtocolRejection` before GL and without token, full-clear, or flip mutation
(`docs/phase5/v1/PHASE_5_DOC.md:1282`–`:1283`). `NO_OPEN_PASS` describes the opposite lifecycle
state and cannot exactly identify a second acquisition. The adjacent main-frame vocabulary
separately models `FRAME_ALREADY_OPEN` and `OPEN_PASS_SNAPSHOT`
(`docs/phase5/v1/PHASE_5_DOC.md:520`–`:529`), confirming that already-open states are distinct
observable protocol outcomes rather than aliases for absence.

Binding §5 exports `ShadowProtocolRejection` and promises exact, pre-GL, mutation-free validation
to Phase 8 (`docs/phase5/v1/PHASE_5_DOC.md:1517`). The state-machine plan also requires every
shadow acquisition result and exact validation rejection to be table-tested
(`docs/phase5/v1/PHASE_5_DOC.md:1685`–`:1687`), which is impossible for this unnamed state.

**Required correction.** Add a distinct shadow rejection such as `PASS_ALREADY_OPEN`; specify
that a second `beginPass` returns it before GL and without mutation; reproduce the exact behavior
in §5.1; and include it in the shadow acquisition result table.

**Severity:** correction

**touches interface/change-trigger region: yes**

### candidate-002 — Shadow result families and view lack implementable carrier declarations

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:1261`–`:1285` and
`docs/phase5/v1/PHASE_5_DOC.md:1517`

**Claim.** Phase 5 publishes `ShadowEstateView`, `ShadowPassSnapshot`, and four closed shadow
result families to Phase 8, but supplies only variant names and prose. It does not declare the
view's exact methods or the concrete carriers needed to convey the acquired snapshot, rejection
reason, or backend failure. Phase 8 would have to invent Phase-5-owned public types.

**Evidence.** The target names `ShadowEstateResult = Available(ShadowEstateView) |
ShadowEstateUnavailable(reason,generation)` and lists result-family permits clauses, but none of
the listed `Acquired`, `Rejected`, `Applied`, `BackendFailed`, `Completed`, or `Aborted` variants
has a concrete declaration or payload (`docs/phase5/v1/PHASE_5_DOC.md:1261`–`:1272`). The
following pseudocode describes method names and prose semantics, including that acquisition
returns a snapshot, rejection returns the exact reason, and backend failure carries
`BufferFailure`, but it still does not define the `ShadowEstateView` signatures, the
`ShadowPassSnapshot` record, or the result carriers
(`docs/phase5/v1/PHASE_5_DOC.md:1275`–`:1285`).

This precision is not optional downstream wiring. RC3 assigns shadow FBO structure, lifecycle,
and real state-machine-tested flip semantics to Phase 5 while assigning pass wiring to Phase 8
(`docs/design/v2.0-RC3/DESIGN.md:1630`–`:1637`), and the handoff says Phase 8 must *use*
`ShadowEstateView` (`docs/phase5/v1/PHASE_5_DOC.md:1864`). The adjacent main lifecycle defines
concrete result records such as `Begun`, `Rejected`, `BackendFailed`, `Acquired`, and `Completed`
with their exact payloads (`docs/phase5/v1/PHASE_5_DOC.md:531`–`:555`). Binding §5 nevertheless
exports the shadow type names and behavioral categories without closing their carrier shapes
(`docs/phase5/v1/PHASE_5_DOC.md:1517`).

**Required correction.** Declare the exact `ShadowEstateResult` and `ShadowEstateView` method
signatures, the complete `ShadowPassSnapshot` shape, and concrete variants for every shadow result
family. At minimum, encode the acquired snapshot, exact rejection reason, and operation backend
failure; add other payloads only where the documented semantics require them. Mirror the complete
normative carrier contract in §5.1.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported clean areas remain clean on re-derivation. The revised shadow protocol is
otherwise consistent across §4.10, §5.1, and §8.1: validation rejection is pre-GL and
mutation-free; bind, clear, and copy backend failure leaves the token open and flip unchanged;
completion alone flips; and abort closes without flip and requires full clear.

Phase 3 and Phase 4 consumption matches their selected binding regions. Phase 5 records rather
than silently assumes the requested Phase 1 additions. Main-frame lifecycle, resize publication,
texture-overlay leasing, fixed-unit binding, SCREEN handoff, conformance mapping, format and
transfer rules, sizing, and the Appendix B.3 unit table were checked and are otherwise coherent.
All thirteen mandatory sections are present and there are no assigned open questions.

Prior settled material does not clear either candidate. Round 19's shadow resolution introduced
the current closed result-family names and exact-rejection promise, but did not define an
already-open acquisition reason or the concrete result carriers and view signatures. Round 1 had
already ordered an implementable `ShadowEstateView` with operation inputs/results; Round 19
recognized that the earlier resolution remained incomplete. Candidate-002 therefore identifies
an unresolved implementability gap in that settled requirement, while candidate-001 identifies a
newly exposed hole in Round 19's exact rejection vocabulary. No surviving candidate was dropped
on re-derivation, and there were no Gate drops.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both findings are localized cross-phase contract corrections and do not require rebuilding the
Phase 5 architecture, so `FAIL` is not warranted. The recent correction counts are
2 → 1 → 2 → 2 for Rounds 17–20: they are not strictly decreasing, and this round again exposes
incomplete closure of the shadow interface. Convergence has not been established and the verdict
must not be softened to `PASS`.

The next required action is a scoped fix-up resolving candidate-001 and candidate-002 and
appending resolutions to this review. Both corrections require changes to the binding §5
cross-phase interface region, so the `cross-phase-interfaces` change trigger applies: Phase 5
owes a fresh verification round before it can close.

## Resolutions

### candidate-001 — applied

Added `PASS_ALREADY_OPEN` to the closed shadow rejection vocabulary and made a second
`beginPass` return `Rejected(PASS_ALREADY_OPEN)` before GL and without token, full-clear, or flip
mutation. Mirrored that exact outcome in binding §5.1 and added it explicitly to the exhaustive
shadow acquisition table in §8.1.

### candidate-002 — applied

Replaced the shadow result shorthand with concrete public declarations for
`ShadowEstateResult`, its available/unavailable carriers, every `ShadowEstateView` method,
`ShadowPassSnapshot`, and all four operation-result families. The declarations close the payloads
for acquisition, exact rejection, backend failure, completion, and abort while retaining the
already-settled lifecycle semantics. Mirrored the complete carrier and method contract in §5.1.

### Notes deferred

None.

The compact `§0.22` addendum records only the correction scope and fresh-review obligation.
Because §5.1 changed, the manifest's `cross-phase-interfaces` trigger fires and Phase 5 requires a
fresh verification round before closure.
