# Phase 5 Architecture Review 40

## 1. Identification and verdict

- **Target:** `docs/phase5/v1/PHASE_5_DOC.md`, current complete document, §§0–12.
- **Assigned SHA-256:** `a1f2239ebf8b3aa0c2a9592026e1af5c282e26ecb0a3e68397dffb2f4bc01418`. This is the assignment identifier; no checksum command was run.
- **Intended preserved report:** `docs/phase5/reviews/PHASE_5_REVIEW_40.md`. This reviewer did not write that file or edit any document.
- **Governing design:** the target header selects `docs/design/v2.0-RC3/DESIGN.md`, not v3. Part I and the Phase 5 assignment, including its literal doc gate, were reviewed.
- **Verdict: PASS-WITH-CORRECTIONS.**
- **Counts:** blocking **0**; corrections **2**; notes **2**.
- **§5 impact: yes.** C40-1 requires an amended exported operation/result contract and corresponding receiving branches. C40-2 requires eliminating contradictory incorporated publication semantics.

The established ownership and lifecycle architecture does not need rebuilding. Two bounded corrections remain: a mutation-bearing failure during snapshot preparation has no legal result, and active publication/checklist prose still requires the cross-stage resource equality that the new evidence protocol correctly forbids. This is not literal PASS, verified dependent consumption, or final integration clearance.

## 2. Review method, inputs, and evidence limits

This was independent whole-document architecture verification, not a patch-only review. The current target was read completely, including the historical header addenda, current signatures, algorithms, failure table, test plans, milestones, decisions, handoffs, and implementation checklist. Historical assertions were not treated as successful tests or fresh verification.

The review read:

1. `AGENTS.md`, `docs/MOVES.md`, the execution-wording migration overlay, RC3 Part I, and RC3's complete Phase 5 assignment at lines 1572–1685.
2. `docs/research/v1/RESEARCH.md` §§0–1, assigned §3.6.3, §4.1, §4.3, Appendix B, and relevant extra sampling/flip/custom-texture contract material.
3. The assigned Pintonium design §5, relevant B4/B13 and divergence rows, and the narrow fixed-unit evidence used by the target.
4. The complete binding §5 regions of current P1 v14, P3 v1, and P4 v1, together with relevant actual facade, resource, routing, parameter, selector, and lifecycle definitions incorporated by those contracts.
5. Actual receiving mechanisms in P7's frame-begin, gbuffers, fullscreen, depth-copy and evidence paths; P6's fixed-sampler participant; P8's shadow execution, terminal release and cleanup path; P13's overlay publication/lease/resize lifecycle; P2 v2's current `/4` evidence acceptance; and P14's explicitly ungranted optional batch boundary.
6. **All of `docs/PHASE_INTEGRATION_REVIEW.md`**, including the original report, resolutions and every appended dated follow-on. These were historical assertions and authority/disposition records, not independent PASS evidence.
7. The shipped G6 buffer/format/type author documentation and the target-disclosed narrow behavioral digest ranges. Only behavioral facts were used from the latter; no decompiled implementation was used as code or architecture structure.

The historical local `reference-src/pintonium-9c2fcc1` directory was unavailable. The corresponding permitted files were independently recovered from the actual `Xplodin/Pintonium` **9c2fcc1** source pin: every Java file in the assigned `targets/` directory and its actual `backed/` listing, the depth replacement mixin, the relevant shadow target implementation, depth-copy/format support, and narrow fog-comment evidence. No moving `Pintonium-main` checkout was substituted for that pin. The pinned root license was also read. Minecraft `Framebuffer` descriptors and SRG names were independently resolved with the Cleanroom mappings tool.

No build, test, linter, formatter, checksum, runtime experiment, or implementation validation was performed. No forbidden chatlog/transcript content or Oculus/transformation-library implementation was read or searched. Source searches used explicit file paths or the assigned source directory, not repository-wide discovery.

## 3. Correction findings

### C40-1 — Give snapshot-preparation backend failure a legal result and receiver transition

**Severity:** correction. **Confidence:** high. **§5 impact:** yes.

**Exact locations:**

