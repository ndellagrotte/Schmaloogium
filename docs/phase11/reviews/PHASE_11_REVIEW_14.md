# Phase 11 Architecture Review — R14 / Frozen Attempt 7

## Identity, scope, and authority

- **Owner:** `docs/phase11/v1/PHASE_11_DOC.md`, complete §§0–12.
- **Round:** R14.
- **Frozen SHA256:** `f94602333126981289753226f8adf10e462cda2b24039eb6a8bc401f84538001`.
- This is the supplied inventory identity, also recorded at `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_7.json:65–70`; I did not run a checksum or other validation command.
- **Selected governing design:** `docs/design/v3/DESIGN.md`, as selected by the owner's header, not by a newest-version rule. I read Part I §§G0–G12 and the Phase 11 specification at `docs/design/v3/DESIGN.md:2279–2353`.
- **Governing research:** `docs/research/v1/RESEARCH.md` §§0–1, §3.4, §6.3, Appendix D, Appendix F.6, and the OQ-22 row. Published pack-author evidence was read directly at `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:326–425`. `docs/MOVES.md` supplied version/path and retired-tooling context.
- Dependency review included the current Phase 3 and Phase 6 §5 publications and their incorporated declaration-capture, schema, value, bridge, submission, and retirement definitions. Receiving behavior was inspected in Phase 7 composition, Phase 12 presentation, Phase 2 evaluator orchestration, Phase 14 measurement, and Phase 1 diagnostic dispatch. Historical reviews and fix receipts were not used as proof.

**Result:** 1 substantive correction; 2 non-blocking notes. **§5 impact: YES**, because the correction changes the compilation/name-resolution semantics incorporated from §4.1 into §5, and should extend the §5.6 conformance catalog.

## Independent whole-owner checks

### Language, planning, and interpreter

The owner covers the documented declaration types, literals, operators, named functions, vector/color access, matrix row/column access, fourteen view booleans, biome inputs, and the complete seven-name exclusion union. The exclusion disposition correctly follows `docs/research/v1/RESEARCH.md:1493–1512` rather than the shorter design restatement. The finite binary32 policy, checked int boundary, domain errors, parser bounds, backend ID, and source-less unsupported-backend failure are explicit. The interpreter-first recommendation and conditional compiled-backend experiment remain architecture decisions rather than measured performance claims.

I independently traced the current reader-to-dependency graph in `docs/phase11/v1/PHASE_11_DOC.md:613–645`. For `a=b+c; b=a; c=temperature`, the SCCs are `{a,b}` and `{c}`; only `{a,b}` is cyclic. Reverse dependency-to-reader propagation reaches dependents of that cycle, not its independent prerequisite `c`. A uniform independently reading `c` remains valid. For forward declaration `a=b+1; b=temperature`, unresolved dependency counts are initially one for `a` and zero for `b`, yielding `b,a`. Distinct dependency edges, ready-set source-ordinal tie breaking, and complete emission after invalidity removal are consistent. This closes the specifically requested graph-orientation/SCC question for the graph the document currently defines. The separate namespace restriction in Correction 1 still excludes a documented class of graph nodes.

I also inspected load-time reachability, once-per-refresh memoization, declaration-order submission, local invalidity propagation, lazy operators/functions, random consumption without rewind, and per-definition smooth overlays. The per-cell last-commit clock at `docs/phase11/v1/PHASE_11_DOC.md:692–745` retains elapsed time across skipped branches, advances the controller only once per observed frame, and commits value/timestamp together. The late-use and late-first-use tables at `:1354–1356` agree with those rules by inspection. The smoothing equation is corroborated only as behavior by the permitted digest at `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:597–601`; no decompiled implementation was consulted.

### Dependency and receiving-side contracts

