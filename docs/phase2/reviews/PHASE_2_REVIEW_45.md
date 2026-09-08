# Phase 2 Whole-Owner Architecture Review — R45 / Attempt 7

## Identity and verdict

- **Owner:** `docs/phase2/v2/PHASE_2_DOC.md`, complete §§0–12.
- **Frozen inventory identity:** `fb89db5d8670bf065cb32564f976c8cc58337738b722a49387154feb2592d9ca` (supplied identity; not represented as an independently executed checksum).
- **Verdict:** PASS.
- **Substantive corrections:** 0.
- **Notes:** 1.
- **Section 5 impact:** none.

## Scope and authority

This was a fresh, read-only whole-owner architecture review, not a review confined to the latest receipts. The governing authority was selected through the current owner header and `docs/MOVES.md`: `docs/design/v3/DESIGN.md`, including its global contracts at lines 1–1146 and Phase 2 specification at lines 1261–1342, together with `docs/research/v1/RESEARCH.md`. A globally newer design revision was not substituted for the owner-selected revision.

The research checks covered the baseline/tier and timing material at lines 210–257 and 305–328, rendering and resource evidence at lines 474–582 and 655–706, licensing and open questions at lines 891–1031, reference links and early appendices at lines 1075–1227, and the cadence/reference appendices at lines 1532–1579. Dependency review included the relevant §5 contracts, the definitions those contracts incorporate, and actual receiving branches outside §5. Historical reviews and fix receipts were not treated as proof.

## Independent checks

1. **Execution boundary and complete milestone domains.** The owner preserves a headless conformance process and a separately launched mod client rather than placing the conformance module on the mod classpath. The package and dependency boundary agrees with `docs/phase1/v14/PHASE_1_DOC.md:1696–1765` and `2225–2280`. Phase 2's run-domain contract at `docs/phase2/v2/PHASE_2_DOC.md:695–802` defines required shots, paths, feature states, and repeat ordinals before examining successful outputs. Missing members cannot disappear from the denominator. T0, T1, motion, T2, and T3 have distinct closure requirements; empty or incomplete domains cannot become PASS. The sensitive scene families retain mandatory motion paths and independent repeated executions.

2. **Evidence identity and replayable comparison decisions.** Comparison records at `docs/phase2/v2/PHASE_2_DOC.md:804–887` retain the decision policy and transitive input closure rather than only a summary result. FEATURE_DELTA requires both child T1 comparisons to pass while the selected numeric OFF/ON comparison demonstrates a difference. The scene/path, plan, manifest, world-template, baseline-option, and calibration sections collectively distinguish subject identity, comparison identity, and reusable world identity. Historical schema versions do not silently enter current-schema acceptance. The manual reference route does not substitute a requested time or wall-clock wait for evidence of comparable captured time.

3. **One GL-profile codec and typed resource evidence.** The profile wire delegates parsing and canonical writing to Phase 1 rather than inventing a second Phase 2 grammar. The mandatory profile fields and target-specific maxima are established in `docs/phase1/v14/PHASE_1_DOC.md:2917–3025`. Phase 2's resource projection at `docs/phase2/v2/PHASE_2_DOC.md:1300–1344` distinguishes PLANNED from REALIZED data, DEFAULT_RGBA from an explicit RGBA8 request, typed fog clears from constant components, and requested allocations from fallback allocations. The receiving pure planner and projection contract were checked at `docs/phase5/v1/PHASE_5_DOC.md:1230–1330` and `2646–2711`; invalid and unavailable outcomes remain distinguishable rather than producing fabricated successful resources.

4. **Actual clock/capture routing and durable timing closure.** The controlled-timing contract was traced through `docs/phase6/v1/PHASE_6_DOC.md:1050–1105,1387–1460` and `docs/phase7/v1/PHASE_7_DOC.md:1925–2165,3100–3182`. The receiving flow establishes the checkpoint before admitted frames, acknowledges owner-thread tick work, applies pose after ticks, captures through the designated final listener, and restores on termination. Phase 2's durable timing core at `docs/phase2/v2/PHASE_2_DOC.md:1204–1285` retains the actual checkpoint-origin summary and progression while the lease is valid. Later normal revocation does not erase already captured evidence, and incomplete progression or restoration cannot be converted into a COMPLETE manifest. The GL-error receiver at `docs/phase7/v1/PHASE_7_DOC.md:2177–2250` preserves receipt order and failure evidence and includes final draining; equal-valued receipts are not deduplicated into a cleaner result.