- P5 §2.2, lines **854–857**: `PassSnapshotResult` contains only `Acquired` and `Rejected`.
- P5 §2.2, lines **882–895**: acquisition/rejection rules require rejection to perform no GL and leave the open token and flip state unchanged.
- P5 §4.2.1, lines **1350–1353**: before a write, P5 advances the side's write revision and restores its base filter; failure aborts before the write, and **snapshot preparation performs this work for write sides**.
- P5 §5.1, lines **2474–2476**, incorporates the snapshot and mipmap result contracts.
- Actual receivers: P7 §4.4's snapshot-acquisition step and §4.6 steps 1–3, approximately lines **1090–1093** and **1192–1210**; P7 §5.4 incorporates these P5 contracts.

**Claim and evidence:** The new freshness policy correctly makes base-filter restoration mandatory before writing a previously mipmapped side, but assigns that fallible native operation to `snapshot`. P1's actual `TextureService.setParameters` contract at `docs/phase1/v14/PHASE_1_DOC.md:4275–4285` explicitly permits native failure, exposes it through error draining, and invalidates a partially applied parameter value. P5's acquisition return type cannot describe this event: returning `Acquired` would expose a pass after failed preparation; returning `Rejected` would falsely claim zero mutation and an unchanged frame; throwing contradicts P5 §6's no-buffer-failure-through-the-driver boundary. The existing `MainMipmapResult.Failed(..., true)` is not a solution because that is returned by a different, later operation, and P7 invokes it only for fullscreen shader snapshots.

**Concrete affected sequence:** A deferred shader requests mipmaps of colortex0 and explicitly disables its automatic flip. Its readable main side can therefore retain the enabled mipmap filter when the following translucent gbuffers pass prepares to write that same main side. P5 must restore the base filter during the next snapshot preparation. If that setter/restoration fails, the design requires aborting before drawing, but the actual acquisition boundary supplies neither a backend-failure variant nor a defined already-aborted disposition to P7. The same problem exists whenever a snapshot's required write-side preparation encounters an observable backend error; this is not dependent on a guessed implementation.

**Impact:** The producer and consumer cannot simultaneously implement the published success, mutation-free rejection, no-throw, and abort-on-restoration-failure guarantees. Generic recovery can also attempt another abort when the owner has already consumed the frame, because P7's explicit no-second-abort branch currently recognizes only `MainMipmapResult.Failed`.

**Required correction:** Close this transaction at an actual callable boundary. Either give snapshot preparation a distinct mutation-bearing failure result, with exact frame/snapshot validity, ownership, full-clear and shaders-off disposition, or move all such preparation to a separately result-bearing operation that both gbuffers and fullscreen callers must execute before drawing. Preserve mutation-free `Rejected`; do not encode backend failure as protocol rejection or success. Update P5 §§2/4/5/6/8/12 and P7's ordinary, nested-resume and fullscreen receiving branches, including outer-finally behavior and independently owned lease cleanup. Define whether the failure has already aborted the frame so the receiver does not complete, discard or abort it twice.

**Required architectural acceptance case:** After a successful mipmap enable, inject failure in the next write-side base-filter reset. The operation must return its defined failure, perform no draw/new flip, retain exactly the stated cleanup responsibilities, require safe recovery, and never fall into a second lifecycle call on an already-consumed frame. This is a requested future case, not an executed test.

### C40-2 — Remove active plan-to-runtime equality requirements from publication and gates

**Severity:** correction. **Confidence:** high. **§5 impact:** yes, through the publication/inspection semantics incorporated from §2.2; the established staged wire schema need not change.

**Exact locations:**

- P5 §2.2, lines **716–719**: candidate `inspection()` exposes the same `BufferResourceSnapshot.Available` “derived by planning.”
- P5 §9, line **3007**: the resource-projection milestone requires “identical plan/candidate/runtime evidence.”
- P5 §12, line **3270**, item 27: the publisher test hook still requires “plan/candidate/estate equality.”
- The conflicting correct contract is P5 §4.1.1, lines **1237–1273**, especially **1269–1273**, and §5.1, lines **2470–2472**.

**Claim and evidence:** D-P5-32 introduces a real discriminant and different required data. Pure planning returns **PLANNED** with no allocation payload. A successful candidate and its accepted estate return **REALIZED**, with actual allocated format and allocation origin for every color row. Consequently these complete values are unequal even when the requested format succeeds; whole-estate RGBA fallback makes the allocation distinction additionally substantive. Nevertheless the active public-shape explanation, milestone and implementation checklist still instruct an implementer to propagate/compare one identical plan/candidate/runtime value.

