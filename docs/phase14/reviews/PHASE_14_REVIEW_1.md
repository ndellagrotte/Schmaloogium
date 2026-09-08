# Phase 14 — Fresh whole-owner architecture review R1

**Verdict: PASS-WITH-CORRECTIONS**  
**Counts: blockers = 0; corrections = 7; notes = 3.**  
**section5Impact: true.**  
**Reviewed owner:** `docs/phase14/v1/PHASE_14_DOC.md`, frozen SHA-256 `a683b1df196deccc0e35b0e24a547b7e9cdf75f8c3277968bbfc9a8557374f31`.

This is a whole-document architecture review, not an implementation certification or a review limited to the latest integration edits. The current owner is substantially specified, but the seven corrections below prevent a literal fresh PASS for BUILD readiness. They are concrete owner-document corrections rather than requests to implement currently ungranted optional paths.

## Review scope and method

Read the complete Phase 14 owner, including its historical provenance and current superseding receipts; header-selected DESIGN v3 global requirements, §G5.3, and the Phase 14 assignment; governing RESEARCH §§0/1/4.8/6.2/6.3/9/11; the applicable Pintonium design/reference sections; and the current dependency §5 surfaces of Phases 5, 6, 7 and 13 with their incorporated binding, publication, retirement, failure and smoothing rules. Also inspected the relevant Phase 1 facade/recording contracts, Phase 2 measurement interfaces, Phase 4 compiler/identity contracts and Phase 11 measurement handoff.

Consumer-side tracing covered Phase 5’s actual sixteen-row physical-binding dispatch, Phase 7’s scope activation and fixed-function paths, Phase 4’s existing synchronous compiler and pending split request, and Phase 13’s synchronous preparation/publication and deferred retirement. No sibling is certified by these reads.

No files were edited. No validation commands, builds, tests, formatters, linters or runtime experiments were run. No prohibited Oculus pipeline/transform, library, relocated GLSL, chatlog or transcript content was opened.

## Required owner corrections

### C14-1 — Reconstruct texture-object sampling state before sampler fallback

**Evidence:** Phase 14 §4.1.2, lines 601–607, changes `TextureService.setParameters(t,p)` so that only the object-state half reaches `glTexParameter*`; the sampler-state half exists in the sampler cache. Its §6 sampler-failure row, line 1778, then clears units and demotes the affected estate to `NONE` without replaying that omitted state. These two instructions are not a behavioral no-op when combined.

A concrete case is a live texture whose effective owner parameters specify NEAREST and CLAMP_TO_EDGE while A1 has left the texture object’s corresponding parameters at their defaults or an earlier value. A later sampler-creation failure demotes the estate; binding sampler zero exposes the texture object’s stale parameters. The [Khronos `glBindSampler` reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBindSampler.xhtml) explicitly states that sampler zero reactivates the bound texture’s own sampler state. Correctly deriving `SamplerKey` does not repair that fallback.

**Required change:** Specify a render-thread demotion transition that reapplies the complete authenticated current owner parameters to every affected live owned texture, or rebuilds/contains the estate if that cannot safely complete, before admitting draws under `NONE`. Preserve borrowed-object restrictions and existing retirement accounting. The failure and equivalence procedures must cover a transition after successful sampler-backed use, not only startup under `NONE`.

### C14-2 — Give sampler clearing an actual consumer before fixed-function drawing

**Evidence:** Phase 14 §4.1.4, lines 648–653, requires unknown and `Unused` units to use sampler zero while restricting sampler work to Phase 5’s existing physical operation. Phase 5 §4.12.3, lines 2416–2427, calls `TextureService.bindToUnit` only for `BoundObject` rows. Its non-final `FixedFunctionEmpty` path uses purpose `NONE` and no shader bindings (lines 2380–2384). Phase 7 §4.4, lines 1140–1156, explicitly permits `FixedFunction` activation and drawing inside an open frame. Thus the existing bind decorator receives no call for the very rows that need the required clear.

Phase 14 §4.1.5, lines 674–683, places the clear at the frame’s outermost `finally`; that is too late for a shader-to-fixed-function transition within the same frame. Moreover, the existing facade does not expose an outer-frame scope for the backend to wrap merely by asserting that it “rides” Phase 7’s finalization guarantee.

