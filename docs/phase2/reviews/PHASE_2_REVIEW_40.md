# Phase 2 — Independent §G1.2 Review 40

## 1. Reviewed artifact and review boundary

- **Target:** `docs/phase2/v2/PHASE_2_DOC.md`, complete document, §§0–12.
- **Review identity:** SHA-256 `bfc01f1c347e1d618a7947689674d9c38e78654060125e128f123fe9165c81bd`, supplied with the assignment. This report does not claim an independently executed checksum measurement.
- **Governing design:** `docs/design/v3/DESIGN.md`, as explicitly adopted by the target header. V3 was not treated as a global migration of other phases.
- **Intended report path:** `docs/phase2/reviews/PHASE_2_REVIEW_40.md`.
- **Method:** fresh, read-only whole-document architectural review, including consuming-side owner contracts. Prior findings, resolutions, and receipts were treated as historical claims rather than verification evidence.
- **Execution boundary:** no files edited; no builds, tests, formatting, validation commands, client captures, GL experiments, or runtime calibration performed. This review does not certify dependency eligibility, final integration, implementation, or runtime/OQ results.

## 2. Authority and evidence read set

### Governing and assigned inputs

1. `docs/design/v3/DESIGN.md`: complete Part I, §G0–§G12, and the complete Phase 2 assignment and literal doc gate.
2. `docs/research/v1/RESEARCH.md`: §§0–1; assigned §§8–9, Appendix G, the §5.1 RenderBook material and §12.5 reference; incorporated licensing and OQ material in §§10–11; relevant §§3.1, 3.2, 3.5 and §§4.1–4.7; Appendices A, B’s relevant buffer/sampler material, D, F, and H. Additional uniform/property inventories were read to check the claimed scene and source-free golden coverage, not to expand the phase’s scope.
3. `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md`: complete §§4, 7, and 19.
4. `docs/phase1/v14/PHASE_1_DOC.md`: complete §5, following the incorporated module, test-fixture, capability-profile serialization, facade/error, replay/recording, diagnostic, package-grant, and CI contracts into their defining sections.
5. All three current workflow files: `.github/workflows/build.yml`, `release.yml`, and `release-to-cf-mr.yml`. Their existing repository behavior was distinguished from the architecture’s proposed module/task wiring.

### Cross-phase and decision evidence

6. `docs/PHASE_INTEGRATION_REVIEW.md`: original IR-01–IR-29 findings, all recorded Resolutions/follow-ons, and the recorded decision rulings relevant to this receiver. Historical eligibility and verification receipts were not promoted into fresh certification.
7. Current relevant owner contracts: Phase 3’s schema-21 inspection and engine-option codec; Phase 4’s registry construction, detached inspection, independent own-build disposition, and closure obligations; Phase 5’s pure planning and exact resource projection; Phase 6’s required replay-error sink and failure retention; Phase 7’s controlled capture, checkpoint/restore, replay collector, and canonical `/3` receiver; Phase 8’s pure celestial and shadow-camera fixtures; Phase 11’s evaluator-run vectors and outcomes; and Phase 12’s authenticated session/option bridge and persistence outcomes.
8. The complete recorded rulings in `docs/decisions/U1_TEXTURE_SAMPLING.md`, `GEOMETRY_PRIMITIVE_COMPATIBILITY.md`, and `TEXTURE_SIDECAR_DEFAULTS.md`.

### Permitted reference checks and limitations

Licensing rules were read before reference mining. The historical local Pintonium alias was unavailable; the current `reference-src/Pintonium-main` tree was not substituted for the declared revision. Targeted upstream files were recovered at the exact revision: [README](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1a4814cafc0242370757e9e05ea83c5be3/README.md), [MixinEntityRenderer_Shaders.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1a4814cafc0242370757e9e05ea83c5be3/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java), and [MixinRenderGlobal_Shaders.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1a4814cafc0242370757e9e05ea83c5be3/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinRenderGlobal_Shaders.java). These checks were restricted to licensing/provenance and hook inventory; no restricted transformation or pipeline implementation was mined.

