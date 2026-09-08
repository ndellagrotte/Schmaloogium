# Phase 14 — Attempt 6, Round 3 Whole-Owner Architecture Review

## Frozen owner and scope

- **Owner:** `docs/phase14/v1/PHASE_14_DOC.md`
- **Frozen SHA256:** `073ed21a715217a0369450e9f38840c6b0a6af75901c35048d08c985cef48d94`, as identified by `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_6.json`.
- **Scope:** the entire current owner, sections 0–12, including A1–A7, all cross-phase interfaces, failure/lifetime rules, threading, evidence plans, milestone gates, OQ-15/OQ-22 specifications, decisions and implementation checklist. This is a whole-owner review, not merely verification of the latest correction receipt.
- **Authority:** the owner's selected `docs/design/v3/DESIGN.md`, especially Part I and the Phase 14 specification at lines 2514–2588; governing `docs/research/v1/RESEARCH.md`; repository restrictions; `docs/MOVES.md` and `docs/tooling/CODEX_MIGRATION_OVERLAY.md`. Dependency contracts retain their own selected governing revisions. Historical reports and the owner's initial-build narrative were not treated as proof of current correctness.

## Independent checks

1. **Sampler state and ordinary fixed-function transitions.** Traced the complete authenticated object-baseline requirement in `docs/phase14/v1/PHASE_14_DOC.md:626–644`, ordinary FINAL clearing at lines 708–747, and the current §5 receipts. The material distinction is now correct: sampler objects do not eliminate the texture object's sampling state, and ordinary sampler-zero use must expose the latest successful owner parameters. Reciprocal receivers include `docs/phase1/v14/PHASE_1_DOC.md:4466–4474`, `docs/phase5/v1/PHASE_5_DOC.md:2608–2609`, `docs/phase7/v1/PHASE_7_DOC.md:3429–3434`, and `docs/phase13/v1/PHASE_13_DOC.md:1852–1861`. The P5-bind → P4-activate → draw boundary remains intact, rather than being repaired by an unauthorized P7 rebind.
2. **DSA and target-bearing values.** Checked the creation/edit-only strategy, exclusion of DSA unit binding, binding neutrality, authenticated conversion and mandatory synchronous target-bearing receipt at `docs/phase14/v1/PHASE_14_DOC.md:780–885` and `1749–1766`, against P1/P5/P13's current allocation and parameter contracts. The grant does not authorize worker uploads or mutation of borrowed textures.
3. **Async ownership and both synchronization directions.** Read the complete A4 and §5.9 protocols, including main allocation/parameter completion → readiness fence → main-context flush → worker readiness, followed by worker upload → completion fence → worker flush → publication. The new incoming edge is received at `docs/phase7/v1/PHASE_7_DOC.md:3917–3924` and `docs/phase13/v1/PHASE_13_DOC.md:1868–1873`. Both sync objects remain accounted for through rejection, cancellation and quarantine. This agrees with the producer-context progress requirement in the [ARB_sync specification](https://registry.khronos.org/OpenGL/extensions/ARB/ARB_sync.txt); queue publication or an outgoing worker fence cannot substitute for the incoming edge. P4/P7/P13's current synchronous contracts remain authoritative because the split/resumability APIs are still ungranted.
4. **Readback consumer and evidence separation.** Followed `CenterDepthResult` to P6's sampling/smoothing behavior, including the finite depth contract, first-sample initialization and Unavailable retention (`docs/phase6/v1/PHASE_6_DOC.md:778–780`, `1040–1046`, `1050–1078`). The owner correctly leaves PBO enablement gated on an explicit P6 age grant and the separate mathematical/perceptual/native checks. The four corrections below concern the specified mechanism itself, not absent future runtime evidence.
5. **Debug, measurements and audit scope.** Checked KHR_debug capability/activity against the current P1 receiver, label/group algorithms, the P7 boundary request, P11's metrics handoff, allocation methodology, ownership-filtered redundant-state audit, and the distinction between facade recordings and native traffic. A6/A7 do not authorize vanilla/chunk optimization or sibling-owned call-sequence changes. Current schema23 and MaterializedSource-v23 receipts supersede earlier numeric history without widening P14 into a parser.
6. **Reference discipline.** Consulted the permitted Pintonium design digest and available implementation counterparts for sampler mechanics, DSA selection, debug groups and fence polling. Used the primary Khronos specifications to resolve native GL edge conditions; these checks do not certify any platform, implementation or pack tier.

## Required corrections

### C14-1 — Normalize pixel-pack state before the four-byte PBO transfer

**Severity: P2 — incorrect depth data or GL errors on the enabled readback path.** `docs/phase14/v1/PHASE_14_DOC.md:939–946` allocates exactly four bytes per slot, while lines `975–985` specify a one-float `glReadPixels` and restoration of framebuffer/PBO bindings but never establish the pixel-pack state that determines the transfer layout. A legal incoming `GL_PACK_SKIP_PIXELS=1` makes the one-float write start four bytes into this four-byte buffer, producing `GL_INVALID_OPERATION`; incoming `GL_PACK_SWAP_BYTES=true` instead reverses the float's bytes. The subsequent fence can signal even though the transfer failed, so mapping the slot is not proof that it contains the requested depth. These behaviors follow directly from [glReadPixels](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glReadPixels.xhtml) and [glPixelStore](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glPixelStore.xhtml). There is no inherited default-pack-state precondition in P6's source contract, and P1's `TextureData` layout concerns uploads rather than this new readback (`docs/phase1/v14/PHASE_1_DOC.md:3940`, `5273–5274`).

**Minimal owner fix:** specify a binding-neutral native readback scope that saves the relevant incoming pack state, establishes a canonical unswapped zero-skip layout for the four-byte transfer, and restores exactly the previous state and PBO binding on every exit. Make transfer/map/unmap failure incapable of publishing a Sample. Extend the planned native state/error checks to a non-default pack-state case. This belongs inside P14's backend mechanism and does not require a public free-standing pixel-store API.

### C14-2 — Clamp labels below, not to, GL_MAX_LABEL_LENGTH

**Severity: P2 — the debug feature itself generates GL_INVALID_VALUE.** `docs/phase14/v1/PHASE_14_DOC.md:1290–1293` requires truncation *to* `GL_MAX_LABEL_LENGTH`; the same boundary appears in `DebugLabelCoverageTest` at line `2142` and checklist item 12 at line `2801`. KHR_debug instead requires the label length, excluding the null terminator, to be **strictly less than** that limit. Thus a label reaching the prescribed clamp boundary is still invalid, leaves the intended label unapplied, and contaminates the GL-error channel the design explicitly intends to protect. See the ObjectLabel length/error rules in the [KHR_debug specification](https://registry.khronos.org/OpenGL/extensions/KHR/KHR_debug.txt).

**Minimal owner fix:** define the maximum submitted label length as `GL_MAX_LABEL_LENGTH - 1` GLchar bytes, accounting for the native string encoding rather than assuming a Java character count is a byte count. Update the corresponding test/checklist boundary so exact-limit and over-limit inputs produce a legal submitted label and no GL error.

### C14-3 — Drain virtual debug groups without issuing native pops for them

**Severity: P2 — the recovery path can recreate the stack-underflow bug it is meant to contain.** `docs/phase14/v1/PHASE_14_DOC.md:1308–1316` correctly distinguishes issued pushes (`depth`) from overflow pushes that issued no GL command (`virtualDepth`). But the frame-boundary rule at lines `1317–1320` checks their sum and then says to drain it to zero **with real pops**. If an exception/abort leaves overflow pushes outstanding, native popping cannot discharge those virtual entries: popping once per combined entry exceeds the number of actual pushes, while decrementing only real depth leaves virtual depth uncleared. KHR_debug generates `GL_STACK_UNDERFLOW` when the default group is popped. This is an explicitly planned overflow-plus-THROWN/abort case, not an unsupported caller pattern; the planned test at line `2141` also requires it to be safe.

**Minimal owner fix:** make the boundary drain explicitly consume/reset virtual entries without GL, then emit exactly `depth` native pops and reset real depth, or invoke the already specified virtual-aware `popGroup()` until both counters are zero. Preserve the single diagnostic. Name the leaked-overflow-plus-abort sequence in the planned balance test.

### C14-4 — Remove the unsupported two-frame warm-up guarantee

**Severity: P2 — the specified scheduler and the advertised result timing disagree.** `docs/phase14/v1/PHASE_14_DOC.md:981–982` promises that Unavailable warm-up lasts at most two frames. Yet the ring expressly permits `N=3` for late-signalling drivers at lines `939–949`; step 3 only accepts an actually signalled fence at lines `965–974`; and the timeout policy at line `1971` waits until an in-flight slot is older than N frames before resetting, with synchronous demotion only after three consecutive occurrences. A permitted run with no signalled fence on the third call therefore has no `last` sample and still returns `Unavailable`, contrary to the bound. Repeated timeout/reset can extend this further. P6 observably retains the old accumulator, or remains uninitialized before the first sample, during those additional unavailable frames (`docs/phase6/v1/PHASE_6_DOC.md:1040–1046`).

**Minimal owner fix:** describe warm-up as lasting until the first valid completion, bounded operationally by the explicitly stated timeout/demotion policy, rather than by an unconditional two-frame assertion. Keep controlled one-frame-ready expectations separate from late/stuck-fence behavior, and align the planned ring cases and C4's steady-scene warm-up terminology with that distinction. Do not satisfy the claimed bound by adding a blocking wait or inventing a depth value. The existing ungranted P6 age request remains ungranted.

## Notes

### N14-1 — Cross-owner receipt is present; certification remains separate

The current P1/P5/P7/P13 receivers substantively carry D-P14-31/32, including the two-direction upload dependency and ordinary sampler-zero baseline. P4/P7 compiler/resumability requests, P6's age permission and the separate multi-bind boundary remain proposals, as required by `docs/phase14/v1/PHASE_14_DOC.md:1582–1584`, `1628–1644`, `1668–1675`, `1842–1869` and `1946–1949`. This is a cross-owner integration observation, not a PASS for those siblings. Main must perform final integration against their fresh reports; no correction here grants an optional API.

### N14-2 — Historical pinned source evidence is not newly reproduced

`docs/MOVES.md:45–54` resolves the historical Pintonium rename to `reference-src/pintonium-9c2fcc1`, but that pinned directory is absent in this checkout. `reference-src/Pintonium-main` is available and supplied permitted corroboration. It was not represented as the missing historical commit or used to silently upgrade historical confidence tags. This limits reproduction of exact pinned-source observations, not the independently grounded Khronos findings above.

## Limitations and disposition

This was a read-only documentation/source investigation. No repository file was changed; no formatter, linter, build, test suite, GL experiment or runtime capture was run. The described counterexamples are specification-level traces, not claims of executed tests. No forbidden transcript/source tree or OptiFine decompilation was mined. The review grants no implementation clearance, OQ closure, runtime portability, pack-tier result or sibling certification.

**Section 5 impact: no required interface change.** The four fixes repair P14's detailed native algorithms and their planned checks while preserving existing SPI signatures, owner boundaries and the pending-grant disposition. They should not be used to widen P1/P6/P7 contracts implicitly.

**Final verdict: PASS-WITH-CORRECTIONS.** Required corrections: **4**. Notes: **2**. The architecture is not structurally failed: fallback, ownership and optional-extension boundaries are intact, and the attempt-5 load-bearing baseline/synchronization fixes are received. The four current mechanism defects prevent a literal PASS for this frozen owner. Apply owner-local corrections, preserve this report, and obtain the required fresh review and separate Main integration before clearing the architecture gate.

## Resolutions — 2026-09-08 owner-local correction

The frozen review above is preserved unchanged. The following are documentation corrections,
not executed scenarios or a replacement verdict:

- **C14-1 → D-P14-35:** §4.3.3 now saves actual read-FBO/PBO bindings and relevant pack
  alignment/row-length/skips/swap state, establishes the four-byte unswapped zero-skip layout,
  and restores on every exit. Publication requires successful transfer/fence/map/unmap and
  finite depth; failed candidates never become Sample. §§5.1/6/8.1–8.2/12 incorporate the
  contract, including nondefault skip/swap state and transfer/map/unmap failures.
- **C14-2 → D-P14-36:** §4.5.2 uses at most `GL_MAX_LABEL_LENGTH-1` UTF-8 GLchar bytes,
  with truncation at complete code-point boundaries. §§5.1/6/8/12 cover max−1/max/over-max
  and multibyte boundary inputs without expecting post-truncation name uniqueness.
- **C14-3 → D-P14-37:** §4.5.3 boundary recovery discards virtual entries without GL,
  then pops exactly outstanding real groups and emits one rate-limited diagnostic.
  §§5.1/6/8/12 explicitly cover leaked virtual overflow followed by THROWN/abort and
  a subsequent balanced frame without default-group underflow.
- **C14-4 → D-P14-38:** §4.3.3 warm-up lasts until first valid completion or timeout/demotion,
  not two frames. N=3 late/stuck behavior, >N age timeout, three consecutive resets and
  successful-completion counter reset are explicit. §4.3.6 separates the fixed five-frame
  measurement exclusion from readiness and prevents vacuous C4 success; §§5.1/6/8/12 agree.

No SPI signature, public pixel-store API, sibling document, schema23 identity or inspection
tree changed. P6 age, P4/P7 worker/resumability and P13 async-upload proposals remain
ungranted. No validation command, build, test, formatter, linter or runtime experiment was
run. Historical confidence and N14-2's pinned-source limitation remain unchanged; fresh
whole-owner review and Main integration are still required before architecture clearance.
