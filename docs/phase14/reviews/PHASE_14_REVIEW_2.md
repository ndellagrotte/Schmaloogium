# Phase 14 — Attempt 5, Fresh Whole-Phase Review R2

## Frozen owner and authority

- **Owner:** `docs/phase14/v1/PHASE_14_DOC.md`.
- **Frozen owner SHA256:** `fd9b2cdb4ef162db6ba08e2b03e88879b4a7f13a6a7e2f6d3b69eb9edb8c04d7`, as recorded in `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_5.json:70–73`. This identifies the assigned frozen owner; it is not a claim of a newly executed checksum command.
- **Scope:** the entire current owner, §§0–12, including architecture, cross-phase contracts, failure handling, evidence ledger, verification procedures, milestones, OQ-15/OQ-22, decisions, and upstream requests. This is not a focus-list-only or previous-report confirmation review.
- **Governing authority:** the owner's header selects `docs/design/v3/DESIGN.md` (`docs/phase14/v1/PHASE_14_DOC.md:5–29`). I used that revision's Part I and Phase 14 specification, `docs/research/v1/RESEARCH.md`'s relevant source-confidence, adaptation, GL/JVM, milestone and open-question sections, and `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md`'s relevant center-depth, GL, shared-context and do-not-inherit sections. `AGENTS.md`, `docs/MOVES.md`, and the execution-wording overlay supplied repository restrictions and citation interpretation. Historical receipts and prior verdicts were not treated as present proof.

## Independent checks

1. **Actual receiving contracts.** Read the current §5 contracts of declared dependencies `docs/phase5/v1/PHASE_5_DOC.md:2511–2808`, `docs/phase6/v1/PHASE_6_DOC.md:1802–2003`, `docs/phase7/v1/PHASE_7_DOC.md:2100–3848`, and `docs/phase13/v1/PHASE_13_DOC.md:1374–1724`, together with their relevant load-bearing implementations-as-designed. Narrow reciprocal reads covered Phase 1's complete parameter mapping, sampler normalization and replay obligations; Phase 4's activation, identity and pending compiler split; Phase 2's source-free evidence contract; and Phase 11's metrics and conditional expression-backend methodology. No sibling phase is certified by this report.
2. **A1 sampler integration.** Checked complete authenticated parameter derivation, borrowed-object restrictions, fixed sixteen-unit policy, complete preflight before normalization, physical binding ownership, all four binding outcomes, Bound-only lease transfer, resizing and retirement. Traced the newly specified fixed-function clearing operation through the receiving P1/P4/P5/P7 dispatch, rather than stopping at P14's producer. Full replay before runtime NONE admission is specified, but it does not cover ordinary fixed-function passthrough; correction 1 identifies the resulting gap.
3. **A2 and native evidence.** Checked DSA's facade-internal ownership, cache-coherent binding requirements, baseline fallback, and separate facade/native/conformance evidence. `docs/phase14/v1/PHASE_14_DOC.md:2040–2059` correctly refuses to treat recorder traces as native sampler, DSA or transition proof. The present review makes no such inference either.
4. **A3 center-depth timing.** Compared the current P6 request/sample boundary and tick-based CPU smoothing recurrence against P14's raw-source, age-aligned recurrence, live-output and perceptual comparisons. The owner distinguishes a legitimate latency-induced output difference from a recurrence defect (`docs/phase14/v1/PHASE_14_DOC.md:984–994,1024–1028`). The age-bearing interface remains ungranted and C1–C4 remain independent gates; synchronous operation remains authoritative. No executed replay or perceptual result is claimed.
5. **A4 shared contexts.** Checked producer fence/flush/publication, nonblocking render-thread admission, acknowledged cancellation, and quarantine versus destruction. The corrected worker-to-render completion rule and watchdog containment are substantive improvements (`docs/phase14/v1/PHASE_14_DOC.md:1112–1133`). P4/P7/P13 split APIs remain pending, including the reciprocal receipt at `docs/phase7/v1/PHASE_7_DOC.md:3840–3845`. The separate render-to-worker storage dependency remains missing; correction 2 addresses that direction.
6. **A5–A7 and evidence gates.** Checked installed GL4.3/KHR-debug capability plus the existing flag without requiring a debug context; retained OQ-3 and optional-operation gates; checked P11's interpreter-first metrics contract and real-pack/AST-attribution threshold before any compiled-backend experiment; and checked allocation/native-call attribution, source-free output, and lack of vanilla optimization authority. The current schema22 receipt and P4 positional-route-v2 identity remain opaque owner-issued handoffs, not permission for asynchronous work or reconstruction of identity. Historical schema receipts are superseded rather than independently flagged.
7. **Completeness and disposition.** All thirteen required sections and the seven modernization areas have architecture, fallback and evidence dispositions. The defects below are discrete protocol corrections, not grounds for structural FAIL or demands for runtime evidence to be executed at this architecture gate.

