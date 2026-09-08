# Phase 6 — Independent Architecture Review 25

## 1. Reviewed artifact and scope

- **Target:** `docs/phase6/v1/PHASE_6_DOC.md`, complete document, §§0–12.
- **Assigned snapshot SHA-256:** `d9658e38d6439ab2ddd00190985fca083a3c9e4aa9e0ea1c336bba513d1bea4e`.
- **Hash qualification:** this is the assignment-supplied frozen identity, not an independently computed checksum.
- **Governing design:** the target's declared `docs/design/v2.0-RC3/DESIGN.md`. Other phases' adoption of v3 does not change Phase 6's authority.
- **Intended report path:** `docs/phase6/reviews/PHASE_6_REVIEW_25.md`.

This was a fresh, read-only, whole-document §G1.2 review, not a patch-only check or a continuation of a historical verdict. No files were changed. No builds, tests, formatters, linters, runtime experiments, or verification commands were run. The findings below concern architecture contracts and active implementation instructions, not unperformed runtime evidence.

## 2. Authority and evidence read set

### 2.1 Governing material

Read `AGENTS.md` and `docs/MOVES.md`; RC3 Part I, including §G1.2, the dependency/integration gates, licensing/provenance rules, and the mandatory document structure; and the complete RC3 Phase 6 specification, required inputs, scope, and literal gates at lines 1689–1802.

Read `docs/research/v1/RESEARCH.md` §§0–1, §3.4, §4.2, §4.4, Appendix B.3, the complete Appendix D, Appendix F.6, and the relevant Appendix A.3 and E hook entries. Read the shipped author-document uniform/sampler material in `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt`.

Read the original findings in `docs/PHASE_INTEGRATION_REVIEW.md`, their Resolutions, and all recorded follow-ons through the current timing/schema amendments. This includes the maintainer's explicit IR-18 prepared-submission decision, the option-only SSAA disposition, old-hand-light policy, the U1 correction, and subsequent schema/native-source/asset rulings. Read the recorded U1, geometry-primitive, and texture-sidecar decision documents. Historical findings, grants, and receipts were treated as claims to compare with current owner/receiver text, not as verification of that text.

### 2.2 Dependency and receiving-side contracts

Read the current complete §5 publications of Phases 1, 3, 4, and 5, together with incorporated material relevant to this target: module/seam placement; uniform uploads and locations; drain/replay semantics; declaration/type catalogs; macro and smoothing inputs; effective program/sampler layouts; selection and barrier dispatch; activity-token lifetime; registry publication; and the sole fixed-sampler resolver.

Read current receiving-side contracts and dispatch material in:

- **Phase 7:** frame/matrix ordering, actual frame-timing copying, uniform signal routing, sampler-resolver injection, runtime retirement, instance repetition, atlas-binding evidence, and replay-aware capture publication.
- **Phase 2:** replay-attribution manifest provenance and the actual timing/capture contract.
- **Phase 8:** celestial/shadow-matrix event delivery, the shared sampler/barrier boundary, and restoration containment.
- **Phase 9:** held-item policy, entity/block-entity scope admission and restoration, and delivery through the existing Phase 6 sink.
- **Phase 11:** fixed schema/runtime correspondence, every custom-submit result branch, exact refresh counters, accepted-prefix behavior, reset, and terminal controller disposal before Phase 6 retirement.
- **Phase 13:** Known/Unknown atlas metadata and the authenticated actual-binding adapter to Phase 6.
- **Phase 14:** center-depth consumption, the still-requested sample-age permission, and mandatory synchronous fallback while that permission is absent.

These reads assess the stated interfaces and their matching receivers. They do not certify those phase documents as independently verified dependencies.

### 2.3 Permitted reference inspection and limitation

Read the required Pintonium design §6 and B1/B6 material after the licensing rules. Inspected permitted current `reference-src/Pintonium-main` uniform/program/smoothing/notifier/matrix/center-depth sources and its LGPL license material. These corroborate the distinction between cadence acquisition and activation, cached uploads, tick-domain smoothing, first-value seeding, notifier hazards, sampler-integer mechanics, and the reasons not to import reference policy wholesale.

The target's historical `reference-src/pintonium-9c2fcc1` checkout was not present at the cited paths; the available checkout is `Pintonium-main`. Accordingly, this review does **not** reauthenticate that historical revision or certify its exact source-line citations. Current-source observations are corroboration only, not substituted pinned evidence. No prohibited transformation source, Oculus pipeline, library/relocated-transform implementation, transcript, chatlog, or repository-root text log was read. No source was copied or dependency added.

