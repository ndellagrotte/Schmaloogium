## 0. Method and reading order

I first independently re-derived both supplied candidates against the complete Phase 6 target,
the governing v3 Part I, Phase 6 specification, document gate and mandatory template, the contract
ground truth in `docs/research/v1/RESEARCH.md`, and the manifest-selected binding contracts of
Phases 1, 3, and 4. Listed Pintonium and OptiFine materials were treated only as supporting
evidence, never as contract.

Only after settling both dispositions did I read Phase 6 Reviews 1–22, in numeric order, including
their resolutions, to compare the candidates against settled material. There were no reading-order
deviations, no network use, and no agent fan-out. Per the dispatched atomic-role rule and the
verify-loop skill, I did not invoke `$verify-loop`, `scripts/verify`, another `codex exec`, or any
subagent. No forbidden source was read. The Gate reported no drops, and no candidate was eliminated
before adjudication.

## 1. Findings

### candidate-001 — Custom upload algebra exceeds the authoritative six-type grammar

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:1225-1238` and
  `docs/phase6/v1/PHASE_6_DOC.md:1406-1418`
- **Claim:** The binding custom-uniform upload extension point exports integer-vector and matrix
  outputs that no authoritative pack-facing custom-uniform declaration can select.
- **Evidence:** Contract ground truth closes uploaded custom-uniform declarations to
  `float|int|bool|vec2|vec3|vec4`
  (`docs/research/v1/RESEARCH.md:1493-1496`). The target's own conformance map promises those same
  six output types (`docs/phase6/v1/PHASE_6_DOC.md:449`). Nevertheless, the closed
  `CustomUploadCommand` algebra adds `Int2`, `Int3`, `Int4`, and `Mat4`
  (`docs/phase6/v1/PHASE_6_DOC.md:1225-1238`), and §5 exports that broader algebra to Phase 11,
  including a binding Phase 1 request solely to execute `Int3`
  (`docs/phase6/v1/PHASE_6_DOC.md:1406-1418`). Linked-layout type checking cannot make an
  undeclarable output reachable. Integer-vector and matrix forms may remain on the built-in
  expression-input value side where independently required, but they are not contract-authorized
  custom upload outputs.
- **Required correction:** Restrict `CustomUploadCommand` and its §5 contract to `Float1`, `Int1`,
  `Bool1`, `Float2`, `Float3`, and `Float4`. Remove `Int2`, `Int3`, `Int4`, `Mat4`, D-P6-18, and the
  Phase 1 `ivec3` request unless authority is first amended to permit those output declarations.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

### candidate-002 — The change-trigger region omits binding runtime declarations in §2.2

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:318-348` and
  `docs/phase6/v1/PHASE_6_DOC.md:1382-1399`
- **Claim:** Binding public runtime declarations can change outside the manifest-selected §5
  region without the required synchronized §5 edit and fresh verification trigger.
- **Evidence:** Section 2.2 declares the public `UniformRuntimeFactory`, `UniformBuildResult`, and
  `UniformRuntime` signatures (`docs/phase6/v1/PHASE_6_DOC.md:318-348`), and the target expressly
  identifies §2.2 as one location of its binding definitions
  (`docs/phase6/v1/PHASE_6_DOC.md:114-115`). Section 5 summarizes those types and their semantics
  (`docs/phase6/v1/PHASE_6_DOC.md:1382-1386`), but its synchronization invariant explicitly
  incorporates only §§4.2, 4.9, and 4.13 (`docs/phase6/v1/PHASE_6_DOC.md:1396-1399`). Thus a method
  signature edit confined to §2.2 can alter a declared binding API without changing the watched
  interface region. The governing template assigns exposed named interfaces and data contracts to
  §5 (`docs/design/v3/DESIGN.md:838-840`). A semantic summary is not an exact incorporation of the
  callable declarations.
- **Required correction:** Explicitly incorporate the binding public declarations and signature
  semantics from §2.2 into §5, or reproduce their complete consumer-visible shape there, and make
  the synchronization invariant require every consumer-visible §2.2 API change to update the
  corresponding §5 row in the same revision.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported clean areas survived re-derivation. The corrected Pintonium B1/B6 mappings,
maintenance-addendum numbering, current verification-status wording, custom-refresh counter
synchronization, sampler maps, frame ordering, center-depth decision, smoothing rules, notifier
audit, provider seams, and Appendix D inventory remain coherent. Phase 3 declaration metadata and
Phase 4 layout, access, barrier, generation, and degradation contracts are otherwise consumed
consistently. The existing §5 incorporation rule correctly covers the detailed schemas and
semantics it actually names in §§4.2, 4.9, and 4.13.

Neither candidate was refuted, cleared, subsumed, or dropped on independent derivation. Reading
Reviews 1–22 last did not settle either candidate away. In particular, Review 20's correction
retained `Int3` by requesting the missing Phase 1 verb but did not reconcile it with the
authoritative declaration grammar, while Review 21's synchronization correction covered only the
three detailed sections then identified and did not incorporate §2.2's separately declared
binding runtime API. There were no Gate drops.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both findings are bounded interface corrections rather than structural misses requiring a rebuild,
so `FAIL` is not warranted. Literal `PASS` is unavailable while two corrections remain.

Both corrections require changing the manifest-selected `cross-phase-interfaces` region. The next
required action is a governed fix-up resolving candidate-001 and candidate-002 and appending the
resolution record to this review, followed by a fresh verification round before Phase 6 can close.

Trend/convergence: Round 22 reached literal PASS for the bytes and candidate surface reviewed
there, but Round 23 admits two newly exposed interface corrections. The loop has therefore
regressed from zero to two corrections and is not currently converged. Both defects remain locally
repairable, so the trend requires fix-up and another review rather than escalation to `FAIL`.

## Resolutions

### candidate-001 — resolved

Re-derived against `docs/research/v1/RESEARCH.md:1493-1496`, the pack-facing custom declaration
grammar has exactly six upload types. Section 4.13's `CustomUploadCommand` is now closed to
`Float1`, `Int1`, `Bool1`, `Float2`, `Float3`, and `Float4`; the built-in expression-input
`ExpressionValue` algebra remains broader because it serves a different, independently specified
surface. The synchronized §5 row now names only the six authorized outputs. All `Int3`/`ivec3`
dependency text, the additive Phase 1 request, D-P6-18, and the corresponding open-item/handoff
claims were removed. Neighboring command payload prose was narrowed as well.

This intentionally changes the manifest-selected cross-phase-interface region. A fresh verify
round is required before Phase 6 can close.

### candidate-002 — resolved

Section 5.1 now incorporates the complete consumer-visible callable shape declared in §2.2 for
`UniformRuntimeFactory`, `UniformBuildResult`, and `UniformRuntime`, including parameter and return
types, the closed build/adoption/reset domains, and all runtime accessors and operations. Its
synchronization invariant now expressly covers §2.2 and requires every consumer-visible API
change there to update the corresponding §5 row in the same revision.

This also intentionally changes the manifest-selected cross-phase-interface region and therefore
requires the same fresh verify round.

The compact §0.22 addendum records only the corrections and the current status remains not
verified. No version roll was performed.

### Notes deferred

None; the adjudication admitted no notes.
