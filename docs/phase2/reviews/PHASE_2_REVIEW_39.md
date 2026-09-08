# Phase 2 — Whole-document verification, round 39

## 1. Review identity and scope

**Target:** `docs/phase2/v2/PHASE_2_DOC.md`  
**Governing design:** `docs/design/v3/DESIGN.md`, as selected by the target header; Part I and the complete Phase 2 specification. This review does not adopt v3 for another phase.  
**Scope:** the whole current Phase 2 architecture, including incorporated §5 semantics, not only D-P2-32 or the recent diff.  
**Counts:** **blocking=0; corrections=5; notes=0**.

The schema20 receipt is coherent, but five independently demonstrated defects remain in the conformance machinery and its gate assignments. They are bounded architecture corrections rather than grounds to rebuild the entire document.

## 2. Inputs and evidence discipline

Read the governing Part I and complete Phase 2 assignment; Research §§0–1, §8, §9, Appendix G, the assigned RenderBook references, and targeted contract material including Appendix A.2 and D.2. Read PD §§4, 7 and 19. Read the complete current P2 document, R38, and R37 including its appended Resolutions. R38 is a historical literal PASS with no Resolutions section; it does not certify the subsequent integration/schema amendments or exclude whole-phase findings in this commissioned review.

Read P1’s actual §5 and relevant incorporated seam, profile, recording, diagnostics, fixture and CI contracts, together with its current R28 literal PASS. Followed actual producer/receiver contracts in P3, P4, P5, P6, P7, P11, P12 and P13 rather than accepting receipt names alone. Read the three current workflows, `docs/build/READINESS.md`, and the explicit U1, geometry-compatibility and sidecar-default decision files.

Reference provenance was checked before any implementation-source mining. The historical `reference-src/pintonium-9c2fcc1` tree is absent. The available `reference-src/Pintonium-main` has no `.git` metadata; its README describes LGPL licensing while its root LICENSE contains GPLv3 text. It was not substituted for commit `9c2fcc1a4814cafc0242370757e9e05ea83c5be3`. No implementation-source claim was newly derived from that unverified revision, and no source was copied. PD and the target’s historical observations remain dated evidence, not refreshed runtime certification.

No files were changed. No build, test, formatter, linter, validation command, runtime experiment, pack acquisition, golden generation or image acquisition was performed. No forbidden transcript, decompile or transformation-library input was used.

## 3. Findings

### R39-1 — Separate motion execution from architecture closure

- **Location:** `docs/phase2/v2/PHASE_2_DOC.md:547–550`; repeated in the `RUN-MOTION-PATHS` row at line 1335.
- **Claim under review:** v3 requires `RUN-MOTION-PATHS[all six families]` to pass before Phase 2 closes.
- **Evidence and impact:** The governing Phase 2 **doc gate** requires motion scenes to be present per motion-sensitive family; it does not require a rendered run during architecture verification (`docs/design/v3/DESIGN.md:1329–1337`). The target’s run requires a client, two actual executions and approved T1 baselines. Meanwhile §G5.3 requires verified phase documents and resolved final integration before implementation starts (`DESIGN.md:657–685`). Making that client run a Phase 2 closure prerequisite therefore introduces a circular gate: P2 cannot close before the rendering implementation that cannot start before document closure and integration. The existing family/schema design requirement and later runtime acceptance requirement must be stated separately; do not remove the later motion run.
- **Severity:** **correction**.
- **§5 impact:** **yes** — named-run and milestone/gate meanings are incorporated by §§5.1 and 5.3.

### R39-2 — Drive elapsed-time inputs rather than equating frames with ticks

