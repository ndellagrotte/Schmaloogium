# Phase 7 — Independent Whole-Owner Architecture Review R41

## Frozen artifact and authority

- **Owner:** `docs/phase7/v1/PHASE_7_DOC.md`, current §§0–12, lines 1–4422.
- **Frozen SHA256:** `4c553a2b41dc467685ed43e3558e9e501bc7911c123ffb5391da48d6b67881ce`, as identified by `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_5.json` for Phase 7/R41.
- **Governing design:** `docs/design/v2.0-RC3/DESIGN.md`, selected by the owner's header at lines 8–11. I used its Part I and Phase 7 assignment at lines 1805–1953, not the globally newest design.
- **Governing research:** `docs/research/v1/RESEARCH.md`, including the frame flow, hook needs, classic program/sampler contracts, engine flags, cadence and OQ-3/OQ-4 material. `docs/MOVES.md` was consulted to resolve versioned and moved citations.

This was a fresh review of the complete current owner, including both mandated parts—the engine frame driver and hook catalog—and sections covering contracts, failure handling, performance, tests, staging, spikes, decisions and checklist. I examined the patch and current context; prior reports were not accepted as proof. Dependency §5 contracts and load-bearing reciprocal receivers in Phases 1–6 and 8–14 were checked without certifying those owners.

## Independent checks

1. **Frame ordering and terminal ownership.** The sampling-before-resize/clear rule and distinct post-camera capture are explicit at `docs/phase7/v1/PHASE_7_DOC.md:1048–1105`. Shadow completion and execution-close must succeed before main clear. Ordinary and nested acquisition failures share the P5-consumed terminal state at lines 1035–1043 and 1125–1132; fullscreen mipmap failure joins it at lines 1234–1243. These paths prohibit parent resume, unsafe finalization and a second P5 terminal call while preserving independent cleanup. The normal/early-return composite guarantee is therefore not applied to a known-corrupt transaction.

2. **Selection, textures and effective state.** The owner retains the requested slot, exact P4 selection and originating context across nesting; it does not resolve the effective provider again. The closed snapshot/lease/binding/activation branches and ownership transfer are specified at lines 1108–1183. P5 physical binding precedes P4 activation and P6 participants. P4 result-checked release precedes local restoration. The sampler-normalization receipt at lines 3359–3363 preserves P5 preflight ownership and the existing P4/P1 fixed-function route rather than adding a P7 texture or sampler loop.

3. **Depth semantics and enforcement.** Rain and beam properties control writes, not testing. Lines 1501–1527 require an authenticated nested depth-scope override, an actual GlStateManager argument interception, local finally restoration and admission failure for missing enforcement/restore coverage. The `(Z)V` depth-mask mapping and both beacon helper overloads were independently checked through the permitted mapping service. This is stronger than an entry-only setter that vanilla could overwrite.

4. **Cross-owner data delivery.** Current schema22 admission and `MaterializedSource-v22` are received at lines 3468–3475; BLOCK alternate data stays opaque to P7. P6 replay reports are retained through final use, and the installed effective-blend route at lines 2837 onward explicitly reaches `UniformSignalBridge` and the P6 event sink. The current timing receiver at lines 3043–3101 validates live, owner-issued reports and persists actual preparation/warm-up/sample evidence under P2 `/4`, without reconstructing counters from the plan.

5. **Shadow and native receivers.** The current P8 demand/neutralization receipt at lines 926 onward uses the accepted estate and actual generation. The traversal receipt at lines 1760–1769 requires the complete health vector and actual rebuild witness. Lines 1771–1802 incorporate P10's seventeen sky/star rows and the actual TESR/FastTESR ownership, sort and native-range protocol. The consuming boundaries retain authentication, adjacent native repetition and restoration rather than replaying whole renderers or world traversal. The lower TESR dispatcher descriptor was independently checked; the obsolete convenience-method hypothesis is not the active hook contract.

