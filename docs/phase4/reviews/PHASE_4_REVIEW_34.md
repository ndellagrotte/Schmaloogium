# Phase 4 — Independent Whole-Document Architecture Review 34

**Verdict: PASS-WITH-CORRECTIONS**

**Owner:** `docs/phase4/v1/PHASE_4_DOC.md`  
**Assigned frozen SHA-256:** `2766e642ec736e49e4927a08ff7d1c05718be1998fc8ce3998c8d6f6dea22512`  
**Counts:** 0 structural blockers; 2 owner corrections; 2 notes.  
**section5Impact: true.** C34-1 requires an explicit cross-owner initialization/validation contract and corresponding §5 adoption. C34-2 can be corrected without changing the already-correct exposed duration-lock contract.

This is a prospective architecture review of the complete frozen owner, not an implementation certification or a review limited to the latest amendment. The document does not require structural rebuilding, but its real compiler path cannot currently admit an important legal sampler configuration, and its failure table contradicts its current duration-lock protocol. No files were edited and no validation commands, builds, tests, formatters or linters were run.

## Governing evidence and coverage

The owner was read in its entirety, including §§0–12 and all historical addenda. Governance was taken from its header: `docs/design/v2.0-RC3/DESIGN.md`, complete Part I (§G0–§G12), §G5.3, and the complete Phase 4 assignment at lines 1471–1571. `AGENTS.md` and `docs/MOVES.md` were read. Governing research reads covered `docs/research/v1/RESEARCH.md` §§0–1, 3.1–3.2, 3.6.1 and the adjacent compute exclusion, 4.1–4.2, 6.2, 7.3, all of Appendix A, and the incorporated fixed-unit/custom-texture contracts in Appendices B.3/F.5.

Dependency review covered all of Phase 1 §5 and Phase 3 §5, together with the relevant incorporated declarations, capability/handle lifetimes, native configure/link metadata, duration-lock failure rules, complete native materialization/language/attribution rules, canonical fingerprint framing, and immutable same-load asset lifetime. Current schema21, native-source and linked-input grants were assessed as current grants, not as old missing APIs. The dated geometry-compatibility and U1 authority decisions were read.

Producer/receiver tracing included Phase 5 §4.5 positional routing and §4.12.1 sole sampler policy, Phase 6 §4.9 sampler dispatch, Phase 7 §§4.4/4.6 and its Phase 4 receiving contract, Phase 2's ownBuild/golden/manifest receiving clauses, and Phase 10's effective-geometry handoff. These reads assess Phase 4's boundaries only; they do not certify those owners.

## C34-1 — Initialize fixed sampler units before state-dependent GL validation

**Severity:** correction; high impact.  
**Owner locations:** Phase 4 §4.7, especially lines 1354–1369; §§4.10, 5.1–5.4 and 8.3.  
**Receiver evidence:** `docs/phase5/v1/PHASE_5_DOC.md` §4.12.1; `docs/phase6/v1/PHASE_6_DOC.md` §4.9; `docs/phase7/v1/PHASE_7_DOC.md` §§4.2, 4.4 and 4.6.

The compiler's complete sequence links, checks linked geometry, and immediately validates. There is no sampler integer initialization between link and validate. The first specified sampler uploads occur in Phase 6's `SamplerRepointParticipant.afterBind`, after publication, Phase 5 object binding and Phase 4 activation. Phase 5's compiler callback only validates metadata; it does not initialize the newly linked GL program.

The primary OpenGL contracts make this a concrete reachability defect. [glLinkProgram](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glLinkProgram.xhtml), Description, initializes active user uniforms to zero after successful link. [glValidateProgram](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glValidateProgram.xhtml), Description/Notes, validates against current GL state and rejects active samplers of different types referring to the same texture image unit. A legal program with active `sampler2D colortex0` and `sampler1D gaux1` has intended fixed units 0 and 7 under Phase 5's published policy; immediately after link both instead contain zero. The current compiler therefore rejects and deletes that provider before Phase 6 can install the legal mapping. The same issue applies to other active mixed sampler types on distinct intended units. The all-band pure policy validation does not fix GL uniform values.

**Required correction:** specify a candidate-only, owner-authorized initialization phase before `ShaderService.validate`: obtain the exact fixed-unit plan from Phase 5's sole policy/resolver, initialize each active sampler location using those units, and define temporary program selection, error draining, prior-selection restoration and cleanup on every exit. Publish the required Phase 4/5/6 ownership and callable seam in §5 rather than copying the map or calling published runtime participants on an unpublished candidate. Preserve the existing runtime three-participant refresh contract. Add a planned regression case with two active different sampler types on different legal units; a recorder's default validation success is not evidence that this ordering works on GL.

This is one Phase 4 compiler/initialization correction. Phase 5 and Phase 6 receiver amendments are coordination duties, not separate findings against those owners.

## C34-2 — Reconcile the failure table with the adopted duration-lease contract

**Severity:** correction; medium impact.  
**Owner locations:** Phase 4 §4.10, especially lines 1671–1681 and the duration-lock paragraph; §6 rung 2a.  
**Dependency evidence:** `docs/phase1/v14/PHASE_1_DOC.md` §4.7.4, D-P1-57 duration override mechanism, incorporated by §5.2.  
**Receiver check:** `docs/phase7/v1/PHASE_7_DOC.md` §4.4 step 6 receives the strict rollback/poison/no-draw rule.

The adopted activation contract permits `Activated` only after duration-lock acquisition and notification succeed, stops on failed predecessor close, and sends operational failures through safe/off recovery. P1 makes failed close consume the lease and poison shader admission; rollback failure likewise poisons it. Nevertheless, the active §6 rung-2a row still groups alpha/blend application failure with optional debug-label failure and instructs the implementation to disable only the override for the generation and keep the program. That is an incompatible executable failure policy, not merely a historical statement: an implementation following that row can continue without the successful lease required by `Activated`.

