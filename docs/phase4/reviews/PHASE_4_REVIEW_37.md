# Phase 4 Whole-Owner Architecture Review — R37

## Frozen identity and verdict accounting

- **Owner:** `docs/phase4/v1/PHASE_4_DOC.md`, complete §§0–12.
- **Round:** R37, frozen attempt7.
- **Supplied SHA-256 inventory identity:** `b9ae109b0d1b78fe3da22fd1502e92c2f684d83561f30ec7a732d3f92911316d`.
- **Substantive corrections:** 0.
- **Notes:** 1.
- **Structural blockers:** 0.
- **§5 impact from this review:** No. No interface correction is requested.

The hash above is the supplied frozen identity, not a claim that this reviewer ran a checksum command. This review made no mutations and ran no formatter, linter, build, test, or validation command.

## Scope and selected authority

I reviewed the entire current owner, including historical addenda, current representation and algorithms, conformance maps, exposed and consumed interfaces, failure handling, threading, planned tests, milestone staging, decisions, handoffs, and implementation checklist. Historical review verdicts and applied-resolution claims were not used as correctness evidence.

The owner selects **RC3**, not the globally newest design: `docs/phase4/v1/PHASE_4_DOC.md:9-14`; the governing Phase 4 specification is `docs/design/v2.0-RC3/DESIGN.md:1471-1570`. I read RC3 Part I §§G0–G12 and the complete Phase 4 assignment. `docs/MOVES.md` and `AGENTS.md` supplied the citation, per-owner authority, and source restrictions.

The governing research checks covered §§0–1, program/stage and directive contracts, modern stage shape, initialization and program-use behavior, GL geometry constraints, registry extensibility, Appendix A in full, and the relevant fixed-sampler/profile/render-state contracts in `docs/research/v1/RESEARCH.md`. I read Phase 1 and Phase 3 §5 and followed their incorporated, load-bearing facade, source/materialization, geometry, declaration, evaluated-state, resource-projection, and fingerprint definitions. Reciprocal reads were limited to the actual receiving algorithms needed to assess Phase 4’s boundaries; they do not certify those sibling owners.

## Independent checks and evidence

### 1. Registry shape, catalog, sparse traversal, and virtual preludes

The G6 and modern configurations share the same stage/population types. Stage identity is distinct from occurrence, preserving both gbuffers bands without duplicating deferred or composite. Sparse populations retain legal indices through 99, explicit maximum semantics, and an optional named prelude that is not index zero (`docs/phase4/v1/PHASE_4_DOC.md:476-587,1052-1157`).

I checked the classic catalog against `docs/research/v1/RESEARCH.md:1100-1185` and the shipped author table at `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:61-108`. The 60 named shader/virtual slots exclude the external sentinel; fixed cardinality is not an allocation or traversal rule. Compute companions remain dormant and forbidden on gbuffers.

The prelude boundary has a real receiving path, not just matching terminology. Phase 5 validates the planned descriptor, rejects altered/raster/duplicate transitions, toggles only explicit true entries, consumes Applied and NoChange once, and performs no program selection (`docs/phase5/v1/PHASE_5_DOC.md:1594-1609`). Its current sparse-population receipt derives membership from the candidate registry (`:2758-2764`). Phase 7 forwards the contained deferred prelude before raster traversal and does the equivalent for composite (`docs/phase7/v1/PHASE_7_DOC.md:1213-1218,1349-1366`). The owner’s pre/0/5/99, pre-only, absent-pre, and invalid-membership cases are specified as future checks, not asserted executions (`docs/phase4/v1/PHASE_4_DOC.md:2471-2510`).

### 2. Explicit profile ingress and exact evaluated-state consumption

The compiler request carries required `Optional<ProfileName> profileSelection` immediately after configuration (`docs/phase4/v1/PHASE_4_DOC.md:792-800`). Planning evaluates the exact immutable pair once before availability, materialization, policy callbacks, or allocation. It distinguishes `Evaluated(states)` from `InvalidState(failure)` and does not retry with empty intent or infer selection from equal option values (`:1333-1353`).

This matches P3’s actual algebra and implementation contract: `ProgramStateEvaluationResult.Evaluated|InvalidState`, the ordered state list and flip map, typed optional state, and the three enablement booleans (`docs/phase3/v1/PHASE_3_DOC.md:1126-1141`). The binding evaluation algorithm uses finalized options, treats absent/unknown profiles as no profile disables, retains qualified versus unqualified matching, and computes `propertyEnabled && !profileDisabled` (`:2965-2983`). Virtual flips do not become executable program entries.