**Required change:** Name the concrete receiving dispatch that clears sampler bindings before every fixed-function/vanilla draw and on all finalization exits. This may use an explicitly specified existing fixed-function backend operation and/or an adopted normalization hook, but it cannot remain an implied frame callback. Reconcile `Unused` handling with Phase 5’s actual `BoundObject`-only loop, and keep mutation-free `Rejected`/`Degraded` preflight unchanged. Verify the specified sequence shader draw → declaration-empty fixed-function draw → continued frame, not only sampler-zero state after frame completion.

**Receiver note:** This is a Phase 14 correction. Any required P1/P5/P7 receiving-contract change must be explicitly requested/adopted in §5; this review does not grant it.

### C14-3 — Separate facade evidence from native-backend measurements

**Evidence:** Phase 14 §4.7.1 expressly chooses a facade-verb `GLCallLog`; §4.7.3, lines 1462–1463, nevertheless predicts that backend-only A1 eliminates recorded `setParameters` and filter set/restore calls. The caller is unchanged by §4.1.2, so those facade invocations remain. Ledger L-2 then asks that instrument to count actual `glTexParameter*` calls. Section 8.2, lines 1962–1967, also requires sampler-range/per-unit native bind counts and backend demotion injection from `RecordingGLDevice`.

Phase 1 §4.7.5, lines 3896–3995, defines a recorder of facade invocations with synthetic handles and query responses, not an observer of LWJGL backend internals. It explicitly distinguishes one facade event from multiple raw-GL events. No sampler service is added by Phase 14 §2.3, and `ScriptedResponses.glError` does not execute the native sampler cache merely because it queues an error result. Identical facade logs are useful semantic evidence, but they cannot demonstrate removal of native traffic hidden beneath those same calls.

**Required change:** Keep facade-log assertions for facade behavior, but provide an explicit backend-native observation/fault-injection procedure or driver-capture method for A1/A2 mechanics, native churn and native failure transitions. Correct the before/after predictions and ledger decision points so an unchanged facade trace is not mistaken for a failed optimization or native equivalence proof. If a new observation seam is necessary, request it through §5 rather than silently extending P1’s recorder.

### C14-4 — Correct the EMA comparison oracle and capture its inputs

**Evidence:** Phase 14 §4.3.6, line 992, claims the exact bound `B = max(|raw_i − s_i| × (1 − 2^(−Δt_i/h)))`, where `s_i` is the already-updated synchronous output. Phase 6 §4.5, lines 987–1012, instead gives the update from the prior accumulator: `s' = s + α(raw − s)`.

The stated bound fails a simple valid delayed sequence. After warm-up at depth zero, take one tick, half-life one tick, and a raw step to one. The synchronous output becomes 0.5, while a correctly one-frame-delayed source still produces zero, so the difference is 0.5. The proposed bound is `(1 − 0.5) × 0.5 = 0.25`; later samples of the same step only reduce that right-hand side. The procedure therefore rejects a correct delayed implementation. The legal `h=0` case also needs an explicit branch rather than division by zero.

Additionally, lines 958–963 define the captured trace as only `(frameId, uploadedValue)`, which does not contain the raw depth needed to evaluate C2.

**Required change:** Replace C2 with an oracle derived from the actual P6 recurrence and the granted source-age behavior, including initialization, variable smoothing deltas, zero half-life and unavailable/repeated samples. Specify how the required raw samples and timing/age inputs are obtained, or use a controlled replay that supplies them without inventing a public runtime API. Preserve the separate perceptual comparison and the existing P6 grant gate; an algebraic failure must not be classified as measured imperceptibility failure.

### C14-5 — Flush the producer context before publishing a shared fence

**Evidence:** Phase 14 §4.4.2’s handoff diagram, lines 1056–1063, publishes `CompiledShader(...,sync)` immediately after `glFenceSync`, and rule 3, lines 1077–1081, contrasts the fence with `glFlush` without requiring producer flushing. The upload rule uses the same sequence. This worker has no swap loop that otherwise guarantees submission after an isolated job.