## 3. Literal gate and document completeness

| Required architecture element | Independent assessment |
|---|---|
| Every Appendix D entry has a provider, cadence, and milestone | The §4 inventory covers the fixed values, matrices, atlas/held inputs, and all five per-draw dynamics. Late value producers remain distinguished from the v0.1 interface. The remaining instance acceptance/checklist contradiction is R25-2. |
| Exact smoothing formulas | §4.5 specifies time-corrected exponential decay in ticks, asymmetric wet/dry rates, first-target initialization, zero-half-life behavior, paused/regressing/invalid time handling, and eye-brightness conversion. The reference's dryness assignment error is not adopted. |
| Point-by-point Phase 4 barrier fulfillment | §§4.9–4.10 use the effective resolved program/layout and Phase 4's existing three ordered participants, with callback-scoped lookup and a separately retained activity token. No program handle or fourth participant is introduced. |
| Recorded center-depth decision with contract evidence | §4.8 selects the synchronous CPU path and an empty macro contribution. The declaration-rewrite problem, missing fixed sampler allocation, numerical mismatch, and prohibited transformer dependency are addressed rather than hidden. |
| Notifier-to-producer audit | §4.12 names concrete owning producers and distinguishes value updates from texture binding. Phase 7 owns v0.1 entity color; Phase 9 does not postpone or take over that producer. |
| Frame-begin ordering exported in §5 | Sampling, including prior-framebuffer center depth, precedes resize/clear. Current-frame matrix capture is separately post-camera; it is not falsely placed at the earlier clear. |
| All thirteen sections | §§0–12 contain substantive scope, architecture, conformance, detailed design, interfaces, failure handling, performance, tests, staging, OQ disposition, decisions, and implementation hand-offs. |

The literal gate's major mechanisms are present. Whole-document acceptance nevertheless requires the replay transport correction and reconciliation of the active instance instructions below.

## 4. Substantive adversarial audit

### 4.1 Cadence, temporal state, and numerical behavior

The document does not equate “refresh on every switch” with resampling every provider. Tick/frame/signal acquisition is separated from every-activation visitation. Effective-program/generation/layout caches prevent a fallback child from lending its declarations or state to the selected provider. Equal cached values may skip uploads, while declared matrices remain in every attempted activation batch.

The temporal contract distinguishes accepted frames, duplicate calls, stale identities, world transitions, and delayed post-camera capture. Previous snapshots are rotated at the frame boundary rather than by whichever shader first reads them. Singular inversion isolates the inverse rather than invalidating unrelated matrix values. The actual `UniformFrameTiming` publication records accepted inputs and updated counters; Phase 7 consumes that owner value instead of reconstructing time from requested capture settings. Historical detached timing values do not confer live runtime authority.

The center-depth baseline remains one synchronous sample at the prescribed boundary when declared. Phase 14's pending sample-age request is not silently adopted: its own §5.3 explicitly keeps PBO/fence operation forced off until permission lands. That complete fallback is not a missing Phase 6 architecture mechanism or a reason to demand runtime evidence during this review.

### 4.2 Samplers, effective state, and error containment

Phase 5's resolver is injected immediately after configuration and receives the same effective sampler layout, stage, and band used for physical binding. Ready rows carry exact name/full shape/unit; Invalid prevents sampler uploads and degrades the scoped participant. Sorting and plan reuse do not create another unit allocator, and integer repointing does not assume ownership of texture objects.

The bound-only lookup interface and activity-token checks prevent immediate events from writing cached locations after program replacement or fixed-function selection. The cached replay algorithm neither resamples providers nor reevaluates custom expressions, and disable scope remains generation/effective program/uniform. However, the algorithm's result stops at local diagnostics rather than crossing the required replay-evidence boundary; R25-1 is the concrete missing mechanism.

### 4.3 Cross-boundary dispatch and ownership

Receiving-side inspection found explicit routes for celestial/fog/blend signals, shadow matrices, held/entity/block values, and atlas Known/Unknown results. Atlas dimensions alone are not accepted as evidence that an atlas is currently bound: issuer, composition/resource epoch, and bind serial are authenticated before the adapter updates Phase 6. Stale evidence cannot restore an old value.

The Phase 11 bridge has matching Accepted, SkippedAbsent, and Rejected handling, exact completed/aborted-prefix counters, and declaration-order submission. Schema-present runtime values must conform to the same closed type, including the separate boolean upload command. A malformed counter report invalidates the batch rather than pretending that a guessed prefix is safe. There is no additional custom barrier participant.

