# Phase 2 — Fresh whole-document architecture review 42

## Verdict

**PASS-WITH-CORRECTIONS**

**Counts:** 0 structural blockers, 1 required owner correction, 2 review notes. **section5Impact: true.**

The frozen Phase 2 architecture substantially meets its adopted v3 assignment, but its authenticated image-evidence contract requires preparation/warm-up timing records that its process-boundary transport does not carry. This is a discrete interface correction, not a structural failure requiring a rebuild. It prevents a fresh PASS for the current owner document.

## Scope and authority

Reviewed **all of `docs/phase2/v2/PHASE_2_DOC.md`**, §§0–12 and its closing receipts, recovering the elided owner-request/failure section. The assigned frozen SHA-256 is `c99e6cfce5f13cfd6c15cad2860c774444149a09266ad63825ae73a0bd7101c3`; the coordinating agent independently reported that the frozen owner bytes remain unchanged. I did not run a hash or validation command.

Governing inputs reviewed were `docs/design/v3/DESIGN.md` Part I (§G0–§G12), its Phase 2 specification, and the assigned and claim-relevant extents of `docs/research/v1/RESEARCH.md`: §§0–1, 3.1–3.2, 3.5, 4.1, 4.3–4.5, 5.1, 8–11, 12.5, Appendices A.1–A.3, B.1, G and H. I read the required PD §§4, 7 and 19, the three current workflow files, Phase 1 §5 and relevant incorporated capability/handle/recorder/diagnostic/failure contracts, and the actual producer/receiver sections needed for P3 inspection, P4 enrichment, P5 planning, P7 capture and checkpoints, P11 evaluator vectors, and P12 option application. These dependency reads establish the Phase 2 boundary only; they do not certify those owners.

## Required correction

### C42-1 — Persist preparation and warm-up timing across the process boundary

**Severity:** correction. **Owner:** Phase 2. **Receiver follow-up:** Phase 7. **§5 impact:** yes.

**Owner locations:** `docs/phase2/v2/PHASE_2_DOC.md` §4.2.6, particularly lines 828–835 and 855–867; §4.5.4, lines 1124–1125, 1153–1162, 1187–1197 and 1313–1314; §5.1.1, lines 2329–2348.

**Claim being assessed:** §4.2.6 requires `candidateTiming` and `referenceTiming` to retain the exact selected clock and “preparation/warm-up/sample timing records.” It subsequently requires comparison evidence to be cross-checked against authenticated source manifests and approval evidence. §5.1.1 separately requires owner-observed preparation/warm-up checkpoints, prohibits synthesizing their records, and requires P2 to verify that history.

**Evidence:** The sole `/4` run-manifest frame domain contains only post-warm-up samples. Its capture block supplies warm-up counts but no actual preparation/warm-up timing history or origin/checkpoint record. Unknown core fields are rejected, while `x.<producer>.*` extensions explicitly cannot affect a verdict. The retained plan contains targets, not these observations. On the producing side, `docs/phase7/v1/PHASE_7_DOC.md` §4.13, steps 3–5, projects sample timing into that same wire; §5.1’s timing grant at lines 3003–3029 exposes only private `checkpoint`, `validate` and `close` operations and keeps the checkpoint ledger/validation receipts privately until publication or failed-run disposal. It does not define a persisted export of that ledger to the external runner.

**Impact:** On an ordinary capture with warm-up, the headless comparison publisher cannot obtain all of the actual timing records its mandatory comparison schema requires, or later authenticate them from the retained manifest/plan closure. Copying schedule targets or interpreting `actualWarmupFrames` as those observations would violate the express no-inference rule. Following the fail-closed rules instead leaves the affected image-tier evidence incomplete. The live checkpoint validation mechanism may correctly reject a bad execution; that does not supply the separate durable evidence required for subsequent comparison publication and ledger reconstruction.

**Required change:** Specify a canonical, source-free transport and retained representation for the actual origin and preparation/warm-up timing/checkpoint evidence needed by §4.2.6. Bind it to the run’s retained plan/manifest and define coverage, ordering, availability/failure and hash-validation rules; alternatively explicitly define a sufficient owner-issued checkpoint summary and make the comparison contract consume that summary rather than unspecified full records. Amend P2’s exposed §5 contract and obtain the matching P7 producer receipt. The comparison publisher and later ledger reader must be able to consume the same retained owner observations after the client exits, without target reconstruction or verdict-bearing extension keys. Preserve historical schemas/reviews as history and require a fresh review for the changed interface.

## Whole-owner assessment