The current upstream [RenderBook GL extension article](https://raw.githubusercontent.com/tttsaurus/Mc122RenderBook/master/articles/unit_test/junit_gl_extension.md) and [MIT license](https://raw.githubusercontent.com/tttsaurus/Mc122RenderBook/master/LICENSE) were read. This confirms the currently published example’s character, not an unprovided historical revision or successful CI experiment. No transcripts, chatlogs, repository-root text dumps, forbidden Oculus sources, libraries, or relocated GLSL sources were used.

## 3. Whole-document adversarial audit

### 3.1 Literal gate, research conformance, scope, and document completeness

All thirteen required sections are substantively present. The document supplies the four-tier model, named milestone runs, diff/tolerance design, baseline approval and OptiFine protocol, fixture acquisition and licensing controls, source-free goldens, capability-profile fixtures, CI separation, OQ-10 spike, decisions, and implementation checklist. The six scene families have explicit moving-path requirements rather than a static-shot substitute. Dense path samples, bounded multi-frame capture windows, previous/current pose evidence, and the split between architectural scene presence and later client execution address the v3 motion requirement without creating a runtime prerequisite for the doc gate.

The classic/dual-spec distinction is preserved: dual-spec T2 is refused rather than silently skipped; missing fixtures and missing baselines cannot become passes. The seven-pack calibration is evidence-only and cannot weaken D-3. The document explicitly exposes the research scheduling inconsistency rather than silently resolving it. It does not introduce another loader target, rendering engine, pack transformer, automatic image approval, or performance exit criterion.

The findings below concern actual mechanisms inside this otherwise complete design, not absent implementation artifacts or pending calibration.

### 3.2 Producer/consumer interfaces

The source-free golden route has a real receiving side: P3’s same-load schema-21 inspection and archive digest feed the conformance adapter; P4 enriches the same configuration/request and supplies independent final resolution and own-build disposition; P5 performs pure planning with explicit runtime inputs. P5’s complete `Available(SHORTFALL)` is not confused with successful publication, and `Unavailable` is not filled with guessed numeric facts. The nine P3 decision trees remain intact rather than being replaced with a partial hand-maintained summary. Candidate ownership and closure are explicit.

The independent `ownBuild` field is consumed by T3: actual failed builds hidden by `CHAIN` fail, whereas intentional `DISABLED` does not become a compilation failure. This closes the relevant classification boundary instead of merely adding a producer field.

The current P6 replay report has a consuming P7 collector. Its ordered original records flow into P2’s unchanged `gl_errors` representation; failed delivery is retained/latches failure, and final restoration and retirement precede successful sealing. The document does not assume that an empty later drain proves no earlier replay error occurred.

P7’s checkpoint, controlled-step and restoration contracts support actual owner timing rather than an echo of requested values. The agent validates while the owner view is live; the private checkpoint need not itself be invented as a new public wire DTO. P8’s fixture input is the actual main camera and explicit plan/extent, while the pure celestial fixture does not require a fabricated frame or camera identifier. P11 and P12 routes preserve their closed outcomes, unsupported cases, identity checks, and persistence distinctions.

There is nevertheless a concrete profile-serialization conflict at the client/runner boundary, recorded as C40-3.

### 3.3 Lifetimes, failures, and ownership

The process boundary preserves C-4: `:conformance` does not acquire a Minecraft classpath. Capture inputs are runner-resolved and immutable before launch; pack acquisition mode, archive identity, and license cannot be supplied by the pack or rediscovered by the agent. Full environment identity and the authenticated engine-under-test exclusion are distinct, so rebuilding the subject does not erase the regression oracle.

World/cache identity is defined over copied content rather than timestamps. Root establishment, no-follow resolution, replacement detection, and retained directory identity give the licensing containment rule an actual mechanism. Client failure, absent evidence, unsupported wire majors, skipped manual inputs, timeout, uncalibrated comparisons, and every observed GL error have explicit non-pass paths. Real host deadlines remain distinct from controlled shader time.

The two oracle/evidence findings below are consequential precisely because the surrounding design promises reproducible, fail-closed tier claims: its storage identity cannot retain required A/B baselines, and its ledger does not bind the non-run evidence used to decide image tiers.

### 3.4 OQ completeness and binding decisions

OQ-10 has an assigned question, staged experiment, concrete context/compiler/attachment/time/repetition/smoke criteria, evidence to retain, and a usable failure branch. The RenderBook example is evaluated rather than adopted as proof of headless compatibility. Failure keeps hermetic CI and local real-GL work available without moving research milestone gates. Missing experimental results are therefore not themselves an architecture finding.

The U1 numeric texture-slot/sidecar scope, approved texture defaults and recovery rule, option-only `superSamplingLevel`, adjacent prepared submissions, and effective lighting-option precedence are treated as recorded bounded decisions. They do not authorize unrelated aliases, engine SSAA, repeated world traversal, or a broad claim that every OQ is resolved. Current grants and explicit outstanding requests are not confused with completed dependency verification.

### 3.5 Licensing and provenance

The design keeps matrix archives, rendered baselines and OptiFine oracles outside Git; committed goldens and reports are source-text-free metadata. It does not fabricate registry pins or treat competitor parse results as a redistribution grant. Its original micro-pack corpus supplies a genuine hermetic path. One bounded reference-provenance discrepancy is recorded as N40-1.

## 4. Findings

### C40-1 — Include option-state identity in A/B baseline lookup and storage

- **Severity:** correction.
- **Location:** target §4.2.2, lines 637–639; §4.2.4, lines 658–670; §4.7.2, lines 1258–1284; §4.2.5, lines 701–703.
- **Claim:** the required automated T3 A/B comparison cannot retain the two distinct approved baselines its decision procedure needs.
- **Evidence and trigger:** T3 binds one feature to one scene/capture/sample and requires its OFF and ON images to differ beyond `SAME_MACHINE` while both pass T1 against their own baselines. The T1 lookup tuple contains pack/version/scene/capture/sample/tolerance/machine, but no feature variant or option-state identity. The cache path has exactly one PNG for that pack/version/scene/capture/sample, and the committed scene baseline file permits one record per captured sample. Consequently the OFF and ON approvals address the same record and image; promoting the second replaces the first. `FEATURE_OFF` and `FEATURE_ON` distinguish ledger run records, but do not change baseline selection or storage.
- **Impact:** the stable two-oracle A/B gate is unrepresentable. Ordinary repeatable OFF/ON states cannot both be retained and subsequently evaluated as specified; sequencing approvals would replace evidence rather than establish a durable T3 oracle.
- **Required correction:** define a stable explicit option-state/variant identity for T3 baseline selection, on-disk image placement, committed baseline records, and approval provenance, and bind each feature run to the matching identity. Preserve the deliberate rule that changing the engine-under-test does not select a fresh baseline.
- **§5 impact:** yes. This changes the exported T1/T3 procedures and their evidence contract.

### C40-2 — Bind the oracle and comparison inputs into tier-ledger evidence

- **Severity:** correction.
- **Location:** target §4.2.5, lines 690–724; §4.5.4, lines 983–1023 and its remaining canonical blocks; §4.8.2, lines 1348–1357.
- **Claim:** the canonical ledger cannot authenticate the baseline/oracle evidence that determines an image-tier result.
- **Evidence and trigger:** the exact `evidence.index` record contains capture identity, variant, run ID, run-manifest hash, and optional manual-attestation hash. Its validation traverses and hashes those artifacts only. The canonical run manifest carries capture and runtime evidence, but no baseline/oracle manifest reference, comparison-input digest, or hashed diff-decision artifact. T1 requires an approved baseline; T2 additionally depends on independently established timing comparability, and §4.8.2 explicitly requires the ledger to retain hashed oracle evidence and its reason. That evidence has no field or canonical resolution path in the declared index or run manifest. Replacing or removing the oracle manifest, or losing the comparison policy/result used for a claimed pass, leaves every specified ledger hash valid.
- **Impact:** the ledger can retain a hash-valid image-tier pass while the deciding oracle or timing-comparability evidence is absent or no longer identifiable. A later reviewer cannot reconstruct which approved image, oracle timing evidence, and tolerance content justified the stored outcome. Merely retaining candidate captures is insufficient, especially for T2’s new timing gate and T3’s two T1 results.
- **Required correction:** extend the evidence contract with authenticated, canonically located comparison evidence binding the selected baseline/oracle manifest, raster identities, effective tolerance content, and relevant result/comparability evidence to each image decision. Specify missing/mismatched evidence handling. This can reference metadata and externally stored images; it must not relax the never-commit-images policy.
- **§5 impact:** yes. The exported tier ledger/evidence rule and its reporting/consumer contract change.

### C40-3 — Define a compatible encoding for the embedded capability profile

- **Severity:** correction.
- **Location:** target §4.5.4, lines 970 and 983–987, 1021–1023; incorporated dependency `docs/phase1/v14/PHASE_1_DOC.md` §4.7.2, lines 2937–2960; target §5.2’s profile-serialization dependency row.
- **Claim:** `/3` simultaneously requires two incompatible text grammars for its `gl` block without defining an adapter.
- **Evidence and trigger:** P2 says the manifest uses the capture plan’s flat, one-key-per-line scalar and escaping rules, under which strings are JSON strings and repeated data has explicitly declared dense keys. It also requires `GLCapabilityProfile` in P1’s text form and the exact canonical P1 fields under `gl`. P1’s owned serialization uses unquoted strings and an `extensions =` newline-delimited, indented block. A normal nonempty extension set therefore contains lines that are not legal manifest key/value records; vendor and GLSL strings likewise are not in the required JSON-string form. Neither P2 nor the inspected P7 receiving contract defines a nested escaped-profile field or a complete field-by-field translation, including extension ordering/count and key-prefix mapping.
- **Impact:** a conforming P1 profile writer cannot directly produce a conforming `/3` manifest. Independent client and runner implementations must invent incompatible transport details or reject an otherwise valid capability-bearing run.
- **Required correction:** choose and fully specify one transport representation: for example, one named JSON-escaped canonical profile-text scalar decoded through P1’s parser, or an explicit typed field/dense-extension mapping. Preserve P1’s ownership of standalone profile serialization, and update P2’s exact schema and P7’s receiver incorporation together.
- **§5 impact:** yes. The public run-manifest wire contract changes or is normatively clarified.

### N40-1 — Qualify the pinned README attribution for the temporal-blurring claim

- **Severity:** note.
- **Location:** target §0.1’s targeted README verification row; §3.4, lines 515–516; §4.3.4, lines 807–808; PD §19.1.
- **Claim/evidence:** PD §19.1 records the historical quoted “TAA/Bloom/FAA/Chromatic Aberration blurring” report, and the target cites it together with `[V:observed — Pintonium/README.md]`. The recovered README at the declared `9c2fcc1a4814cafc0242370757e9e05ea83c5be3` revision contains general limited-testing warnings, but not that bug statement. The targeted hook files do independently support the narrow hook-inventory observations. This review cannot authenticate the README-specific quotation at that revision.
- **Disposition:** retain the historical PD attribution and distinguish it from newly verified pinned-source facts, or identify the exact alternate source/revision containing the quotation. This does not undermine the normative v3 moving-scene requirement and does not establish that the historical bug report was false.
- **§5 impact:** no.

## 5. Counts, §5 consequence, and disposition

- **Blocking findings:** 0.
- **Corrections:** 3 — C40-1, C40-2, C40-3.
- **Notes:** 1 — N40-1.
- **§5 impact:** **yes**. The fixes touch exported tier/baseline/evidence semantics and the run-manifest receiver contract. Their coordinated amendments require fresh verification; this report does not grant downstream eligibility or replace final integration review.

The subsystem decomposition, required scene/motion design, source-free owner inspection, replay/failure routing, and OQ fallback are substantially complete. The three concrete defects are localized, repairable contract corrections, not structural-rebuild defects. No Resolutions were appended and no implementation or runtime success is asserted.

**Final verdict: PASS-WITH-CORRECTIONS.**

## Resolutions

### C40-1 — Stable OFF/ON baseline identity

Addressed architecturally on 2026-09-08 by D-P2-41 in P2 §§4.2.2/4.2.4–6,
4.7.2–4, 5, 8 and 12. Complete resolved pack and engine-option maps produce a
canonical optionStateSha256 independent of the engine-under-test build. The digest
is required in plans/manifests, feature associations, lookup keys, approval records
and cache paths; separate OFF/ON records and content-addressed raster leaves coexist.
No historical baseline is upgraded by inferred defaults. §5 changed; fresh review
and matching P7 receiving amendments remain required, not runtime approval.

### C40-2 — Authenticated image-decision closure

Addressed architecturally on 2026-09-08 by D-P2-42 in P2 §§4.2.5–6, 4.7–8,
5, 6, 8 and 12. Expanded index records bind canonical content-addressed comparisons;
each binds its candidate/plan, frozen approved baseline or oracle manifest, external
raster hashes/dimensions, complete effective tolerance/mask/calibration contents,
actual comparability/timing evidence, metrics and decision. Automated T3 retains both
same-state T1 children and the OFF/ON delta. No-follow canonical resolution and
transitive reauthentication/recomputation make absent/mismatched inputs invalidate
the effective ledger row to NOT_ATTEMPTED. Images remain outside Git. §5 changed;
this is authored correction, not executed comparison or fresh certification.

### C40-3 — Unambiguous profile transport

Addressed architecturally on 2026-09-08 by D-P2-43 in P2 §§4.5, 5, 8 and 12.
Current capture-plan/run-manifest are `/4`, with no `/1`–`/3` compatibility aliases.
Manifest gl.profile_text is the sole profile field when gl.available=true: one strict
JSON string containing P1 canonical write output. Decode once, invoke P1 parse/write,
require decoded-text canonical equality; duplicate/unknown/missing/conditional keys
and malformed strings reject. P1 retains standalone grammar ownership. P7 receiving
integration and fresh §5 verification remain required.

### N40-1 — Historical PD attribution, not pinned README authentication

Addressed on 2026-09-08 by D-P2-44 in P2 §0.1, §3.4, §4.3.4 and §11.
Temporal-blurring provenance now explicitly remains historical PD §19.1 evidence;
the earlier independently verified pinned-README attribution is not retained.
The review's pinned-source observation is accepted without claiming a new source
check. Historical quotes, original review bodies/verdict and the normative v3
moving-scene obligation remain untouched.

All four dispositions are documentation-only. No implementation, validation,
build, test, lint, formatter, runtime evidence or approval was performed.
The original PASS-WITH-CORRECTIONS remains historical; IR-01 and final §G5.3 remain open.

### Current R32/R39 receiving receipt — separate from the R40 dispositions

On 2026-09-08 P2 D-P2-45/46 received the published P5 §2.2/§4.1.1
PLANNED/REALIZED resource contract (D-P5-30/32), P1 D-P1-57 typed positional
draw-buffer recorder/assertion surface and P4 D-P4-33/34 opaque positional-route-v2
identity. P2 §§4/5/8/11/12 now require evidence_stage, preserve DEFAULT_RGBA versus
explicit RGBA8 requests, carry declarative clear_policy with CONSTANT-only components,
and permit allocated_format/allocation_origin only for REALIZED. Successful whole-estate
fallback remains explicit for every row; pure SIZING is PLANNED and accepted live capture
is REALIZED. Equality is stage/input-specific, not a plan-to-runtime fallback equivalence.
Wrong-variant keys reject rather than default; unavailable remains availability-only.

The C40 complete effective option identity, authenticated comparison closure and sole
gl.profile_text /4 transport remain intact. Original review body, counts and verdict,
and the preceding four dispositions are preserved. This append is a current receiving
receipt only, not an additional finding disposition or new verdict. No implementation,
validation command, test, build, formatting, lint or runtime result is claimed.
Fresh review, IR-01 and final §G5.3 remain open.