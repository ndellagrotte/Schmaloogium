# Phase 11 — architecture review 15

**Date:** 2026-09-08. **Frozen document:** `docs/phase11/v1/PHASE_11_DOC.md` SHA-256 `31869a3cfa698599da74e897cf772f2c94050035495021b4088741922e2f638d`. **Verdict:** PASS-WITH-CORRECTIONS (3 corrections, 2 notes). **§5 impact:** YES — C15-1 hardcodes the superseded schema-22 admission gate inside the §0 header and the binding §5.2 body while the newest §5.2 receipt (D-P11-25) and current Phase 3 bytes make 23 current; C15-2's stale P3/P6 line anchors sit inside the §§5.2–5.4 incorporations. The substantive content of every §5 grant matches current producer and receiver bytes (P2 D-P2-69 adopts all six D-P11-27 vectors; P7/P12/P14/P1 endpoints match), so §5 is sufficient in semantics but requires these two textual fixes plus the §1.1 cutover residue (C15-3) before it is internally consistent; per the owner's synchronization rule the §5 edits require another fresh verification round.

---

# Phase 11 Architecture Review — R15 / Frozen Attempt 8

## 1. Identity, scope, and authority

1. **Owner:** `docs/phase11/v1/PHASE_11_DOC.md`, complete §§0–12 (1,737 lines), read in full.
2. **Frozen SHA256:** `31869a3cfa698599da74e897cf772f2c94050035495021b4088741922e2f638d` — verified by checksum against the working tree; identical to the attempt-8 inventory entry for phase 11 (`docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_8.json`, `reviewRound: 15`). Attempt 8 itself recorded an infrastructure failure (`usage_limit_reached`) with no phase-11 report and no verdict change; this review supplies the missing round on the same frozen bytes.
3. **Governing design:** `docs/design/v3/DESIGN.md`, selected by the owner header ("The commissioning request explicitly selected v3"), confirmed against `docs/MOVES.md` (Phase 11 v1 §0 declares v3; v3 is adopted by Phases 2, 10, 11, 13 only). I read Part I §§G0–G12 (threading model §G2.3 permitting off-thread expression compilation; degradation ladder §G2.4 rungs 1/2a; §G2.5 clean-code-first; module map §G3.1 requiring pure `:engine`) and the Phase 11 specification at `:2279`–`:2353`.
4. **Governing research:** `docs/research/v1/RESEARCH.md` §0/§1, §3.4 cadence (`:1380`–`:1383`), §6.3 expression row (`:781`–`:787`), Appendix D.1–D.4 (`:1329`–`:1393`), Appendix F.6 (`:1491`–`:1512`), and the OQ-22 ledger row (`:1028`). All RESEARCH anchors quoted by the owner resolve correctly in current bytes.
5. **Primary evidence read directly:** `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:326–425` (including the load-bearing author example at `:421`–`:422`: `uniform.float.screenDark=max(...)` then `uniform.vec3.screenDark3=vec3(screenDark, heldItemId, biome)`) and `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:594–622` (smooth correction equation `countUpdates=fade/Δt; kCorr=4.61 − 1/(0.13 + countUpdates/10); k=clamp(Δt/fade·kCorr,0,1)`, distinct up/down fades, reset on pack/world/resize). No decompiled source, no chatlogs, no root `*.txt`, no Oculus transformation path, no glsl-transformer/glsl-transformation-lib source was read.
6. **Historical evidence:** `docs/phase11/reviews/PHASE_11_REVIEW_14.md` (C14-1 → D-P11-27 resolution appended) and current `docs/PHASE_INTEGRATION_REVIEW.md` Resolutions (IR-03, IR-10, IR-15, IR-17, IR-20 remediation records; 3→11 and 6→11 boundary rows) were treated as immutable evidence, not certification.

## 2. Governing-design and research conformance