- **Phase 3 intake:** Current schema is 23 at `docs/phase3/v1/PHASE_3_DOC.md:4152–4218`. The active owner receipt at `docs/phase11/v1/PHASE_11_DOC.md:1159–1166` uses the current constant and supersedes older numeric receipts. Actual declaration capture and publication preserve decoded text, duplicates, attribution, and source order (`docs/phase3/v1/PHASE_3_DOC.md:2891–2908,3540–3559`). The owner does not reopen Properties or acquire binary resources.
- **Phase 6 receiver:** I read the actual schema/value algebras and dispatch at `docs/phase6/v1/PHASE_6_DOC.md:1522–1669`, not merely the owner's older coordinates. Bool uses `Bool1`; active-program absence is `SkippedAbsent`; invalid/type/duplicate cases reject; accepted commands remain ordered. Counted `Completed` and `Aborted` results must match the sink ledger, and invalid counts discard the accepted batch. The owner accounts for all three outcomes without relabeling a sink rejection as an expression error.
- **Lifecycle:** The owner reset/close map at `docs/phase11/v1/PHASE_11_DOC.md:1036–1103` was compared with Phase 6's actual retirement precedence, permanent stale-capability guards, and final-use ordering at `docs/phase6/v1/PHASE_6_DOC.md:1673–1793`. Phase 7's actual construction/adoption/reset/reactivation/close branch at `docs/phase7/v1/PHASE_7_DOC.md:3419–3441` installs one controller, handles Success/Partial/Failure and Activated/Rejected, and does not manufacture a P6 CLOSE alias or fourth participant.
- **Diagnostics and provider handling:** The typed sink, Declaration/SourceLess distinction, runtime versus returned load delivery, coalescing, and contained sink failure were checked across `docs/phase11/v1/PHASE_11_DOC.md:795–982` and the actual P7 collector at `docs/phase7/v1/PHASE_7_DOC.md:3443–3452`. P1's receiver actually routes CHAT and LOG_ONLY and logs both (`docs/phase1/v14/PHASE_1_DOC.md:4917–4945`). Provider Unavailable remains a structural refresh result, not a fabricated neutral snapshot.
- **GUI:** P7 projects only the final attempt disposition (`docs/phase7/v1/PHASE_7_DOC.md:3402–3417`). P12's actual presenter forwards the unchanged optional P11 projection to `showErrors`, handles source-less entries, clears on empty, and rejects older/different-pack views (`docs/phase12/v1/PHASE_12_DOC.md:1225–1245,1483–1496`). This is not inferred from an acknowledgment row.
- **Conformance and measurement:** P2's executable-design dispatch consumes the closed steps, exact typed diagnostic observations, both compiler entry points, and FAIL/UNSUPPORTED outcomes at `docs/phase2/v2/PHASE_2_DOC.md:1758–1804`. P14's actual measurement procedure at `docs/phase14/v1/PHASE_14_DOC.md:2490–2507` retains the real-pack-miss plus AST-dispatch attribution trigger, independently of synthetic stress. No run, measurement, or sibling-owner certification follows from those document checks.

## Substantive corrections

### C14-1 — Admit documented custom-uniform references into the expression graph

- **Severity:** correction — contract-visible compatibility defect.
- **Owner locations:** `docs/phase11/v1/PHASE_11_DOC.md:511–517`, `:585–595`, `:613–616`, and D-P11-4 at `:1591`.
- **Claim:** Uniform output names cannot be referenced by another expression, and only `variable.*` definitions enter the resolvable dependency graph.
- **Independent evidence:** The shipped author specification explicitly declares `uniform.float.screenDark=...` and then reads that custom uniform in `uniform.vec3.screenDark3=vec3(screenDark, heldItemId, biome)` at `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:421–422`. The preceding statement that variables are reusable and not uploaded does not say that uniforms are forbidden as expression inputs. Appendix F.6 likewise contains no such prohibition. Contemporary corroboration is visible in `reference-src/Pintonium-main/common-shaders/src/main/java/net/irisshaders/iris/uniforms/custom/CustomUniforms.java:61–69,278–293,309–326`: uniform and variable definitions enter the same resolver-visible collection, with upload status recorded separately. This current checkout corroborates the interpretation; it is not proof of the absent historical pin.
- **Observable breakage:** The published `screenDark3` example reaches Phase 11 intact through Phase 3's lossless declaration stream, but the mandated resolver rejects `screenDark` as UNKNOWN_NAME. The dependent vec3 uniform is consequently disabled at load even though the specification demonstrates it as valid. The existing SCC correction cannot repair this because custom uniforms are expressly excluded from the graph.
- **Minimal owner fix:** Remove the unsupported uniform-name prohibition and extend the existing clean-room definition graph/resolver to include referenced custom uniforms, retaining an independent uploaded/non-uploaded designation. Apply the same exact SCC membership, reverse-reader invalidity, declaration-boundary conversion, and once-per-refresh memoization rules to those definitions, while preserving original uniform submission order and Phase 6's sole upload ownership. State duplicate/collision behavior for the shared resolvable namespace, revise D-P11-4, and add an original uniform-to-uniform reference vector plus its cyclic/error-isolation counterpart. This does not require borrowing a reference implementation or adding GL knowledge to `engine.expr`.
- **§5 impact:** YES. §4.1 semantics are expressly incorporated at `docs/phase11/v1/PHASE_11_DOC.md:1128–1132`; synchronize the compiler/plan rows and §5.6 receiving vectors. No Phase 3 configuration-schema change is implied solely by accepting these expression references.