- **Location:** `docs/phase2/v2/PHASE_2_DOC.md:833–838`; related wire fields at lines 1012–1015 and conditional request R13 at line 2069.
- **Claim under review:** fixed rendered-frame ordinals fix animated-texture ticks and `frameTimeCounter`; a warm-up measured in frames provides eight half-lives measured in ticks; no G6 wall-clock-derived input exists.
- **Evidence and impact:** Research Appendix D.2 explicitly defines `frameTime` as last-frame seconds and `frameTimeCounter` as runtime seconds (`docs/research/v1/RESEARCH.md:1345–1347`). The actual P6 producer defines elapsed/accumulated rendered seconds at `docs/phase6/v1/PHASE_6_DOC.md:832–834` and separately advances smoothing from world tick plus render partial tick at lines 937–940. P7’s receiving capture loop (`docs/phase7/v1/PHASE_7_DOC.md:1638–1676`) fixes pose/frame order but supplies no deterministic time or tick stepping. Thus two valid runs with the same sample ordinals and camera poses but different frame durations present different clock, animation and potentially smoothing inputs. Logging partial ticks does not suppress them, and the current manifest does not even carry the promised `frameTimeCounter`. This undermines IDENTICAL self-checks and temporal baselines without any renderer defect. Specify a real capture-only clock/tick contract with the owning producers, preserve tick-based half-life units, and record the actual relevant time inputs; the conditional partial-tick-only request does not solve elapsed seconds or tick advancement.
- **Severity:** **correction**.
- **§5 impact:** **yes** — determinism, capture scheduling and evidence are public contracts consumed by P7 and time-producing owners.

### R39-3 — Distinguish intentional disablement from failed sourced fallback

- **Location:** `docs/phase2/v2/PHASE_2_DOC.md:676–681`; repeated by `RUN-T3` at line 1341 and the handoff at lines 2577–2580.
- **Claim under review:** every slot shipping a source must be SOURCED; CHAIN with source proves compilation failed.
- **Evidence and impact:** Research Appendix A.2 requires profile-disabled and `program.<name>.enabled=false` programs to be treated as absent and to use fallback. The actual P4 owner explicitly records source presence **before enablement**, retaining true for disabled sources (`docs/phase4/v1/PHASE_4_DOC.md:1138–1145`). Its canonical CHAIN and ABSENT branches retain that independent fact, and CHAIN intentionally covers both disabled inheritance and failed-source inheritance (lines 1164–1177). Consequently a pack that deliberately disables a shipped child while using a valid ancestor is rejected by P2’s T3 predicate even when it implements the contract correctly. The consumed two-field distinction is insufficient for the claimed inference. Reconcile a source-free enabled/build-disposition distinction with P4 and carry it through P7/P2 evidence; preserve the rejection of actual masked failures without requiring legitimately disabled programs to compile and execute.
- **Severity:** **correction**.
- **§5 impact:** **yes** — T3, P4 resolution evidence and P7 serialization require a coordinated owner/receiver correction.

### R39-4 — Keep the engine under test out of baseline-invalidating environment identity

- **Location:** `docs/phase2/v2/PHASE_2_DOC.md:1263–1267`; defining hash at lines 1091–1094 and mod inventory obligation at line 840.
- **Claim under review:** mod-set changes invalidate a baseline, while an engine change is deliberately compared against the existing baseline.
- **Evidence and impact:** The manifest records **every** mod ID and jar hash, and `environment.modSetSha256` hashes those complete records. Schmaloogium is itself the mod jar containing the engine. A rebuilt released engine jar therefore changes this hash even when every external mod, world, pack and scene is unchanged. The earlier invalidation row yields NO_BASELINE before the following “Engine behaviour changed” row can provide its intended regression comparison. The same hash also selects the world cache, so engine changes unnecessarily select a new world identity. Retain the tested engine’s exact build identity as provenance, but define a separate stable comparison/world-environment identity that excludes the subject being regression-tested. Otherwise T1 does not serve as a cross-build regression oracle.
- **Severity:** **correction**.
- **§5 impact:** **yes** — world/mod provenance, baseline invalidation and tier evidence are incorporated public meanings.

### R39-5 — Specify every threshold used by the image-diff predicate

