# Phase 1 — Attempt 5 independent whole-document architecture review, R32

## Frozen owner and disposition

- **Owner:** `docs/phase1/v14/PHASE_1_DOC.md`
- **Frozen SHA256:** `f752978505d897c562f590efdacfe5d36bbb2170f9b05c0a9e60fb71332e74a2`
- **Inventory:** `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_5.json:5–9` assigns this document, digest and review round 32. This is the supplied frozen identity, matched to the inventory; I did not execute a new hashing or validation command.
- **Required corrections:** 0. **Non-blocking notes:** 1.
- **Further §5 correction required:** No. The frozen revision already changes §5; this verdict does not erase its separate integration-review obligation.

## Scope and governing authority

Read the complete current Phase 1 document, §§0–12, including historical addenda, current overriding contracts, module/build migration, façade declarations and semantics, recorder, failure/restoration laws, lifecycle/compatibility placement, licensing, implementation/testability plan, OQs, decisions, handoffs and checklist. Historical passages were interpreted through the current incorporation and authority rules, not treated as independently active interfaces.

The owner explicitly selects **`docs/design/v2.0-RC2/DESIGN.md`**, not the globally newest design (`docs/phase1/v14/PHASE_1_DOC.md:12–18`). I independently read RC2 Part I and its complete Phase 1 assignment/doc gate (`docs/design/v2.0-RC2/DESIGN.md:957–1067`), the relevant governing `docs/research/v1/RESEARCH.md` contracts, and `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` §§2 and 16–18. I consulted `docs/MOVES.md` before resolving historical reference paths. Prior review verdicts were not used as proof.

Receiver inspection covered the actual load-bearing dispatch/adoption boundaries in P2 v2 and P3–P7/P10/P13/P14 v1, rather than assuming that an exported owner operation was necessarily consumed. These checks support this owner's boundary review only; they are not whole-document certification of those phases.

## Independent architecture checks

### 1. Candidate sampler initialization has a complete receiving path

`docs/phase1/v14/PHASE_1_DOC.md:3395–3415` defines pre-GL authentication, complete fixed-unit assignments, absent-location handling, bounded lookup/upload error windows, private selection, exact predecessor restoration and distinct recoverable/fatal restoration outcomes. It deliberately avoids public fixed-function normalization and runtime participant dispatch during candidate construction. Recorder success/failure and previous-selection evidence are specified at `:3430–3438`; §5 incorporates the operation at `:5145`.