The actual receivers now enforce the new distinction rather than masking it. P7 §4.13 at `docs/phase7/v1/PHASE_7_DOC.md:1847–1862` copies REALIZED from the paired accepted estate and permits cross-stage comparison only for declared fields. P2 v2 §4.5.4 at `docs/phase2/v2/PHASE_2_DOC.md:1149–1187` rejects PLANNED as complete live evidence and forbids allocation keys in PLANNED; its inspection adapter at **1918–1930** requires PLANNED and explicitly rejects full cross-stage equality. Thus this finding is grounded at both consuming dispatch/validation points, not just in matching type names.

**Impact:** Following the old public-shape sentence yields PLANNED live publication rejected by the capture protocol; following the old checklist equality yields an impossible acceptance assertion or pressure to fabricate allocation facts during pure planning. The later dated withdrawal of the old rule is useful authority, but does not make these still-active implementation instructions consistent.

**Required correction:** State in §2.2 that successful creation builds a new REALIZED projection from the deterministic plan plus successful allocation/fallback outcome, and that candidate-to-accepted-estate equality is exact. Change §§9/12 to require same-input PLANNED-to-PLANNED equality, successful candidate-to-estate REALIZED equality, and declared-field-only cross-stage comparison. Keep historical addenda and review bodies unchanged. Update the corresponding §5 publication/inspection incorporation or explicit change receipt so consumers have one contract. Do not change the correct `/4` stage or conditional-field grammar.

**Required architectural acceptance case:** A pure plan and a successful candidate for identical inputs must differ in evidence stage and permitted allocation fields; a fallback candidate must preserve requested formats while reporting RGBA fallback for every realized color row; publication must preserve that candidate's REALIZED value exactly. P2/P7 should require no compensating reconstruction.

## 4. Notes

### N40-1 — Preserve the independently verified pin and licensing qualifications

**Severity:** note. **§5 impact:** no.

The recovered [pinned `ShadowRenderTargets.java`](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shadows/ShadowRenderTargets.java) actually toggles `flipped[target]`, and its color accessor chooses alt/main through that state. This independently supports P5 D-P5-35's qualification of historical PD/RC3 B4 wording. It does **not** establish correctness of the entire shadow pipeline. The actual [pinned root LICENSE](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/LICENSE) begins **GNU GENERAL PUBLIC LICENSE, Version 3**, not LGPL. P5's later receipt at lines **3124–3127** already retains a GPL provenance warning; that qualification must govern future reuse rather than the blanket LGPL wording retained in the original input/provenance ledger at lines **57–62**. File-specific inherited notices and bundled dependency licenses remain separate questions. No source was copied, so this is not a demonstrated incorporation violation or new blocking finding.

### N40-2 — Correct the leftover three-component sizing sentence

**Severity:** note. **§5 impact:** no new interface amendment is required for this wording-only correction.

P5 §2.2 line **710** still says `BufferSizing` equality covers “all three components.” Its declaration at lines **498–500**, §4.11.1, §5.1 line **2471**, and the approved option-only cutover all consistently define exactly **two** components: `mainExtent` and `shadowExtent`. Actual P2 sizing consumption also uses that two-field shape. Change the leftover sentence to two components during fix-up; do not recreate an SSAA field, accessor, default or compatibility alias. This is not evidence that the current declared API still has three fields.

## 5. Literal doc-gate and conformance audit

