# OQ-15 — Shared-context async compile reliability across drivers (compat contexts)

**Open question (verbatim, `docs/research/v1/RESEARCH.md:1021`):** "Shared-context async
compile reliability across drivers (compat contexts)"

**Why it matters (RESEARCH):** §6.2 headline feature. **Blocks:** quality-of-life.
**Phase 14 stake:** whether `AsyncCompileTier.SHARED_CONTEXT` may ever leave
`INLINE`, per family, under D-P14-12's allowlist mechanics.
**Status: OPEN — headless leg executed 2026-09-10; the live leg (the actual question)
has not run.** This artifact follows the shape of `docs/decisions/OQ-3_GL_CONTEXT.md`.
(PHASE_14_DOC §10.1 step 8 names this file `OQ-15_ASYNC_COMPILE.md`; the hyphenated
name is the one recorded here.)

## What the spike needed vs what ran

The full procedure (PHASE_14_DOC §10.1 steps 1–8) requires a **live client**: GLFW
hidden shared-window creation against the real main context on ≥2 driver families
(NVIDIA proprietary + AMD Mesa `radeonsi` minimum), worker-context currency checks,
per-pack K1–K5 measurement with 20 repeats, and the adversarial set. None of that was
executable headlessly, so **no family verdict below is a pass**.

What *was* executable now, over the Phase 1 recorder (`RecordingGLDevice`), is the
**decision machinery** the live leg will plug into:

1. **K1 comparison harness rehearsal.** Two recorder drives of the identical
   request sequence — create shaders, compile, create program, attach, link,
   validate, use, uniform locates, deletes — one standing for the inline path, one
   for the worker-context model (same scripted responses), render **byte-identical
   facade records** (`Oq15HeadlessRehearsalTest.inlineAndWorkerModelDriveRenderIdenticalFacadeRecords`,
   run 2026-09-10, pass). The per-program record-equality comparison §10.1 step 3
   demands (link status, logs, uniform/attribute lists — the recorder's
   deterministic record) is therefore mechanically executable as a harness; the
   live leg replays it against real driver output instead of scripts.
2. **Failure containment.** A scripted worker-compile failure produces a failed
   `CompileResult`, and no `link`/`use` is ever recorded
   (`failedCompileContainsWithoutPublication`, pass) — the "any difference is a
   failure, not a curiosity" posture holds at the facade, and the INLINE fallback
   contains a bad worker without publication.
3. **Row-16/D-P14-12 table semantics** (`DriverPolicyTest`, permanent §8.1 row 16,
   pass): deny-by-default, unknown vendor/renderer → `INLINE`, allowlist →
   `SHARED_CONTEXT`, `FORCE_OFF` overrides everything, denylist overrides
   `FORCE_ON`, blank components are wildcards, matching is case-insensitive
   substring.
4. **Engagement gate composition** (`shippedTableDeniesAndPlannerKeepsInline`,
   pass; `ModernizationPlanDerivationTest.compileStaysInlineUntilOwnersAdoptTheExecutor`,
   pass over all ten matrix profiles): the shipped table deny-defaults every family
   on `AUTO`, and the planner's adoption gate (R-P14→P4-1 + R-P14→P7-1 ungranted,
   D-P14-22) keeps **every** policy row at `AsyncCompileTier.INLINE` regardless of
   what the table would say. The `FORCE_ON` leg of the bare table resolves
   `SHARED_CONTEXT` by design (allowlist override), but no plan can reach the table
   while the executor is not adopted.

Not exercised headlessly (facade does not model it): fence produce/observe/wait
semantics, worker-thread currency, per-pack scene rendering, stall timing. These
are precisely the live leg's subject matter.

## Decision

- **OQ-15 remains open.** No family is allowlisted; `AsyncCompileDriverPolicy.SHIPPED`
  keeps an **empty allowlist and empty denylist** (deny-by-default data in
  `engine/gl/modern/AsyncCompileDriverPolicy.java`). Enabling a family after a live
  pass is a **data change** (add one `Family` row), never a code change.
- Every `GlModernizationPlan.derive` result resolves `compile = INLINE` today;
  the compile row's ledger entry stays open until the live spike passes AND the
  compiler owners adopt R-P14→P4-1 (Phase 4) + R-P14→P7-1 (Phase 7) — after which
  `InlineCompileExecutor` is the same-thread default and automatic fallback.
- **Fallback (designed now, §10.1 part 4):** the current synchronous
  `ProgramRegistryCompiler.compile` ships unless/until both owners adopt; a failed
  spike never migrates an ungranted interface and never changes render-thread
  publication. Failure on every family closes OQ-15 as "shared compat contexts are
  not reliable on our matrix" and writes that back to RESEARCH.md §11.

## Per-family verdict table (the D-P14-12 allowlist source)

| Family | Vendor substring | Renderer substring | Verdict | Missing evidence |
|---|---|---|---|---|
| NVIDIA proprietary, Linux | `nvidia` | — | **NOT RUN** | §10.1 steps 1–8 on live hardware: shared-context creation + currency (steps 1–2); K1 exact record equality per pack (step 3, SEUS Renewed, Chocapic13 V9, projectLUMA + one dual-spec pack); K2 T1-diff zero failures in 20 repeats (step 4); K3 ≤20% long-frames and no median regression (step 6); K4 adversarial set incl. quarantine accounting (step 7); K5 zero driver crashes |
| AMD Mesa `radeonsi`, Linux | — | `radeonsi` | **NOT RUN** | same list |
| Intel Mesa `iris`, Linux | — | `iris` | **NOT RUN** | same list (strongly-preferred fourth) |
| AMD Windows proprietary | `amd`/`ati` | — | **NOT RUN** | same list (strongly-preferred fourth) |

Success requires **all of K1–K5 per family** (K3 partial passes recorded and
allowlisted only by explicit implementation judgment, with the number recorded).

## Live leg checklist (to close)

1. In a pinned Cleanroom dev environment, repeat §10.1 steps 1–2 per family:
   hidden `glfwCreateWindow(1, 1, "", NULL, mainWindow)` from the render thread
   after Minecraft's window exists, both with no version hints and explicit compat;
   record reported versions, hint-state interaction with OQ-3, and restore every
   hint.
2. Worker currency on a dedicated thread; render thread unaffected (draw one
   vanilla frame after).
3. Run the record-equality harness of this artifact against live output, per pack
   (step 3), then K2/K3/K4/K5 (steps 4–7) with the §9.2 condition 3 instrumentation.
4. Append each family's K1–K5 results to the table above; add matching
   `AsyncCompileDriverPolicy.Family` rows only for recorded passes; write the
   closure note into RESEARCH.md §11's status column.