The receiving ingress is concrete. Phase 7 freezes accepted profile intent and forwards it to every P4 build, including equal-option profile changes (`docs/phase7/v1/PHASE_7_DOC.md:3216-3230,3633-3640`). Phase 2’s actual inspection adapter explicitly supplies empty selection and preserves its exact snapshot/build association; it branches on Ready versus ShadersOff and recognizes INVALID_PROGRAM_STATE without fabricating rows (`docs/phase2/v2/PHASE_2_DOC.md:2128-2147`). P4’s registry-wide failure has empty programFailures and no manufactured candidate projection (`docs/phase4/v1/PHASE_4_DOC.md:2059-2078`).

### 3. Fallback and immutable evidence

The owner separates requested-slot source presence, intentional disablement, own build result, and effective fallback status. Successful fallback preserves `ownBuild=FAILED` or DISABLED as appropriate instead of rewriting the requested slot’s history. Unmasked ancestor failure propagates to FAILED even for a missing child; successful ancestor resolution yields CHAIN and the ancestor’s complete state/layout/source bundle (`docs/phase4/v1/PHASE_4_DOC.md:1211-1331`).

Candidate and published evidence are value-equal, detached, and unaffected by later barrier/publication failures. The Phase 2 receiver copies the owner rows directly (`docs/phase2/v2/PHASE_2_DOC.md:2135-2140`); Phase 7 likewise consumes required ownBuild and the existing resolution list rather than reconstructing status (`docs/phase7/v1/PHASE_7_DOC.md:3614-3616`).

### 4. Source, geometry, routing, and pre-allocation compilation checks

P4 consumes catalog-bound materialization rather than reopening source or supplying a replacement OptionState. The P3 native request/result algebra, full declaration types, final language/maps, and same-load resource projections are available through its incorporated §5 contract (`docs/phase3/v1/PHASE_3_DOC.md:1390-1523,2417-2592,3336-3380,3700-3840`). Sparse requirement absence is distinct from source absence.

P4 preserves native and core geometry forms, requires actual ARB support for NativeLegacy, configures the exact native API triple, and compares P1’s cached linked input against finalized P3 expectations before validate/READY. Failures reject the whole provider, not only its geometry stage (`docs/phase4/v1/PHASE_4_DOC.md:1495-1554`). P1 actually grants the configuration transaction and cached metadata semantics (`docs/phase1/v14/PHASE_1_DOC.md:3798-3834,3858-3945`). The bounded author/digest evidence establishes the documented pair and observed triangle/strip topology, not general source compatibility or parity.

Routing preserves positional None entries and capacity accounting; declared attributes retain 10/11/12 with declaration-dependent capability gates (`docs/phase4/v1/PHASE_4_DOC.md:1556-1586`). P7/P10 consume the authenticated effective geometry category and define explicit native compatibility dispatch rather than assuming QUADS is legal (`docs/phase7/v1/PHASE_7_DOC.md:2440-2448`; `docs/phase10/v1/PHASE_10_DOC.md:854-879,1703-1722`). Conditional conversion and prepared-submission repetition remain explicit local decisions with separate implementation/conformance gates.

### 5. Sampler policy, initialization, and select-once activation

The uniform merge and sampler projection retain full structural types, ordered declaration sites, unsupported aggregates, and optimized-out declarations. All provider-band validation and candidate integer-plan callbacks complete before any registry GL allocation. A malformed or throwing policy yields registry-wide INVALID_SAMPLER_POLICY; legitimate conflicting/unsupported provider layouts follow ordinary fallback (`docs/phase4/v1/PHASE_4_DOC.md:1377-1455`).

Phase 5 is the actual sole policy receiver/implementation: it derives initialization assignments through its resolver, preserves every exact name, and uses the same policy fingerprint (`docs/phase5/v1/PHASE_5_DOC.md:2407-2480`). P1 initializes the unpublished linked candidate and restores the exact prior selection, distinguishing restored failure from poisoned admission (`docs/phase1/v14/PHASE_1_DOC.md:3458-3477`). P4 branches on those outcomes before validation (`docs/phase4/v1/PHASE_4_DOC.md:1457-1491`).

Runtime selection is authenticated once, force-shadow precedes fallback, and activation uses the retained binding. Phase 13 rejects stale selection before granting a lease (`docs/phase13/v1/PHASE_13_DOC.md:1320-1345`). Phase 5 validates credentials and resolves all sixteen rows before mutation, then separates Bound ownership transfer from Rejected, Degraded, and BackendFailed (`docs/phase5/v1/PHASE_5_DOC.md:2533-2580`). Phase 6’s actual participant consumes the effective layout through the same resolver, without a second map or texture binding (`docs/phase6/v1/PHASE_6_DOC.md:1184-1268`).