| Required check | Independent result |
|---|---|
| App B.3 fixed table, including depthtex1 unit 11 | Present in §4.12.1 and consistent with the controlling research ruling: depthtex1 is 11; fullscreen depthtex2 is 12; gbuffers 12 is unused. Conditional `shadow` uses the effective provider's direct compatible watershadow declaration, not shadow count. |
| App B.1 color identities, allocation, clear and flip rows | Mapped in §3.1; four-buffer baseline, scan-driven growth to eight, paired storage, gbuffers-main versus fullscreen-alt writes, explicit/virtual transitions and post-success flips have concrete mechanisms. |
| App B.2 depth/shadow rows | Mapped in §3.2; real borrowed depthtex0, copied-depth fallback, weather-before-translucent consumption, independent shadow color/depth demand, real shadowcolor state and terminal neutral behavior are specified. |
| App B.4 formats and transfer vocabulary | §3.4/§4.2 enumerate the 37 pack-facing formats, separate private plain-RGBA representation, integer transfer/filter branch, pixel formats and pixel types. DefaultRgba remains distinct from explicit RGBA8. |
| Explicit clear/flip state machines | §§4.4/4.6 specify side selection, full-clear override/consumption, successful-completion flips, metadata rebase and abort normalization. C40-1 concerns the new fallible preparation edge, not absence of the machines. |
| Recorded frame-end decision | D-P5-4 checks App F.7 and rejects reference copy-back in favor of metadata rebase. No reference mechanism silently overrides the selected contract. |
| Fog alpha 1.0 | Explicit in clear and resource policies. Both narrow pinned pipeline comment sites and clear construction independently retain the stated Sildur's pink-reflection rationale. |
| depthtex0 replacement traced to reference | Pinned mixin and P1 borrowed-depth authentication/attachment operations were read. Cleanroom mappings independently confirm `createFramebuffer(II)V`, `func_147605_b`, `useDepth/field_147619_e`, and `depthBuffer/field_147624_h`. |
| Real shadow flips rather than historical stub claim | Local generic flip semantics are real architectural requirements. Current pin qualification is retained; reference comments do not certify shadow execution. |
| Pintonium do-not-inherit boundaries | Dynamic units, unconditional sixteen-color allocation and alt-to-main copy-back are not adopted as pack contract. No transformer or unverified expression implementation was used. |

## 6. Whole-document and receiver/lifetime audit

All thirteen required sections are present and substantive. Section 0 selects the governing revision and distinguishes historical evidence; §1 separates adjacent ownership; §§2–4 define values, operations and algorithms; §5 exposes/consumes contracts; §§6–7 cover failure, threading and ownership; §§8–9/12 stage future evidence rather than claiming it ran; §10 correctly assigns no Phase 5 OQ; §11 records decisions, approved scope corrections and remaining gates. C40-1 and C40-2 identify the remaining inconsistencies in those otherwise complete sections.

The following boundaries were traced through their actual architectural receivers:

- **P1 → P5:** ordinary foreign versus authenticated borrowed depth permissions, first versus steady copy, depth-only versus combined attachment, complete owned texture parameters and binding restoration. P5 does not gain raw GL names or foreign mutation authority.
- **P3/P4 → P5:** current schema admission, immutable resource/format inputs, exact effective selector, positional None holes, dense physical attachment packing, symbolic-route expansion and opaque registry identity. No P5 source parser or fallback resolver is introduced.
- **P5 → P7:** selection → snapshot → mipmap where applicable → physical binding → activation → draw → completion; virtual transitions, balanced draw-buffers-none lease, depth refresh and consumed degraded copy points. The snapshot-preparation failure boundary is C40-1; the later main-mipmap failure has an explicit already-aborted receiver branch.
- **P5/P13/P7 binding lifetime:** only Bound transfers the overlay lease; all other outcomes leave caller ownership. Invalidated snapshots still require closure. Retired publications reject use while genuine leases delay deletion; no retired owner becomes an active fallback.
- **P5 → P8:** full sixteen-row shadow binding, same selector/context, per-buffer mipmap degradation versus result-level neutralization, no repeated lifecycle call after owner-aborted neutralization, terminal fixed-function release before main continuation, and retained compatible neutral backing.
- **P5 → P2/P7 evidence:** current receiving grammars distinguish planned declarations from realized allocation. C40-2 is producer-document residue, not a recommendation to weaken those receivers.
- **P5 → P14:** optional MULTI_BIND remains ungranted and disabled without the owner-controlled batch boundary. Complete synchronous parameters do not automatically approve optional sampler/async execution.

Publication acceptance, provenance rejection, consecutive off publication after consumer failure, preceding-success `deliveredCount`, reverse cleanup and borrowed-handle non-deletion are concrete architecture obligations. No additional structural omission requiring a whole-document rebuild was established.

## 7. Scope, open questions, and implementation evidence