- **Location:** `docs/phase2/v2/PHASE_2_DOC.md:1168–1174`; required predicates at lines 1146–1149.
- **Claim under review:** a named tolerance profile completely determines a reproducible three-level diff verdict.
- **Evidence and impact:** L2 requires `rmse <= maxRmse` and L3 requires `clusterCount <= maxClusters`, but none of the executable profile rows supplies either threshold, and no default or deliberate inactive value is defined elsewhere in the target. This is not merely a lack of measured calibration: the advertised initial profiles cannot evaluate two mandatory predicates even for the week-one synthetic-image tests. Implementers must invent different limits or silently drop those gates. Complete the profile schema and initial/calibrated treatment for both values, including fail-closed handling of missing fields, while retaining ADVISORY’s explicit no-verdict behavior.
- **Severity:** **correction**.
- **§5 impact:** **yes** — the tolerance-profile contract is explicitly exposed by §5.1.

## 4. Literal Phase 2 doc-gate audit

| Literal criterion | Assessment |
|---|---|
| Every Research §9 exit criterion traceable to a specified run | The map at §3.5 names every exit criterion, including classic T0/T1, first T2, terrain T2, T2/T3, options persistence and later progression. However R39-3 and R39-4 prevent the mapped T3 and regression predicates from faithfully serving those criteria. The separately reported Appendix G timing conflict remains explicit rather than silently changing authority. |
| Fixture licensing encoded structurally | Satisfied architecturally by §§4.10–4.11: out-of-worktree retained roots, no-follow traversal, pinned hash checks, manual canonical acquisition, external caches, report/upload boundaries, source-free goldens and manifests instead of committed rendered images. Unpopulated pins fail rather than float. This is a design result, not permission to acquire or redistribute anything. |
| Before-renderer subset explicitly listed | Present at §9.2 and front-loaded in §12. Synthetic scene/wire/ledger/diff/golden machinery is distinguished from live capture. Complete real goldens require actual P3/P4/P5 implementation, and OQ-10’s full result additionally requires its later Stage D smoke; neither is supplied by a hand-built document. R39-1 corrects the contradictory architecture-close gate. |
| OQ-10 complete, with fallback | Satisfied as a spike specification: question, local baseline, CI configurations, compatibility/compile/FBO/flakiness criteria, real-smoke stage, partial-success disposition and local/pre-release fallback. No OQ result is claimed. |
| Motion scenes present per motion-sensitive family | Satisfied in the architectural scene inventory: all six families require dense moving paths and bounded sample windows. R39-2 remains a correctness defect in deterministic execution, and R39-1 separates presence from later execution proof. |
| Shadow/sky/weather covered despite absent working reference | Satisfied by `terrain-day`, `night-shadows` and `weather-rain`, with explicit reference-gap provenance. Pintonium outcomes do not define their pass conditions. |

**The doc gate is not currently clean overall.** Most structural rows are met; the corrections above are required before literal PASS.

## 5. Current schema20 and sibling-boundary audit

The new D-P2-32 receipt was checked against the actual P3 owner, not an imagined prior schema:

- P3 §2.2 publishes required `assets` immediately after `sources`; §§4.1/5.1 bind exact same-load issuance, post-archive immutable lifetime and closed Acquired/Missing/Unreadable/InvalidReference outcomes. P2 does not invent an I/O grant from that capability.
- P3 §5.1.1 publishes exactly nine inspection trees. P2 §4.11.4 maps all nine without dropping a branch: pack/dimensions/properties/idMappings/assets into named `[properties] owner.*` trees; options, programStates, resources and macros into their corresponding existing sections. Source and diagnostic lists remain separate.
- The ninth tree is the canonical manifest Sequence. AVAILABLE preserves zero-byte presence; MISSING and UNREADABLE preserve distinct tokens with absent count/hash. The digest-string leaf is TextHash under the generic String codec, not a raw-digest exception. P2 copies the owner projection without opening assets or serializing binary data, cursors or provider identity.
- Configuration, nested ID and inspection schemas must all equal 20; snapshot fingerprint must match the returned configuration before enrichment or serialization. Missing trees or foreign/missing assets cannot become an invented empty schema20 manifest. Projection version 1 and the separate wire majors are not old-schema escape routes.
- P4 §5.6 receives the exact Inspected configuration through the synchronous same-request candidate route; Ready is copied and closed, ShadersOff stays incomplete, and geometry mismatch detail remains sanitized. P5 §5.1 supplies the actual pure planner and complete Available/SHORTFALL versus Unavailable distinction. P2 does not substitute P3 minima, P4 success or observed pixels for P5 resource evidence.
- P7 §4.13 explicitly receives `/2`, dense frame execution, actual current/previous pose reports, complete owner projections and final shutdown. This closes the former missing `/2` receiver, but does not close R39-2’s independent clock defect.
- P13 §§4.3.2/5.2 explicitly dispatch all asset outcomes; invalid pairing is not optional absence, primary failure is not sidecar recovery, and only positively classified optional-owned-sidecar unreadability takes the baseline/one-warning route. P7 retains the exact configuration across resource-only NONE. Inspection metadata is not preparation input or a recovery verdict.
- P11 §5.6’s Activate/Refresh/Reset/Close and PASS/FAIL/UNSUPPORTED algebra is consumed by the named evaluator run, including script exhaustion, independent expected observations and source-free local-matrix disposition. P12 §5.3’s bridge preserves validation, partial persistence receipts, Internal session handling and final reload-outcome observation.