- **Doc gate and milestone mapping:** §§3.1–3.5 map all seven App G packs and the v0.1–post-v0.5 exit criteria to named runs. The six initial scene families include real dense motion; shadow, sky and weather are expressly covered without treating Pintonium as their oracle. Static T2 selection does not discharge the separate six-family motion obligation. The provisional dual-spec release cadence is explicitly identified as an upstream Research conflict, not silently narrowed.
- **Before-renderer reachability:** §§2.2, 4.11, 4.14 and 9.2 preserve the `:conformance → :engine` dependency direction. The fixture/parser/golden skeleton can be built without Minecraft or GL. Complete real goldens expressly wait for same-load P3 inspection, same-request P4 enrichment and P5 pure planning; a synthetic skeleton is not represented as a matrix result.
- **Closed results and lifetimes:** P3 `Off/Failed/Inspected`, P4 `Ready/ShadersOff`, and P5 `Valid/Invalid` with `Available/Unavailable` have explicit dispositions. Inspection candidates remain caller-owned and are closed in `finally`. PLANNED sizing is not promoted to REALIZED allocation evidence. P11 `FAIL/UNSUPPORTED` cannot disappear into a passing aggregate, and P12 queued persistence is not mistaken for completed rendering.
- **Tier and comparison invariants:** Current §§4.2.5–4.2.6 establish nonempty run-specific evidence domains and exact set equality. T0 requires no comparison, T2 requires its static shot ordinals rather than unavailable PATH oracles, and T3 includes primary and full OFF/ON/manual feature evidence. Stable effective option identity and separate raster-addressed paths preserve both feature baselines. The remaining timing-transport hole is C42-1, not a reopening of the superseded domain corrections.
- **World and image identity:** §§4.5.5 and 4.10.3 now use a complete admitted generation descriptor, immutable cache receipt and pre-load save hash. P7’s D-P7-47 receiver explicitly verifies and projects those inputs. Subject-only builds do not fragment the world/baseline identity. The canonical option-state/raster image paths agree across promotion, manual oracle placement, cache layout and comparison reconstruction.
- **Diagnostics and capture failure:** Every actual GL error fails T0 regardless of attribution. P1 owns the replay-attribution law; P6/P7 supply ordered owner results. The current P2/P7 text retains collector failures and late restoration errors through shutdown and prevents COMPLETE after delivery loss. Hook application reports remain owner projections, not capability inference from images.
- **Licensing and authority:** Cache-outside-worktree enforcement, workspace-only report uploads, source-free P3 projection, external rendered-image storage, manual canonical acquisition and explicit fail-after-change golden regeneration satisfy the adopted conservative artifact policy. No forbidden transformation source was read or adopted in this review.
- **OQ-10:** §10 contains the exact question, local/CI procedures, compatibility-profile and real-smoke criteria, partial-success disposition and a fallback that moves no milestone gate. An unrun spike is an implementation prerequisite, not an architecture defect.

## Notes and limitations

**N42-1 — Architecture review only.** No files were edited. No tests, builds, formatters, linters, capture runs, fixture downloads, calibration, oracle approval or validation commands were executed. Their absence is not reported as a defect. Current `.github/workflows/*` remain template workflows; Phase 1’s documented migration and Phase 2’s proposed job fill are not claimed to exist in executable form. R2–R4 in P2 §5.4 remain explicitly requested integration work rather than grants invented by this review. This result does not clear IR-01, final §G5.3 integration, implementation commencement or any sibling phase.

**N42-2 — Historical reference evidence remains qualified.** PD §19’s temporal-blurring statement is historical evidence and is correctly qualified by D-P2-44; the normative motion requirement comes from the adopted design. The permitted local hook lookup found `reference-src/Pintonium-main/...`, not the historical `pintonium-9c2fcc1` path. I did not transfer a historical revision identity to that checkout or present its README as newly authenticated evidence. No superseded historical PASS or schema receipt is treated as a certificate of the frozen owner.

## Conclusion

The current document is coherent enough for a bounded fix-up, but **C42-1 must close the durable timing producer/transport/consumer chain before this owner can receive PASS**. Its correction changes the binding interface region and therefore requires fresh verification under §G1.3. Final integration remains independently open.

## Resolutions

### C42-1 — Durable preparation/warm-up/origin evidence (2026-09-08)

Authored D-P2-50 in the owner §§4.2.6, 4.5.4, 5.1.1 and milestone/checklist/decision
receipts. Current `/4` now requires timing availability/completeness/restoration, an
owner-issued source-free checkpoint-origin summary and dense live-validated actual
PREPARATION/WARMUP/SAMPLE reports with acknowledgment-ledger counts. Existing frames
remain post-warm-up samples only. Explicit coverage, ordering, scalar/absence/failure,
hash/retention and manifest/plan/sample-join rules prevent targets or counts from
manufacturing observations. Both publisher and later ledger reader reconstruct the
same canonical timing text from retained authenticated manifests after the client exits.

P7 receives the exact timing.* projection in P2 §4.5.4 through its existing private
checkpoint/validate/close protocol; it must retain and project actual origins and all
validated reports before private disposal and settle restoration before publication.
The matching P7 producer receipt is an integration obligation, not inferred from the old
private ledger. No verdict-bearing extensions, reconstructed origin, public checkpoint
getters or new P1/P6 API are introduced.

This is an architectural correction, not a fresh verdict. The original body, counts and
verdict above remain unchanged. No validation commands, implementation or runtime result
are claimed; fresh whole-owner/receiver review and final integration remain open.