The [Khronos ARB_sync specification, §5.2.2](https://raw.githubusercontent.com/KhronosGroup/OpenGL-Registry/main/extensions/ARB/ARB_sync.txt) explains that an unflushed fence command may never signal and that flushing a different context does not submit the issuing context’s command stream. A render-context `glWaitSync` is a visibility/order barrier, not a producer flush. Its nonblocking CPU return does not prevent the render GPU stream from waiting indefinitely.

**Required change:** Require a worker-context flush after inserting the completion fence and before publishing the completion message, for both compile and texture-upload work. Specify matching ownership/disposal on failed or canceled publication. Make the isolated-job-then-idle-worker case part of OQ-15’s proposed progress test. Keep A4 gated; no currently synchronous caller must be migrated by this correction.

### C14-6 — Do not equate watchdog expiry with safe context destruction

**Evidence:** Phase 14 §4.4.2, lines 1085–1089, promises that a hung worker is torn down and the build restarts synchronously, costing only a late switch. Section 4.4.3 repeats teardown on watchdog/probe failure. The context is made current on the worker and remains there (§4.4.2’s diagram). Section 5.9, lines 1703–1710, correctly requires exactly-once disposal only after worker acknowledgement and explicitly forbids deleting objects still in use; the unconditional timeout ladder does not explain how that requirement can be met by a worker hung in a native call.

[GLFW’s `glfwDestroyWindow` contract](https://www.glfw.org/docs/latest/group__window.html#gacdf43e51376051d2c091662e9fe3d7b2) requires destruction on the main thread and requires the context not to be current on another thread. Timeout alone establishes neither safe detachment nor worker quiescence. Waiting indefinitely for acknowledgement also contradicts the promised bounded recovery.

**Required change:** Distinguish cooperative cancellation/acknowledged shutdown from a genuinely unresponsive worker. Require worker detachment acknowledgement before context destruction; define retention/quarantine and fail-closed behavior for unresolved context/jobs rather than claiming they can always be torn down and replayed in-session. Align the failure ladder and proposed fallback test with the existing §5.9 lifetime gate. Exact future P4/P7/P13 adoption remains separate.

### C14-7 — Receive P4’s current registry fingerprint domain

**Evidence:** Phase 14’s active schema21 receipt, lines 1723–1725, and active decision D-P14-25, line 2284, identify `RegistryFingerprint/own-build-v1` as the current carried domain. Current Phase 4 §4.11, lines 1820–1824, explicitly supersedes that domain with `RegistryFingerprint/positional-route-v2` and invalidates old cached candidates, fingerprints and selectors. This is not a complaint about the explicitly historical schema20/19 receipts: the stale statement is in the active receipt.

**Required change:** Update the active P14 receiver receipt and its current identity-gate expectations to P4 D-P4-33’s `positional-route-v2` domain. Preserve D-P4-31 and earlier domains as historical provenance only, carry the new identity opaquely, and retain rejection of old identities without upgrades. No P3 schema increment or P14 reimplementation of registry hashing is needed.

## Notes — not additional corrections

1. **Existing resolutions remain resolved.** The current complete P1 texture-parameter/sampler split and P13 owned-source conversion are received; sampler zero is reserved for genuinely unknown foreign/incomplete state, not used as a substitute for implementing owned texture semantics. P1 D-P1-54 debug activity/placement is also received. The explicitly superseded historical objections to those grants are not re-raised.
2. **Optional absence is not itself a defect.** P4 compiler splitting, P7 resumability, P13 stageable upload, and P5/P1 sampler batching remain ungranted and the current synchronous/per-unit or `NONE` paths remain authoritative. Likewise no compiled P11 evaluator is required absent the specified measured miss. The v0.5 stall-gate interpretation in §9.2 is already openly identified as an upstream clarification request; DESIGN’s measurable-reduction gate and its failed-spike-does-not-stall rule should remain visibly unresolved rather than being treated as measured success. This review does not invent a new one-frame numerical gate.
3. **Evidence limitations are preserved.** The historical `reference-src/pintonium-9c2fcc1/` checkout was unavailable. Permitted mechanism-only files in the available `reference-src/Pintonium-main/` checkout were inspected where useful, but that checkout was not substituted as proof of the historical commit. The external primary specifications above support the sampler, sync and GLFW corrections. Historical source citations and historical reviews remain evidence of their original sessions, not fresh runtime results.

## §5 impact and readiness boundary

**section5Impact is true:** C14-7 directly corrects the active §5.9 receiver identity; C14-2 needs an explicit normalization/binding receiver contract or precise existing backend dispatch; C14-3/C14-4 must identify any newly required measurement seam instead of assuming it; and C14-6 must keep the watchdog design consistent with §5.9’s acknowledged-disposal lifetime requirement. These are owner corrections with limited receiver follow-through, not authorization to edit or certify siblings.

After integrating these corrections, Phase 14 needs a fresh whole-owner review for a literal PASS. This review grants no implementation permission, no runtime/OQ closure, no certification of other phases, and no final §G5.3/IR-01 clearance.

## Resolutions — 2026-09-08 owner correction, unverified

Original review body and verdict above are preserved. These are documentation dispositions,
not a fresh review result, runtime evidence, OQ closure or implementation permission.
No validation commands, builds, tests, formatters, linters or runtime experiments were run.

- **C14-1:** P14 §§4.1.2/4.1.7/5.10/6/8.2 and D-P14-26 require retained complete
  authenticated owner parameters, draw exclusion during replay, full object-state restoration
  before clearing/committing NONE, or owner containment/rebuild. Borrowed restrictions and
  live/retiring references survive. Evidence explicitly covers failure after successful use.
- **C14-2:** §§4.1.4–5/5.10 receive the coordinated P1 prepareUnitBindings(mask) seam,
  called by P5 after full successful preflight before BoundObject binds, and native clearing
  in P1 useFixedFunction with existing P4/P7 terminal releases. No backend frame callback or
  batching self-grant. §8.2 includes shader→empty fixed-function→continued-frame evidence.
- **C14-3:** §§4.7.3/7.5/8.1–3 distinguish unchanged facade invocations/model checks from
  actual LWJGL backend capture, native state queries and temporary backend-local fault
  injection. Recorder glError is not native-cache execution; ledger and checklist no longer
  treat facade counts as native churn or unadopted shadow simplification as a measured win.
- **C14-4:** §4.3.6 replaces the invalid one-step bound with two independent exact P6
  recurrence replays over explicit raw/timing/age/status/reset schedules, covering initialization,
  variable/zero deltas, h=0, unavailable/repeated samples and the 0→1 step. Existing SPI/input
  replay needs no new public API; native age evidence is separate. C1/C2 are correctness,
  not imperceptibility results; the P6 age grant remains gated.
- **C14-5:** §§4.4.2/5.9 require fence→producer-context flush→publication for compile and
  upload, nonblocking completion admission, and inventoried ownership on failed publication.
  §10.1 adds isolated-job/idle-worker progress evidence. Async grants remain unadopted.
- **C14-6:** Watchdog expiration terminal-fails logically but does not dispose resources.
  §§4.4/5.9/6/8/10 require quiescence and worker-detachment acknowledgement before
  main-thread destruction; otherwise quarantine uncertain worker/context/jobs/objects and
  prohibit replay/false successful shutdown. Unresolved quarantine disqualifies driver
  allowlisting; isolated-process adversarial evidence never force-kills a worker for cleanup.
- **C14-7:** §5.9/D-P14-29 receive P4 D-P4-33 RegistryFingerprint/positional-route-v2
  opaquely, rejecting older identities without reconstruction/upgrades. Separate coordinated
  P3 semantics require current-constant schema22 admission; numeric21/20/19 and own-build-v1
  receipts are historical. projectionVersion=1, nine trees and unrelated asset/native/option
  contracts remain intact.

**Receiver follow-through:** Main owns reciprocal P1/P5 normalization/fixed-function declarations,
P4/P7 existing terminal dispatch receipts, and P1/P5/P13 replay/containment receipt. Future
P4/P7/P13 async proposals must include producer flush and acknowledged disposal/quarantine;
they remain optional/ungranted. P2 receives separate source-free native/facade/replay evidence,
not a new recorder or runtime observation API. Fresh whole-owner review remains required.