# Phase 8 whole-owner architecture review — R11 / attempt7

**Owner:** `docs/phase8/v1/PHASE_8_DOC.md`  
**Frozen inventory SHA-256 supplied by Main:** `590dfd9f2bad5a2fcb2a40d2af14fc3b165d54c934cba36fb29a5ec6141888b5`  
**Result:** PASS-WITH-CORRECTIONS — one substantive correction, one evidence limitation. The hash identifies the assigned inventory; this review did not run a checksum command.

## Scope and selected authority

Independently read the entire current Phase 8 owner, §§0–12, including its detailed state, traversal, hook-health, lifecycle, failure, interface, and verification contracts. Followed `docs/MOVES.md` for versioned coordinates. The authority selected by the owner's header is `docs/design/v2.0-RC3/DESIGN.md:92–1109` and its Phase 8 assignment at `:1957–2034`, not the globally newest design. Read the governing portions of `docs/research/v1/RESEARCH.md`, including §§0–1, §4.5, Appendices A.3, B.2–B.3, D.2–D.3, E's relevant rows, and F.1.

Read the declared dependencies' binding §5 contracts and incorporated load-bearing definitions: `docs/phase4/v1/PHASE_4_DOC.md`, `docs/phase5/v1/PHASE_5_DOC.md`, `docs/phase6/v1/PHASE_6_DOC.md`, and `docs/phase7/v1/PHASE_7_DOC.md`. Traced the relevant receiving branches in those owners and in `docs/phase2/v2/PHASE_2_DOC.md`, `docs/phase9/v1/PHASE_9_DOC.md`, and `docs/phase13/v1/PHASE_13_DOC.md`. Historical review verdicts and acknowledgment text were not treated as proof. This does not certify those sibling owners.

## Independent checks

- **Planning and publication identity:** Checked pure planning before provider construction, the requested/absent distinction, structural rejection precedence, final-registry creation, separate plan and publication identity, and invocation authentication. Phase 8 consumes the current registry identity rather than recomputing an older fingerprint. The current profile-selection identity is defined at `docs/phase4/v1/PHASE_4_DOC.md:1899–1947` and incorporated by Phase 8's binding registry amendment at `docs/phase8/v1/PHASE_8_DOC.md:1757–1760`.
- **Pipeline and ownership:** Traced accepted-estate authentication, disabled-shadow neutralization, same-selection/context root-shadow activation, camera publication, shadow depth lifecycle, Bound-only lease transfer, and independent cleanup. The receiving texture contract actually validates the expected publication before lease acquisition and transfers ownership only on Bound (`docs/phase13/v1/PHASE_13_DOC.md:1321–1355`). Phase 7's invocation-result receiver distinguishes suppression, completion, and failure rather than falling through to main drawing (`docs/phase7/v1/PHASE_7_DOC.md:3006–3105`).
- **Camera and traversal:** Checked orthographic/perspective construction, signed-remainder snapping, day/night angles, celestial-vector conventions, full versus extruded-prism culling, exactly one forced vanilla rebuild, pass-local visit handling, unchanged main terrain frame token, lossless pending-update handling, and restoration on exceptional exits. Also traced the prescribed solid/cutout_mipped/cutout ordering, Forge pass 0, one depth split, optional translucent terrain, Forge pass 1, mipmap generation, prepared submissions, and ID/color restoration. One omitted native state effect is reported below.
- **Receiver-bound outline exclusion:** The owner selects the outer `isRenderEntityOutlines` invocation, not an inner new-outline-list branch. Its exact-receiver, non-reentrant entity-call guard additionally requires the current Phase 7 execution and slot epoch (`docs/phase8/v1/PHASE_8_DOC.md:1568`, with §4.8.2). The actual receiving contract applies that predicate suppression only in the guarded call and preserves the original predicate elsewhere (`docs/phase7/v1/PHASE_7_DOC.md:1772–1789`). The bounded vanilla body and current Cleanroom patch corroborate why excluding the entire branch also handles retained `entityOutlinesRendered` before tile-entity traversal. This is an architecture check, not evidence of an applied transformation.
- **Health wire contract:** Independently checked the current catalogue: OUTLINE 1 + BLOB 1 + CLOUD 1 + ENTITY 13 + FORGE 2 + REBUILD 2 + RESTORE 31 + SLOT 1 + TERRAIN 1 + TRAVERSE 7 = 60 scalar rows, with 59 non-CLOUD requirements. Checked expected-one observations, ASCII order, actual-count preservation, per-row dispositions, aggregate rules, and the flattened-v2 preimage/domain at `docs/phase8/v1/PHASE_8_DOC.md:1570–1676`. Phase 7 rejects malformed/older evidence and copies the projection unchanged (`docs/phase7/v1/PHASE_7_DOC.md:1772–1789`); Phase 2 validates the same owner projection (`docs/phase2/v2/PHASE_2_DOC.md:1360–1371`). These receivers agree on the presently declared catalogue; C1 identifies an additional state obligation that catalogue does not presently cover.
- **Interop and supporting evidence:** The required narrow framework-event query returned no result; a broader render-event query was inspected rather than interpreting the empty result as comprehensive proof. The Forge setter/getter and current pass-aware source patches corroborate the chosen scoped-pass mechanism. The permitted author documentation, bounded shadow behavior digest, Pintonium design digest, and current camera-related source regions were inspected under the provenance limitation below.

## Substantive correction

### C1 — Preserve the main translucent-sort cache across shadow terrain