**Required correction:** split optional debug-feature degradation from duration-lease failure. State in §6 that acquire/notification failure cannot produce a drawable activation, predecessor-close failure stops activation, and close/rollback failure retains poisoned admission until the existing safe/off recovery establishes safety. Keep isolated uniform/expression degradations distinct. Align its planned failure cases with §4.10 and P1 rather than weakening those already-received contracts. No new §5 API is required for this correction.

## Whole-owner assessment

- **Doc gate:** §§4.1–4.3 demonstrate the G6 six-step/five-identity schedule and modern ten-occurrence shape without a fixed 8/16 structural limit. Sparse indices reach 99, gbuffers occurs in two bands, and compute is excluded from gbuffers while remaining dormant elsewhere.
- **Catalog and inheritance:** §3.2 accounts for the 60 named classic shader/virtual slots plus the external sentinel. §4.6 preserves full effective-provider state, root-shadow noninheritance, explicit child-shadow edges, disabled/missing distinctions, and total requested-own-build versus effective-resolution evidence.
- **New boundary values:** positional Attachment/None routing reaches Phase 5's actual packing dispatch without dropping holes; ownBuild reaches Phase 2/7 `/4` handling without inference; the six-category geometry input reaches the authenticated Phase 7/10 adapter with NONE reserved for true absence. No Phase 4 producer-side silent-drop defect was found in those traced routes.
- **Source and geometry:** §§4.7–4.9/5.3 receive catalog-bound materialization, exact current schema, full declarations/maps, native API versus source-layout precedence, mandatory configure drains, cached linked-input comparison, and whole-provider fallback. They do not revive the removed translator or treat missing geometry as an allowed partial success. Runtime legality and conformance remain separate.
- **Ownership and closed results:** candidate versus detached metadata ownership, compiler/composer credentials, transfer only on Accepted, pre-release Rejected versus post-release RecoveredOff, generation changes, off-state absence, current frame/context authentication, retained-selector nested restoration, and callback-scoped lookup are specified. Phase 7's inspected dispatch handles Selected/Skipped/StalePublication/ShadersOff and drawable versus no-draw activation outcomes explicitly.
- **Required staging:** §§9–12 cover v0.1 registry/compiler/barrier infrastructure, v0.2 shadow invocation, v0.5 instance execution/texture integration and later modern families. §5.7 honestly leaves the optional Phase 14 split pending and preserves synchronous compilation; an unrun OQ-15 experiment is not an architecture defect in Phase 4.
- **Authority/licensing:** the owner retains the D-6 seam and fixed 10/11/12 locations; rejects heuristic pack-layout fallback and copied unit allocation; and records its Pintonium mechanism decisions. The conditional geometry conversion and adjacent prepared-submission policies are bounded by explicit maintainer authority, not claimed as OptiFine parity. U1's superseded unspecified key-suffix requirement was not re-raised.

## Notes and limitations

**N34-1 — Historical source-coordinate availability.** `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` §§3/13 and the shipped G6 author program/configuration tables were read. The exact historical `reference-src/pintonium-9c2fcc1/...` source paths named by the owner are absent from this checkout. A bounded path lookup found the permitted `reference-src/Pintonium-main/...` checkout; direct reads there corroborate recursive memoized fallback, the relevant program-parent edges, bind→uniform/sampler/image updates, incompatible attribute numbering, pass-state fields and generation invalidation. Those reads do not authenticate the old checkout identity or retroactively replace its historical citations. The behavioral digest was used only for the stated geometry topology, never as copied implementation structure. No forbidden transcript, Oculus transformation directory, library bundle or relocated GLSL source was read.

**N34-2 — Verification boundary and receiver status.** The frozen owner remains subject to these corrections and fresh review of any changed §5 contract. Historical PASS statements and old schema/domain receipts were preserved as history; in particular D-P4-35 explicitly supersedes D-P4-31's `/3` receipt with `/4`. A receiver-local contradictory pending-grant phrase observed in Phase 10 was referred to that owner, not counted as a Phase 4 correction. No runtime compatibility, shader-pack tier result, real-context draw proof, sibling PASS or final IR-01/§G5.3 integration clearance is granted by this review.

## Disposition

Apply C34-1 and C34-2 in a separate owner correction session, with explicit receiver coordination for C34-1. Because the required compilation/initialization contract affects §5, a fresh whole-document review is required before verified downstream consumption. The correct current verdict is **PASS-WITH-CORRECTIONS**, not PASS and not structural FAIL.

## Resolutions

### C34-1 — Candidate sampler initialization

D-P4-36 adds pure initializationAssignments to the existing P5 policy seam; D-P5-38
reuses the sole all-band resolver. P1 D-P1-59 owns temporary selection, active-location
integer uploads and exact restoration through initializeSamplerUnits. P4 requires Completed
before validate, handles restored failure as explicit VALIDATE-stage initialization failure,
and contains poisoned/unproven restoration registry-wide. Runtime P6 dispatch is unchanged.
Mixed active sampler types on legal distinct units remain an explicit real-driver case,
not a claimed result. Changed §5 requires fresh whole-owner verification.

### C34-2 — Duration failure containment

D-P4-37 separates optional debug degradation from failed lease acquisition/notification
and poisoned close/rollback. The active failure table now matches §4.10/P1; no failed
required override can produce Activated or continued drawing.

### N34-2 clarification

The independent P10 reviewer confirmed its §§0.6/5.3/D-P10-19 already superseded the
pending-owner phrase mentioned in N34-2. It is not promoted into an active integration defect.
Original findings, counts and verdict remain unchanged.