The maintainer's recorded **pack-option compatibility only** disposition controls `superSamplingLevel`; historical research/design labels are not an engine SSAA algorithm. The target preserves pack source/option behavior without adding extent multipliers, jitter, repeated world draws, resolve, AA UI or an inferred sampling resource. U1 likewise removes only unspecified filter/wrap key suffixes; it does not remove numeric discriminators or Phase 13 sidecar ownership.

The architecture retains the classic conformance obligations, static and camera-path scenes, source/image licensing restrictions, and planned recorded-GL leak/lifetime coverage. None was executed here. Live framebuffer completeness, first/steady depth-copy behavior on actual drivers, full mipmap/filter restoration, neutral sampling, mixin application, temporal image parity and complete classic matrix gates remain implementation/runtime evidence obligations—not failures merely because this read-only review did not run them.

## 8. Required closure

Apply C40-1 with its actual P7 receiving transition, remove the contradictory active evidence-equality clauses under C40-2, and preserve the note qualifications. Record the resolutions without rewriting historical authority or review bodies. Because §5 and incorporated semantics are affected, obtain a fresh review of the amended owner and affected receiver contracts before verified consumption. This report supplies neither dependent-wide clearance nor the final §G5.3 integration verdict.

## Resolutions

### 2026-09-08 — Owner fix-up, unverified

The original review above is preserved verbatim. This is an owner change receipt, not a
replacement verdict, executed acceptance case, literal PASS, or verified dependent consumption.

- **C40-1 — owner contract corrected.** P5 §§2/4/5/6/8/9/11/12 now define
  `PassSnapshotResult.Failed(BufferFailure failure,String diagnosticId,boolean frameAborted)`.
  Mutation-bearing write preparation (including base-filter reset, binding restoration and
  contained backend exceptions) returns it with `frameAborted=true`: no snapshot is exposed,
  the frame is consumed, old pass/binding snapshots are invalid, no draw/new flip occurs, and
  the stale estate requires full-clear/shaders-off recovery and safe-point replacement.
  `Rejected` remains pre-mutation and `Acquired` requires complete successful preparation.
  Even failed internal cleanup cannot convert this result into success/rejection/throw.
  Caller-owned bindings, overlay leases, P4 activity and local-state scopes retain independent
  finally cleanup; invalidation neither transfers nor discharges their ownership.
  The exact P7 handoff is §4.4 ordinary acquisition and nested child/parent-pop reacquisition,
  §4.6 fullscreen acquisition before mipmaps and terminal cleanup, plus outer finish/abort/finally
  and §5 incorporation. A shared terminal marker bars parent resume, remaining composites/final,
  and repeated complete/discard/commit/abort on the consumed frame. P7's owner performs those
  receiving edits; this receipt does not assert that independent amendment is verified.
- **C40-2 — active equality instructions corrected.** P5 candidate inspection now derives a new
  REALIZED projection from the deterministic plan and successful allocation/fallback outcome.
  §5, publication proof, milestones and implementation checklist require equal-input
  PLANNED-to-PLANNED equality, exact successful candidate-to-accepted-estate REALIZED equality,
  and declarative-field-only cross-stage comparison. Off publishes Unavailable(SHADERS_OFF),
  never candidate allocation facts. Requested formats survive fallback while every realized color
  row reports actual fallback allocation/origin. Existing `/4` staged grammar is unchanged.
- **N40-1 — qualifications retained.** The pinned GNU GPL Version 3 license qualification
  governs future reuse rather than the historical blanket LGPL label. Pinned flip/accessor
  behavior qualifies historical B4 negative wording without certifying the shadow pipeline.
  Active test/checklist wording now tests real local flip behavior, not the historical attribution.
  Historical authorities, quotations and review bodies remain unchanged; no source was copied.
- **N40-2 — wording corrected.** `BufferSizing` equality is over exactly `mainExtent` and
  `shadowExtent`; no third component, SSAA API or compatibility alias is introduced.

Grounding used the header-selected design/research and actual P1 parameter/error contract and
P7 acquisition/lifetime receivers. No validation commands, builds, tests, formatters, runtime
experiments or new source inspection were run. The base-filter-reset failure and staged fallback
cases are explicit future acceptance obligations in P5 §8, not claimed execution evidence.
Fresh whole-document owner and receiving-contract review remains required. IR-01 and final
§G5.3 remain open.