### 6. Barrier lifetime, publication, and current identity

P4 closes the previous duration lease before acquiring the next, publishes effective blend before participants, confines lookup to callbacks, and invalidates activity before release, replacement, failure, or deletion (`docs/phase4/v1/PHASE_4_DOC.md:1617-1895`). The P1 duration-lock implementation contract supplies real interception, restoration, error handling, and poisoning rather than immediate-setter emulation (`docs/phase1/v14/PHASE_1_DOC.md:3512-3540`). P7’s nested suspend/pop algorithm releases and reacquires through the original selector; the shadow receiver explicitly handles every activation and terminal-release result (`docs/phase7/v1/PHASE_7_DOC.md:1112-1181`; `docs/phase8/v1/PHASE_8_DOC.md:929-980`).

Publication validates before disturbing the old barrier, distinguishes pre-release Rejected from post-release RecoveredOff, handles an authenticated absent old barrier, and transfers candidate ownership only on Accepted (`docs/phase4/v1/PHASE_4_DOC.md:1949-2016`). P7’s actual rebuild/failure branches compensate off rather than resume an incoherent prior pipeline (`docs/phase7/v1/PHASE_7_DOC.md:882-923`). P6 retirement follows actual old-barrier invalidation, retaining services across rejection (`docs/phase6/v1/PHASE_6_DOC.md:1755-1788`).

The current registry domain is `RegistryFingerprint/profile-selection-v3`, including explicit intent, evaluated state, positional routing, required ownBuild, source/materialization and geometry identities. It remains distinct from generation and uniform-cache identity (`docs/phase4/v1/PHASE_4_DOC.md:1897-1947`). P3’s current schema23 and `MaterializedSource-v23` codec are explicit (`docs/phase3/v1/PHASE_3_DOC.md:3298-3327,4153-4250`); P4 adopts them without upgrades. P5 receives the current opaque registry domain and invalidates profile-only estate pairing (`docs/phase5/v1/PHASE_5_DOC.md:2752-2757`).

## Substantive corrections

None established. The current owner’s contracts and inspected receiving branches do not expose a concrete Phase-4-owned defect requiring amendment in this round.

## Notes

### N37-1 — Historical pinned-source and license confidence remains limited

**Severity:** Note; no §5 change required.

**Location:** `docs/phase4/v1/PHASE_4_DOC.md:31-69,109-114`.

The owner’s historical source claims name `reference-src/pintonium-9c2fcc1`, which is not present in the inspected reference inventory. The available checkout is `reference-src/Pintonium-main`. I independently read its permitted fallback resolver/catalog, program-use barrier, attribute binding, pass bundle, phase override, and both generation producer and consumer. These corroborate the narrow mechanisms: `ProgramFallbackResolver.java:28-44`, `ProgramId.java:11-46`, `Program.java:28-34`, `ProgramCreator.java:21-25`, `CompositeRenderer.java:156-163,431-442`, `PipelineManager.java:78-88`, and `forge122/.../IrisChunkProgramOverrides.java:135-139`, all under the current `reference-src/Pintonium-main` tree. They do not prove the absent historical checkout’s exact bytes or coordinates.

The current root `reference-src/Pintonium-main/LICENSE:1-3` says GPL version 3, while `common-shaders/src/main/java/net/irisshaders/iris/gl/shader/ProgramCreator.java:1` carries an LGPLv3-derived-file notice. Consequently this inspection cannot certify a blanket LGPL license for the historical pin or the complete present tree. No implementation copying occurred. Preserve this distinction in evidence and verify the exact files, revision, license scope, and notice obligations before reuse; do not silently replace historical coordinates with current ones. This is a provenance limitation, not demonstrated registry behavior breakage or a demand to run future runtime tests at this architecture gate.

## Limitations and final assessment

This is an architecture-only Phase 4 verdict. The owner’s tests and acceptance traces were inspected, not executed. No source implementation, Java compilation, GL behavior, client rendering, native geometry parity, pack tier, dependency verification, sibling-owner correctness, final integration clearance, or source-license clearance is certified. Historical superseded status and schema receipts were not treated as active alternative contracts. Forbidden transcripts, transformation implementations, Oculus blocked paths, and OptiFine decompiled implementation were not read.

All thirteen owner sections are present and substantive. The RC3 doc gate is satisfied by inspection: both registry configurations are representable, Appendix A rows and fallback semantics are covered, the use-program barrier is explicit, and generation invalidation is exposed with real receiving paths. No structural rebuild or corrective §5 amendment is justified by this review.

**Corrections: 0. Notes: 1. §5 impact: No.**

PASS
