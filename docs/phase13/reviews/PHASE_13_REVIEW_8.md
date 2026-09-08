# Phase 13 — architecture review 8

**Date:** 2026-09-08. **Frozen document:** `docs/phase13/v1/PHASE_13_DOC.md` SHA-256 `9fa88180b8c2d21957e80a523858b7d8176335a11994da19d650e2014975ddb5`. **Verdict:** PASS-WITH-CORRECTIONS (2 corrections, 3 notes). **§5 impact:** §5 text changes are required but no published or consumed interface signature, type, variant, ordering, fingerprint domain or lifetime rule changes. Correction 1 changes normative acquisition text (§4.3.2) that §5.2's Phase3 rows incorporate; Correction 2 re-resolves stale sibling-phase coordinates/quotations inside the §5.2 consumed-contract table (and four matching pins in §§1.1/1.2/3.2/4.6). Current §5 grants are SUFFICIENT: every produced and consumed contract was found reciprocally declared at its actual owner (P1 D-P1-52/63/64/66, P3 schema23 + assets + R1, P5 D-P5-50 + candidate/binding protocol, P6 D-P6-20/R7-10/R7-11, P7 evidence/sink/health/transaction, P8 R7-12/13, P14 D-P14-43 and parameter receipts). No new grant, no receiver amendment and no boundary owner action is required by either correction. Because §5 bytes change, the document's own rule stands: a fresh whole-document review returning literal PASS is still required before verified downstream consumption.

---

# Phase 13 — Independent whole-owner architecture review (review 8)

## 1. Subject, frozen identity and authority

1.1 Document under review: `docs/phase13/v1/PHASE_13_DOC.md`, 2482 lines, SHA-256 verified by me as `9fa88180b8c2d21957e80a523858b7d8176335a11994da19d650e2014975ddb5`, matching the frozen attempt-8 entry at `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_8.json` (phase 13, reviewRound 8).

1.2 Governing design confirmed from the document's own header (`docs/phase13/v1/PHASE_13_DOC.md:12`): `docs/design/v3/DESIGN.md`, Part I §G0–§G12 plus the Phase 13 specification only. The v3 Phase 13 row (`docs/design/v3/DESIGN.md:2436-2440`) confirms milestone v0.5, dependencies 3/5/7 and `OQs: —`, exactly as §0 and §10 state. P13 is therefore correctly on v3 while P4/P5/P7 remain on RC3; the assignment's expectation that P2/P10–P14 use v3 is met.

1.3 Dependency documents were hash-checked against the same frozen manifest before any line pin in this report was written: P3 `aa05ec63…`, P4 `b9ae109b…`, P5 `84ead9d9…`, P6 `0ebee7f4…`, P7 `3458f5ae…`. All match, so every coordinate below is against the frozen set.

1.4 Authority handling: research outranks design except the three narrow maintainer rulings. `docs/decisions/U1_TEXTURE_SAMPLING.md` (§0.8, D-P13-26) and `docs/decisions/TEXTURE_SIDECAR_DEFAULTS.md` (§0.9, D-P13-27) are used by the document exactly as narrow corrections, with the local policy explicitly labelled maintainer authority rather than author-document or runtime evidence (`docs/phase13/v1/PHASE_13_DOC.md:2374-2381`). Historical addenda §§0.4–0.13 are treated as immutable history and were not scored as current contradictions. I read no `chatlogs/` directory, no repository-root `*.txt`, no OptiFine decompile, and no restricted Oculus transform/libs/glsl-relocated path; no such source is cited here.

## 2. What I actually read

2.1 The complete owner document, all 2482 lines, in four contiguous passes.

2.2 Governing scope: `docs/design/v3/DESIGN.md:2435-2443`; RESEARCH texture rows `docs/research/v1/RESEARCH.md:592-600`; Pintonium §11 rows `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:630-642`.