## Non-blocking notes

### N14-1 — Historical source pins are unavailable; current corroboration is not pin verification

- **Severity:** note.
- **Owner locations:** `docs/phase11/v1/PHASE_11_DOC.md:34–40,79–87,438–448`.
- The cited `reference-src/pintonium-9c2fcc1/` paths do not exist in this checkout. I attempted the exact cited class path and inspected the actual reference inventory; the available tree is `reference-src/Pintonium-main/`. Bounded reads of its CustomUniforms and IrisFunctions corroborate resolver/graph shape and the unregistered-round/incomplete-vararg warning, but not historical byte identity. The current pipeline's update call at `reference-src/Pintonium-main/common-shaders/src/main/java/net/irisshaders/iris/pipeline/CommonIrisRenderingPipeline.java:522–526` also cannot be substituted for the historical cadence claim. Current README/root license material is an umbrella claim, not independently established stareval chain of title. Preserve the clean-room/no-reuse gate and qualify historical corroboration; do not silently repoint historical citations or promote license confidence.

### N14-2 — The checked verification-profile item describes retired machinery

- **Severity:** note; not a runtime or architecture blocker.
- **Owner location:** `docs/phase11/v1/PHASE_11_DOC.md:1656–1657`.
- The current checklist says the target profile exists and each fresh round revalidates selectors. `docs/MOVES.md:95–103` explicitly records removal of profiles and the verification loop, with the header now authoritative. Preserve genuine historical preflight provenance, but replace the current checklist hook with the actual document-review workflow rather than asking implementers to execute retired machinery.

## Limitations and disposition

This was a read-only architecture review. No files were changed; no build, test, formatter, linter, checksum, model validation, or runtime command was run. All source reads stayed within the permitted author-document, digest, and bounded OSS areas; no chatlogs, root transcripts, forbidden Oculus transformation paths, transformer implementation, or OptiFine decompiled implementation were read. License conclusions remain qualified, and zero-allocation, numerical implementation fidelity, platform behavior, and real-pack performance remain future implementation evidence gates.

The document is substantially complete and does not require structural reconstruction. Its exact SCC/reverse-reader/dependency-first treatment is sound by inspection, but the documented uniform-reference compatibility defect is current and substantive. Fresh verification is required after the affected §5 semantics are amended.

**Corrections: 1. Notes: 2. §5 impact: YES.**

**PASS-WITH-CORRECTIONS**

## Resolutions

### C14-1 — Architecture amendment, 2026-09-08

D-P11-27 in `docs/phase11/v1/PHASE_11_DOC.md` explicitly supersedes D-P11-4's unsupported
uniform-reference prohibition without erasing that historical decision. §§4.1–4.5 now register
uniforms and variables in one exact-name namespace with an independent upload designation,
first-owner duplicate/collision handling, forward references and declared-type converted memo
values. Exact SCC membership and reverse-reader invalidity preserve independent prerequisites;
both definition kinds evaluate at most once per refresh, with eager definition ordering,
AST-local lazy effects and transactional smooth commits. §§4.8/4.9 retain declaration-order,
uniform-only P6 submission and normal SkippedAbsent handling, without GL knowledge.

§5 compiler/plan rows and incorporated semantics, six original §5.6 receiving vectors,
§8 cases and §12 checklist are synchronized. P2 receiving adoption is Main-owned; this resolution
does not certify that receiver. No P3 configuration-schema bump or old-schema compatibility is added.
N14-1's historical-pin qualification is retained explicitly in §0.14; N14-2's active checklist hook
now uses current document review rather than retired verification machinery.

The frozen review body, counts and PASS-WITH-CORRECTIONS verdict above are unchanged. These are
documented architecture resolutions, not fresh verification or runtime evidence. No validation,
build, test, formatter or linter was run; fresh owner/receiver review remains required.