**No additional defect was demonstrated in D-P2-32’s nine-tree schema20 mapping.** That local result does not certify the entire current P3/P4/P5/P7/P11/P12/P13 documents or establish runtime capability.

## 6. Whole-document completeness and historical dispositions

All thirteen §G9 sections, numbered 0–12, are present and substantive: header/provenance; scope/ownership; module architecture; contract map; detailed machinery; interfaces; failure/degradation; threading/performance; testability; milestone staging; OQ spikes; decisions/handoffs; implementation checklist.

The process boundary preserves C-4; the live capture agent stays in `:mod` and the golden adapter consumes public engine contracts. Error, absence, skip, no-baseline, unavailable and incomplete outcomes generally remain distinct. Human baseline approval and explicit fail-after-update golden regeneration remain mandatory. T2 stays classic-only with a manual static-shot oracle, while motion has a separate self-baseline run. Tolerance values are correctly labelled unmeasured, apart from the missing required fields in R39-5.

R37’s corrected attribution rule remains correct: per-call cadence narrows a diagnostic window but cannot manufacture causal attribution; every recorded GL error still fails T0. R38’s acceptance of that correction is historical evidence, not a reason to suppress the independently demonstrated findings above. Stale review-status prose was not promoted into an additional finding or mistaken for the current review inventory.

## 7. Review applicability, §5 impact and remaining gates

**P1 R28 is the current literal PASS.** P2’s declared dependency is P1; this review does not revive its already-closed owner-review gate.

The precise dependency rules are §G1.3’s definition of a verified document and valid dependency input, and §G5.3(1)’s prohibition on a dependent **build session** consuming an unverified phase doc. They do not prohibit this explicitly commissioned independent reviewer from examining current unverified producer/receiver architecture for symmetry. P3 schema20 and the other amended owner interfaces remain unverified implementation inputs; reading and assessing them here does not grant their certification. P2’s own current result is determined by its demonstrated defects, not by a blanket invented prohibition on reviewing unverified siblings.

**§5 impact: yes.** Existing changes to P2’s inspection, capture and evaluator contracts were in scope. Resolving R39-1 through R39-5 changes exposed or incorporated gate, determinism, evidence or tolerance semantics, and therefore requires corresponding §5 receipts plus a fresh whole-document review under §G1.3. P4/P7/time-provider amendments must be reconciled at their actual owners, not invented solely in P2.

`docs/build/READINESS.md` records an unchanged-template Java25/Gradle9.7.0 build with NO-SOURCE tests, an actual NVIDIA compatibility context, and a pre-menu JEI/HEI classloading failure. It supplies no renderer, pack, capture, baseline, T0–T3 or completed OQ evidence. This review reran none of it. All applicable individual reviews, IR-01 and final §G5.3 integration remain separate; implementation starts only after the governing final-integration findings are resolved.

## 8. Verdict

# PASS-WITH-CORRECTIONS