5. **Hook-health consumers, not acknowledgments alone.** The flattened shadow contract was checked against its actual catalogue at `docs/phase8/v1/PHASE_8_DOC.md:1577–1734` and Phase 7's health consumer at `docs/phase7/v1/PHASE_7_DOC.md:1740–1870`. The 60-row catalogue, 59 non-CLOUD aggregate, first outline row, and current fingerprint version agree with the Phase 2 evidence fields at `docs/phase2/v2/PHASE_2_DOC.md:1345–1387`. Sprite health was also traced to the actual outer load scope and failure paths at `docs/phase13/v1/PHASE_13_DOC.md:1430–1568`: application health is not inferred from a successful inner callback, and runtime extent information is distinct from frozen application evidence.

6. **Exact-current inspection and same-request joins.** Phase 2's inspection/golden procedure at `docs/phase2/v2/PHASE_2_DOC.md:2000–2200` was checked against the actual closed inspection outcomes and nine typed source-free decision trees at `docs/phase3/v1/PHASE_3_DOC.md:3987–4135`. The Phase 4 receiving contract at `docs/phase4/v1/PHASE_4_DOC.md:2314–2366` consumes the exact configuration and explicit `Optional.empty()` profile selection from the same request, returns a closed result, and closes a ready candidate after copying evidence. Phase 2 does not acquire a named-profile inspection grant or synthesize the opaque fingerprint codec. Phase 5 consumes the exact returned view, identity, capabilities, and explicit runtime inputs; unavailable prerequisites do not become fabricated complete plans.

7. **Shared semantics and secondary evidence consumers.** The expression adapter and diagnostic sink were checked against `docs/phase11/v1/PHASE_11_DOC.md:1272–1415`, including seven operation families, independent expected values, and distinct FAIL/UNSUPPORTED outcomes. The programmatic settings and internal-session routes were checked at `docs/phase12/v1/PHASE_12_DOC.md:1453–1564`, including canonical settings, current identity checks, and completion receipts. The typed clear and conventional-input grants incorporated through Phase 1 §5 were traced to `docs/phase1/v14/PHASE_1_DOC.md:3964–4435`; recorder observations are not promoted to native display-list playback proof. The bounded Phase 14 receipts at `docs/phase14/v1/PHASE_14_DOC.md:1831–1840,1971–1997,2195–2240` maintain the distinction between model, facade, and native evidence and between main-thread readiness and worker completion.

8. **Failure, staging, and architecture-gate limits.** The remainder of owner §§5–12 was reviewed for contract handoff, ownership, failure handling, test placement, staging, OQ10 fallback, and the final milestone checklist. Missing historical fixtures or unmeasured calibration values remain explicit blockers for the later evidence they govern, not invented results. CI and source-inspection scaffolding are described as implementation work rather than claimed as existing runtime certification. The reference-free and fallback routes remain present. No substantive current contradiction requiring an owner correction was established.

## Substantive corrections

None.

## Notes

### N45-1 — Preserve the distinction between historical attribution and current corroboration

**Severity:** informational; no correction required for this architecture verdict.

The historical material in `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:163–210,374–490,822–841` is an author digest, not a recovered checkout of its historical pin. The local historical `reference-src/pintonium-9c2fcc1/` tree was not available. Narrow permitted current-source corroboration was performed using `reference-src/Pintonium-main/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java:1–189`, `MixinRenderGlobal_Shaders.java:1–75` in the same directory, and `reference-src/Pintonium-main/README.md:1–87`. Those current hook placements neither authenticate the historical pin nor establish historical temporal-blur behavior. The current README's LGPL description and the GPL heading at `reference-src/Pintonium-main/LICENSE:1–35` also do not establish a uniform, reuse-approved license for every file or transitive component.

**Observable risk if the distinction is later removed:** current-source observations could be presented as historical evidence, or a repository-level label could be mistaken for file-level reuse authorization. **Minimal disposition:** retain the owner's existing qualification; authenticate the relevant historical source and per-file licensing before making stronger historical or reuse claims. No §5 amendment is needed for this note.

## Limitations

This review establishes document-level architecture consistency for the frozen Phase 2 owner, not implementation correctness, native GL behavior, runtime visual quality, achieved comparison thresholds, historical pin authenticity, or comprehensive source-license clearance. No builds, tests, formatters, validation commands, runtime captures, or checksum commands were executed. Forbidden implementation and transcript sources were not used. Reciprocal owners were inspected only to establish the relevant receiving behavior; they are not independently certified by this Phase 2 verdict.

**Final verdict: PASS**
