# Phase 5 Verification Review — Round 27

## 0. Method and reading order

I independently re-derived the sole Gate-surviving candidate before consulting prior reviews. I
read the complete Phase 5 target, the RC3 Part I rules, mandatory template, Phase 5 specification,
and document gate, and the manifest-selected binding regions of Phases 1, 3, and 4. I compared the
public signatures in §2.2 with the manifest-declared §5 interface region and checked the sizing
preconditions in §4.11. Supporting implementation evidence was not needed to decide this
document-structure and interface-honesty candidate.

Only after settling that interpretation did I read Phase 5 reviews 1 through 26 in numeric order
and compare the candidate with their findings and resolutions. No prior review mentions or settles
the omitted `Extent2i` declaration. I used no network access, forbidden source, or prior-session
transcript. In particular, I did not open the forbidden `*.txt` supporting-evidence path. There
was no agent fan-out or delegation. In accordance with the dispatched atomic-role instruction and
the verify-loop skill, I did not invoke the loop, run `scripts/verify`, or start another Codex
session. There were no deviations from the resolved reading contract, no candidates eliminated
before adjudication, and no Gate drops.

## 1. Findings

### candidate-001 — The binding interface omits the public `Extent2i` value required by its exposed carriers

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:1635`–`:1649`

**Claim.** Binding §5 exposes planning requests, runtime inputs, structural sizing, resize
notices, and main-depth snapshots whose public shapes require `Extent2i`, but it neither names nor
defines that public leaf value. A dependent or final-integration reader restricted to the binding
surface cannot recover the exact `(int width, int height)` contract without consulting §2.2.

**Evidence.** Section 2.2 expressly says its illustrative signatures define the cross-phase
contract and declares `public record Extent2i(int width, int height) {}` before embedding that
type in `BufferSizing` (`docs/phase5/v1/PHASE_5_DOC.md:350`–`:373`). The same public type is
required to construct `BufferRuntimeInputs`, and therefore both `BufferPlanRequest` and
`BufferBuildRequest` (`docs/phase5/v1/PHASE_5_DOC.md:408`–`:428`). Binding §5 exposes those
request and sizing families but only describes their extent semantics in prose; its otherwise
explicit supporting-value inventory never names `Extent2i`
(`docs/phase5/v1/PHASE_5_DOC.md:1635`–`:1649`).

The governing rules make dependency §5 the surface consumers build against
(`docs/design/v2.0-RC3/DESIGN.md:269`–`:271`) and require every promise to dependents to be
specified rather than gestured at (`docs/design/v2.0-RC3/DESIGN.md:291`–`:292`). This omission
also survives the template's allowance for load-bearing signatures in §2: final integration is
specifically limited to each phase's §§1, 5, and 11
(`docs/design/v2.0-RC3/DESIGN.md:645`–`:648`), so §2.2 is equivalent detailed coverage but cannot
make the binding region independently complete.

**Required correction.** Add `Extent2i` to the relevant exposed-contract row in §5.1 and state
its exact public shape as `Extent2i(int width, int height)`. Mirror only validation semantics the
document actually promises. The present record declares no validating constructor, and §4.11
states positive display dimensions as a sizing precondition rather than an `Extent2i`
constructor invariant (`docs/phase5/v1/PHASE_5_DOC.md:1408`–`:1418`); the fix must not invent
constructor-level positivity enforcement.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported Round-26 surface remains clean on re-derivation. The successful texture
binding snapshot consistently contains sixteen ascending rows for units 0–15, uses the closed
`Bindable`/`Missing`/`Absent` outcome algebra, assigns binding iteration to Phase 7, leaves fixed
sampler-number uploads to Phase 6, and mirrors those obligations in §5 and the conformance tests.
The neighboring overlay validation, lease ownership, whole-call rejection, and unit-15 absence
semantics remain coherent.

The dependency and conformance clean areas also remain clean. Phase 5's consumption of the
manifest-selected Phase 1 borrowed-depth operations, Phase 3 configuration/resource values, and
Phase 4 detached candidate view matches those binding contracts. Publication, resize, main-frame,
shadow, clear, flip, depth-copy, sizing, format, and fixed-unit behavior otherwise have
equivalent detailed and binding coverage. The conformance map covers the governing Appendix
B.1/B.2/B.3/B.4 requirements and the Phase 5 document gate.

The strongest clearing interpretation was that §2.2 already declares the exact type and §5 need
only name the larger carrier contracts. That interpretation fails under RC3's explicit treatment
of dependency §5 as the build-against contract and its prescribed final-integration reading
surface. `Extent2i` is a public value that consumers must construct and inspect, not a private
transitive implementation detail. The correction is nevertheless narrow: the candidate does not
support adding positivity validation absent from the current public declaration. Candidate-001 is
admitted at correction severity. No candidate was dropped on re-derivation, and there were no
Gate drops.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The sole admitted defect is a localized omission from the declared cross-phase data contract and
does not require rebuilding the Phase 5 architecture, so `FAIL` is not warranted. The supplied
trend data is empty. Direct prior-review comparison shows Round 23 reached literal PASS, while
Rounds 24–26 each exposed and corrected a successive part of the texture-binding interface; this
round finds a separate public-value omission, so convergence is not yet established.

The next required action is a scoped fix-up resolving candidate-001 and appending its resolution
to this review. Because that fix must change the manifest-declared §5 region, the
`cross-phase-interfaces` change trigger applies: Phase 5 owes a fresh verification round before it
can close.

## Resolutions

### candidate-001 — resolved

Re-derived against the public declarations in §2.2, the sizing preconditions in §4.11.1, and
RC3's binding-interface and final-integration rules. Binding §5.1 now names `Extent2i` beside
`BufferSizing` and specifies its exact public shape as `Extent2i(int width, int height)`. It also
states that the record itself performs no constructor validation and leaves positive display
dimensions as a sizing precondition, so the correction does not invent enforcement absent from
the declared public shape. A compact §0.29 addendum records the change.

This correction changes the manifest-declared `cross-phase-interfaces` region. Phase 5 therefore
owes a fresh verification round before it can close.

### Notes deferred

None.