6. **Expression lifecycle and diagnostics.** Lines 3325–3357 receive P11 construction, the one P6 bridge, compilation, fresh activation tuples, reset-before-adoption and terminal-close-before-P6-retirement ordering. The receiver was checked against `docs/phase11/v1/PHASE_11_DOC.md:1093–1117` and its incorporated lifecycle contract. Returned compiler diagnostics and controller-emitted diagnostics have separate delivery paths, avoiding double delivery and invented P1 fields.

7. **Composition, reload and failure disposition.** The coordinated transaction at lines 3668–3724 maintains publication ownership, actual generation adoption, textures-before-IDs and required geometry invalidation. Failure converges to off rather than resurrecting the old pipeline. The failure matrix at lines 3849–3888 remains consistent with those branches. Optional asynchronous split/resumability proposals remain explicitly unadopted at lines 3840–3846; timeout neither acknowledges quiescence nor authorizes destruction or successful shutdown.

8. **Scope and architecture-gate honesty.** All eighteen Appendix-E rows have an owner/deferral disposition. Reference-free sky/weather/clouds remain identified as such. Sections 8–12 provide planned tests, milestones, OQ-3/OQ-4 experiments and implementation obligations without claiming those experiments have run. Deferred celestial values agree with P6's explicit v0.2 ownership rather than constituting a missing v0.1 producer.

## Required corrections

**None.** I found no substantive current defect attributable to the frozen Phase 7 owner that requires an owner edit before this architecture review can pass. No required Phase 7 §5 change is identified.

## Notes

### N41-1 — Cross-owner integration note: P4 virtual-pre population contract

**Owner of the concern: Phase 4, not Phase 7.** `docs/phase4/v1/PHASE_4_DOC.md:477–494` exposes only Singleton, NamedPrograms and SparseArray populations. Its kind-restricted lookup/construction rules at lines 1026–1033 do not specify how a named virtual-pre descriptor coexists with sparse indexed raster descriptors in the single deferred/composite step prescribed at lines 1048–1051. P7 consumes that promised descriptor unchanged at `docs/phase7/v1/PHASE_7_DOC.md:1203–1208` and bypasses shader selection for it at lines 1335–1338; it does not own an alternative registry constructor or descriptor factory.

**Observable integration consequence:** a literal implementation of the upstream population rules cannot unambiguously construct/retrieve both the virtual prelude and sparse family from that same step. **Disposition:** resolve the representation/lookup contract in P4, then check its P5/P7 receivers during integration. Do not repair it by having P7 synthesize a descriptor or reinterpret flip policy. This is not counted as a required P7 correction or a sibling verdict.

### N41-2 — Historical pinned-source confidence remains qualified

The exact Pintonium and Cleanroom historical paths cited at `docs/phase7/v1/PHASE_7_DOC.md:26–34` are unavailable in the current reference tree after resolving the moves documented in `docs/MOVES.md:42–52`. Available newer checkouts, permitted mapping/API checks, the retained Pintonium design and the allowed behavioral digest provide corroboration, but do not independently reproduce those historical pinned-file line claims.

**Disposition:** retain the distinction between historical `[V:observed]` evidence and currently checked mappings/API or behavioral corroboration. The mapping checks confirm callable identities, not injection cardinality or runtime interception. OQ-3/OQ-4 and actual hook-application evidence remain the future implementation gates already specified at `docs/phase7/v1/PHASE_7_DOC.md:4113–4178`; their nonexecution is not an architecture correction. No forbidden implementation source or decompile mining was used.

## Limitations and final disposition

This was a read-only architecture investigation. No repository files were edited and no builds, tests, formatters, native rendering, capture run or pack-tier validation were executed. This review neither certifies sibling phases nor grants optional APIs, establishes runtime compatibility, or closes the separate final-integration/implementation gates. Historical source availability limits are preserved above rather than replaced with a claim of pinned-source verification.

**Final verdict: PASS.** Zero required owner corrections; two notes. The frozen Phase 7 architecture passes this independent whole-owner review. Integration remains separate, including the upstream virtual-pre population note.