## Required corrections

### 1. Preserve authenticated object sampling state across ordinary fixed-function passthrough

**Severity:** high correctness impact when A1 is enabled; required owner-contract correction.

**Owner evidence:** `docs/phase14/v1/PHASE_14_DOC.md:611–617` directs `setParameters` under a sampler tier to apply only the object-state half to the texture and route its sampler-state half to the cache. Lines `690–699` then require clearing all native samplers before every fixed-function draw. Complete sampler-state replay at `720–733` is limited to runtime demotion to NONE. The same limitation is exported in §5.10 at `1824–1834`.

**Receiving path:** `docs/phase5/v1/PHASE_5_DOC.md:2390–2393` explicitly makes fixed-function FINAL bind the owned compatible frozen colortex0 at unit zero. `docs/phase7/v1/PHASE_7_DOC.md:1253–1262` binds through P5, immediately activates through P4, prohibits row rebinding, then draws. `docs/phase4/v1/PHASE_4_DOC.md:1692–1697` dispatches the fixed terminal to `ShaderService.useFixedFunction`. Therefore the sampler established for that colortex is removed immediately before the passthrough draw, without tier demotion or object-state replay.

**Observable breakage:** the [ARB_sampler_objects specification](https://raw.githubusercontent.com/KhronosGroup/OpenGL-Registry/main/extensions/ARB/ARB_sampler_objects.txt), §3.9.2, specifies that binding sampler zero reactivates the texture object's sampler state. The required colortex state is CLAMP_TO_EDGE and NEAREST or LINEAR according to format (`docs/phase5/v1/PHASE_5_DOC.md:1391–1394`), but P14's optimized parameter path does not maintain that sampler-state half on the object. Ordinary fixed-function FINAL can consequently sample with stale/default filter, wrap or other sampler parameters instead of the authenticated owner state. This is a normal no-final-program path, not an allocation failure or runtime fallback. Successful sampler-backed rendering followed by this transition therefore lacks the behavioral equivalence A1 promises.

**Minimal owner fix:** extend §§4.1 and 5.10 so every owned texture that will be sampled with sampler zero has its latest complete authenticated sampling state established on the object before the clear and draw. An implementation may maintain an owner-equivalent object baseline or replay tracked divergent state at the safe transition, but must preserve all-unit clearing, binding neutrality, the existing P5-bind/P4-activate order, failure containment, and borrowed ownership. Extend the native transition procedure at `docs/phase14/v1/PHASE_14_DOC.md:2055–2057` to verify the owned passthrough texture's effective sampling state and output after successful sampler use, through ordinary fixed-function FINAL, then continued shader rendering—without inducing demotion. Coordinate affected receiver obligations; do not fix this by leaving a sampler bound or bypassing the existing dispatch.

**§5 impact:** yes. This is attributed to P14's optimized state split and transition contract; the P1/P4/P5/P7 references establish the consumer path, not additional findings against those owners.

### 2. Establish render-thread storage completion before worker texture upload

**Severity:** medium correctness impact in the proposed optional upload protocol; required owner-contract correction before that protocol can be adopted.

**Owner evidence:** `docs/phase14/v1/PHASE_14_DOC.md:1134–1139` assigns texture creation/storage allocation to the render thread and `glBindTexture` plus `glTexSubImage2D` to the shared worker. Its fence and producer flush occur after the worker's upload. The requested P13 staging extension at `1686–1691` similarly requests render-thread allocation, worker upload and completion polling without an allocation-readiness dependency. The completion/disposal protocol at `1756–1765` and the future-receiver obligation at `1836–1839` cover the worker's outgoing publication, not the main context's preceding storage creation.

**Observable breakage:** shared object names and Java queue publication do not order commands in separate GL contexts. The [ARB_sync specification](https://raw.githubusercontent.com/KhronosGroup/OpenGL-Registry/main/extensions/ARB/ARB_sync.txt), §5.2 and Appendix D.3.1, requires completion of one context's changes before another context executes commands dependent on those changes; flushing another context does not submit the producer's commands. Here the worker upload depends on storage established by the main context. Without a main-to-worker readiness dependency, it may execute before that storage is complete/visible. A fence inserted after the upload on the worker cannot retroactively order the earlier allocation. The protocol therefore permits an invalid or incorrectly ordered upload even if its worker-to-render completion fence later signals.

**Minimal owner fix:** add an explicit readiness handoff for render-created upload storage—for example main-context allocation followed by a fence and main-context flush, with the worker establishing completion before its bind/upload. Define ownership and cancellation/quarantine treatment of this readiness sync as well as the existing outgoing completion sync. Keep the render thread nonblocking and preserve the subsequent worker fence→worker flush→publication sequence. Carry this requirement into §§5.5/5.9/5.10's pending P13/P7 contract and the isolated-job spike, including a case where progress cannot be accidentally supplied by a later main-context swap or flush.

**§5 impact:** yes. This finding does not grant an upload API or allege a present implemented runtime failure. Current synchronous upload remains required. It corrects the concrete proposed protocol before owner adoption; it does not require running the future spike during this review.

## Note

### N1. Historical pinned source evidence remains historical

**Non-corrective evidence-confidence note.** The owner preserves pinned `reference-src/pintonium-9c2fcc1` receipts in its source tables and ledger (`docs/phase14/v1/PHASE_14_DOC.md:449–482,1968–1987`). That pinned directory was not available in the current permitted tree. Available corroboration was read narrowly from `reference-src/Pintonium-main/common-shaders/src/main/java/net/irisshaders/iris/gl/IrisRenderSystem.java:24–48,358–394,458–610`, its `gl/sampler/GlSampler.java:1–36`, and `reference-src/Pintonium-main/common/src/main/java/org/embeddedt/embeddium/impl/gl/sync/GlFence.java:1–53` and `gl/debug/GLDebug.java:285–365`. These support specific sampler, DSA, fence-poll and debug-gating mechanics, but do not authenticate the absent historical pin or establish current pack-exercised/native performance results. The owner already distinguishes historical receipts from current backend proof. Preserve that distinction; no required owner edit follows from this limitation.

## Limitations and final disposition

This was a read-only architecture investigation. No owner file or report file was written; no build, formatter, linter, test, runtime experiment, pack-tier comparison, native capture or project-wide validation was executed. No forbidden pipeline/transform implementation, library or relocated implementation, chatlog, root transcript, or decompiled source was used. Optional APIs remain ungranted. Unexecuted future evidence is not counted as a defect by itself, and this review provides neither sibling certification nor final integration approval.

**Final verdict: PASS-WITH-CORRECTIONS.** Two substantive required corrections remain, both affecting §5 obligations; one non-corrective source-confidence note is recorded. The overall architecture is reviewable without structural rebuild. Main should apply the two owner protocol fixes, coordinate any changed receiving obligations, and obtain fresh review of the changed frozen owner set before separate final integration.

## Resolutions — owner documentation correction, 2026-09-08

The original review body, frozen-owner receipt and verdict above are preserved. These are
owner correction dispositions, not a fresh review or replacement PASS.

1. **Required correction 1:** D-P14-31 and active §§4.1/5.10/6/8.2 establish the complete
   latest authenticated object parameter baseline on every successful setParameters alongside
   sampler cache state. The sampler/object classification no longer omits object sampling
   writes; only proven redundant writes may be skipped. Ordinary fixed-function FINAL clears
   all sixteen samplers while retaining P5-bind → P4-activate → draw, binding neutrality,
   borrowed nonmutation and failure containment, then resumes shader rendering without
   demotion. Native evidence now explicitly queries effective object state and compares output
   through that successful transition, including an authenticated update. The earlier
   demotion/failure-injection experiment remains separate. §§7/11/12 carry the corrected
   performance hypothesis and exact P1/P4/P5/P7/P13 receiving obligations.
2. **Required correction 2:** D-P14-32 and §§4.4/5.4/5.5/5.9/5.10 require main-context
   allocation → allocation-readiness fence → main producer flush → worker observed readiness
   before bind/upload, then the separate worker completion fence → worker flush → publication.
   Render admission stays nonblocking. P13 retains incoming-sync/object/payload ownership;
   the worker inventories its outgoing sync until accepted transfer, and both syncs require
   exactly-once acknowledged cleanup after GPU completion or accounted quarantine. Pending
   P7/P13 grants, failure cases and §10.1 isolated-job spike include cancellation at both
   edges and proof without progress accidentally supplied by later main swaps/flushes.
   Synchronous upload remains authoritative; no staging API is adopted.
3. **Coordinated identity receipt:** D-P14-33 receives exact-current containing/nested/
   inspection schema23 and MaterializedSource-v23, rejecting all other versions without
   upgrades. Older numeric receipts remain historical; nine metadata-only trees and
   projectionVersion1 are unchanged. P9 retains registry resolution; P14 adds no parser.
4. **N1 retained:** no historical pinned source receipt is upgraded to current/native proof.
   L-2/L-4 retain their IDs; no health IDs/catalogue rows change. Main still owes reciprocal
   receiver/integration receipts and fresh review of changed owners.

Documentation only: no validation command, build, test, formatter, linter, runtime/native
experiment or fresh verification was executed. No implementation clearance or new PASS
is claimed.

**Reciprocal allocation receipt:** D-P14-34 additionally receives P1 D-P1-63/P5 D-P5-42/
P13 D-P13-36 target-bearing mandatory synchronous values and exact conversion after reading
their active allocation sections. P1 D-P1-64/P5 D-P5-43/P13 D-P13-38 receive the complete
baseline law; P13 receives the two-sync proposal as ungranted. No new source/format policy,
borrowed mutation, worker API, executed evidence or fresh verdict follows from these receipts.