Permanent retirement is distinct from generation adoption and world reset. Rejected retirement retains ownership and borrowed-service lifetime; successful retirement invalidates retained capabilities without GL calls. Candidate abort, accepted replacement, recovered-off disposition, and shutdown have distinct orderings. Phase 7 and Phase 11 consumption preserves final callback/restoration use before releasing the runtime's borrowed services. These are current architecture agreements, not evidence that the coordinated implementations already obey them.

### 4.4 Scope, OQs, and provenance

Phase 6 has no assigned OQ requiring a separate spike. The GPU center-depth candidate has a reasoned rejection and complete baseline, rather than a placeholder awaiting an experiment. Future performance/capture/hook proof remains explicitly implementation work. The schema21 and same-load asset changes do not grant Phase 6 source reopening, binary texture interpretation, or new ownership of physical bindings. Current owner grants remain subject to their independent review gates, and IR-01/final integration is not cleared by this review.

## 5. Findings

### R25-1 — Publish the owner-defined replay result to its capture consumers

- **Severity:** correction.
- **Location:** `docs/phase6/v1/PHASE_6_DOC.md` §4.11, lines 1277–1318; missing corresponding publication in §§2.2 and 5, particularly the consumed GL-error row at line 1710.
- **Claim:** Phase 6 performs the required cached replay but never specifies how its replay verdict reaches Phase 7 and Phase 2 as `ReplayAwareGLError`.
- **Evidence:** Phase 1 §5.2, line 5007, requires exactly one `ReplayAwareGLError(GLError error, boolean attributed)` for each error in the triggering nonempty drain and expressly names Phase 6 as the emitter. The true/false rule cannot be inferred from a label or the original `GLError`. Phase 7 §5.2, line 2976, and R7-6 at line 3258 say it copies Phase 6's accepted result. Phase 2 §5.2, lines 2101–2109, makes that owner-defined result the sole admissible source of its total manifest attribution boolean. The complete Phase 6 document has no `ReplayAwareGLError` publication. Its factory accepts only the ordinary `DiagnosticReporter`, its runtime exposes no replay-result accessor/sink, and §4.11 ends with WARN/log reporting and participant degradation. Phase 4's result is only Continue or `Degraded(String diagnosticId, String disabledScope)`. Phase 1's diagnostic reporter routes `EngineDiagnostic`; no agreed typed replay transport or consumer dispatch is specified through it.
- **Trigger and impact:** When an upload batch produces a nonempty drain, Phase 6 can locally isolate or fail to reproduce the error, but the capture adapter has no contracted route to receive the original error plus that verdict. Implementers must invent an interface, lose results, or reconstruct forbidden attribution. The P7/P2 total replay-aware manifest promise is therefore not implementable from the published Phase 6 surface.
- **Required correction:** Specify a concrete Phase 6 publication/transport for the existing Phase-1-owned value, including per-trigger cardinality, ordering/correlation, delivery lifetime, and failure behavior, and publish it in §5 with the matching Phase 7 receiving route. Keep Phase 1's type ownership and attribution law, Phase 6's replay/disable ownership, Phase 7's copying role, and Phase 2's serialization/assertion role distinct. Do not solve this by inventing a new GL verb, fourth participant, or label-based attribution rule.
- **§5 impact:** yes; this is a missing cross-phase result mechanism, not merely an absent test.

### R25-2 — Use N total instance values in the acceptance scene and checklist

- **Severity:** correction.
- **Location:** target §8.2, line 2015, and §12 item 27, line 2271.
- **Claim:** The active test scene requires an “instance sequence 0…N,” and the implementation checklist tells Phase 7's composite loop to use “values 0…N.” Both retain an inclusive upper bound inconsistent with the adopted count meaning.
- **Evidence:** Target §4.4.4, line 907, and §4.12, line 1343, correctly specify N total draws with IDs 0…N−1. Its incorporated §5 contract agrees. The integration review's dated 2026-09-07 IR-18 ruling expressly chooses N total adjacent prepared submissions, IDs 0…N−1; Phase 1 §5.2 and Phase 7 §4.6 consume that decision. These are active implementation/test rows, not historical quotations.
- **Trigger and impact:** Following the checklist for `instanceCount=N` produces N+1 instance values/draws; following the acceptance row would bless that result or reject the correctly bounded implementation. For N=1, the difference is already observable as an extra copy.
- **Required correction:** Reconcile both rows to N total copies, IDs 0…N−1. Preserve the detailed distinction between outer fullscreen neutral-zero restoration and saved-parent restoration for genuinely nested prepared submissions; no change to the already-correct detailed repetition boundary is required.
- **§5 impact:** no new interface is needed for this correction; existing §5 semantics are the authority the two rows must follow.