1. The pure-`:engine` placement, complete grammar/operator/function ownership, biome/view provider SPI, program-switch cadence after built-ins, error-isolation rung 1, rung-2a `Aborted` reservation, and the interpreter-first evaluator decision with OQ-22 handoff all match the v3 Phase 11 row and §G2.3–§G2.5.
2. The seven-name exclusion union (five D.4 dynamics plus `fogMode`/`fogColor`) follows RESEARCH App F.6 (`:1501`–`:1505`) over the DESIGN row's stale five-name restatement (`docs/design/v3/DESIGN.md:2300–2302303` verified to list only five); D-P11-9 and the §11.4 upstream request remain accurate, and current Phase 6 independently adopts the same seven-name union (`docs/phase6/v1/PHASE_6_DOC.md:1602–1606`, §5 row `:1819`).
3. The function/operator surface, fourteen view booleans, both declaration forms, matrix two-index access, and vector member set match App F.6 and the shipped author document exactly; no extra function is admitted (the Pintonium checklist is used only as a negative audit, consistent with the unregistered `round`/vararg TODO evidence).
4. `pi` bits `0x40490fda` (decimal `3.1415926` → nearest binary32) is correct; the smooth state machine is a faithful behavior-only restatement of the permitted digest.

## 3. Scope and §5 cross-phase audit (current bytes, not decision-log prose)

1. **Phase 3 (consumed §5.2):** current `PackFrontEnd.CURRENT_SCHEMA_VERSION = 23` (`docs/phase3/v1/PHASE_3_DOC.md:704`), schema23/`MaterializedSource-v23` identity (`:581–583`, `:3302`), D-P3-73 typed selector ranges (`:5068`) — the D-P11-25 receipt content matches the producer. The `CustomExpressionDecl`/enum algebra quoted in §5.2 matches `:3540–3557` field-for-field; duplicates retained, lossless unescaping, ordered fingerprint participation (`:2891–2908`, `:3291–3296`); the precipitation handoff is recorded at `:1726–1729`. Phase 11 does not reopen Properties bytes. ✔ content.
2. **Phase 6 (consumed §§5.3–5.4):** the bridge/view/sink algebra, `Accepted`/`SkippedAbsent`/`Rejected(stableDiagnosticId)` outcomes, `Bool1` distinctness, authoritative three-counter ledger with invalid-counter whole-batch discard, built-in-first third-participant ordering, permanent `retire(UNPUBLISHED_ABORT|REPLACEMENT|SHUTDOWN)` with `Retired|AlreadyRetired|Rejected(WRONG_THREAD|ACTIVE_CALLBACK)`, and no CLOSE alias all match current P6 §4.13 (`:1521–1658`) and §4.14 (`:1704+`). ✔ content.
3. **Phase 7 (receiver):** current `docs/phase7/v1/PHASE_7_DOC.md` installs exactly one controller via `create(metricsSink, diagnosticSink)` + `installCustomUniformBridge` (`:3500–3502`), calls `compilePhase3` with the accepted fingerprint/declarations/runtime schema/live context schema/canonical backend ID (`:3503–3505`), activates the complete tuple (`:3507–3509`), collects typed diagnostics and adapts through P1 (`:3523–3526`), and publishes the final-attempt GUI snapshot through `ExpressionDiagnosticGuiSource.current()` (`:3357–3359`, `:3482–3495`). ✔
4. **Phase 12 (receiver):** `showErrors(List<EngineDiagnostic>, Optional<ExpressionDiagnosticGuiSnapshot>)` with D-P12-24 direct adoption, final-outcome lifetime, older-serial/different-pack rejection, and source-less rendering (`docs/phase12/v1/PHASE_12_DOC.md:413–415`, `:1289–1306`, `:1558–1571`). ✔
5. **Phase 2 (receiver):** `RUN-EXPRESSION-CONFORMANCE` in §4.9 consumes the complete P11 §5.6 contract (`docs/phase2/v2/PHASE_2_DOC.md:1754–1771`), and **D-P2-69 in operative §5.3 explicitly adopts all six D-P11-27 vectors** — `EXPR-OPERATORS/uniform-reference`, `uniform-conversion-memo`, `definition-lazy-effects`, `EXPR-ERROR-ISOLATION/uniform-cycle`, `uniform-runtime`, `shared-namespace` — with "a uniform used as an input remains resolvable even when its upload is absent" (`:2674–2684`). ✔
6. **Phase 14 (receiver):** §5.8/L-11/S-22-6/D-P14-20 accept the exact OQ-22 method, the exact `ExpressionMetrics` field list, the real-pack-miss-plus-AST-dispatch trigger, and interpreter fallback (`docs/phase14/v1/PHASE_14_DOC.md:1888–1899`, `:2183`, `:2551–2569`, `:2628`). ✔
7. **Phase 1 (receiver):** `EngineDiagnostic(severity, channel, messageKey, args, detail)` with `WARN` (not WARNING) and `CHAT/SHADER_GUI/LOG_ONLY`, no stableId field, CHAT-no-player degradation to log (`docs/phase1/v14/PHASE_1_DOC.md:4963–4990`) — P11 §5.5's adaptation mapping (WARNING→WARN, CHAT_AND_LOG→CHAT, LOG_ONLY→LOG_ONLY, coalescing retained in the typed collector) fits the actual type. ✔