The consumer is explicit: `docs/phase4/v1/PHASE_4_DOC.md:1377–1425` initializes after link/input verification and before state-dependent validation, continues only on Completed, treats restored failure as candidate-local failure, and treats unproven restoration as registry/off containment. Thus the new result alternatives are neither silently dropped nor collapsed into successful fallback. The need to avoid validating distinct sampler types at their default shared unit is corroborated by the published [glValidateProgram contract](https://docs.gl/gl4/glValidateProgram). P1 remains mechanism-only; P5 remains the fixed-unit policy owner and P6 remains the runtime uniform participant owner.

### 2. Occupied-unit normalization reaches both shader and vanilla transitions

`docs/phase1/v14/PHASE_1_DOC.md:3417–3427` defines the exact sixteen-bit mask, precondition ordering, no texture/active-unit mutation, error containment and public fixed-function clearing. `docs/phase5/v1/PHASE_5_DOC.md:2427–2444` supplies the concrete consumer: complete sixteen-row zero-GL preflight, one normalization call, ascending occupied-unit binds, and Bound only after every operation succeeds. Late failure transfers no lease and permits no activation or draw.

The other receiver is not an implied callback: `docs/phase14/v1/PHASE_14_DOC.md:690–703` uses the existing public `useFixedFunction` route and requires clearing unknown state before fixed drawing. `docs/phase7/v1/PHASE_7_DOC.md:3361–3364` explicitly receives that result-checked normalization obligation. The separation between sampler objects and texture-object parameters is consistent with the published [glBindSampler contract](https://docs.gl/gl4/glBindSampler).

### 3. Runtime demotion restores actual texture state, not merely the tier enum

P1's binding receipt at `docs/phase1/v14/PHASE_1_DOC.md:5148` requires authenticated latest complete owner parameters, private baseline replay, binding/error/restoration checks, and containment/rebuild when replay is failed or unproven. It preserves borrowed-object restrictions and live/retiring accounting.

The reciprocal transaction in `docs/phase14/v1/PHASE_14_DOC.md:720–733` stops affected-estate admission, authenticates and replays every affected owned object, then clears samplers and commits NONE only after success. This closes the observable failure mode in which a sampler-backed texture would otherwise resume NONE drawing with stale object defaults. The full owner parameter model in P1 §4.7.7 remains the common value contract, rather than a second reduced fallback parameter vocabulary.

### 4. Duration locks, poisoning and recording remain implementable across the module seam

`docs/phase1/v14/PHASE_1_DOC.md:3440–3449` lets both the real backend and engine recorder issue private implementations of the unsealed opaque lease without split packages, reflection, exposed construction state or an engine-to-mod edge. Acquisition/close rules at `:3451–3490` distinguish absent aspects from locked OFF, capture retained disabled factors, suppress ordinary attempted writes, restore through cache-coherent private bypass, attempt independent cleanup and poison admission on failed restoration.

The concrete mod-only bridge at `:3492–3501` has an actual receiver. `docs/phase7/v1/PHASE_7_DOC.md:1670–1706` specifies the exact alpha/blend hook family, CORE admission consequence, delegated-overload deduplication and suppression of intermediate bypass observations. Its installed route at `:2838–2869` forwards complete effective values to the accepted P6 runtime, handles rejected/failed delivery and retains the endpoint through final restoration/retirement. Recorder semantics model the lease/restoration outcomes without pretending that façade events establish native call counts.

### 5. Schema22 is an authority receipt, not a new P1 parser or codec

`docs/phase1/v14/PHASE_1_DOC.md:5147` receives exact current containing/nested/inspection equality and MaterializedSource-v22 while retaining the nine projection trees and projectionVersion=1. This agrees with `docs/phase3/v1/PHASE_3_DOC.md:4039–4102`, which explicitly makes earlier numeric receipts historical and requires P1's changed-§5 reread without a new façade. No P1 binary acquisition, provenance parser, local schema repair or optional API grant is inferred.

### 6. Whole-phase foundations remain coherent beyond the focus amendments

The package/module assignments and C-1…C-4 preserve a pure engine, loader-owned adaptation and conformance's engine-only edge. The jcpp admission retains implementation-time exact pin/closure/license verification, no public jcpp types and once-only runtime packaging. The build migration is not anchored to superseded July versions: `docs/phase1/v14/PHASE_1_DOC.md:2392–2422` matches the currently inspected wrapper 9.7.0, Unimined 1.4.36-kappa, inline loader 0.6.10-alpha, existing JEI mod-only dependency and mod-owned Buildship/generated-template callback. It correctly records, rather than conceals, the three workflows' 9.6.1 setup-input mismatch and requires alignment during implementation.

I independently read the current build/scripts/template source and resource inputs, and consulted the Cleanroom mixin guide, mixin template and scaffolding checklist. The template tool's older upstream pins were not substituted for the actual checkout. The DEFAULT/MOD/PRE_INIT separation, client-only discovery, sticky early whole-family veto, service inventory and bootstrap handoff remain architecture contracts with stated implementation verification, not executed loading claims.

The existing linked-geometry/fullscreen rules, authenticated vertex-source and borrowed-depth boundaries, complete texture parameters, bounded/source-free diagnostics, replay attribution, assigned OQ fallbacks and implementation checklist were also reviewed. No current owner contract defect requiring a further amendment was established.

## Required corrections

**None.** There is no evidence-backed current P1 defect warranting PASS-WITH-CORRECTIONS or a structural rebuild.

## Notes

### N1 — Historical pinned source identity is not freshly reproduced

**Severity:** informational evidence limitation; no demonstrated architecture breakage and no required owner edit.

`docs/phase1/v14/PHASE_1_DOC.md:39`, `:84` and `:4855–4858` identify historical Pintonium/loader snapshots. `docs/MOVES.md:49–51` resolves their recorded renames, but the available checkout instead contains `reference-src/Pintonium-main/` and `reference-src/Cleanroom-0.6.12-alpha/`; the moved historical pinned directories are not present. I did not equate these current trees with the historical bytes.

Permitted current Pintonium files independently corroborate the service inventory, the three bootstrap anchors and class-scan/config arrangement: the complete `MinecraftVersionShimService`, `GLStateManagerService`, `RenderSystemService`, startup mixins, `CeleritasVintageMixinPlugin`, and `mixins.celeritas.json` were inspected under `reference-src/Pintonium-main/`. This corroboration is narrower than reproducing the historical pinned-source verification. Preserve that distinction; do not relabel the old evidence as newly verified. This note does not require resurrecting forbidden sources or reopening architecture solely because future execution has not occurred.

## Limitations and final disposition

This was a read-only architecture review. No repository/report files were changed; no builds, tests, formatters, linters, runtime/native experiments, dependency resolution, pack-tier runs or validation commands were executed. No forbidden chatlogs/transcripts, Oculus pipeline/transform, relocated GLSL/libs or glsl-transformer implementation were read. Published GL references corroborate API semantics, not actual backend behavior.

Optional worker/context/recorder and other ungranted optimization APIs remain ungranted. Current architecture implementability does not establish native cache coherence, restoration correctness, successful mixin transformation or pack compatibility. Those remain the explicitly named implementation and conformance obligations, not new corrections demanded at this gate.

**Final verdict: PASS.** Retain the frozen Phase 1 owner unchanged for separate integration review. This disposition certifies only its whole-document architecture gate; it grants no code, runtime, pack-tier, sibling-phase or final integration clearance.
