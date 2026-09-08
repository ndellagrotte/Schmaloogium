# Phase 11 — architecture review 16

**Date:** 2026-09-08. **Frozen document:** `docs/phase11/v1/PHASE_11_DOC.md` SHA-256 `14f70050f54b7f11f7e398670b497afaf6dc1662a4f30b78d92bbbc255784323`. **Verdict:** PASS-WITH-CORRECTIONS (0 blocking / 3 correction / 2 note). **§5 impact:** corrections reword one §5.2 identity sentence, repoint three §4.8/§5.3/§5.4 citation ranges, and restate §5.6's P2 adoption verb as a request, so §5 text changes (no grant-content change) and a fresh verification round remains required before dependent consumption, alongside the doc's own standing §0.13 requirement.

---

## 1. Scope, identity and authority actually verified

- **Doc gate on identity:** recomputed `sha256sum docs/phase11/v1/PHASE_11_DOC.md` → `14f70050f54b7f11f7e398670b497afaf6dc1662a4f30b78d92bbbc255784323`, equal to the frozen hash. Line count 1742 as declared. Review proceeds.
- **Read in full:** the complete phase 11 document (all 1742 lines, in ranges); `docs/design/v3/DESIGN.md` Part I lines 1–1136 (§G0–§G12, including §G1.2 protocol, §G2 D-1..D-10, §G4, §G9 template, §G11 rules of engagement) and the Phase 11 spec at 2279–2356; `docs/research/v1/RESEARCH.md` lines 11–107 (§0–§1).
- **Conformance-map spot checks recomputed against current cited bytes:** RESEARCH Appendix F.6 (`:1493`–`:1512`: both declaration forms, inputs, fourteen booleans, seven-name exclusion, operator list, exact 32-function list, `if`/`smooth`/`between`/`equals`/`in`/`vec2/3/4`, precipitation rule); Appendix D (`:1319`–`:1383`: D.1–D.4 inventories, the five-entry D.4 table, the `:1380`–`:1383` cadence sentence); §6.3 row `:787`; OQ-22 register row `:1028`; §10.1/§10.3 licensing; PD §14 at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:707`–`:728`.
- **Dependency docs read at the assigned depth:** P3 `docs/phase3/v1/PHASE_3_DOC.md` §0 header + §0.61–§0.65, §1, §3.2 rows `:1726`–`:1729`, §4.8 `:2880`–`:2928`, §5.1 algebra `:3539`–`:3603`, §5.3 schema discipline `:4159`–`:4326`, §11 hand-off rows `:5160`–`:5180`; P6 `docs/phase6/v1/PHASE_6_DOC.md` §0 header, §1, §4.13 `:1521`–`:1673`, §4.14 `:1674`–`:1799`, §5.1 rows `:1805`–`:1831`, §11 Phase-11 rows; P7 `docs/phase7/v1/PHASE_7_DOC.md` §4 IR-17/D-P7-51 region `:3399`–`:3586`, §5.5 `:4090`–`:4100`; P2 `docs/phase2/v1/PHASE_2_DOC.md` §4.9 `:1208`–`:1229`, §5.1 `:1593`–`:1610`, §5.3 `:1643`–`:1662`; P12 `docs/phase12/v1/PHASE_12_DOC.md` §5.3(C) `:1571`–`:1584`; P14 `docs/phase14/v1/PHASE_14_DOC.md` §5.8 `:1888`–`:1899`, D-P14-33 `:1966`–`:1973`; P4 `docs/phase4/v1/PHASE_4_DOC.md` RegistryFingerprint identity rows `:1937`–`:1938`, `:2788`, `:2800`.
- **Boundaries honored:** read-only except this file. No file under `reference-src/**`, `docs/**/chatlogs/`, no root `*.txt`, no other `PHASE_*_REVIEW_*.md`, no `docs/build/**`, no `docs/PHASE_INTEGRATION_REVIEW.md` was opened. No build, test, Gradle, GL, or network execution of any kind.
- **Authority:** the doc's v3 pin (`docs/design/v3/DESIGN.md`, unadopted revision, per §G0.4/§G0.5 REV5) is correctly declared with the honest "integration fix-up unverified" status; the assignment row (depends on 3, 6; no Phase 7 dependency; OQs: —; v0.4) matches DESIGN `:2281`.

## 2. Scope and §5 audit

**Scope — in coverage (spec 2292–2327 vs doc):** every item is designed and mapped — grammar/literals/`pi`/booleans (§4.2), full operator set (§3.1/§4.6), complete member/matrix access (§4.2), the exact 32-function surface with `if`/`smooth` (§3.2/§4.6/§4.7), input binding incl. biome params, `BIOME_*`, fourteen view booleans (§3.3/§4.4/§4.10), the seven-name per-draw exclusion (§3.3, D-P11-9), `variable.*` memoized intermediates (§4.5), typing/coercion (§4.3), every-switch-after-built-ins cadence (§2.2/§4.8), smooth per-id persistent time-corrected state (§4.7), evaluator interface + decision criteria + v0.4 recommendation + OQ-22 handoff (§4.11/§10.1), error isolation per the ladder (§4.9/§6), provider SPI (§4.10). Nothing from Scope — in is dropped.

**Scope — out discipline:** value acquisition stays Phase 6 (§1.2/§5.3), Properties capture stays Phase 3 (§1.2/§5.2), GUI stays Phase 12 (§1.2/§5.5.1); compiled evaluator and measurement are Phase 14's (§1.2/§10.1); orchestration/adapter is P2's (§5.6); `mod.glue` owns the snapshot implementation (§1.2/§4.10). No scope-out concern is designed here.

**Template completeness (§G9):** all thirteen sections present and substantive (§0–§12). No OQ is assigned to Phase 11 (DESIGN `:2281`), and §10 correctly documents the OQ-22 ledger emission as a handoff with question/procedure/success/trigger/candidate-success/fallback — a full §G4.4-shaped spike spec even though unowned.

**Conformance-map audit:** §3.1 and §3.2 leave zero unmapped in-scope rows. The function table is exactly the F.6 list — 32 named callables, no extras, each carrying surface provenance plus the §4.6/D-P11-13 exact-semantics split. Spot-checks that passed against cited text: seven-name exclusion (RESEARCH `:1501`–`:1505` five-entry D.4 + `fogMode`/`fogColor`, "does not override or narrow D.4"; P6 `:1602`–`:1606` matches); fourteen booleans (RESEARCH `:1499`–`:1501`); cadence sentence (RESEARCH `:1380`–`:1383`); permitted catalog = D.1–D.3 minus `fogMode`/`fogColor` (both are D.2 rows; `fogDensity`/`skyColor`/`centerDepthSmooth` correctly remain permitted); `pi` = binary32 of 3.1415926 (`0x40490fda` — recomputed, correct); precedence/associativity conventional with no Pintonium aliases; PD §14's "full checklist" claim honestly contradicted by the incomplete `IrisFunctions` registry (§11.2 item 2, PD `:719`–`:725`).

**Interface honesty:** consumed P3 surface exists in P3 §5.1 (`:3545`–`:3566` algebra field-for-field identical to §5.2's quotation) and §5.3 (`CURRENT_SCHEMA_VERSION` is 23, `:4161`); consumed P6 surface exists in P6 §4.13/§4.14/§5.1 (bridge `:1535`–`:1540`, lookup/values `:1542`–`:1560`, commands/results, six submit rules, `Bool1`-only-bool, `retire(UNPUBLISHED_ABORT|REPLACEMENT|SHUTDOWN)` with `Retired|AlreadyRetired|Rejected(WRONG_THREAD|ACTIVE_CALLBACK)`, no `reset(CLOSE)` alias). Exported surfaces have recorded receiver adoption: P7 IR-17 + D-P7-51 + §5.5 row 11 (construction/install/compile/`schmaloogium:typed-ast-interpreter-v1`/activation/reset ordering/terminal-close-before-retire/typed collector), P12 §5.3(C) (snapshot consumption, serial/pack rejection, clear lifetime), P14 §5.8 (exact 10-field `ExpressionMetrics` acceptance, measurement ownership), P3 §11 `:5165`–`:5169` (D-P11-27 first-owner policy acknowledged by the producer). The one exception is P2 — finding C-3.

**Binding decisions (D-1..D-10) and §G4.2:** no contradiction. D-6 seam enforced (pure `:engine`, §1.3/§7/§8.3); D-8/G7 rule 2 honored — the smooth correction equation is a declared behavior-only restatement of the allowed digest, not decompiled structure; D-10 served by §5.6/§8.4; G2.4 ladder mapped row-by-row in §6 with no whole-program failure path; G2.5 interpreter-first with a measured, non-bypassable budget.

**Licensing/Pintonium:** stareval gate closed to clean-room exactly per §G11.2 rule 3 / §G7 item 8 (§0.2 item 3, §0.3, D-P11-1, §3.4 row); PD-derived claims in §3.4 carry `[V:observed — Pintonium <path>]`; PD §17/§18-style do-not-inherit rows handled (incomplete registry, update/push split not adopted, no location maps); no AGPL trace — `glsl-transformer`/`glsl-transformation-lib` appear only as never-read prohibitions.

**Current-identity discipline:** schema23 containing/nested/inspection + `MaterializedSource-v23` current (P3 §0.65, `:704`, `:4161`; independently matched by P14 D-P14-33) and correctly the governing §5.2 receipt via D-P11-25/D-P11-28; `projectionVersion=1` and the nine source-free trees correctly restated as unchanged; P6 disposal mapped only to retirement reasons with P11 CLOSE retained terminal and never forwarded (both sides consistent, P6 `:1681`–`:1682`, `:2346`–`:2355`); capture-plan/run-manifest/health-report identities are correctly absent from this doc. One stale identity found — C-1.

## 3. Required corrections

### C-1 (correction) — stale P4 registry-fingerprint identity presented as current in §5.2

- **Sites:** `docs/phase11/v1/PHASE_11_DOC.md:1207` (§5.2, D-P11-20 schema21 receipt block): "P4 D-P4-31's `RegistryFingerprint/own-build-v1` **remains** opaque identity in normal composition".
- **Failure path:** the D-P11-25 receipt — the only governing (non-historical) §5.2 grant — is silent on the P4 registry identity, so the own-build-v1 sentence is the sole P4-identity guidance a composition implementer or fresh reviewer carries forward. P4's current bytes supersede that domain: D-P4-43 (`docs/phase4/v1/PHASE_4_DOC.md:1937`–`:1938`) "supersedes D-P4-33's historical `RegistryFingerprint/positional-route-v2` with `RegistryFingerprint/profile-selection-v3`", making `own-build-v1` (D-P4-31, `:2788`) two generations historical. A receiver keying composition identity on own-build-v1 never matches P4's actual profile-selection-v3 token domain, and reviewer approvals record a dead identity as live.
- **Owner+receivers:** P11 owns the §5.2 sentence; P4 owns the identity domain itself; downstream holders of the opaque token are the P7 composition (and P2 inspection, per D-P4-43's own rows). No P3/P6 grant content is affected; the opacity treatment itself is correct and unchanged.
- **Minimal resolution:** inside the D-P11-20 block, mark own-build-v1 as the receipt-time domain ("was the opaque composition identity at receipt; current domain is P4 D-P4-43's `RegistryFingerprint/profile-selection-v3`"), or add the current profile-selection-v3 identity to the governing D-P11-25 receipt. Either edit is textual; the scheme-23 admission logic is untouched.

### C-2 (correction) — three P6 §4.13 citation ranges undershoot the bytes they claim (stale against current P6 §0.28/§0.29 amendments)

- **Sites:** (a) `docs/phase11/v1/PHASE_11_DOC.md:806` (§4.8): prefix-commit + superseding-branch claim cited to `docs/phase6/v1/PHASE_6_DOC.md:1637`–`:1658`; (b) `docs/phase11/v1/PHASE_11_DOC.md:1242` (§5.3): "typed immutable upload submission and closed refresh result (`:1561`–`:1578`)"; (c) `docs/phase11/v1/PHASE_11_DOC.md:1262` (§5.4): grants "binding in Phase 6 §4.13 (`docs/phase6/v1/PHASE_6_DOC.md:1521`–`:1658`)".
- **Evidence (recomputed against current bytes):** the accepted-prefix commit rule text is at P6 `:1659`–`:1662` ("Phase 6 commits only the accepted prefix, in order…") and the superseding invalid-counter branch at `:1663`–`:1668` ("This invalid-result branch supersedes the otherwise-applicable accepted-prefix commit rule") — both outside site (a)'s range, which ends at the counter-must-match-ledger sentence. The closed `CustomRefreshResult` (`NoCustoms`/`Completed`/`Aborted`) is at `:1579`–`:1585`, outside site (b)'s range. §4.13 now ends at `:1673`, so site (c) undershoots the section it names by fifteen lines. The claimed semantics themselves are accurate — only the coordinates lag P6's 2026-09-08 §0.28/§0.29 amendments, the exact drift class D-P11-29 exists to prevent.
- **Failure path:** the next verifier recomputing anchors per the doc-gate finds cited ranges that do not contain the claimed commit/supersession/refresh-result bytes and rejects the receiver receipts; an implementer reading only the cited spans misses the invalid-counter branch that discards the whole accepted batch on a ledger mismatch — the operative disposal rule for `Completed` as well as `Aborted`.
- **Owner+receivers:** P11 owns the citations; P6 owns the moved bytes; P7's composition (implements prefix/retirement ordering) and the P2 adapter (asserts prefix counters) consume them.
- **Minimal resolution:** repoint site (a) to `:1659`–`:1668` (or section-level §4.13), site (b) to `:1561`–`:1585`, site (c) to `:1521`–`:1673` or to bare "§4.13". No semantic edit is required; the incorporated text already matches the verified endpoints.

### C-3 (correction) — §5.6 asserts P2 adoption of RUN-EXPRESSION-CONFORMANCE that no current P2 bytes contain

- **Sites:** `docs/phase11/v1/PHASE_11_DOC.md:1304` (§5.6 opening: "R11-2 requests and Phase 2 **adopts** the named RUN-EXPRESSION-CONFORMANCE…"), reinforced by the §5.1 consumer cell at `:1153` naming "Phase 2 RUN-EXPRESSION-CONFORMANCE" as if existing.
- **Evidence:** P2's current named-run catalogue (`docs/phase2/v1/PHASE_2_DOC.md:1213`–`:1226`) defines twelve run ids and no `RUN-EXPRESSION-CONFORMANCE`; P2 §5.1 (`:1593`–`:1610`) exposes/consumes no such row; P2 §5.3 (`:1655`–`:1657`) is explicit that a run absent from §4.9 "is a request against this document, made in the requesting phase's §5 and answered by a §G1.3 fix-up here — not invented locally". P11's own governing statements (D-P11-16 "receiving contract", §11.3 "Owner/receiver adoption is unverified; no run is claimed", §0.13) correctly treat adoption as outstanding — only the §5.6 verb overstates it in the present tense.
- **Failure path:** a harness implementer reads §5.6 as a verified dependency input and builds against a `RunId` absent from P2's `RunRegistry`, bypassing the §G5.3 gating invariant that dependency docs be verified before consumption; the mismatch surfaces only at conformance wiring.
- **Owner+receivers:** requester P11; owner P2 must add the run and adapter row through its own fix-up before the contract binds; conditional downstream consumer is P14's differential reuse (§5.1 `:1153`). P6's `CustomSubmitResult`/`CustomUploadCommand`/`CustomRefreshResult` ownership claimed by §5.6 is correctly anchored in P6 §4.13.
- **Minimal resolution:** reword §5.6's first sentence to request posture ("P11 requests; adoption by P2 is pending P2's §4.9/§5 fix-up and remains unverified"), and gloss the §5.1 consumer cell accordingly ("Phase 2 RUN-EXPRESSION-CONFORMANCE — requested, unadopted"). D-P11-16 and §11.3 need no change.

## 4. Notes

- **N-1 — §2.2 overview inverts the clock/context order.** §2.2 lists "snapshots the non-Phase-6 context exactly once, opens one evaluation epoch, advances the refresh clock…", while binding §4.8 steps 3–4 open the epoch and advance the clock (step 3) *before* issuing the `ExpressionContextRequest` (step 4), and §4.7 states "This clock observation commits before context sampling". §2.2 is outside §5's incorporation list (§§2.3, 4.1–4.5, 4.8–4.12), so this is not contract-bearing, but the overview should be aligned at the next fix-up to avoid a mis-implementation of step order.
- **N-2 — §1.2's P3 anchor is loose at both ends.** `docs/phase3/v1/PHASE_3_DOC.md:2891`–`:2908` begins with unrelated `PixelType` enumeration (`:2891`–`:2896`) and ends before the attribution/duplicate-retention sentences (`:2909`–`:2915`) that the bullet's "preserves raw expression text and source order" claim rests on. The declaration-capture dispatch itself (`:2898`–`:2908`) is inside the range, so the claim is verifiable; tighten to `:2898`–`:2915` when §5 anchors are next touched (same family as C-2, lower impact since §1 is non-incorporating).
- **Review boundary:** the doc's `[V:doc]` rows citing `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties` (§0.3, §3.1–§3.3, §4.2, §4.7) and the `SHADER_ENGINE_IMPL.md` smooth digest were **not** independently re-verified — `reference-src/**` is outside this review's permitted inputs. Those rows rest on the author session's recorded reads; their provenance tags and the §G7 rule-2 behavior-only handling are internally consistent.
- The fourteen `ViewEntityFlags` fields, the six D-P11-27 receiving vectors' arithmetic (truncating `%` = −2/`0xc0000000`, floor `fmod` = 1, `frac(−1.25)` = 0.75, `round(−1.5)` = −1, conversion-memo 4.5/`0x40900000`), and both smooth late-use traces were recomputed by hand against §4.3/§4.6/§4.7 and are internally exact, including clock sequences 0,1,1,2,2 and committed-time columns.

## 5. Areas audited and found sound

- **Shared-graph scope (D-P11-27/28/29/30, C14-1 repair):** one uniform/variable namespace with first-owner registration before expression validation; declared type/kind fixed independently of expression success; `DUPLICATE_NAME` for every later occurrence with no fallback-to-later-owner; invalid owners stay resolvable so readers get `INVALID_DEPENDENCY`, not `UNKNOWN_NAME`; `UNIFORM`-only upload designation; exact SCC membership (multi-vertex or self-edge only), reverse-reader invalidity propagation, preserved primary errors, independent prerequisites of cycles; prerequisite-first emission with source-ordinal tie-breaks; converted-once memo slots with declaration-order submission. §1.1/§1.2/§1.3/§3.3/§4.1/§4.4/§4.5/§5.1 are mutually consistent, P3's §11 receipt acknowledges the policy, and all fifteen §5.6 vectors pin it.
- **schema23 admission identity:** governing D-P11-25 receipt matches P3 §0.65/`CURRENT_SCHEMA_VERSION`=23/`MaterializedSource-v23`/nested-IdMappingInput exactly; D-P11-28's header/§5.2 consistency done; older receipts correctly demoted to historical.
- **Terminal lifecycle split:** P11 `CLOSE` terminal and P11-owned; P6 disposal exclusively `retire(UNPUBLISHED_ABORT|REPLACEMENT|SHUTDOWN)` with final-use ordering, `Retired|AlreadyRetired` releasing borrowed services, `Rejected(WRONG_THREAD|ACTIVE_CALLBACK)` preserving ownership; P4 result-dependent reason choice (Rejected candidate caller-owned; Accepted/RecoveredOff → REPLACEMENT after actual invalidation) matches P6 §4.14.3 verbatim; P7 D-P7-51 ordering agrees step-for-step with §4.12's map, including WORLD_EPOCH before P6 reset and FRAMEBUFFER_RESIZE having no P6 counterpart.
- **GUI projection (IR-17):** §4.9.1 record shapes identical to P7 `:3536`–`:3551` and P12 `:1571`–`:1584`; ACCEPTED reserved for final admitted composition; source-free fixed-template summaries; serial/pack rejection; clear-before-selection/off/shutdown; no third/fourth channel and no reverse conversion from P1 records.
- **Diagnostics/lifecycle mechanics:** closed severity/channel domains; stable-ID derivation inputs (never prose); synchronous delivery with contained sink failure and coalescing-per-activation; `NoCustoms` emits nothing; unsupported-backend rejection before adaptation in both entry points, empty list included; metrics record identical field-for-field to P14 §5.8's acceptance.
- **Semantics surface:** strict binary32 per operation; truncating `%` vs floor `fmod` distinction; lazy `&&`/`||`/`if`/`in` with non-rewinding left-to-right random consumption and eager definition scheduling; transactional smooth value/timestamp commits with per-cell elapsed accrual and snap/ramp equation; degradation ladder rung-1/2/2a mapping with no whole-program path; `:engine` purity and off-thread compile permission (G2.3 `:410`–`:415`), interpreter budget as measured gate with OQ-22 handoff owned by P14 (P14 §5.8 accepts method and record).
- **Licensing posture:** clean-room stareval outcome recorded with the required gate wording and `IrisFunctions` cross-reference both ways; PD §14's overclaim reported as an input contradiction rather than smoothed over; no prohibited source listed as read.

## 6. Verdict

**PASS-WITH-CORRECTIONS** — 0 blocking, 3 correction (C-1 stale P4 identity in §5.2; C-2 three stale P6 §4.13 anchor ranges in §4.8/§5.3/§5.4; C-3 §5.6's present-tense P2 adoption claim), 2 notes (N-1 §2.2 ordering prose; N-2 §1.2 anchor looseness). No finding requires rebuilding the document: all three corrections are coordinate/wording edits whose underlying incorporated semantics were verified against current dependency bytes. Because corrections touch §5 text (§5.1 gloss, §5.2 sentence, §5.3/§5.4 coordinates, §5.6 verb), the doc does not close as verified this round: a fresh verification round remains required, and the doc's own standing §0.13 requirement is unchanged. No build, test, GL, or network command was executed by this review; all evidence above is recomputed from current repository bytes.

## Resolutions

Resolved 2026-09-08 by the Phase 11 §G1.3 fix-up session; the report body above is byte-identical,
with only this section appended.

- **C-1 — applied.** §5.2's D-P11-20 receipt (was `:1207`–`:1208`, now `:1217`–`:1219`) no longer
  presents `own-build-v1` as current; it now reads "P4 D-P4-31's `RegistryFingerprint/own-build-v1`
  was the opaque composition identity at receipt; the current domain is P4 D-P4-43's
  `RegistryFingerprint/profile-selection-v3`", keeping the opacity treatment and the
  no-new-getters/no-resolution-evidence posture unchanged. Repoint verified before editing:
  `docs/phase4/v1/PHASE_4_DOC.md:1937`–`:1938` ("D-P4-43 supersedes D-P4-33's historical
  `RegistryFingerprint/positional-route-v2` with `RegistryFingerprint/profile-selection-v3`") and
  D-P4-31's own-build-v1 cutover row at `:2788`. Recorded as **D-P11-31** (§11.1, `:1675`) and
  §0.15 (`:171`–`:180`).
- **C-2 — applied.** All three named sites recomputed against current Phase 6 bytes: (a) §4.8
  (was `:806`, now `:816`) `:1637`–`:1658` → `:1659`–`:1668` — verified the accepted-prefix commit
  rule at P6 `:1659`–`:1662` and the superseding invalid-counter branch at `:1663`–`:1668`; (b)
  §5.3 upload/refresh bullet (was `:1242`, now `:1253`) `:1561`–`:1578` → `:1561`–`:1585` —
  verified the closed `CustomRefreshResult` at P6 `:1579`–`:1585`; (c) §5.4 binding sentence (was
  `:1262`, now `:1273`) `:1521`–`:1658` → `:1521`–`:1673` — verified §4.13 ends at `:1673` with
  the §4.14 heading at `:1674`. Additionally, the identical stale `:1637`–`:1658` coordinates also
  sat on §5.3's accepted-prefix bullet (was `:1246`, now `:1257`) and were recomputed under this
  same correction to section-level §4.13 (`:1521`–`:1673`), since that bullet's claims span the
  submission rules through the prefix-commit rule. No semantic text changed — the incorporated
  claims already matched the verified endpoints. Logged in §0.15 under the D-P11-29
  coordinate-drift discipline; pure repoints, no new decision ID.
- **C-3 — rejected with evidence.** The premise that "no current P2 bytes contain"
  RUN-EXPRESSION-CONFORMANCE is false: the review audited the superseded
  `docs/phase2/v1/PHASE_2_DOC.md`, while the current `docs/phase2/v2/PHASE_2_DOC.md` records
  adoption at D-P2-28 — `:247` "D-P2-28 adopts `RUN-EXPRESSION-CONFORMANCE`"; the §4.9 run-table
  row at `:1758` with adapter prose from `:1763`; the §5 checklist row at `:2430`; the adoption
  note at `:2680`–`:2681` ("P11 §5.6 R11-2 is now adopted as `RUN-EXPRESSION-CONFORMANCE`
  (§4.9), not substituted with a Properties or shader-load run"); and the §11.1 decision row at
  `:3241`. P11 §5.6's "requests and Phase 2 adopts" and the §5.1 consumer cell therefore stand
  unchanged; §5.6 was not edited. Adoption's verification state remains governed by §0.13 and
  P2's own unverified markers, which is orthogonal to the existence claim C-3 disputed.
- **Notes N-1/N-2 — not applied**; neither was incorporated into an applied correction.