**Severity:** P2 / substantive contract correction.  
**Owner anchors:** `docs/phase8/v1/PHASE_8_DOC.md:1389–1394` prescribes the translucent call; `:1563` specifies the native four-argument `RenderGlobal.renderBlockLayer` invocation. The reversible-state list at `:1022–1046`, detailed traversal restoration in §4.7, and H8-RESTORE-01 at `:1561` omit `prevRenderSortX`, `prevRenderSortY`, and `prevRenderSortZ`.

**Evidence and observable breakage:** The actual vanilla [`RenderGlobal.renderBlockLayer` body, lines 1109–1136](https://github.com/KealJones/mc-1.12.2-source_files/blob/master/src/minecraft/net/minecraft/client/renderer/RenderGlobal.java#L1109-L1136) compares the render-view entity's position against those three caches. On movement greater than one block, it updates all three caches and schedules transparency sorting for at most the first fifteen eligible chunks in the currently installed `renderInfos`. Phase 8 deliberately installs its shadow traversal list before invoking that body. If translucent shadows are enabled and the shadow list differs from the main list—for example, a tight shadow prism excludes a main-visible transparent chunk—the shadow call consumes the movement threshold using the shadow list. The later main translucent call sees the same entity position and therefore skips its sorting branch. Main-only transparent chunks can retain stale blending order, including after the camera stops. The mapped 1.12.2 fields are `field_147596_f`, `field_147597_g`, and `field_147602_h`, independently resolved through the mapping tool.

The actual reciprocal branch does not contain this effect: Phase 7 explicitly preserves the vanilla operation during authenticated shadow execution (`docs/phase7/v1/PHASE_7_DOC.md:1179–1189`), and H-TERRAIN-02 bypasses main copy/deferred/water policy rather than the native sorting branch (`:1609`). Restoring only the traversal list and five `lastViewEntity*` caches therefore cannot preserve main rendering behavior.

**Minimal owner fix:** Add exact snapshot/restoration of these three native sort-cache doubles around the shadow translucent invocation, with guaranteed restoration on both normal and exceptional exit. Preserve legitimately scheduled work; do not replay traversal or indiscriminately roll back renderer queues. Specify the mapped accessor obligations alongside the existing restoration ledger. Because §5.1 incorporates the restoration and complete-health contracts (`docs/phase8/v1/PHASE_8_DOC.md:1690–1700`), update that interface and coordinate the corresponding Phase 7/Phase 2 catalogue receivers. The owner's own domain rule at `:1666–1672` requires a new health domain if the catalogue/accessor meanings change. Record an architecture fixture covering a moved camera and a main-visible transparent chunk absent from the shadow list, including a thrown shadow draw; executing that future fixture is not required at this documentation gate.

## Notes and limitations

### N1 — Historical source pins remain unverified; current corroboration is narrower

**Severity:** informational evidence limitation; no additional owner correction counted. The historical inputs named at `docs/phase8/v1/PHASE_8_DOC.md:40–50` and `:71–75` reference `pintonium-9c2fcc1` and `cleanroom-0.6.6-alpha`. Those historical paths were not available in the inspected inventory. Current corroboration instead used bounded regions under `reference-src/Pintonium-main/` and `reference-src/Cleanroom-0.6.12-alpha/`, plus the permitted author documentation/digest and the linked vanilla source mirror. Those current files do not prove historical pin identity, exact historical loader behavior, or source-license clearance. No source was copied into implementation, and no comprehensive license audit is claimed.

This was a read-only architecture review. No builds, tests, runtime launches, hook application, native rendering, checksum validation, or implementation certification were performed. No forbidden implementation trees or transcripts were read. Existing future runtime and release gates remain gates, not completed evidence.

## Final verdict

The current owner has coherent principal planning, publication, physical-binding, outline-guard, and declared health-receiver contracts, but its promised restoration is incomplete for the native translucent layer it explicitly invokes. C1 is a bounded owner/interface correction rather than a structural redesign. **Section 5 impact: yes.**

PASS-WITH-CORRECTIONS

## Resolutions

2026-09-08 architecture correction; the original review body, C1/N1, counts and
PASS-WITH-CORRECTIONS verdict above remain historical and unchanged.

- **C1 — resolved in architecture by D-P8-37:** owner §§4.4/4.7/4.8.4 now require exact
  receiver-bound snapshot and independent finally restoration of prevRenderSortX/Y/Z on
  normal/throw paths around the actual shadow translucent call. Legitimate tasks/completed
  sorting survive; no queue rollback, changed frame token or traversal replay. Restore failure
  forbids main continuation. §§4.13/5 specify six independent mapped GET/SET observations,
  complete 66-row/65-non-CLOUD flattened-v3 health, and unchanged scalar wire shapes.
  §§8/12 include moved-camera main-only transparent-chunk, injected-throw and each-accessor
  failure fixtures. P7/P2 receiving edits and fresh owner/receiver review are required.
- **N1 — retained limitation:** no historical source-pin, loader, source-license or runtime
  proof is newly claimed. The correction uses the reported native effect and declared mapped
  targets as architecture input, not evidence of applied hooks.
- **Related receiver grant, not a second R11 finding:** D-P8-38 exposes plan-independent
  public CelestialMath angles/sample for P7 R43 C1, preserving one formula and unchanged P6
  event. The originating P7 report is resolved by its owner.

Section 5 changed and remains unverified. No builds, tests, validators, native rendering or
implementation certification were performed; no governing/release gate is waived.