2.3 Current dependency §5 and the actual algorithms behind the grants: P3 `:581-582,703-704,857-955,1084-1087,1615-1616,1721-1725,1889-1986,3231-3235,3349-3363,3572-3654,5053-5068,5246-5268`; P4 `:1783-1801,2127-2131`; P5 `:1022-1071,1127-1130,2357-2369,2505-2618,2619-2700,2726-2735,2902-2910`; P6 `:619-623,990-1002,1217-1221,1487-1500,1822-1826,2106-2110`; P7 `:503-523,744-763,1205-1223,1362-1371,1598-1605,1614-1766,1791-1796,1881-1894,1944-1947,2227-2243,2665-2672,2932-2935,3539-3612,3838-3850,3879-3928,4486-4494`; P8 `:1547-1562,1878-1882,1979-2020`; P14 `:1783-1821`; P1 `:2947-2988,4496-4542`; P2 `:886-890`.

2.4 Current integration Resolutions in `docs/PHASE_INTEGRATION_REVIEW.md` (IR-03/04/06/08/21/23/24 bodies and the Resolutions section at `:1036-1160,1258-1281,1364-1369,1534-1536`), plus the P13 review history `docs/phase13/reviews/PHASE_13_REVIEW_1..7.md` as historical evidence only.

## 3. Scope and ownership audit

3.1 §1.1's six owned areas match the v3 assignment exactly: companion `_n`/`_s` atlases, generated noise, custom textures in all three App F.5 forms, the `atlasSize` value source, texture publication production, and App E rows 10/11. §1.2's adjacent-ownership table correctly cedes unit assignment/physical binding to P5, sampler-integer and `atlasSize` upload to P6, tangent frames to P10, key parsing/lossless declarations to P3, and sampler/async optimization to P14. §1.3's hard boundaries (no attachments, no unit assignment, no GL outside `mod.glue`, no policy in mixins, no macro text) are consistent with the detailed design throughout.

3.2 App E deferral acceptance is real: P7 `:1792-1793` still carries `DEFERRED(P13,v0.5)` for `TextureMap` and `TextureAtlasSprite`, and §4.6 takes exactly those two rows with eight IDs (seven active FEATURE, one dormant).

3.3 Producer/consumer reciprocity for the freshly changed D-P13-42/43 boundary — the specific area the assignment flags — is complete in both directions:

- The three-argument lease is declared identically by P13 (`docs/phase13/v1/PHASE_13_DOC.md:314-317,1770`), P5 (`docs/phase5/v1/PHASE_5_DOC.md:2902-2908`), P7 (`docs/phase7/v1/PHASE_7_DOC.md:3839-3846`), P8 (`docs/phase8/v1/PHASE_8_DOC.md:2013-2020`) and P14 (`docs/phase14/v1/PHASE_14_DOC.md:1815-1820`), including the two added closed reasons `INVALID_BASE_BINDING`/`STALE_BASE_BINDING` and the same precedence position after issuer/thread/composition and latest serial/epoch.
- `BaseAtlasContext` and the three lease observations exist verbatim in the P5-owned protocol (`docs/phase5/v1/PHASE_5_DOC.md:1059-1070`), and the actual consuming branch is present, not merely referenced: `docs/phase5/v1/PHASE_5_DOC.md:2528-2543` resolves winning unit 0 first, consumes `baseTexture`/`baseAtlasContext` when unit 0 is undeclared, otherwise `atlasContext(winningUnit0Handle)`, selects only the matching atlas/kind companion, and routes NonAtlas/Unavailable/no-match to the kind's `DefaultFill` with SUPPRESS_DRAW when that default is missing or incompatible. Custom precedence stays ahead of the whole branch, and `candidateOrdinal` is explicitly barred from choosing among atlases — matching §4.1.5 and §4.3.4 of the owner.
- `CandidateOrigin.DefaultFill(CompanionKind)` is declared at `docs/phase5/v1/PHASE_5_DOC.md:1026` and mapped to `BindingOriginKind.NEUTRAL` at `:1128`, consistent with P13 §5.2's origin vocabulary.
- The opaque evidence type and its minting rule are P7-owned and reciprocal: `docs/phase7/v1/PHASE_7_DOC.md:3547-3578` (mod binding-observer sole minter; private base handle, P13 association, PipelineVersion, resourceReloadEpoch, monotonic bindSerial; `P13 alone maps actual accepted base-object incarnation to AtlasId`; explicit initial Unavailable is issuer-owned). P6 receives the unchanged `updateAtlasSize(Int2)` sink at `docs/phase6/v1/PHASE_6_DOC.md:1487-1500,1823`.
- Sole before-draw refresh through P5 is real on both sides: `docs/phase5/v1/PHASE_5_DOC.md:2626-2643` (close old binding, fresh context-bound lease, same binder on the still-open pass, no flip/completion/root activation/new native binder; observer dispatch batched and committed after all binds), P7 `:3560-3575` and `:4491` (D-P7-73), P8 `:1547-1562,1979-1981` (`ShadowExecutionView.currentBaseBinding()`, `ShadowBaseBindingReceiver.refreshBaseBinding`).