### R25-N1 — Mark the expression-exclusion clarification as resolved

- **Severity:** note.
- **Location:** target §4.13, lines 1473–1478, and §11.4, lines 2207–2212.
- **Claim/evidence:** These active passages still say Appendix D.4 and F.6 disagree and require a future authorized clarification. Current RESEARCH §3.4 and Appendix F.6 explicitly specify all five D.4 dynamics plus fogMode/fogColor, and F.6 expressly says it does not narrow D.4.
- **Impact/disposition:** The implemented architecture's seven-name exclusion and Phase 11 view are already correct, so this is authority-ledger maintenance rather than a behavioral defect. Preserve dated historical disagreement records, but make the active status acknowledge the current authority. No §5 semantic change is needed.

### R25-N2 — Describe the verification profile only as historical machinery

- **Severity:** note.
- **Location:** target §11.4, lines 2231–2232; historical context also appears in §0.
- **Claim/evidence:** The active GRANTED row says `verification/targets/phase-6.json` “is data-only, pinned” and has driven the review loop. `AGENTS.md` and `docs/MOVES.md` explicitly state that verification profiles and their loop machinery were retired and removed on 2026-08-08; the target header now determines governance.
- **Impact/disposition:** This does not invalidate the historical rounds or the present RC3 declaration. Mark the profile statement historical so it cannot be mistaken for an available current gate. Do not recreate the retired tooling or alter immutable historical receipts. No §5 impact.

## 6. Counts, impact, and disposition

- **Blocking findings:** 0.
- **Corrections:** 2 — R25-1 and R25-2.
- **Notes:** 2 — R25-N1 and R25-N2.
- **§5 impact:** yes, because R25-1 requires an explicit Phase 6 replay-result publication and matched consumer contract.

The architecture does not require structural reconstruction: its provider seam, cadence model, fixed-sampler delegation, frame/matrix separation, custom bridge, and retirement model are substantively specified. The missing replay transport is nevertheless a real owner-to-consumer contract defect, and the inclusive instance bounds must not remain executable instructions. After correction, the affected §5 contract requires fresh whole-document owner/receiver verification; dependent implementation, runtime proof, and final integration remain separately gated.

**Final verdict: PASS-WITH-CORRECTIONS**

## Resolutions

### R25-1 — Applied separately, 2026-09-08; fresh review owed

P6 §§2.2/4.11/5 publish required `UniformReplayErrorSink.accept(UniformReplayReport)` and
immutable `UniformReplayReport(ProgramUniformCacheKey program,List<ReplayAwareGLError> errors)`.
The eight-argument factory adds the required P7-owned observer after diagnostics; lifecycle
inventories retain it through final restoration and successful terminal retirement. P1 owns
the unchanged error/verdict law; P6 preserves every original error and occurrence/order,
including false cleanup/foreign evidence, without double-counting probes. Barrier and immediate
delivery finish before return. Observer failure retains available original evidence, latches
non-complete capture, and reaches P7 through existing degradation/exception containment rather
than a swallowed participant throw. Logging rate limits do not suppress typed evidence or the
existing broader escalation handoff. P5 receives the exact factory request arity only; P7/P2
receive the route without new facade verb, fourth participant, frame field or wire/schema bump.
§8 records boundary cases, not performed tests. This is an applied contract fix, not a PASS.

### R25-2 — Applied separately, 2026-09-08

Active §8.2 and §12 require N total copies with IDs0…N−1; N=1 is one draw.
Outermost fullscreen neutral-zero restoration remains distinct from genuinely nested
prepared-submission saved-parent restoration. The original review quotation remains unchanged.

### R25-N1 — Applied separately, 2026-09-08

Active §§4.13/11 acknowledge current RESEARCH §3.4/F.6's explicit seven-name exclusion and
non-narrowing of D.4 as resolved upstream. Existing schema/view behavior and dated historical
disagreement records remain unchanged; no RESEARCH authority edit was performed.

### R25-N2 — Applied separately, 2026-09-08

Active §11.4 labels the earlier profile as historical machinery retired/removed on 2026-08-08,
with current §0 governance. No profile/tooling was recreated and original receipts were not
rewritten. All four dispositions are architecture-only; no builds/tests/lint/formatters,
validation commands, runtime proof, self-PASS or alteration of the frozen review body.