## 4. D-P11-27 deep check (the amended area)

1. **Shared namespace / first-owner policy:** §4.1 registers the earliest source-ordinal occurrence of each name before resolution, fixes kind/type independently of expression success, disables every later occurrence with `DUPLICATE_NAME` with no fallback, keeps an invalid owner resolvable so readers get `INVALID_DEPENDENCY` rather than `UNKNOWN_NAME`, and rejects collisions with all reserved/excluded names. Deterministic and circularity-free. The `EXPR-ERROR-ISOLATION/shared-namespace` vector pins every branch of this law, and I verified its expected outcome by hand (owner `variable.float.shared` serves `reader=0.75`; the later `uniform.int.shared` and `variable.float.failed` are disabled; `dependent` gets `INVALID_DEPENDENCY`; only `reader` and `good` submit).
2. **Forward resolution / both kinds:** §4.4 resolution order (literals → `BIOME_*` → context names → fixed inputs → registered custom definitions of either kind) is unambiguous given §4.1 collision rejection; self-reference is a self-cycle, not an unknown name. `EXPR-OPERATORS/uniform-reference` pins the shipped `screenDark3`-style forward uniform→uniform plus variable→uniform reference with declaration-order submission (`tint` then `level`, `Completed(2,0,0)`, `bridge` never submits) — hand-verified.
3. **Exact SCC / reverse-reader isolation:** §4.5's reader-to-dependency orientation, exact SCC membership (multi-vertex components or self-edge singletons only), reverse dependency-to-reader propagation preserving independent prerequisites, and residual-never-cycle-evidence are sound; `EXPR-ERROR-ISOLATION/uniform-cycle` (only `a,b` cyclic; `c` neither; `reader` invalid; `c`,`good` submit) and `uniform-runtime` (sticky runtime disable until reset/new activation) hand-verified.
4. **Converted once-per-refresh memo values:** §4.3's single declaration-boundary conversion before `VALUE`, promoted reads, and sink-independence of stored values are pinned by `uniform-conversion-memo` (one random sample per refresh; `count` converts 2.75→Int1 2; `reader`=4.5=`0x40900000`; `SkippedAbsent` does not erase the memo; `Completed(1,1,0)` twice) — hand-verified.
5. **Eager scheduling / AST-local lazy effects:** §4.5's eager definition execution plus §4.6's branch-local skipping of nodes/effects/errors is pinned by `definition-lazy-effects` (eager `hidden` consumes 0.75 once; `a`'s lazy branches consume nothing; exactly two samples; ordered `a=0.25,b=0.5`) — hand-verified against the prerequisite-first/ready-source-tie scheduling rule.
6. **Declaration-order uniform-only submission:** invariant 4, §4.8 step 6, and the P6 command map (bool→`Bool1` only) are consistent with P6's definition-order/ledger contract.
7. **Smooth machinery:** the §4.7 clock (advance once per observed different counter; same-counter refresh sees dt=0), per-cell last-committed timestamps, transactional value/timestamp commits, and no-startup-ramp initialization are internally consistent, and I recomputed all three §5.6 smooth tables (`same-frame-reset`: 0,0,1 then reset-NoCustoms then fresh 0.25; `lazy-late-use`: clocks 0,1,1,2,2 / outputs 0,0,1,0,0 / committed 0,0,1,1,2; `late-first-use`: outputs 0,0,0.25,0,1 with bits `3e800000`,`3f800000`) — all match the specified state machine exactly.
8. **Remaining vectors:** `remainder-vs-floor` (−5%3=−2=`0xc0000000`, fmod(−5,3)=1, frac(−1.25)=0.75, round(−1.5)=−1), `lazy-random`, `row-column` (`[2][1]`=9=`0x41100000`, not transposed), `divide`, `unsupported-empty`, and `unavailable` (Aborted(0,0,0), one context sample) all check out arithmetically and against §§4.6/4.8/4.11.
9. **Boundary tracing:** every D-P11-27-produced semantic crossing a boundary has a confirmed consumer branch — P2 D-P2-69 (vectors), P6 §4.13 (`Int1` upload of converted uniforms, ledger), P7 §4.13 composition steps, P12 §5.3(C). No silent drop found.

## 5. Corrections

### C15-1 — Current-schema statement contradicts the newest receipt: header and §5.2 body hardcode schema 22 while current identity is 23

- **Priority:** 1. **Confidence:** 0.97.
- **Owner locations:** `docs/phase11/v1/PHASE_11_DOC.md:20–22` (§0 header: "Current Phase 3 exact-current schema (22 adopted by D-P11-21) … are adopted provisionally in §5") and `docs/phase11/v1/PHASE_11_DOC.md:1183–1184` (§5.2 binding body: "composition accepts exactly current `CURRENT_SCHEMA_VERSION` (22 adopted by D-P11-21)").
- **Failure path:** Both statements name 22/D-P11-21 as *current*. The newest §5.2 receipt D-P11-25 states admission uses "the current constant, now23" and that "D-P11-21 and older numeric receipts are historical", and the current producer bytes make 23 binding (`docs/phase3/v1/PHASE_3_DOC.md:704` `CURRENT_SCHEMA_VERSION = 23`; `:581–583` schema23/`MaterializedSource-v23`; attempt-8 shared identity: P3 schema23/projectionVersion1/MaterializedSource-v23). An implementer gating admission on the header or the §5.2 body sentence requires `schemaVersion == 22` and rejects every current P3 configuration as a version/mismatch — no declarations are ever extracted, compiled, or reused, so custom uniforms are permanently `NoCustoms`; a careful reader instead finds two mutually exclusive "current" values inside one binding section. The established cutover pattern (the body parenthetical tracked D-P11-20's 21 before D-P11-21's 22) shows D-P11-25's insertion simply failed to update the standing text and the header.
- **Minimal resolution (owner, §0 and §5.2):** update both statements to the current constant and newest receipt — e.g. §0: "Current Phase 3 exact-current schema (23 received by D-P11-25; newest §5.2 receipt governs) …" and §5.2 body: "accepts exactly current `CURRENT_SCHEMA_VERSION` (23 adopted by D-P11-25)" — or make both insertion-proof by deferring to the newest §5.2 receipt without naming a number, mirroring §0.11's addendum-proofing technique. No receiver change required: P2/P7 receipts already reference the current constant/schema23.

### C15-2 — Dependency line anchors throughout §§1.2/1.3/3.3/4.8/5.3/5.4 point at superseded P3/P6 bytes

- **Priority:** 2. **Confidence:** 0.95.
- **Owner locations:** `docs/phase11/v1/PHASE_11_DOC.md:195` (P3 `:754–756`), `:225` (P3 `:757`), `:446` (P6 `:1266–1267`), `:780` (P6 `:1253–1259`), `:804` (P6 `:1325–1334`), `:1236–1244` (§5.3 bullets citing P6 `:1200–1205`, `:1207–1225`, `:1226–1250`, `:1283–1286`, `:1296–1329`), `:1260` (§5.4 citing P6 `:1389–1391`).
- **Failure path:** I read the cited ranges in current dependency bytes. P6 `:1200–:1391` now contains §4.9 fixed-sampler policy, §4.10 barrier fulfillment/program caches, §4.11 upload batching, and replay-evidence text — not the bridge signature, view/sink algebra, built-in-first ordering, install-lifetime rule, seven-name exclusion, or counter-ledger binding those citations claim to incorporate. P3 `:748–:763` contains `PackCandidateId`/`InternalPackSource` declarations, not the declaration-capture ownership or precipitation handoff. §5.3's bullets are the binding adoption of the Phase 6 §5 surface; a reader following the quoted coordinates cannot locate the consumed contract at its cited bytes (e.g. "one `CustomUniformBridge.refresh(...)` (`…PHASE_6_DOC.md:1200–1205`)") and may bind to materially different sampler/replay text. The owner's own round history (§0.9 Round 6, §0.10 Round 7) treats exactly these anchor sets (§5.2, §5.3, §4.8, §3.3) as contract-bearing and corrects them when they drift; they drifted again when P6's retirement amendment and P3's schema growth moved the content.
- **Minimal resolution (owner):** repoint to current coordinates or demote to section-level references as the retirement bullet already does ("P6 §4.14"). Correct current endpoints: P6 §4.13 "Custom-uniform extension point" `docs/phase6/v1/PHASE_6_DOC.md:1521–1658` (schema `:1524+`, bridge `:1535–1540`, view `:1542–1560`, commands/outcomes `:1561–1578`, install rule `:1588–1591`, seven-name exclusion `:1602–1606`, submission/precedence/counters `:1637–1658`); P3 declaration capture `docs/phase3/v1/PHASE_3_DOC.md:2891–2908` and algebra `:3540–3557`; precipitation handoff row `:1726–1729`. The bullets' semantic content already matches those endpoints — only the coordinates change.

### C15-3 — §1.1 ownership bullet still scopes dependency analysis to `variable.*`, contradicting the D-P11-27 shared graph

- **Priority:** 3. **Confidence:** 0.85.
- **Owner location:** `docs/phase11/v1/PHASE_11_DOC.md:181` (§1.1: "dependency analysis for `variable.*`, cycle/error propagation, and once-per-refresh memoization").
- **Failure path:** §0.14 lists the sections amended by D-P11-27 (§§2/4/5/6/7/8/12) and §1 was not among them, so the owned-components enumeration — the first scope statement an implementer reads — still describes the superseded variable-only graph of D-P11-4, while §4.1/§4.4/§4.5 and the incorporated §5.1 compiler row define one shared uniform/variable namespace and graph. Building to §1.1's scope reproduces exactly the C14-1 defect D-P11-27 closed (uniform references rejected as `UNKNOWN_NAME`).
- **Minimal resolution (owner):** one-line rewrite, e.g. "dependency analysis for the shared uniform/variable definition graph, cycle/error propagation, and once-per-refresh memoization". No receiver change.

## 6. Notes (optional, non-blocking)

1. **N15-1 — Receiver-side stale phrase (P3, Main-coordinated):** current Phase 3 §5 consumer prose still says Phase 11 owns "duplicate diagnostics and first-valid policy" (`docs/phase3/v1/PHASE_3_DOC.md:5155–5157`). Because P3 assigns the policy's ownership (not its content) to P11, D-P11-27's first-owner rule governs and no producer grant is violated; but the phrase is stale on the receiving side and should be synchronized by Main when P3 is next amended. Phase 11 cannot edit it.
2. **N15-2 — Verification limits (unexecuted evidence):** the historical Pintonium `9c2fcc1` pins cited in §§0.1/0.3/3.4 remain unavailable in this checkout (only `reference-src/Pintonium-main/` exists); §0.14 honestly retains N14-1's qualification, so they stand as provenance only and I did not rely on them. I did not re-perform the stareval license web lookup, run any build/test/formatter/linter (none is permitted), and no runtime, G6-parity, hook-result, performance, or real-pack evidence exists — the measured §4.11 budget, OQ-22 ledger, matrix dispositions, and P2 conformance runs remain future implementation gates. Documentary checks are not native or runtime proof.

## 7. §5 grant sufficiency

**Sufficient in content; not yet internally consistent in text.** Every §5 row's substantive semantics — compiler/plan/controller/schema/provider/backend/random/metrics/diagnostics+GUI-projection/conformance-vectors — matches both the current producer endpoints (P3 schema23 projection, P6 §4.13/§4.14) and every current receiver adoption (P7 construction/collector/GUI source, P12 §5.3(C), P2 §4.9 + D-P2-69 with all six D-P11-27 vectors, P14 §5.8/L-11, P1 §4.9.4), and the §5.2 receipt chain incorporates the current schema23/MaterializedSource-v23 identity. However, C15-1 sits inside the §5.2 binding body (and the §0 header) and C15-2 sits inside the §5.3/§5.4 citations, so §5 requires those two textual fixes before its incorporation is self-consistent; C15-3 (§1.1) is outside §5 but is the same D-P11-27 cutover residue. Per the owner's own synchronization rule, the §5 edits require another fresh verification round before closure, which §0.14 already states.

## 8. Verdict

The document is architecturally sound and materially complete: the D-P11-27 amendment is correctly designed, internally consistent across §§2/4/5/6/7/8/12, backed by directly read shipped-author evidence, and fully adopted by its receivers in current bytes; the smooth/random/clock machinery, isolation ladders, lifecycle map, diagnostics/GUI projection, and OQ-22 handoff all check out against current dependency and receiver contracts. Three corrections remain — the contradictory current-schema statement (C15-1), the drifted dependency anchors (C15-2), and the §1.1 cutover leftover (C15-3) — all owner-side text fixes with no receiver amendment required. Fresh verification is required after they land.

**Corrections: 3. Notes: 2. §5 impact: YES (text-level; content sufficient). PASS-WITH-CORRECTIONS.**

## Resolutions

### C15-1 — Current-schema statement corrected, 2026-09-08

D-P11-28 in `docs/phase11/v1/PHASE_11_DOC.md` (§11.1). The §0 header now reads "Current Phase 3
exact-current schema (23 received by D-P11-25; newest §5.2 receipt governs)" and the §5.2 binding
body reads "accepts exactly current `CURRENT_SCHEMA_VERSION` (23 adopted by D-P11-25)". Both defer
to the newest §5.2 receipt, whose own text ("the current constant, now23") and
schema23/`MaterializedSource-v23` identity govern; D-P11-21's decision row and historical receipt
remain untouched as history. Textual consistency repair only: no schema bump, no admission-gate or
grammar change, no receiver amendment.

### C15-2 — Dependency anchors repointed, 2026-09-08

D-P11-29. Coordinates only; the incorporated semantics were already verified to match the current
endpoints.

- §1.2: P3 declaration-capture anchor `:754–756` → `docs/phase3/v1/PHASE_3_DOC.md:2891–2908`.
- §1.3: precipitation-handoff anchor `:757` → `docs/phase3/v1/PHASE_3_DOC.md:1726–1729`.
- §3.3: seven-name exclusion `:1266–1267` → `docs/phase6/v1/PHASE_6_DOC.md:1602–1606`.
- §4.8: install rule `:1253–1259` → `docs/phase6/v1/PHASE_6_DOC.md:1588–1591`; invalid-counter
  branch `:1325–1334` → `docs/phase6/v1/PHASE_6_DOC.md:1637–1658`.
- §5.3 bullets: bridge `:1200–1205` → `:1535–1540`; view `:1207–1225` → `:1542–1560`;
  commands/outcomes `:1226–1250` → `:1561–1578`; built-ins-first bullet demoted to the
  section-level reference "P6 §4.13" (the review verified no dedicated sub-range for it, matching
  the retirement bullet's style); submission/prefix `:1296–1329` → `:1637–1658`.
- §5.4: "Phase 6 §5 (`:1389–1391`)" → "Phase 6 §4.13 (`docs/phase6/v1/PHASE_6_DOC.md:1521–1658`)".

### C15-3 — §1.1 shared-graph scope, 2026-09-08

D-P11-30. The §1.1 ownership bullet now reads "dependency analysis for the shared
uniform/variable definition graph, cycle/error propagation, and once-per-refresh memoization",
matching D-P11-27's §§4.1/4.4/4.5 shared namespace and the incorporated §5.1 compiler row. No
receiver change.

The frozen review body, counts and PASS-WITH-CORRECTIONS verdict above are unchanged. These are
documented textual resolutions of the three corrections only; no validation, build, test,
formatter or linter was run, and no fresh verification is claimed. Fresh whole-owner review of the
amended document remains required, per the verdict above. N15-1 (P3 receiver-side phrase) is
Main-coordinated and untouched here.