3.4 The D-P13-42 sampling baseline flows unchanged end to end. §4.1.4 derives minification from the accepted `AtlasDescriptor.mipmapLevels` (greater than zero → `NEAREST_MIPMAP_LINEAR`, zero → `NEAREST`; mag `NEAREST`; wrap `REPEAT`; standalone 1×1 default level-zero `NEAREST/NEAREST/REPEAT`; a missing sprite inside a full atlas inherits the atlas object's policy). That exact triple is stored in `ReadyAsset`, hashed as baseline=effective with `NOT_APPLICABLE` sidecar (§4.5.1), carried on the candidate, and applied through the mandatory complete P1 setter (§5.5). The allocation table's companion row uses `mipLevels = AtlasDescriptor.mipmapLevels + 1`, which is coherent with the P1 law that `max` equals the admitted contiguous last mip and 0 when nonmipped (`docs/phase1/v14/PHASE_1_DOC.md:4496-4499,4538-4541`). P14 maps all six min modes and both mag modes by identical enum names with no mip-mode reduction (`docs/phase14/v1/PHASE_14_DOC.md:1783-1787`), and P7 explicitly disclaims choosing sampling defaults (`docs/phase7/v1/PHASE_7_DOC.md:3847-3848`). No downstream owner re-derives the policy and no base-atlas parameter inheritance is introduced.

3.5 Acquisition, publication, candidate failure and lifetime were checked against the real owners. P3's actual shapes match what §§4.3.1/4.3.2/4.3.5 consume: `PackAssetSnapshot.pack()/manifest()/acquire()` and the closed `Acquired/Missing/Unreadable/InvalidReference(NULL_PATH|NOT_DECLARED)` result (`docs/phase3/v1/PHASE_3_DOC.md:874-893`), the read-only heap position-zero BIG_ENDIAN cursor (`:1925-1928`), the retained-but-uninterpreted `TextureSidecarRef` and its reachable optional-sidecar-only Unreadable recovery with P13 owning the single warning (`:1972-1976,3644-3648`), `TexturePropertyDecl` fields and closed dispositions (`:914-916,2617-2621,3572-3576`), and the three `CustomTextureSpec` variants plus `NoiseTextureSpec.Override` (`:939-955,3628-3652`). Failure classification in §6 is exhaustive over the closed code domain, keeps entry-local `FailedAsset` distinct from whole-build `Failed`, and keeps sidecar recovery outside the failure domain while still requiring a separate `PARAMETERIZATION_UNSUPPORTED` when the recovered baseline is independently illegal. Lifetime matches P5 `:2645-2652` and P7 `:3879-3928`: retire first, `isCurrent()` false immediately, deferred owned deletion after lease drain, borrowed handles released and never deleted, Bound-only transfer, exactly-one closure on every exit, and compensation to off with no revival of a retiring publication. The §5.3 ten-step transaction mirrors P7's current list step for step, including Phase 13 preparation-then-build at step 8 and Phase 9 after Phase 13 at step 9.

## 4. Required corrections

### Correction 1 — §4.3.2's normative acquisition gate still pins schema 21, contradicting the current schema 23 identity it must enforce

- Location: `docs/phase13/v1/PHASE_13_DOC.md:987-989`.
- Failure path: this paragraph is the binding preparation algorithm, not a dated receipt. It instructs preparation to "enforce exact CURRENT_SCHEMA_VERSION equality (21 adopted 2026-09-08)" and to "Reject 20/all other schemas without repair". The current producer constant is `CURRENT_SCHEMA_VERSION = 23` (`docs/phase3/v1/PHASE_3_DOC.md:704`, with `MaterializedSource-v23` at `:581-582,3302`), and the same owner document elsewhere requires exactly 23 (§5.2 row at `docs/phase13/v1/PHASE_13_DOC.md:1750`, receipt at `:1761-1765`, planned check `schema23_exactNestedGate` at `:2201`, R1/U1 at `:1922,1980`). Read literally, §4.3.2 rejects every real P3-issued configuration as `IDENTITY_MISMATCH` before decoding, which would disable all pack-binary acquisition — PNG, raw bytes, noise override and every `.mcmeta` sidecar — while §5.2 simultaneously admits it. Two mutually exclusive normative gates on the one path are not resolvable by §0.12's "prior numeric receipts are historical" rule, because §4.3.2 is not a receipt paragraph.
- Minimal owning resolution (P13 alone; no receiver action, no grant change): in `docs/phase13/v1/PHASE_13_DOC.md:987-989`, replace the parenthetical with the current constant under D-P13-37 (23) and restate the rejection clause as rejecting 22 and all other schemas, keeping the rest of the sentence — matching nested `IdMappingInput`, required same-load assets, exact configuration pairing and `assets.pack()` — unchanged. No other text moves.

### Correction 2 — Stale sibling-phase line anchors, including two misattributed quotations and one quotation absent from the cited document, inside §5.2 and four other active rows

- Locations: `docs/phase13/v1/PHASE_13_DOC.md:1755`, `:1756`, `:1757`, `:1825`, and the same defect class at `:286`, `:292`, `:633`, `:1475`.
- Failure path: each of these rows claims to incorporate a named dependency contract at an exact coordinate, and three of them quote sentences attributed to specific lines. Against the frozen dependency hashes none of the coordinates resolves to the cited content:
  - `:1755` cites `docs/phase4/v1/PHASE_4_DOC.md:1785–1796` and quotes line 1787 as "resolve is detached handle-free inspection, not selection authority". P4 `:1783-1801` is the activation step list and `validateSelection`; the quoted sentence is at `docs/phase4/v1/PHASE_4_DOC.md:2128`.
  - `:1756` cites `docs/phase5/v1/PHASE_5_DOC.md:2344–2375` and quotes line 2363 as "Bound alone transfers lease into closeable sixteen-row snapshot with BoundObject/Unused". P5 `:2357-2369` is `ConsumerFailed.deliveredCount` resize text; the quoted sentence is at `docs/phase5/v1/PHASE_5_DOC.md:2729`, and P5's §5.1 begins after `:2700`, so the whole cited range lies inside §4.11.
  - `:1757` cites `docs/phase7/v1/PHASE_7_DOC.md:504–520,749–819,2230–2268` and states line 2233 incorporates "complete ten numbered steps of §4.1". P7 `:2227-2243` is the `UniformReplayReport` collector contract, P7 `:744-763` is the §3.3 hook-needs table, and the quoted phrase occurs nowhere in the current P7. The real ten-step transaction is `docs/phase7/v1/PHASE_7_DOC.md:3880-3928` (with the §4.1 body around `:900-930`).
  - `:1825` and `:633` cite `docs/phase6/v1/PHASE_6_DOC.md:993-998` for the conditional watershadow rule; that range is the smoothing-decay math. The rule is at `docs/phase6/v1/PHASE_6_DOC.md:1217-1221`.
  - `:286` cites `docs/phase7/v1/PHASE_7_DOC.md:1365–1366` for "DEFERRED(P13,v0.5)"; that range is the IR-12 scope disposition. The App E rows are at `docs/phase7/v1/PHASE_7_DOC.md:1792-1793`.
  - `:292` cites `docs/phase5/v1/PHASE_5_DOC.md:2360–2363` for P5's sole resolver/candidate/binding ownership; that range is resize-dispatch text. The ownership rows are at `docs/phase5/v1/PHASE_5_DOC.md:2726-2735`.
  - `:1475` cites `docs/phase7/v1/PHASE_7_DOC.md:1206–1219` for the hook catalog and CORE/FEATURE/OBSERVER health classes; that range is the shadow-execution traversal guard. The class table and the `require=0`/`expect=1` rule are at `docs/phase7/v1/PHASE_7_DOC.md:1598-1605`, with the enum at `:2933`.
  This is not cosmetic: §5.2 is the binding consumed-contract region, and a receiver or implementer following these pins reads unrelated normative text (for example P7's uniform-replay collector in place of the transaction it is told to mirror). One quotation cannot be verified at any coordinate in the cited document, so the row asserts an incorporation that the frozen evidence does not support. The same defect class was corrected once before for this document's Phase 3 anchors (§0.5), so the standard is already established here.
- Minimal owning resolution (P13 alone; no receiver action): re-resolve each of the eight pins against the frozen dependency bytes and either drop the quoted sentence or re-quote it at its actual line — P4 `:2128`; P5 `:2726-2735` for ownership and `:2729` for the Bound-only sentence; P7 `:1792-1793`, `:1598-1605` (plus `:2933`) and `:3880-3928`; P6 `:1217-1221`. No semantic claim in any of these rows needs to change; each cited contract was independently confirmed present at its real coordinate during this review.

## 5. Notes (optional; no correction required)

5.1 §4.4 and P5 `:2599-2601` both route an "authenticated known non-atlas" unit 0 object to `NonAtlas` and an unprovable association to `Unavailable`, but neither document enumerates which object classes are "known non-atlas" (for example a P13-owned custom texture that wins unit 0). This is observationally inert today because both values reach the identical P5 outcome — the kind's explicit `DefaultFill` — so it is a clarity suggestion only, not a behavioural gap.

5.2 Cross-phase hygiene for Main, outside P13's authority: P6 carries the mirror-image stale pin, citing `docs/phase5/v1/PHASE_5_DOC.md:2360` for resolver text that now lives near `:2726` (`docs/phase6/v1/PHASE_6_DOC.md:620,622`). If Main sweeps Correction 2, the same sweep should cover sibling documents; I did not score this against Phase 13.

5.3 Evidence limits of this review. Everything above is documentary. I ran no build, test, formatter, linter or any other command beyond reading and hashing, and I made no edit. Nothing here proves runtime behaviour: the H13 hook ordering and the outer `loadSprites` try/finally, the Post-only extent acceptance, `TextureHookHealth/application-v2` counts, box-filter mip parity, the C-TX01 byte order of `0xFF7F7FFF`, the noise vectors (`xorshift(-1)=253983`; channel(1,1,1) remainder −115, byte 141) and the D-P13-42 sampling choices are all honestly deferred by the document to future implementation, T2/T3 conformance and fresh reviews. The document's own qualifications — that the available Cleanroom 0.6.12-alpha `TextureMap` patch is corroboration rather than the absent historical pin, that MCP mappings are not observed synchronization, and that the permitted digest corroborates matching mip chains but not the local default policy — are accurate and I did not demand runtime evidence for them. No forbidden source was opened and no OptiFine decompile identifier appears anywhere in the document.

## 6. §5 sufficiency statement

Current §5 grants are sufficient. Every consumed grant is present and reciprocal at its actual owner in the frozen set — P1 packages and `GLCapabilityProfile` limits (`docs/phase1/v14/PHASE_1_DOC.md:2947-2988`) plus the complete synchronous parameter mapping and object baseline (`:4496-4542`); P3 schema 23, `PackConfiguration.assets()` and the R1 typed macro pair; P5 D-P5-50 with the full candidate/binding protocol; P6 D-P6-20 and R7-10/11; P7 evidence, sink, health domain and the ten-step transaction; P8 R7-12/13 with the shadow refresh receiver; P14 D-P14-43 and the parameter/baseline receipts. Every produced contract in §5.1 is mirrored by a named consumer. No boundary defect requiring an owner-and-receiver pair was found, so neither correction names a receiving owner: both are internal to `docs/phase13/v1/PHASE_13_DOC.md`. Because those edits touch §5 text, the document's own closing rule still applies — a fresh whole-document review returning literal PASS remains required before verified downstream consumption, and IR-01, implementation and conformance gates are untouched.

## 7. Verdict

**PASS-WITH-CORRECTIONS** — 2 required corrections, 3 optional notes. The architecture itself is sound: the D-P13-42 sampling baseline and the D-P13-43 authenticated base-object association, the third opaque evidence argument, the two added rejection reasons, the explicit `DefaultFill(kind)` candidate and the sole P5 before-draw refresh through P7/P8 are complete, internally consistent and reciprocally adopted by every receiver. The two corrections are a contradictory normative schema constant on the acquisition path and a set of stale sibling-phase citations in the binding §5.2 region; both are P13-local, both have exact minimal resolutions, and neither changes an interface. PASS is withheld only because those corrections remain outstanding. No implementation, G6 parity, hook result, optional async grant or final integration PASS is claimed by this review.

## Resolutions

2026-09-08 attempt-8 owner documentation fix-up; the frozen review body, notes and
**PASS-WITH-CORRECTIONS** verdict above are preserved. Documentation corrections only:
no schema, grant, interface or fingerprint-domain change, and no build, test, formatter,
linter or other validation command was run.

- **Correction 1 — addressed by D-P13-44.** `docs/phase13/v1/PHASE_13_DOC.md:988-989`
  now enforces exact `CURRENT_SCHEMA_VERSION` equality at 23 under D-P13-37 and rejects
  22/all other schemas without repair. The nested `IdMappingInput`, required same-load
  assets, exact configuration pairing and `assets.pack()` clauses are unchanged, and the
  rest of the document already required 23 (§5.2 row `:1750`, receipt `:1761-1765`,
  `schema23_exactNestedGate` at `:2201`, R1/U1 `:1922,1980`), so the two gates agree.
  Decision row recorded in §11.1.
- **Correction 2 — addressed by D-P13-45.** All eight stale sibling-phase pins were
  re-resolved to the verified current coordinates with no semantic claim changed:
  `:286` → P7 `:1792-1793` (App E DEFERRED(P13,v0.5) rows); `:292` → P5 `:2726-2735`
  (ownership rows); `:633` and `:1825` → P6 `:1217-1221` (conditional watershadow rule);
  `:1475` → P7 `:1598-1605` plus the `HookHealthClass` enum at `:2933`; `:1755` → P4
  `:2128` with the "resolve is detached handle-free inspection, not selection authority"
  sentence re-quoted verbatim at its actual line; `:1756` → P5 `:2726-2735` with the
  Bound-only lease-transfer sentence re-quoted verbatim at `:2729`; `:1757` → P7
  `:3880-3928` (ten-step transaction), with the unverifiable "complete ten numbered
  steps of §4.1" line-quote dropped as permitted. Decision row recorded in §11.1.
- Note 5.2's mirror-image stale pins in P6 (`docs/phase6/v1/PHASE_6_DOC.md:620,622`) are
  outside this document's authority and were not edited here; left for Main's cross-phase
  sweep.