**Counts: blocking=0; corrections=5; notes=0.**  
**Interface changed / correction impact: yes.**

The current document cannot receive literal PASS while its architecture closure depends on a later client run, its temporal inputs are not actually deterministic, intentional program disablement is classified as failure, engine rebuilds invalidate the regression oracle, and two required tolerance thresholds are undefined. Apply corrections in a separate fix-up, record Resolutions, reconcile affected owners/receivers, and obtain a fresh whole-document review. No implementation clearance or renderer/conformance result is granted.

## Resolutions

### 2026-09-08 — Separate architecture fix-up; not verification

The original review, findings, counts and PASS-WITH-CORRECTIONS verdict above remain intact.
Only this Resolutions section is appended. P2 §5 changed; fresh whole-document review and
final integration remain required. No implementation clearance, runtime experiment, validation
command, build, test, formatter, linter, acquisition, golden, calibration or human approval
was performed in this fix-up.

| Finding | Authored correction / route | Owner and receiver status; remaining gate |
|---|---|---|
| R39-1 | P2 §§3.5/4.9/5.1/8/12, D-P2-33 separate six-family scene/schema architecture closure from mandatory later two-run motion execution and approved T1 baselines | P2 owner authored; receivers consume named-run meanings, not an implementation-before-doc prerequisite. Fresh review pending; no motion execution claimed |
| R39-2 | P2 §§4.4–4.5/4.8/5.1.1/5.4/8, D-P2-34 publish capture-plan/3 and run-manifest/3, fixed elapsed/tick/partial input, divisible preparation and tick-based ceil warm-up, actual P6/P7 timing, real host deadlines and fail-closed acknowledgment. Static G6 oracle remains manually uncontrolled; absent timing comparability cannot pass T2 | P2 wire owner authored; P6 actual snapshot and P7 checkpoint/control/report amendments coordinated with owning agent and architecturally received, unverified. Exact owner receipt/fresh reviews/final integration and future runtime evidence remain; no frames=ticks or target-echo evidence |
| R39-3 | P2 §§4.2.4/4.5.4/4.11.4/5.1.1/5.4/8, D-P2-35 require P4 requested-slot ownBuild in golden and runtime projection. DISABLED is intentional, while own FAILED behind CHAIN still fails T3; status/source presence alone never infer failure | P4 owns classification; P7 serializes; P2 consumes same-request/accepted projections. Coordinated owner amendments and receiver architecture are unverified; source-free recorded goldens do not certify runtime compiler success |
| R39-4 | P2 §§4.4–4.5/4.7/5.1.1/8, D-P2-36 retain full inventory/modSetSha256 plus authenticated subjectModId/subjectJarSha256, derive externalModSetSha256 excluding only known engine subject, migrate world-cache and baseline invalidation to world/external identity | P2 authenticates launch identity before cache selection; P7 checks loaded inventory, runner authenticates returned fields. Subject-only rebuild retains oracle; external changes invalidate. Old missing identities are NO_BASELINE. Owner/receiver architecture unverified; no fabricated inventory or arbitrary exclusion grant |
| R39-5 | P2 §§4.6/5.1/8, D-P2-37 define six required finite-domain thresholds, complete initial RMSE/cluster-count values, exact boundary/all-masked behavior and fail-closed loading. Unmeasured execution remains distinct from calibration and ledger acceptance; ADVISORY remains report-only | P2 profile/diff/report owner authored; all diff consumers receive same predicates. Fresh review and future six-metric calibration plus human baseline approval remain; authored initial numbers are not measured evidence |

P2 additionally receives the concurrent P3 R55 schema21 correction in D-P2-38: payload-free
ScreenProfileEntry(), exact external TEXTURE_RECTANGLE→TextureTarget.RECTANGLE (bare external
RECTANGLE invalid), and usable-base-or-explicit-override structural classification. Nested
IdMappingInput, inspection and current materialization domain follow active CURRENT_SCHEMA_VERSION;
D-P3-69 assets and nine-tree source-free meanings remain. This is an unverified receiver receipt,
not a new review finding or an upgrade of R39's schema20 audit.
