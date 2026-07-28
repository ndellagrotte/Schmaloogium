# Schmaloogium — Design Document (**REV3:** Oculus Revision, v2.0-RC3)

> **REV3:** **Status:** v2.0-RC3 "OCULUS", 2026-07-26. This file is
> `docs/design/v2.0-RC3/DESIGN.md` and is the **RC3 candidate overall**. It follows
> v2.0-RC2 (`docs/design/v2.0-RC2/DESIGN.md`) in revision history, but authoring this
> candidate does not migrate downstream governance: **RC2 governs Phase 1 from its §0.11
> onward, and Phase 1 has thirteen numbered reviews; v1.1 governs Phase 2.** RC1, RC2,
> and v1.1 remain available at their versioned paths.
> **REV3:** **Sources:** [RESEARCH.md](../../research/v1/RESEARCH.md) remains the source of
> truth; [PINTONIUM_DESIGN.md](../../reference/pintonium/v1.0/PINTONIUM_DESIGN.md) (`PD`)
> remains the working 1.12.2 reference report; and
> [OCULUS_DESIGN.md](../../reference/oculus/v1.0/OCULUS_DESIGN.md) (`OD`) is the
> citation-gated Oculus mining report. Oculus material is evidence, never contract.
> **REV3:** **What changed (RC2 → RC3):** no phase redesign, adoption, or phase-governance
> migration. This revision adds the Oculus evidence boundary and reading rules (§G12),
> its path alias (§G0.2) and licensing boundary (§G7 item 8), records the gated answer to
> the prior §G11.5 gap list, corrects §G0.4's stale governance prose and literal mapping
> counts, and audits every addition at the close. The six Gate-dropped findings remain
> inadmissible; all five discovered RESEARCH conflicts are recorded, with RESEARCH
> controlling every disposition.
> **What changed (REV1 → REV2):** no phase redesigns — this revision trues the document
> against the repository and its own change channels. Path and process conventions are
> re-anchored to the post-reorg tree (§G0.2 aliases into `reference-src/`; §G1 doc/review/
> brief locations and the `/verify-loop` mechanization); the four outstanding §11.5
> upstream requests from PHASE_1_DOC and PHASE_2_DOC are absorbed (§G2.4 rung 2a, the
> §4.1/§3.5 probe-set citation split, the §G1 path conventions, the §G6 derived-artifacts
> clause); an adoption procedure is recorded (§G0.4); and reference citations are
> corrected or sharpened against direct re-reads of `reference-src/` and the MCP mappings
> (§G11.3 traps, template ground truth, licensing state, code coordinates). Every change
> is `**REV2:**`-marked in place and audited in the closing tables.
> **What changed (v1.1 → REV1):** the Pintonium codebase — a legally clean (LGPL-3.0), *working*
> Celeritas+Oculus fork running demanding shader packs on 1.12.2 LWJGL3 — is now a
> primary reference implementation for Phases 3–14. That revision added rules of
> engagement for it (§G11), corrected or strengthened specific phase designs against its
> verified findings, and recorded the decisions its divergences force. RESEARCH.md remains
> the contract; PD is evidence, never contract (§G0.1a).
> **Audience:** AI agent sessions building or verifying one phase each (protocols in §G1),
> and the project owner supervising them.

### **REV3:** Revision highlights (RC2 → RC3)

| **REV3:** Area | RC2 | RC3 |
|---|---|---|
| Candidate status | Stale all-v1.1 governance statement and eleven-review Phase 1 count | **REV3:** RC3 remains the candidate overall; RC2 governs Phase 1 from its §0.11 onward, Phase 1 has thirteen reviews, and v1.1 governs Phase 2 |
| Reference set and path conventions | RESEARCH + PD/Pintonium rules | **REV3:** adds gated OD/Oculus evidence, the `Oculus/…` alias, and §G12; Oculus is loader-independent evidence only, never a 1.12.2 hook reference |
| Licensing | Pintonium and general Iris/Angelica boundaries | **REV3:** adds Oculus LGPL/sub-license boundaries, the unresolved `org.taumc` prohibition, and the clean-room outcome for unverified stareval |
| Prior §G11.5 gaps | All ten recorded as "no help available" | **REV3:** sliders answered; held-item, `blockEntityId`, and companion-atlas policy partial; the other six gaps remain unanswered |
| Contract conflicts | Pintonium conflicts and pre-decided rejections | **REV3:** records all five Oculus/RESEARCH conflicts; the three retained quotes keep their gated anchors, while C-PB07 and C-TX01 remain recorded discoveries with failed quotes |
| Adoption accounting | Approximate "`~16`" mapping count | **REV3:** literal counts corrected to 12 mappings for v1.1 and 13 for RC2; no RC3 line pins, `DESIGN_PINS`, `PHASE_FACTS`, adoption, or governance changes |
| Closing audit | REV1 and REV2 change audits | **REV3:** adds a RC2→RC3 additions audit matched to the actual edit sites |

### Revision highlights (RC1 → RC2)

| Area | RC1 | RC2 |
|---|---|---|
| Adoption machinery | "not yet adopted" note only | **§G0.4 adoption procedure**: harness line-pin re-derivation, operator-doc updates, phase-doc migration via §G1.3 fix-ups, MOVES.md relabel rule |
| Path conventions (§G0.2) | reference trees as workspace-root siblings | shorthands become **aliases** into gitignored `reference-src/`; RESEARCH.md §12.1's stale sibling path reported as a conflict per §G0.1; PD's true location corrected |
| §G1 artifact locations | repo-root `PHASE_<N>_DOC.md`; unnumbered review "next to the phase doc" | `docs/phase<N>/v<K>/` + `reviews/PHASE_<N>_REVIEW_<R>.md` + `briefs/`; `/verify-loop` + `VERIFY_LOOP_BRIEFS.md` named as the §G1.2/§G1.3 mechanization (resolves PHASE_2_DOC §11.5 item 3) |
| Session input hygiene | §G1.2 author-context bar only | explicit **forbidden-sources rule**: `docs/**/chatlogs/` and root `*.txt`, stated by pattern |
| Degradation ladder (§G2.4) | five rungs; single-feature GL failure uncovered | **rung 2a** — feature-level failure disables that feature only; five-rung phase docs grandfathered to relabel at next fix-up (resolves PHASE_1_DOC §11.5 item 4) |
| Fixture policy (§G6) | pack files only | + **derived-artifacts clause**: `[D-P2-5]`/`[D-P2-6]`, manifests-not-images, explicit `-PupdateGoldens` (resolves PHASE_2_DOC §11.5 item 4) |
| Licensing state (§G7, P1) | "Phase 1 executes the license swap" | swap recorded as executed (commit `aa917a6`); residual scope = or-later statement, headers, `mcmod.info` |
| G8/S5 Kirino material | "local sketch material" at `projects/kirino` | empty uninitialized submodule — track upstream directly |
| Pintonium repo traps (§G11.3) | untracked-directory copy; stray jsons; "copy of this document" | tracked-in-HEAD deleted **zip**; empty VintageFix leftovers; untracked stale **v1.1** copy |
| Backend count (§G3.1, G10, P1) | "4 MC backends" | 3 platform modules + 2 shared in the checkout; PD §2's `:babric` absent from the tree — annotated, PD unmodified |
| P1 spec ground truth | §4.1 cited for the extension set; pin location unstated; plain source sets; "JUnit template-wired" | §4.1/§3.5 citation split (resolves PHASE_1_DOC §11.5 item 3); pin at `build.gradle:60`; `java-templates`/`resource-templates`; JUnit = build flag, no `src/test/` yet |
| Code coordinates (P4, P9, P10, P12) | `ProgramCreator.java:21-25` bare; elided forge122 paths; bare `RenderGlobalMixin.java:459` | range re-verified at the line + double-bound-11 detail; full Iris-package paths; full core-source-set path + `:72`/`:454` companion facts |
| Front matter | supersedes v1.1; REV1 what-changed | supersedes v1.1 **and** RC1; REV1→REV2 what-changed added; P1/P2 verification state recorded |

### Revision highlights (v1.1 → REV1)

| Area | v1.1 | REV1 |
|---|---|---|
| Reference set | RESEARCH.md + OF decompile (observation-only) + Iris/Angelica reading | + **Pintonium**: working, LGPL, 1.12.2-proven shader engine; rules of engagement in §G11 |
| P3 preprocessor | hand-rolled per §3.5 | **jcpp (Apache-2.0, verified)** with `#version`-hoisting and line-preserving macro-injection techniques (PD §7.2) |
| P3 pitfalls | — | Pintonium bugs B1/B2/B3/B12 become explicit conformance rows (dead comment directives, stubbed component analysis, miswired halflife, `#`-stripping) |
| P5 depthtex0 | copy targets + mechanics, mechanism open | **Framebuffer depth-renderbuffer→texture swap** — proven 1.12.2 reference, now mandatory reading (PD §5.2) |
| P5 frame-end flip | App F.7 carryover assumed | explicit **recorded decision**: OF-faithful carryover vs Iris-style copy-back (both mechanisms specified; PD §5.1) |
| P5 clears | App B.1 ruling | + verified flip-aware clear semantics and the **fog-clear-alpha-forced-to-1.0** quirk (Sildur's pink reflections, PD §5.1) |
| P6 centerDepthSmooth | sync `glReadPixels` (+PBO at P14) | **GPU-side smoothing candidate** (1×1 ping-pong + `#define` redirect, no AST needed) with mandatory contract check; P14's PBO item becomes conditional (PD §6.3) |
| P6 wiring | — | notifier→producer audit required (Pintonium's unwired `blendFunc` notifier NPEs, PD B6) |
| P7 hooks | App E + OF digest + Cleanroom patches | + **verified 7-row injection timeline** into `EntityRenderer`/`RenderGlobal` (PD §4); sky/weather/clouds flagged as reference-free |
| P9 dual-spec | resolve names against registries | + **modern→1.12 alias table** and the `MC_VERSION`→11300 re-parse trick (PD §8.1) |
| P10 id delivery | 56-byte format everywhere | + **constant-attribute candidate** (`glVertexAttribI3i`) for entity/TE ids, contract-checked (PD §8.2) |
| P10 OQ-5 | detect-and-bail, mechanics TBD | + concrete **detection anchors** — Pintonium *is* the canonical adversary (PD §9) |
| P12 OQ-9 | vanilla-GuiScreen fallback unbuilt | fallback **already built and working** in Pintonium's GUI screens (PD §14) |
| P14 | sampler/DSA/KHR_debug on paper | deployed references exist (PD §15); G8/S2 feasibility evidence upgraded to strong |
| Phase context budgets | as listed | +4–10k each for assigned PD reading |

---

## Part I — Global sections

Everything a phase session must know that is not phase-specific. Phase sessions read all of
Part I; it is deliberately compact. Part II holds the per-phase specifications.

---

## G0. Front matter

### G0.1 Relationship to RESEARCH.md

**RESEARCH.md remains the source of truth.** This document arranges its findings into an
executable design; it does not supersede it. Rules:

- Where this document cites a RESEARCH.md section (`§n`) or appendix (`App X`), the cited
  text is normative and is *not* restated here in full. Phase sessions read the cited text.
- On any conflict between this document and RESEARCH.md, **RESEARCH.md wins**, and the
  conflict must be reported in the phase doc's *Decisions & open items* section — never
  silently resolved.
- RESEARCH.md's confidence tags (`[V:doc]`, `[V:observed]`, `[D-n]`, `[Q:OQ-n]`, `[U]`,
  `[A]` — RESEARCH.md §0.2) carry over unchanged. Phase docs must preserve provenance:
  contract claims cite `§3`/Apps; behavioral claims cite `§4`; platform claims cite `§5`.
- RESEARCH.md facts were verified 2026-07-24. The only fact expected to rot quickly is the
  Cleanroom loader version (daily alpha cadence, OQ-2) — Phase 1 re-verifies and pins it.

### G0.1a Relationship to PINTONIUM_DESIGN.md (REV1)

**PINTONIUM_DESIGN.md (`PD`) is a mining report — evidence, never contract.** It describes
what the Pintonium codebase actually does, cross-checked by direct reads. Rules:

- PD citations are written `PD §n` (e.g. `PD §5.2`). Claims adopted from PD carry the
  provenance tag `[V:observed — Pintonium <path>]` in phase docs.
- On any conflict between PD and RESEARCH.md, **RESEARCH.md wins**; the conflict is
  reported per the G0.1 rule. PD §18 is a standing table of Pintonium behaviors that
  *conflict with* the contract — those are precisely the things not to copy.
- Any adoption of a Pintonium mechanism for a **contract-visible** component (§G4.2)
  follows the decision rule in §G11.4: contract check + recorded phase decision, never a
  silent swap.
- PD itself is a project document and may be read freely; the Pintonium *code* it points
  at is governed by the licensing rules in §G7 item 7 and §G11.2.

### G0.2 Path conventions

**REV2:** the reference trees are no longer workspace-root siblings — the
versioned-directory reorg (`docs/MOVES.md`) placed them inside this repo under
`reference-src/` (gitignored, `.gitignore` line 29: present on disk, absent from git).
The shorthands below are kept as **aliases**, because RESEARCH.md, PD, and this document
cite them throughout; each resolves to the actual directory shown. Where RESEARCH.md
§12.1 says "sibling repo `schlorbium-project/`", that path is stale the same way — a
doc-vs-repo conflict reported here per the §G0.1 rule (RESEARCH.md is not modified; read
its paths through this table).

| Shorthand (alias) | Resolves to | Meaning |
|---|---|---|
| `Schmaloogium/…` | this repo root | the mod + this document + RESEARCH.md (under `docs/`) |
| `schlorbium-project/…` | `reference-src/schlorbium-HD_U_G6_pre1/…` | decompiled OptiFine G6_pre1 reference (study-only — §G7 rules) |
| `cleanroom-src/…` | `reference-src/cleanroom-0.6.6-alpha/…` | Cleanroom platform sources. **Vanilla render classes exist there only as `.java.patch` files** (`cleanroom-src/patches/minecraft/…`); full sources are present only for Forge (`src/main/java/net/minecraftforge/`) and loader internals (`src/main/java/com/cleanroommc/`). For vanilla method bodies and signatures, use the MCP `cleanroom` server recipes (RESEARCH.md §12.4), not cleanroom-src. |
| `Pintonium/…` | `reference-src/pintonium-9c2fcc1/…` | the Pintonium repo (Celeritas + Oculus/Iris fork with 1.12.2 shader support) — LGPL reference implementation and 1.12.2 field report. **Rules of engagement: §G11** (search exclusions, licensing, do-not-inherit lists). **REV2 correction:** `Pintonium/DESIGN.md` is an *untracked, stale copy of design v1.1*, not of this document; `Pintonium/PINTONIUM_DESIGN.md` does **not** exist — the analysis (`PD`) lives only at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md`. |
| `Oculus/…` | `reference-src/Oculus-1.12/…` | **REV3:** the gated Oculus/Iris logic mine, not a usable 1.12.2 hook reference. Every admissible finding is `loader-independent`; use §G12's licensing, repository-trap, evidence, and trust rules. The analysis (`OD`) lives at `docs/reference/oculus/v1.0/OCULUS_DESIGN.md`, not inside the reference tree. |

### G0.3 The load-bearing structural principle

**Phases are subsystems; milestones are implementation staging.** A phase architects its
*entire* subsystem — including parts whose implementation RESEARCH.md §9 schedules for a
later milestone — and tags every designed component with the milestone at which it gets
implemented (`v0.1`…`v0.5`, `post-v0.5`). Phase order ≠ milestone order. Example: Phase 7
designs the depth-copy architecture now, tagged "copies implemented at v0.5", because
retrofitting architecture is the failure mode that kills projects (decision D-4's rationale,
generalized).

### G0.4 Adoption procedure and readiness (**REV3:** RC3 status)

**REV3:** This revision is the unadopted candidate overall. Per `docs/MOVES.md`'s
version-label rule, a design revision keeps its `-RC` label exactly until downstream
adoption; adoption is a deliberate maintainer operation, never a side effect. Partial
governance is already explicit: Phase 1 is governed by RC2 from its §0.11 onward, while
Phase 2 remains governed by v1.1. The line-pin trap is still silent: pointing either
phase at RC3 without re-derivation would not error; it would feed sessions the wrong
text. Adopting RC3 for a downstream phase means, in order:

1. **REV3:** **Re-derive every line pin at adoption time** in
   `.claude/workflows/verify-loop.js`: the appropriate design path, the literal
   section→line mapping counts (**12 mappings for v1.1 and 13 for RC2**), and the adopting
   `PHASE_FACTS` row's `spec`/`docGate` ranges — computed from the governing file's own
   headings, never shifted from another revision. RC3 authoring derives no line pins.
2. **Update the operator docs**: the version-pin warning in
   `.claude/commands/verify-loop.md` (and `docs/tooling/VERIFY_LOOP_BRIEFS.md` if any
   prompt text changes — change both, saying which), plus `docs/MOVES.md`'s
   `DESIGN.md`-collision table and version-label rows.
3. **REV3:** **Migrate only the phase selected for adoption.** Phase 1 currently consumes
   RC2 from §0.11 onward; Phase 2 consumes v1.1. Assess RC3's delta against the sections
   that phase actually consumed; route claim-touching deltas through a §G1.3 fix-up
   session (the review-file `## Resolutions` convention), **not** a rebuild — unless the
   delta invalidates the doc's §5 cross-phase interfaces, which re-opens verification per
   §G1.3's re-verify rule.
4. **Relabel** per the `docs/MOVES.md` version-label rule (the `-RC` suffix means exactly
   "no downstream doc has adopted it"); record any directory move in MOVES.md and run its
   dangling-reference sweep.

**REV3:** Until all four steps are complete for a phase, that phase keeps its current
governing design (RC2 for Phase 1 from §0.11 onward; v1.1 for Phase 2) and this file
remains the candidate overall. A session briefed with line coordinates that do not match
its assigned document stops and reports rather than guessing.

---

## G1. Agent protocol — session types and how to execute them

Every phase passes through two session types, always in this order and always as separate
fresh sessions: a **build session** (§G1.1) authors the phase doc; a **verify session**
(§G1.2) adversarially reviews it. Corrections, if any, are applied by a **fix-up session**
(§G1.3). Scheduling — who may start when — is governed by the wave cadence and gating
invariant in §G5.2–§G5.3.

### G1.1 Build sessions

You are one fresh Claude Code session assigned exactly one phase from Part II. Your contract
has three steps, then you stop:

1. **Read and understand your phase.** Mandatory reading, in order:
   - **REV3:** All of Part I (G0–G12) of this document, plus your own phase spec in Part II.
   - RESEARCH.md §0 (reading guide, tags) and §1 (mission, non-goals, decision log) — always.
   - The **Required inputs** listed in your phase spec: specific RESEARCH.md sections and
     appendices, specific external files, PD sections, and the PHASE docs of your declared
     dependencies.
   - Nothing more. Do not read other phases' specs beyond their titles in G5, other
     RESEARCH.md sections, or unlisted files unless you hit a genuine gap — and if you do,
     record what you read and why in your phase doc's header.
   - **Pintonium reading discipline (REV1):** when your Required inputs list `PD §n`
     sections or `Pintonium/…` paths, the §G11 rules of engagement apply — search
     exclusions (§G11.3), licensing (§G11.2), provenance tags (§G11.4). Treat PD's
     "Relevance" blocks as pointers, then verify load-bearing claims against the cited
     Pintonium source before building on them.
2. **Architect the implementation.** Produce the complete technical design for your
   subsystem: data models, algorithms, state machines, lifecycles, exact semantics, module
   placement per §G3, interfaces exposed to dependent phases. Where your spec assigns an
   open question (OQ-n), write a *spike specification* per §G4.4 — do not attempt to resolve
   the OQ yourself.
3. **Write your phase doc** to `docs/phase<N>/v1/PHASE_<N>_DOC.md` (N = your phase
   number; the existing `docs/phase2/v1/PHASE_2_DOC.md` is the worked example), following
   the mandatory template in §G9. Then **stop working**. **REV2:** paths are repo-relative;
   the old repo-root convention predates the versioned-directory reorg. A build session
   always writes `v1`; version directories roll only at fix-ups, per `docs/MOVES.md`
   (`git mv` + the harness `docVersion` bump, run together, never mid-loop).

**Hard rules:**

- **No code.** No source files, no skeletons, no stubs, no build changes. The deliverable is
  the architecture document only. (Illustrative type/method *signatures inside the doc* are
  encouraged where load-bearing; compilable files are not.)
- **No verification, no review.** Do not run builds or tests, do not launch review/adversarial
  agents, do not screenshot-diff anything, and do not review your own doc beyond ordinary
  care. Verification is the separate verify session (§G1.2) — never yours.
- **No scope creep.** Anything your spec's *Scope — out* list names is owned by another
  phase; state the ownership in your doc §1 and move on.
- **Context discipline.** Finish below ~40% of a 1M-token context (~400k tokens). The
  mandatory reading for every phase is budgeted well under half of that; if you approach the
  ceiling mid-design, finish the doc at reduced detail and flag the gaps in doc §11 rather
  than overrun. If your spec names a fallback split (only Phase 7 has one), invoke it.
- **Decision recording.** Phase-local decisions get IDs `D-P<N>-<k>` (e.g. `D-P3-1`) with a
  one-line rationale each, logged in doc §11. Decisions that would contradict RESEARCH.md's
  D-1..D-10 are not yours to make — flag them instead. **REV1:** decisions adopting a
  Pintonium mechanism for contract-visible behavior must additionally cite the contract
  check performed (§G11.4) — these are exactly the decisions a verify session attacks first.
- **Input contradictions** (doc-vs-doc, doc-vs-behavior — e.g. the App B.3 depthtex1 unit
  inconsistency) are *reported* in doc §3/§11 with your ruling and its provenance, never
  silently smoothed over.
- **Do not modify** RESEARCH.md, any design revision (`docs/design/v1.1/DESIGN.md`,
  `docs/design/v2.0-RC1/DESIGN.md`, `docs/design/v2.0-RC2/DESIGN.md`,
  `docs/design/v2.0-RC3/DESIGN.md` — **REV3:** the list now names all four),
  PINTONIUM_DESIGN.md, OCULUS_DESIGN.md, or another phase's doc. Propose changes to any
  of them in your doc §11 ("requested upstream changes").
- **Forbidden sources (REV2):** prior sessions' transcripts — any directory named
  `chatlogs/` anywhere below `docs/` (any extension), and any `*.txt` at the repo root —
  are never inputs, for any session type: they transmit an author's conversational blind
  spots (the §G1.2 independence rule, generalized). Stated by pattern, not by filename,
  because `/export` mints new root-level transcripts under dated names.
- **Dependency docs are contracts.** What a dependency's PHASE doc exposes in its §5
  (cross-phase interfaces) is what you build against. If it is missing something you need,
  flag the request in your doc §5; do not invent the missing interface as if it existed.
- **Tools:** use the MCP `cleanroom` recipes (RESEARCH.md §12.4) for vanilla symbol
  resolution and platform docs. Web search only if a listed input is missing or
  contradictory — RESEARCH.md's web facts are same-day fresh.

### G1.2 Verify sessions

You are one fresh Claude Code session assigned to adversarially review one completed
`PHASE_<N>_DOC.md`. You must not be the session that wrote it, and you must not be given
the author's conversation context — a reviewer sharing the author's context inherits the
author's blind spots.

1. **Read** exactly what the build session was assigned: Part I, the phase spec,
   RESEARCH.md §0–§1, the spec's Required inputs, and the dependency PHASE docs — then the
   phase doc under review. Same reading list, independent eyes.
2. **Attack the doc.** Your posture is refutation, not summary. Checks, in priority order:
   - **Doc gate** — every criterion in the spec's *Doc gate* met, literally.
   - **Conformance-map audit** — zero unmapped in-scope contract rows; spot-check mapped
     rows *against the cited RESEARCH.md/App text* for semantic fidelity (the
     depthtex1-unit-11 class of error is exactly what you exist to catch).
   - **Interface honesty** — everything the doc consumes from dependencies actually exists
     in their §5; everything promised to dependents is specified, not gestured at.
   - **Scope discipline** — nothing designed that the spec's *Scope — out* assigns
     elsewhere; nothing from *Scope — in* silently dropped.
   - **Template completeness** — all thirteen G9 sections present and substantive; every
     assigned OQ carries a full spike spec (question/procedure/criteria/fallback, §G4.4).
   - **Binding decisions** — no D-1..D-10 contradicted; no contract-visible component
     "improved" (§G4.2).
   - **Pintonium compliance (REV1)** — every PD-derived claim carries its provenance tag;
     every §G11.4-gated adoption (GPU centerDepthSmooth, flip copy-back, constant-attribute
     delivery, …) shows its contract check and recorded decision; the relevant §G11.5
     do-not-inherit rows are demonstrably handled; nothing traces to the AGPL
     transformation-lib (§G11.2 rule 2); stubbed/dead Pintonium features (PD §17) are not
     re-inherited.
3. **Write** `docs/phase<N>/reviews/PHASE_<N>_REVIEW_<R>.md` (R = this review round,
   starting at 1 — rounds are always numbered; the harness computes the name): a findings
   list (each finding: location, claim, evidence, severity **blocking / correction /
   note**) and exactly one verdict — **PASS**, **PASS-WITH-CORRECTIONS**, or **FAIL**.
   Reserve FAIL for structural misses that require rebuilding the doc; fixable defects
   are PASS-WITH-CORRECTIONS. Then **stop** — do not fix anything yourself. **REV2:**
   review briefs, when kept, live in `docs/phase<N>/briefs/`, named for the session they
   commission. In practice §G1.2/§G1.3 run mechanized — `.claude/commands/verify-loop.md`
   is the operator runbook and `docs/tooling/VERIFY_LOOP_BRIEFS.md` the readable prompt
   copy; the evidence rules there (repo-relative citations, the forbidden-sources
   pattern) bind hand-run sessions equally.

Verify sessions obey the build-session hard rules (no code, no scope creep, context
discipline). Context budget: the build session's mandatory reading plus the phase doc
itself — comfortably inside the same ceiling.

### G1.3 Fix-up sessions and the "verified" state

- On **PASS-WITH-CORRECTIONS**: a fresh session reads the phase spec, the phase doc, and
  the review file; applies the corrections to `PHASE_<N>_DOC.md`; and records each
  resolution in the review file under a `## Resolutions` heading. On **FAIL**: rerun the
  build session with the review file added to its Required inputs.
- **Re-verify only if §5 changed.** If corrections altered the doc's *Cross-phase
  interfaces* section, the doc goes through a fresh verify session before any dependent
  consumes it; otherwise the fix-up closes the phase.
- A phase is **verified** when its latest review verdict is PASS, or PASS-WITH-CORRECTIONS
  with all resolutions recorded and no §5 change outstanding. Only verified docs are valid
  dependency inputs (§G5.3 invariant).

**REV2 provenance note:** the §G1.1–§G1.3 path conventions above resolve PHASE_2_DOC
§11.5 item 3 ("§G1.1, §G9 and §G1.2 all name repo-root paths that no longer match the
repository"). The convention adopted is the versioned-directory scheme the reorg actually
chose (`docs/phase<N>/v<K>/` + `reviews/` + `briefs/` — `docs/MOVES.md`), which
superseded the request's own `docs/phase<N>/artifacts/` proposal; that layout no longer
exists.

---

## G2. Mission and constraints digest

**Mission (D-2, RESEARCH.md §1.1):** OptiFine/Iris-format shader-pack support for Cleanroom
clients on MC 1.12.2. Just shaders. Nothing else. The non-goals list (§1.2) is binding;
scope creep must argue against that written record.

### G2.1 The decision log (binding on every phase)

Condensed from RESEARCH.md §1.3 — phase docs honor these or flag, never silently overturn:

| ID | Decision (short form) |
|---|---|
| D-1 | Cleanroom-exclusive |
| D-2 | Shaders only; written non-goals list (§1.2) |
| D-3 | Target = the fixed pack-compatibility matrix (App G), not "Iris parity" |
| D-4 | Stage registry architected for the full modern stage set from day one; wires a subset |
| D-5 | Mixin-based hooks only; no class replacement; ~25–30 targeted injections (App E) |
| D-6 | Engine-core / loader-glue seam; core headless-testable and GL-abstracted |
| D-7 | GPL-3.0-or-later license (template's MIT-style LICENSE must be replaced) |
| D-8 | Published docs + OSS source OK; LGPL-3.0 reuse with compliance; two prohibitions: glsl-transformer (AGPL) never-copy, OF decompile behavioral-observation-only |
| D-9 | Compatibility-profile GL baseline; no core-profile rewrite |
| D-10 | Conformance harness from week one |

### G2.2 Hard constraints

- **Compat profile is mandatory** (D-9, §6.1): packs are GLSL-120-era fixed-function-coupled
  code. LWJGL3's value is modern entry points *within* compat. `GL_QUADS` stays available;
  keep the triangle-strip composite fallback anyway.
- **No UBOs for the pack contract** (§1.2, §6.1): packs declare default-block uniforms;
  per-program upload with location caching is structurally required.
- **Cooperate with fixed-function state**: vanilla renders through FF matrices, client
  arrays, and display lists; matrix uniforms are captured from the FF stack at defined
  moments (§4.4). **REV1 note:** this cooperation is no longer paper-only — Pintonium
  demonstrates FF-matrix capture, FF-stack shadow matrices, and `GL_QUADS` interop in
  production on the 1.12.2 compat context (PD §6.2, §16).
- **LWJGL3-native code only**: never compile against `org.lwjglx` (runtime-only shim, itself
  in flux — OQ-21).
- **Alpha-platform drift**: loader/toolchain versions are pinned by Phase 1 and re-verified
  deliberately, never floated.

### G2.3 Threading model

The render thread owns all GL. Permissible off-thread work: pack file discovery/parsing,
preprocessing, expression compilation (Phase 11), and — only via the Phase 14 shared-context
design with its mandatory synchronous fallback — shader compilation and texture upload.
Everything else runs on the render thread. Phase docs state thread ownership for each
component (doc §7). Chunk-build worker threads are a first-class thread-safety surface for
Phase 10's side channel (Pintonium's thread-local pattern is the proven shape, PD §9).

### G2.4 Failure philosophy: never crash the client

Graceful degradation is contract-adjacent behavior packs implicitly rely on (§4.7). The
degradation ladder, top to bottom:

1. A custom uniform that errors at runtime disables *that uniform* only.
2. A built-in uniform whose GL upload errors disables *that uniform* only.

2a. **(REV2)** A pack *feature* whose GL call fails at runtime — neither a single uniform
(rungs 1–2) nor the whole program (rung 3) — disables **that feature** only, at the
owning phase's discretion, while the program keeps running. Numbered `2a` so existing
§6 maps stay valid: phase docs written against the five-rung ladder (PHASE_1_DOC §6's
explicitly unnumbered row; PHASE_2_DOC's ladder mapping) re-label their feature-level
rows to 2a at their next §G1.3 fix-up — no rebuild. Resolves PHASE_1_DOC §11.5 item 4,
the gap Phases 5, 6 and 13 would otherwise each re-derive privately
(`[fix-up: PHASE_1_REVIEW_7.md V7-7]`).

3. A program failing compile/link/validate deletes itself, reports a user-visible error, and
   resolves through the backup chain (App A.2).
4. A capability gate failing at init turns the pack off gracefully with a chat error.
5. Nothing in the shader engine ever crashes the client or corrupts the vanilla framebuffer
   path. Shaders-off must always be a reachable state.

Every phase doc has a §6 mapping this ladder onto its subsystem.

**REV1 note on rung 3:** Pintonium's synthesized fixed-function-consumer programs
(PD §13) validate a further rung-3 design space — *generated* passthrough programs as a
degradation step — with the standing warning that heuristic pack-layout detection is a
root cause of its Solas entity-brightness bug (PD §19.2): heuristics are never a contract
path (§G4.2). Schmaloogium's backup-chain design (App A.2) is the principled version of
the same need.

### G2.5 Performance posture

Clean code first, optimize with evidence (§6.3): modern GC removes OF's allocation-discipline
constraint; do not replicate array caches / mutable-pose machinery. Initial performance is
allowed to be worse than OF-with-shaders (§2.4); optimization happens inside our own pipeline
only (Phase 14), never by drifting into the §1.2 non-goals.

---

## G3. Architecture overview

### G3.1 Module layout

Three Gradle projects, enforcing the D-6 seam structurally (Phase 1 refines names and build
mechanics; the *seam itself* is not refinable — §5.2 makes it strategically load-bearing,
because Kirino-Engine may replace the entire render backend under us):

```
:engine       Pure-JVM engine core. Java 25. ZERO dependencies on Minecraft, Forge,
              Cleanroom, Mixin, or LWJGL. Testable headless via JUnit alone.
  engine.pack        pack discovery, file model, dimension folders, sources
  engine.preprocess  #include, macro header, preprocessor, option discovery/rewrite
  engine.config      shaders.properties model, options/profiles/screens, ID-file grammar,
                     persistence formats
  engine.registry    stage registry (modern superset shape), program slots, backup chains,
                     per-program state
  engine.buffers     framebuffer/color-buffer policy: ping-pong, flips, clears, formats,
                     sizing (policy only — GL objects live behind the facade)
  engine.uniforms    built-in uniform model, cadences, smoothing math, value-provider
                     interfaces (implementations live in :mod glue)
  engine.expr        custom-uniform expression language
  engine.gl          the GL facade: interfaces + a GLCapabilityProfile value object +
                     a recording/replay implementation for headless tests

:mod          The Cleanroom mod. Depends on :engine.
  mod.core           @Mod entry, lifecycle, config, engine bootstrapping
  mod.glue           adapters: world-state sampling, Forge registries, resources,
                     the LWJGL3 implementation of engine.gl
  mod.mixin          all Mixin classes (SRG-targeted, MixinConfigs manifest attribute)
  mod.gui            pack selection + options screens (ModularUI)
  mod.compat         coexistence detection (chunk-renderer replacers etc.), bail registry

:conformance  The Phase 2 harness: scene specs, capture drivers, image diff, fixture
              downloader, headless golden-run tests of :engine against recorded
              GLCapabilityProfiles.
```

Dependency rule, stated as a testable constraint: **`:engine` compiles with no classpath
entry from Minecraft/Forge/Cleanroom/Mixin/LWJGL, and `:mod` never reaches into `:engine`
internals beyond its published interfaces.** Phase 1 specifies the enforcement mechanism.

**REV1 note:** Pintonium validates that a single version-agnostic shader core can drive
radically different MC backends behind ~3 service interfaces (PD §2) — but its core is
*not* headless-testable (static GL calls throughout). Our module-based seam is strictly
stronger for D-10; their service-interface inventory is a completeness checklist for our
`mod.glue` providers (Phase 1). **REV2 count note:** the 9c2fcc1 checkout holds three
platform modules (`forge1710`, `forge122`, `modern` — the last a 12-version stonecutter
project) plus two shared cores; PD §2's diagram additionally names a `:babric` backend
absent from the checkout. "Multiple backends" stands; count them from the tree, not from
PD.

### G3.2 One-frame data flow (orientation only; normative detail in §4.4)

```
frame begin ─ sample world state ─ snapshot prev-frame camera/matrices
  → SHADOW PASS (Phase 8) → bind main FBO, clear per rules (Phase 5)
  → GBUFFERS phases with per-phase program dispatch (Phases 4/7)
  → depth copy → DEFERRED ping-pong passes → translucent terrain → hand
  → COMPOSITE ping-pong passes → FINAL to screen (Phase 7)
```

Uniforms refresh at every program switch (Phase 6); vertices carry the extended attributes
(Phase 10) stamped with alias IDs (Phase 9); every source line the GL sees passed through
the Phase 3 front-end; every buffer it writes is Phase 5's.

**REV1 note:** PD §6.1 adds a hard ordering constraint to "frame begin": world-state
sampling / uniform-update notification fires **before** any buffer resize or clear (the
center-depth sample must read intact depth). Phase 7's frame-begin sequence encodes this.

### G3.3 Where the mixins live

All vanilla-touching code is `mod.mixin` + `mod.glue`, targeting the App E class/method
catalog. Mixins stay *dumb*: they observe state, delegate to engine entry points, and never
contain policy (the reference's "use program is the universal state barrier; hook sites stay
dumb" rule, §4.2, adopted as our own).

**REV1 note:** externally validated — Pintonium's `Program.use()` is literally this
barrier (memory barrier → use-program → uniform/sampler/image re-push, PD §3.2), and its
proven 1.12.2 hook set (PD §4) consists of exactly such dumb delegation sites.

---

## G4. Cross-phase contracts

### G4.1 Vocabulary

Pack-facing terms are used **verbatim** in code, docs, and identifiers: colortex, flip,
backup chain, gbuffers, deferred, composite, shadowcomp, `mc_Entity`, `at_tangent`, dfb/sfb,
etc. — the App H glossary is the project dictionary. Do not invent synonyms; a reader
holding `doc/shaders.txt` must recognize every name.

### G4.2 "Contract-visible" components

A component is **contract-visible** when packs can observe its behavior (everything in
RESEARCH.md §3 and Apps A–D, F). Contract-visible designs must match the cited contract
exactly, cite their provenance row, and may not "improve" semantics (flip quirks, clear
colors, unit numbers, fallback chains are all load-bearing). Non-contract internals may be
modernized freely (§4.8 Keep/Adapt/Skip is the map).

**REV1:** adopting a Pintonium mechanism for a contract-visible component is allowed only
through the §G11.4 decision rule (contract check + recorded `D-P<N>-<k>`). The standing
candidates are: GPU-side `centerDepthSmooth` (PD §6.3), frame-end flip copy-back (PD §5.1),
constant-attribute id delivery (PD §8.2). Everything in PD §18 is pre-decided: **do not
adopt** (attribute locations 11–14, dynamic unit map, dimension-folder semantics,
`Random(0)` noise, …).

### G4.3 Milestone tags

Every designed component carries exactly one tag: `v0.1` … `v0.5`, or `post-v0.5`. The tag
means "implemented at that milestone", per the §G0.3 principle. Phase docs aggregate tags in
their §9 so the implementation effort can be sliced by milestone across phases.

### G4.4 OQ / spike protocol

Phases do not resolve open questions; they specify **spikes** for the implementation effort.
A spike spec contains: (1) the question, verbatim from RESEARCH.md §11; (2) a concrete
procedure (what to build/run/measure, in dev env or CI); (3) success and failure criteria;
(4) the fallback design if the spike fails — designed *now*, so a failed spike never stalls
a milestone. Spike results are recorded back into RESEARCH.md §11 (status column) by the
implementation effort, plus an addendum note in the owning phase doc.

### G4.5 Error taxonomy and logging

Three user-facing channels: chat errors (pack-level failures, capability gates), the shader
GUI (per-program compile errors, per RESEARCH.md §4.7), and the log. Log channels are
per-subsystem (`schmaloogium.pack`, `.compile`, `.frame`, `.gl`, …; Phase 1 fixes the list).
Debug affordances reserved from day one: `-Dschmaloogium.debug.saveSources` (the
`shaders.debug.save` equivalent, App F.8) and KHR_debug labels/groups in dev (Phase 14 —
deployed pattern available at PD §15).

### G4.6 GL discipline

All engine GL goes through the `engine.gl` facade — no direct LWJGL calls outside
`mod.glue`'s facade implementation. Cooperation with `GlStateManager` is one-directional:
we *observe* its state (e.g. `blendFunc` uniform) and restore what we perturb; we never
bypass it for state it caches (the cache would go stale and break vanilla rendering).
Per-program alpha/blend overrides lock state for the program's duration and release it
after, per §4.2.

**REV1:** the observation direction must be *wired*, not assumed — Pintonium's 1.12.2 tree
never assigns its `blendFunc` state notifier, and any pack declaring the `blendFunc`
uniform NPEs at program build (PD B6). Phase 6 designs the notifier; Phase 7 owns the
hook that feeds it; Phase 6's doc carries a notifier→producer audit table.

---

## G5. Phase index and dependency graph

### G5.1 The 14 phases

| # | Phase | One-line scope | Milestone | Depends on | OQs |
|---|---|---|---|---|---|
| 1 | Foundation & project architecture | Template → real project: modules, seams, Mixin wiring, license, pins, test scaffolding | v0.1 | — | OQ-2, OQ-12, OQ-20 (seam), OQ-21 |
| 2 | Conformance harness | T0–T3 machinery, scenes, diffing, fixtures, CI viability | v0.1 (design) | 1 | OQ-10 |
| 3 | Pack front-end | Discovery → preprocessing → options → shaders.properties → validated PackConfiguration | v0.1 | 1 | OQ-7 (arch) |
| 4 | Stage/program registry & compilation | Modern-superset stage registry, all classic catalog slots, backup chains, compile/link | v0.1 | 1, 3 | — |
| 5 | Framebuffer & buffer architecture | Main/shadow FBOs, ping-pong/flips, clears, formats, sizing, resize | v0.1 | 1, 3, 4 | — |
| 6 | Uniform & sampler system | App D inventory, cadences, smoothing, unit map, value providers | v0.1 | 1, 3, 4 | — |
| 7 | Render-loop integration | Frame driver + the full Mixin hook catalog; v0.1 assembly | v0.1 exit | 2, 3, 4, 5, 6 | OQ-3, OQ-4 |
| 8 | Shadow pass | Celestial camera, texel snap, traversal, depth split, PCF | v0.2 | 4, 5, 6, 7 | — |
| 9 | ID aliasing & per-draw dynamics | Alias resolution, per-mod merge, held-item, entity/TE id uniforms | v0.3 | 3, 6, 7 | — |
| 10 | Extended vertex pipeline | 56-byte format, per-quad attributes, entity stack, both draw paths | v0.3 | 4, 7, 9 | OQ-5, OQ-14 |
| 11 | Custom-uniform expression engine | Full F.6 language, evaluator, error isolation | v0.4 | 3, 6 | — |
| 12 | Options GUI, persistence & reload | ModularUI screens, sliders, profiles, persistence, reload paths | v0.4 | 1, 3 (soft: 7) | OQ-9 |
| 13 | Texture systems | `_n`/`_s` atlases, noise, custom textures, atlasSize | v0.5 | 3, 5, 7 | — |
| 14 | GL modernization & performance | Sampler objects, async readback/compile, KHR_debug, GC posture | v0.5 + QoL | 5, 6, 7, 13 | OQ-15, OQ-22 |

"Depends on" is literal: those phases' `PHASE_<i>_DOC.md` files are declared inputs — and
per the §G5.3 invariant they must be **verified** docs (§G1.3 definition), not merely
written. Phase 12's dependency on 7 is **soft** — it needs only Phase 7's
*reload-lifecycle* section; if PHASE_7_DOC.md is not yet verified, Phase 12 may run
against Phase 7's spec in this document and flag the assumption.

REV1 does not change the phase graph; it changes what each phase reads and decides.

### G5.2 Execution waves with the build → verify cadence

Each wave builds its phases (in parallel), then verifies them (in parallel), before
dependent waves start:

```
Wave 0:  build P1                          → verify P1
Wave 1:  build P2 ∥ P3                     → verify P2 ∥ P3
Wave 2:  build P4                          → verify P4
Wave 3:  build P5 ∥ P6                     → verify P5 ∥ P6
Wave 4:  build P7 ∥ P11                    → verify P7 ∥ P11   (P11 needs only P3+P6)
Wave 5:  build P8 ∥ P9 ∥ P12 ∥ P13 ∥ P14   → verify all five   (only P9's verdict gates Wave 6)
Wave 6:  build P10                         → verify P10
Final:   integration review (§G5.3 item 4)
```

The strictly ordered spine is P1 → P3 → P4 → {P5, P6} → P7. Note the deliberate inversions
against milestone order: the harness (P2) is designed *first* (D-10), and aliasing (P9)
precedes the vertex pipeline (P10) because alias resolution is upstream of `mc_Entity`
stamping.

### G5.3 Workflow invariants

1. **The gating invariant: a phase doc must be verified (§G1.3 definition) before any
   dependent build session reads it.** This is the entire point of the cadence: an
   unverified error in a high-fan-out doc propagates into every dependent and forces their
   rebuilds (P3 feeds eight phases; P1 feeds everything; the leaves feed nothing).
   Verification urgency is proportional to fan-out — highest early, lowest at the leaves.
2. **Waves are a schedule, not a barrier.** A build session may start as soon as *its own*
   dependencies are verified — full completion of the prior wave is sufficient but not
   necessary (e.g. Wave 6 waits only on P9's verification, not on all of Wave 5's; P11 may
   start the moment P3 and P6 are verified, regardless of P7).
3. **The one sanctioned exception** is Phase 12's soft dependency on Phase 7 (§G5.1):
   Phase 12 may build against Phase 7's spec in this document, flagging the assumption. If
   Phase 7's verified doc later contradicts that assumption, Phase 12's doc gets a fix-up
   session (§G1.3), not a rebuild.
4. **Final integration review.** After all 14 phases are verified, one additional fresh
   session reads every phase doc's §1 (scope & boundaries) and §5 (cross-phase interfaces)
   plus each doc's §11 hand-off items, and audits them *against each other* — the sibling
   drift that per-phase verification structurally cannot catch. Checklist: consumed-vs-
   exposed interface symmetry across every dependency edge in §G5.1; shared-ownership
   seams (the P5/P6 texture-unit-map split; P7's App E hook rows deferred to P10/P13 and
   actually picked up there; P3's engine-flag ownership map fully claimed by its owners;
   **REV1:** the P3 macro-header injection point vs P6's centerDepthSmooth decision;
   P6's notifier→producer table vs P7's hook catalog; P4's version-counter invalidation
   vs P12's reload paths); orphaned §11 hand-offs that no later phase adopted. Deliverable:
   `PHASE_INTEGRATION_REVIEW.md`, same findings format and verdict rules as §G1.2, with
   fix-ups per §G1.3. **Implementation starts only after this review's findings are
   resolved.**

---

## G6. Conformance and testing summary

Full strategy: RESEARCH.md §8. What every phase must know:

- **Definition of done is the pack matrix** (D-3, App G): 3 classic packs (SEUS Renewed,
  Chocapic13 V9, projectLUMA) + 4 dual-spec packs (BSL, Complementary ×2, Sildur's).
  Tiers: **T0** loads · **T1** renders plausibly (self-baseline regression) · **T2**
  pixel-parity vs OptiFine G6 (classic packs only) · **T3** feature-complete per pack.
- **REV1:** scene families must include **camera-path motion**. Temporal effects (TAA,
  bloom, anything consuming `previous*` matrices, depth copies, motion vectors) are the
  observed conformance long tail — Pintonium's "blurring" bug class (PD §19.1). A harness
  that only captures static frames will miss exactly the bugs hardest to diagnose later.
- **REV1:** note what the reference *cannot* validate: Pintonium's working packs are
  no-shadow or shadow-tolerant, and it leaves sky/weather/clouds on pure fixed-function
  (PD §4). Scenes exercising shadows, sky, and weather have no working-reference coverage
  — they are covered by the App E catalog and the conformance matrix alone.
- **Testability is sliced three ways:**
  1. *Per-phase headless tests* — every phase doc's §8 specifies JUnit tests of its
     subsystem against the `engine.gl` facade / recorded `GLCapabilityProfile`s. This is
     where the D-6 seam pays rent (§8.3): pack sources run through the front-end and
     resource-sizing decisions validate with no GL context.
  2. *The Phase 2 harness* — scenes, capture, diffing, fixtures, CI. Designed in Wave 1,
     before any renderer exists; its "runnable-before-renderer subset" (fixture downloader,
     preprocessor golden runs, capability-profile replay) is what D-10's "week one" means
     for the implementation schedule.
  3. *Tier gates as implementation exit criteria* — behavioral phases carry §9-derived impl
     gates expressed as Phase-2-defined harness runs.
- **Fixture licensing is settled** (resolved OQ-11): no matrix pack may ever be committed or
  re-hosted. CI downloads at test time (Modrinth API version IDs where available) with a
  local cache; SEUS/Chocapic/projectLUMA are canonical-download-only.
- **Derived artifacts are governed too (REV2).** Goldens, baseline screenshots, diff
  reports and run manifests are where re-hostable content leaks. Binding on every phase,
  adopting Phase 2's policy: golden files carry **no pack source text** (`[D-P2-5]`), and
  **no rendered images enter the repository** (`[D-P2-6]`) — committed oracles are
  *manifests* (hashes + provenance), the images stay in local/CI caches; goldens are
  never auto-updated (regeneration is an explicit flag, `-PupdateGoldens`, that still
  fails the run it regenerates in). Resolves PHASE_2_DOC §11.5 item 4 ("those rules bind
  only this phase; a clause in §G6 would bind all of them").
- T1 baselines are hand-approved once v0.1 first renders, then become the regression oracle.
  T2's oracle is OptiFine G6 screenshots captured manually outside CI (Phase 2 defines the
  protocol).

## G7. Licensing and legal rules for sessions

Binding on every session (from D-7/D-8, RESEARCH.md §10):

1. **Schmaloogium is GPL-3.0-or-later.** **REV2:** the LICENSE-file swap is already
   executed in the repo (full GPLv3 text; commit `aa917a6` "Update LICENSE from MIT to
   GPL-V3") — Phase 1's remaining license work is the or-later statement, the
   source-header convention, and `mcmod.info` metadata.
2. **The schlorbium repo is behavioral-observation-only.** Sessions reading
   `schlorbium-project/SHADER_ENGINE_IMPL.md` / `DESIGN.md` may restate *behavior* in their
   phase docs, never code structure, class/method names, or identifiers from the decompile.
   The decompiled sources themselves (`net/schlorbium/…`) are last-resort reading when the
   digests are silent — and never a code source.
3. **The contract sources are legally clean**: `schlorbium-project/doc/shaders.txt` and
   `doc/shaders.properties` are the shipped pack-author docs; upstream `sp614x/optifine`
   OptiFineDoc and Iris/shaderLABS docs are published specs. Cite freely.
4. **Iris and Angelica may be read, and their LGPL-3.0 code incorporated** with compliance
   (notices preserved, modifications marked; LGPL-3.0 combines into GPL-3.0-or-later).
   **Exception: Iris's bundled glsl-transformer is AGPL-3.0 — never copy from it.**
5. Dependency digest: Cleanroom platform LGPL-2.1; ModularUI LGPL-3.0 (mod-dependency or
   jar-in-jar both fine under GPL-3.0-or-later; OQ-12 note lives with Phase 1);
   Mc122RenderBook MIT (test-harness patterns freely usable); Kirino-Engine custom license —
   observe API surface only.
6. Matrix shader packs: per-pack licenses, all prohibit bundling (App G) — §G6 fixture
   policy applies.
7. **Pintonium (REV1):** the tree is LGPL-3.0 and may be read *and* incorporated with the
   same compliance as item 4 — with three carve-outs (§G11.2): the
   `org.taumc:glsl-transformation-lib` dependency is treated as **AGPL — never copy, never
   adopt as a dependency**; the vendored `kroppeb/stareval` expression engine is
   **license-unverified (upstream repo 404s) — verify before reuse, else clean-room from
   App F.6**; `org.anarres:jcpp` is **Apache-2.0 (verified)** and a clean dependency
   candidate. Pintonium's own fat jar mixes LGPL/AGPL — that is their compliance problem,
   not a precedent (PD §12).
8. **REV3:** **Oculus:** the main tree is **LGPL-3.0-only**, with zero SPDX headers found;
   preserve notices and mark modifications. Vendored JOML is MIT but excluded from the
   evidence survey; vendored digraph is Apache-2.0 and keeps its separate notice. The
   stripped transformation binary and hard-blocklisted transformer boundary are never
   copied or adopted. Primary-source licensing for
   `org.taumc:glsl-transformation-lib` remains unresolved, so the existing prohibition
   and AGPL-risk label stand. Vendored `kroppeb/stareval` also remains unverified:
   **clean-room from RESEARCH App F.6 unless independently verified**. Oculus's commented
   JCPP stanza is not active dependency evidence, and Gate-dropped PB-08 is not restored
   to prove otherwise (OD §3.1–§3.4, §15).

## G8. Post-v0.5 provisional roadmap (sketch — NOT phases, no sessions)

*This section is deliberately non-binding. It exists so pre-v0.5 phases can shape
architecture for growth (D-4, §7.3–§7.4) without designing the future in detail. A DESIGN.md
revision after v0.5 conformance data exists will turn this into real phases. Source:
RESEARCH.md §3.6, §9 "post-v0.5" row, §11.*

Provisional slices, in likely order:

- **S1 — Modern pass arrays & buffer growth:** wire shadowcomp/prepare/begin/setup into the
  (already superset-shaped) stage registry; `/* RENDERTARGETS */`; colortex to 16 (then 32);
  shadowcolor2–7. Mostly "turning on" what P4/P5 sized for. **REV1:** Pintonium's
  shadowcomp machinery exists but its shadow-target flip is stubbed (PD B4) — S1 must
  implement shadow ping-pong for real, not port the stub.
- **S2 — Compute & storage:** `.csh` + `_a…_z` dispatch slots, work-group directives,
  colorimg/shadowcolorimg, custom images, SSBOs, indirect dispatch. GL 4.2/4.3-floor-gated
  per pack; excludes macOS/old GPUs (§3.6.8). **REV1: feasibility evidence upgraded to
  strong** — Pintonium runs compute/SSBO/image load-store/indirect dispatch, pack-exercised,
  on the 1.12.2 compat context (PD §15). This is the strongest available evidence that S2
  works on Cleanroom.
- **S3 — Identity & capability negotiation:** finalize OQ-7 (leaning option 3: OF-era
  identity + honest Iris-style feature flags + own `SCHMALOOGIUM` macro + per-pack
  overrides — the P3 identity-set architecture makes this a configuration decision, not a
  rewrite); era-bridge experiments for `#version 150+` packs on the compat context (OQ-18).
  **REV1 observations:** Pintonium defines *no* `IRIS_VERSION` and pays nothing for it with
  Iris-era packs (PD §7.6) — supports the honest-flag posture. Its dual-spec success with
  Complementary Unbound/Solas rests substantially on the AGPL AST transformer (PD §12);
  our compliant escalation path is the **string-level subset** (version-directive handling,
  `texture2D` defines, the `centerDepthSmooth` define) *before* any AST is considered.
- **S4 — Modern geometry data:** `at_midBlock` first (the P10 growth design's canonical
  test), then `renderStage` instrumentation, translucent-split programs /
  `separateEntityDraws` feasibility review against §3.6.8's risk table. **REV1:**
  `at_midBlock` now has a free, working reference — Pintonium computes it (plus block
  light) in its chunk formats (PD §9).
- **S5 — Kirino backend:** if/when Kirino-Engine ships (OQ-20 — the highest-weight
  strategic risk), port `mod.glue`/`mod.mixin` to Kirino passes behind the D-6 seam. Track
  via §7.7 upstream engagement (participate in CleanroomMC Discussion #405 with our hook
  requirements as the consumer use-case). **REV2:** `cleanroom-src/projects/kirino` is an
  *empty, uninitialized git submodule* (a URL pointer to `CleanroomMC/Kirino-Engine`,
  zero files) — there is no local sketch material; track upstream directly.
- **Dual-spec matrix packs** (current BSL/Complementary/Sildur's releases) progress to T1+
  across S1–S4 — materially aided by Phase 9's REV1 dual-spec mechanisms (PD §8.1).

## G9. PHASE_X_DOC.md — mandatory template

Every phase doc uses exactly this skeleton (sections may be short, never absent):

```
# Schmaloogium — Phase <N>: <title> — Architecture

0. Header — phase name; date; inputs ACTUALLY read (paths + RESEARCH.md sections + PD
   sections); dependency PHASE docs consumed; deviations from the assigned reading list,
   with reasons.
1. Scope & boundaries — what this phase owns; an explicit "owned by Phase Y" line for every
   adjacent concern touched (the anti-sprawl device).
2. Architecture overview — responsibilities, placement in the G3 module layout, key types
   and their relationships (prose + signatures where load-bearing).
3. Contract conformance map — a table: every in-scope contract item (RESEARCH.md §3/App
   row) → the design element satisfying it → provenance tag. ZERO unmapped rows; any
   deviation is a flagged decision, never silent. REV1: where the design adopts or
   rejects a Pintonium mechanism, the row carries the PD citation and (for
   contract-visible items) the §G11.4 decision reference.
4. Detailed design — per component: data model, algorithms, state machines, lifecycle,
   exact semantics. The bulk of the document.
5. Cross-phase interfaces — what this phase EXPOSES to dependents (named interfaces / data
   contracts) and CONSUMES from dependencies; requested changes to a dependency's doc are
   flagged here, never silently assumed.
6. Failure modes & degradation — the G2.4 ladder applied to this subsystem, case by case.
7. Threading & performance notes — thread ownership per component; allocation posture;
   known hot paths.
8. Testability plan — headless unit tests (facade / GLCapabilityProfile based), fixtures,
   and which conformance-tier runs exercise this subsystem.
9. Milestone staging — component → milestone tag table; what is architected-now /
   implemented-later.
10. OQ & spike specifications — per assigned OQ: question, spike procedure, success/failure
    criteria, fallback design (the G4.4 protocol).
11. Decisions & open items — phase-local decision log (D-P<N>-k + rationale); input
    contradictions found; items handed to later phases or to G8; requested upstream changes.
12. Implementation checklist — ordered, independently actionable work items for future
    coding sessions, each with milestone tag and test hook.
```

## G10. Open-question assignments

Every open OQ (RESEARCH.md §11) has exactly one owner. Resolved rows listed for
completeness — do not re-litigate them. REV1 annotates rows where PD changes the
evidence picture; **no OQ is resolved by PD alone** — spikes still run as specified.

| OQ | Status | Owner | Handling |
|---|---|---|---|
| OQ-1 | resolved | — | Sanctioned render API is in-progress (Kirino); see OQ-20 |
| OQ-2 | resolved, standing | **P1** | Re-verify current loader release; pin deliberately |
| OQ-3 | open | **P7** | Spike spec: GL context creation / HiDPI / resize under Cleanroom. **REV1:** Pintonium never touches context creation (PD §16) — evidence supports the fallback (hook Framebuffer/resize sites only) as the default plan |
| OQ-4 | open | **P7** | Spike spec: CleanMix divergences on hot render-path injections. **REV1:** Pintonium proves head/INVOKE/TAIL injections on `EntityRenderer`/`RenderGlobal` hot paths apply and run on a MixinBooter-family 1.12.2 loader (PD §4); the spike is now about CleanMix *specifically* |
| OQ-5 | open | **P10** | Coexistence policy design (detect-and-bail first). **REV1:** detection anchors supplied (PD §9) — mod ids `embeddium`/`celeritas_shaders`, packages `org.embeddedt.embeddium`/`org.taumc.celeritas`, `CeleritasWorldRenderer` presence, `@Overwrite` of `RenderGlobal.renderBlockLayer`/`setupTerrain`; Vintagium/Nothirium lineage |
| OQ-6 | resolved | — | Modern pass semantics documented (§3.6) |
| OQ-7 | open | **P3** (architecture) + **G8/S3** (final decision) | Identity set configurable from day one. **REV1:** PD §7.6's `StandardMacros` set is the implemented reference; Pintonium's no-`IRIS_VERSION` posture supports option 3 |
| OQ-8 | mitigated | — | Conformance tiers (§8.2) |
| OQ-9 | open | **P12** | Spike spec: ModularUI fitness for generated screens. **REV1:** the vanilla-GuiScreen fallback is already built and working in Pintonium (PD §14) — fallback risk ≈ zero; the spike now purely judges ModularUI upside |
| OQ-10 | open | **P2** | Spike spec: headless GL in CI |
| OQ-11 | resolved | — | Download-at-test-time fixture policy (§G6) |
| OQ-12 | open | **P1** | Short considered licensing note in PHASE_1_DOC |
| OQ-13 | resolved | — | Modern attributes documented (§3.6.5 / App C.3) |
| OQ-14 | open | **P10** | Spike spec: baked-quad/LightUtil caches on format switch. **REV1:** no evidence from Pintonium — it sidesteps the question by owning the mesh path (PD §9); spike unchanged |
| OQ-15 | open | **P14** | Spike spec: shared-context async compile; sync fallback mandatory. **REV1:** unaffected itself; the *adjacent* PBO async center-depth item becomes conditional on Phase 6's centerDepthSmooth decision (PD §6.3) |
| OQ-16 | resolved | — | Backport graveyard mapped (§2.2) |
| OQ-17 | open | none (messaging only) | Not load-bearing for design; revisit at release marketing |
| OQ-18 | open | **G8/S3** | Era-bridge experiments need a working v0.1. **REV1:** compliant escalation path is the string-level subset first (PD §12 ruling 3) |
| OQ-19 | resolved | — | Iris is LGPL-3.0 (+AGPL caveat) |
| OQ-20 | open | **G8/S5** + **P1** (seam hardness requirement) | Highest-weight strategic risk. **REV1:** Pintonium's ~3-service seam driving multiple MC backends (PD §2) is further evidence seam-hardness is achievable. **REV2:** backend count corrected — the checkout has 3 platform modules (`forge1710`/`forge122`/12-version-stonecutter `modern`); PD §2's diagram also lists a `:babric` absent from the tree (§G3.1 note) |
| OQ-21 | open | **P1** | lwjglx flux: compile LWJGL3-native; track LWJGLXX/LWJGLY. **REV1:** Pintonium runs 1.12.2 on LWJGL3 via lwjgl3ify or Cleanroom with an LWJGL2-reference-relocating coremod (PD §16) — direct posture evidence |
| OQ-22 | open | **P14** | Spot-check ledger for the §6.2/§6.3 modernization claims. **REV1:** PD §15 supplies ledger evidence (DSA tiers, sampler objects, compute/SSBO on 1.12.2 compat) |

---

## G11. The Pintonium reference — rules of engagement (REV1)

### G11.1 What it is and why it is primary

`Pintonium/` is a fork of Celeritas (Sodium/Embeddium lineage) merged with Oculus 1.7
(the Forge Iris port), retargeted at 1.12.2 — a *performance mod with shaders*, not a
standalone shader engine: its shader support rides a replacement chunk renderer. It is
heavily WIP and largely AI-assisted, with confirmed bugs (PD §17) and dead subsystems.

It is nevertheless the **only legally clean, working, public codebase** that (a) runs
demanding Iris-format packs (Complementary Unbound, Solas, Lumina) on a 1.12.2 LWJGL3
compat context, (b) hooks the exact 1.12.2 classes App E catalogs (`EntityRenderer`,
`RenderGlobal`, `Framebuffer`), and (c) solves 1.12.2-specific problems this design
previously specified only on paper — FF-matrix capture, the depth-texture swap,
block-id mapping, option persistence (PD §1). Where the OF decompile is
behavioral-observation-only (G7 rule 2), Pintonium is readable **and** reusable.

**Trust follows PD §20's tiers, never enthusiasm.** Its weaknesses are exactly our
contract-fidelity areas (classic-pack features, shadow pass, comment directives); its
strengths are exactly our unproven areas (1.12.2 hook points, FF interop, buffer/uniform
machinery, front-end).

### G11.2 Licensing (extends §G7 item 7)

1. The tree (minus one dependency) is **LGPL-3.0** — read freely; incorporate with
   compliance (notices preserved, modifications marked; combines into GPL-3.0-or-later).
2. `org.taumc:glsl-transformation-lib` (fork of douira's glsl-transformer): **treat as
   AGPL-3.0 — never copy, never adopt as a dependency** (D-8). Its AST-transform approach
   is also *unnecessary* for our contract era: GLSL-120 fixed-function-coupled packs run
   natively on a compat context — Pintonium's own 1.12.2 entity bridges compile raw
   unpatched pack GLSL, proving it (PD §12).
3. Vendored `kroppeb/stareval` (expression engine): historically MIT per Iris credits, but
   the upstream repo no longer resolves — **verify the license before any reuse**; if
   unverifiable, clean-room implement from App F.6 (which we own regardless). PD §14.
4. `org.anarres:jcpp`: **Apache-2.0 (verified via Maven POM)** — clean dependency
   candidate, adopted by Phase 3 unless the build session documents a deviation. PD §7.2.

### G11.3 Repository traps (read before searching `Pintonium/`)

1. **`celeritas-shader-refactor` is a stale full copy of the repo — as a ZIP.** **REV2
   correction:** it is `celeritas-shader-refactor.zip`, *tracked in git HEAD* but deleted
   from the working tree (`git status` shows ` D`). On-disk searches are therefore clean;
   the residual trap is git-side — do not restore it, `git show` it, or treat anything
   recovered from it as current.
2. Root-level `mixins.vintagefix.json` / `mixins.vintagefix.late.json` are stray files
   referenced by no source set. **REV2:** they are leftover configs from a different mod
   entirely (VintageFix — `org.embeddedt.vintagefix`) and declare zero mixins
   (`"mixins": []`).
3. `Pintonium/DESIGN.md` is a copy of *our* design document — **REV2:** specifically an
   *untracked, stale copy of v1.1* (header "v1.1, 2026-07-24"), not of this revision, and
   not Pintonium documentation. The governing copies live in `docs/design/`.
4. Git history is 9 bulk commits — no useful archaeology; the working tree is the only
   record.

### G11.4 Evidence and adoption rules

- PD citations use `PD §n`; adopted claims carry `[V:observed — Pintonium <path>]`.
- Conflicts: RESEARCH.md wins; report, never silently resolve (§G0.1/§G0.1a).
- **Contract-visible adoptions require a recorded decision** (`D-P<N>-<k>`) showing the
  contract check against the cited App/§ text. Standing candidates: GPU `centerDepthSmooth`
  (PD §6.3 → Phase 6), frame-end flip copy-back (PD §5.1 → Phase 5), constant-attribute id
  delivery (PD §8.2 → Phase 10), jcpp-based preprocessing (PD §7.2 → Phase 3; non-
  contract-visible, but still record the dependency decision).
- **Pre-decided rejections** (PD §18 — do not revisit without new contract evidence):
  attribute locations 11–14 (ours: 10/11/12); dynamic per-program texture-unit allocation
  (ours: fixed App B.3 map incl. depthtex1 at unit 11); Iris dimension-folder semantics
  (ours: OF world −128..128 scan, `.vsh`/`.fsh` only); `Random(0)` noise (ours: contract
  generator); 16-colortex unconditional allocation (ours: scan-driven sizing); missing
  `version.<mcver>` gate; missing `(internal)` pack.
- **Do-not-inherit lists are standing:** PD §17's bug catalogue (B1–B13) and PD §18's
  divergence table. Phase docs consuming Pintonium material show the relevant rows handled
  (doc §3 conformance map).

### G11.5 Trust tiers (condensed from PD §20)

- **Trust and reuse freely:** pack front-end structure (include graph, options, profiles,
  properties, IdMap), ping-pong/flip/clear machinery, depth-copy strategy tiering, uniform
  cadence machinery, the `Program.use()` barrier, the backup-chain model, DSA tiering,
  sampler objects, the KHR_debug pattern, the CenterDepthSampler GPU-smoothing design,
  **REV3:** stareval's evaluator architecture as a shape only (the unverified code has a
  clean-room outcome under §G7 item 8), the mixin
  injection-point map, the FF-matrix capture mechanism, the Framebuffer depth-texture swap.
- **Reuse structure, re-derive values:** the `StandardMacros` identity set, the
  `VintageBlockMaterialMapping` alias table, per-quad tangent/midTexCoord math,
  `ShadowMatrices`/snapping math, the legacy-compat-shader *technique*.
- **Do not copy (contract conflict or license):** attribute location numbering, dynamic
  unit allocation as a contract replacement, dimension-folder semantics, `Random(0)`
  noise, anything from `glsl-transformation-lib`, pack-layout lighting heuristics, the
  stubbed/dead features in PD §17.
- **REV3:** **Answered by gated Oculus evidence:** sliders UI.
- **REV3:** **Partial plumbing or portable policy only:** held-item uniforms and
  `blockEntityId` have partial plumbing; companion atlas stitching has partial portable
  policy. None supplies a 1.12.2 acquisition, setter-scope, sprite, or lifecycle hook
  (OD §11).
- **No help available (design from RESEARCH.md alone):** **REV3:** shadow on 1.12.2,
  sky/weather/cloud hooks, `entityColor`, `version.<mcver>`, the `(internal)` pack, and
  render-quality multipliers remain unanswered (OD §11).

### G11.6 Reading map (phase → PD sections)

| Phase | Read in PD | What it gives |
|---|---|---|
| P1 | §2, §16 | Seam interface inventory; 3-stage bootstrap; mixin-plugin option; platform proofs |
| P2 | §4, §19 | Scene-family gaps; motion-scene requirement (blurring bug class) |
| P3 | §7, §12, §17 (B1–B3, B12) | 80% of the subsystem, LGPL, pack-tested; jcpp; the pitfalls to fix |
| P4 | §3, §13 | Backup-chain model; barrier shape; per-pass state bundle; attribute-location warning |
| P5 | §5, §17 (B4), §18 (flip row) | Ping-pong/flip/clear/depth-copy/resize near-complete; depthtex0 swap mixin |
| P6 | §6, §17 (B1, B6) | Cadence model; capture mechanism; smoothing math; centerDepthSmooth candidate; notifier lesson |
| P7 | §4, §6.1, §16 | Verified injection timeline; frame-begin ordering; bootstrap hooks |
| P8 | §10 | Shadow camera/snap math only; traversal absent — still least de-risked |
| P9 | §8 | Dual-spec alias table; 11300 re-parse trick; held-item gap warning |
| P10 | §8.2, §9 | Constant-attribute trick; tangent math; thread-safety pattern; OQ-5 anchors |
| P11 | §14 | stareval evaluator architecture (license-gated); function-set checklist |
| P12 | §14 | OQ-9 fallback already built; persistence patterns |
| P13 | §11, §7.6 | Custom-texture model; noise divergence; companion-atlas gap; macro wiring |
| P14 | §15, §6.3 | Sampler objects; DSA tiers; KHR_debug; compute/SSBO evidence; PBO conditionality |

## G12. The Oculus reference — rules of engagement (**REV3:** added)

### G12.1 What it is and why it is useful (**REV3:** added)

**REV3:** `Oculus/` declares a Minecraft 1.12.2 target but overlays predominantly
1.16.5-facing source. The provenance gate found a 74-file modern-marker union, 12 files
with MCP markers, 132 `@Mixin` annotations with zero targets named `RenderGlobal`,
`EntityRenderer`, or `Framebuffer`, and modern `mods.toml` loader metadata. It is
therefore **not a usable 1.12.2 hook-point reference**. Pintonium remains the working
1.12.2 hook source; build metadata alone proves no runtime compatibility (OD §1).

**REV3:** Oculus is useful as a loader-independent Iris/Oculus logic mine: front-end
dataflow, preprocessing pitfalls, pass/buffer policy, shadow math, uniform models, GUI
behavior, and companion-texture policy. Its five-stage workflow reduced 70 raw
candidates to 37 Citation-Gate-kept findings; all 37 are classified exactly
`loader-independent`, and zero are `1.12.2-hook` (OD §2, §16, §18). OD is evidence,
never contract; RESEARCH remains supreme.

### G12.2 Licensing (extends §G7 item 8) (**REV3:** added)

1. **REV3:** The main Oculus tree is LGPL-3.0-only. Preserve notices, mark
   modifications, and keep separately licensed material separately accounted (OD §3).
2. **REV3:** The hard blocklist is exactly
   `src/main/java/net/coderbot/iris/pipeline/transform/`, `libs/`,
   `glsl-relocated/`. Never read through, cite, copy, summarize, or depend on those
   boundaries; the stripped transformation binary carries unresolved provenance and
   AGPL risk (OD §2.4, §3.2).
3. **REV3:** Vendored JOML is MIT and survey-excluded. Vendored digraph is Apache-2.0
   and retains its separate notice. Neither license turns excluded or ungated material
   into subsystem evidence (OD §3.1–§3.2).
4. **REV3:** Primary-source licensing for
   `org.taumc:glsl-transformation-lib` remains unresolved, so the standing prohibition
   remains: do not copy and do not adopt it as a dependency. Vendored stareval has no
   verified license/header outcome; clean-room from RESEARCH App F.6 unless independently
   verified. Oculus does not prove active JCPP wiring: the stanza is block-commented and
   PB-08 failed Gate (OD §3.2, §3.4, §15).

### G12.3 Repository traps (read before searching `Oculus/`) (**REV3:** added)

1. **REV3:** The directory and Gradle properties say 1.12.2; the active source and
   loader metadata do not. Never infer a hook, mapping, build platform, or runnable port
   from the declared target (OD §1, §12 item 1).
2. **REV3:** The README marks the project closed. Treat hybrid-source uncertainties as
   frozen evidence boundaries, not missing details an agent may guess (OD §1, §12 item 2).
3. **REV3:** Modern Mixins/accessors can expose portable policy but cannot establish
   1.12.2 injection sites. Hook design stays with RESEARCH App E plus
   Pintonium/vanilla evidence (OD §1, §12 item 15).
4. **REV3:** The JCPP declaration is inside a block comment. It is historical intent,
   not active dependency proof; dropped PB-08 cannot be repaired or reinstated
   (OD §3.4, §12 item 3, §15).
5. **REV3:** Exclude `net/coderbot/iris/vendored/joml/` from every search denominator
   and finding survey. Its 69 files dominate the physical tree and are not Oculus
   subsystem evidence (OD §2.4, §3.1).
6. **REV3:** A modern import, Mixin target, accessor, method name, or abstract rendering
   phase never becomes a `1.12.2-hook` finding by analogy. OD's classification is a hard
   boundary, not a confidence downgrade (OD §1–§2, §14).

### G12.4 Evidence and adoption rules (**REV3:** added)

- **REV3:** Cite the mining report as `OD §n`. A phase doc using a retained finding
  records `[V:observed — Oculus <repo-relative path>; loader-independent]` and stays
  inside OD's 37-item §16 audit. Oculus claims never outrank RESEARCH or supply
  1.12.2 hooks.
- **REV3:** The six Gate-dropped findings are exactly **PB-07, PB-08, SH-05, UN-08,
  TX-03, and TX-09**. They are not cited, repaired, paraphrased into evidence, or
  adopted. Ancillary anchors omitted by the Gate do not silently return either
  (OD §2.2, §15–§16).
- **REV3:** Contract-visible adoption still requires a recorded phase decision
  (`D-P<N>-<k>`) and a check against the governing RESEARCH §/Appendix, just as §G11.4
  requires. RC3 records evidence and dispositions only; it adopts no mechanism and
  changes no phase governance.
- **REV3:** Keep pack-facing vocabulary verbatim: **colortex, flip, gbuffers, backup
  chain, `mc_Entity`** (§G4.1). Oculus/Iris terminology does not rename the contract.

**REV3:** **All five discovered RESEARCH conflicts and their dispositions** (OD §13.2):

| Conflict | Gated record | Disposition |
|---|---|---|
| C-FE02 — whole include graph vs App F.3 same-file option confirmation | Retained exact quote at `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/include/IncludeGraph.java:240`: `        return Collections.singletonList(this);` | **REV3:** RESEARCH App F.3 wins; implement same-file confirmation and test ambiguous names |
| C-BF06 — final-pass copy-back vs App F.7 flip carryover | Retained exact quote at `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/postprocess/FinalPassRenderer.java:93`: `        // TODO: We don't actually fully swap the content, we merely copy it from alt to main` | **REV3:** RESEARCH App F.7 wins; copy-back may be studied as mechanics, never substituted for last-writer flip carryover |
| C-UN04 — smoothing deciseconds vs RESEARCH ticks | Retained exact quote at `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/uniforms/transforms/SmoothedFloat.java:53`: `        this.decayConstantUp = computeDecay(halfLifeUp * 0.1F);` | **REV3:** Surface the unit conflict in Phase 6; RESEARCH App A.3 ticks remain normative unless deliberately revised |
| C-PB07 — apparent RetroFuturaGradle/Java 8/MixinBooter platform vs selected Unimined/Java 25/CleanMix platform | **The PB07 quotation failed Gate** for leading-whitespace mismatch; no Oculus quote is cited or adopted | **REV3:** Record discovery only; draw no build-platform conclusion, and retain the RESEARCH-selected platform |
| C-TX01 — apparent packed normal default `0x7F7FFFFF` vs RESEARCH §4.6 `0xFF7F7FFF` | **The TX01 quotation failed Gate** for leading-whitespace mismatch; no Oculus quote is cited or adopted | **REV3:** RESEARCH §4.6 stays normative; representation/byte order remains unresolved and is never silently swapped |

### G12.5 Trust tiers (exact §G11.5 vocabulary) (**REV3:** added)

- **Trust and reuse freely:** **REV3:** subject to LGPL compliance and a contract check,
  use front-end dataflow structure, deterministic discovery, include-graph architecture,
  pass-transition/invalidation/teardown patterns, notifier/captured-state patterns,
  discrete slider/navigation/apply-discard UI structure, and Minecraft-type-independent
  companion-atlas policy (OD §14).
- **Reuse structure, re-derive values:** **REV3:** shadow matrices and clipping/snapping/
  culling math; LabPBR thresholds and packed defaults; compute barrier selection;
  smoothing units/constants; companion scaling and mipmap numeric policy (OD §14).
- **Do not copy (contract conflict or license):** **REV3:** blocked transformation
  material; alt-to-main copy-back as contract behavior; whole-graph option confirmation;
  whitespace-destructive properties preprocessing; source-era-specific Mixins/accessors;
  unverified stareval or unresolved taumc code; and every failed Citation Gate quote
  (OD §14–§15).
- **No help available (design from RESEARCH.md alone):** **REV3:** all 1.12.2 hooks,
  1.12.2 shadow traversal/invocation, sky/weather/cloud hooks, `entityColor` delivery,
  `version.<mcver>`, the `(internal)` pack, and render-quality multipliers. Sliders are
  answered; held-item and `blockEntityId` plumbing and companion-atlas portable policy
  are partial only (§G11.5; OD §11, §14).

### G12.6 Reading map (phase → OD sections) (**REV3:** added)

**REV3:** This map does not amend any phase's current Required inputs, adoption state, or
governing design. It tells a future adoption/fix-up (or a brief that explicitly assigns
OD) which gated sections correspond to each phase; §G12.1–§G12.5 remain the boundary for
every such read.

| Phase | Read in OD | What it gives |
|---|---|---|
| P1 | §1–§3, §12, §15–§17 | **REV3:** provenance counterexample and licensing checklist; no selected-build-platform evidence |
| P2 | §2, §4, §12, §16–§17 | **REV3:** deterministic ordering, preprocessing edge cases, TAA bind regression, negative fixtures |
| P3 | §3–§4, §12–§17 | **REV3:** front-end structure and pitfalls; no active JCPP proof |
| P4 | §4, §5, §13–§17 | **REV3:** portable pass/slot/barrier policy under the fixed RESEARCH contract |
| P5 | §5, §12–§17 | **REV3:** lifecycle and buffer policy; copy-back conflict rejected |
| P6 | §7, §11, §13–§17 | **REV3:** cadence/plumbing patterns and the smoothing-unit conflict; no 1.12.2 producers |
| P7 | §5, §11, §14, §17 | **REV3:** abstract phase/teardown policy; no 1.12.2 or sky/weather/cloud hooks |
| P8 | §6, §11–§14, §17 | **REV3:** shadow math/policy only; no 1.12.2 traversal or invocation |
| P9 | §7, §11, §17 | **REV3:** partial held-item and `blockEntityId` plumbing; no acquisition/setter hooks |
| P10 | §1, §14, §17 | **REV3:** no gated addition; `mc_Entity` remains governed by RESEARCH/Pintonium |
| P11 | §3, §15, §17 | **REV3:** no gated evaluator addition; stareval is clean-room unless independently verified |
| P12 | §4, §8, §11–§12, §14, §17 | **REV3:** discrete sliders, navigation, apply/discard, conditional reload, and enumeration failure |
| P13 | §3, §9, §11–§17 | **REV3:** companion-atlas portable policy; no 1.12.2 stitching/lifecycle hooks |
| P14 | §5–§6, §12–§14, §17 | **REV3:** compute/barrier and GL-binding pitfalls; re-derive values and use Pintonium for deployed tiers |

---

## Part II — Phase specifications

*Each spec is the complete assignment for one session. Format: Objective · Deliverable ·
Scope (in / out) · Required inputs · Architecture requirements · Doc gate (what makes the
phase doc complete) · Impl gate (the §9-derived criterion recorded for later coding
sessions) · Context budget. **REV1 changes** are summarized at the top of each revised
spec and integrated into the body.*

---

### Phase 1 — Foundation & project architecture

**Milestone:** v0.1 · **Depends on:** — · **OQs:** OQ-2, OQ-12, OQ-20 (seam hardness), OQ-21

**REV1 changes:** adds the Pintonium seam-inventory as a completeness checklist for
`mod.glue`; a proven three-stage bootstrap sequence; a mixin-registration convenience
option; OQ-20/OQ-21 evidence notes.

**Objective.** Turn the pristine CleanroomModTemplate into Schmaloogium's real project
architecture: the module split that enforces the D-6 seam, the GL facade, Mixin wiring,
license, version pins, and the conventions every later phase builds on.

**Deliverable.** `PHASE_1_DOC.md` per §G9.

**Scope — in:**
- Template conversion plan: rename `com.example.modid` → final root package/mod id
  (propose `schmaloogium`); `gradle.properties` fields; `Reference.java` templating;
  `mcmod.info`/`pack.mcmeta`; AT file rename (decide whether ATs are needed at all for
  v0.1 — prefer none until a hook requires one). **REV2 ground truth:** `Reference.java`
  sits in the `src/main/java-templates/` source set and `mcmod.info`/`pack.mcmeta` in
  `src/main/resource-templates/` (Blossom expands them) — not in plain `java`/`resources`.
- **License swap to GPL-3.0-or-later** (D-7). **REV2:** the LICENSE file itself is
  already swapped (GPLv3, commit `aa917a6`) — remaining scope is the or-later statement,
  the source-header convention, and `mcmod.info` metadata.
- **Version pins** (OQ-2): re-verify the current Cleanroom loader release (template pins
  0.5.17-alpha — **REV2:** in `build.gradle`'s unimined block, l. 60, *not*
  `gradle.properties`; 0.6.6-alpha was current 2026-07-24 — daily cadence, so check
  again), pin it and the Unimined kappa/Gradle/Java-25 toolchain deliberately; document
  the re-pin procedure.
- **Gradle module split** per §G3.1 (`:engine`, `:mod`, `:conformance`): concrete
  `settings.gradle`/`build.gradle` restructuring plan against the template's actual build
  scripts (`gradle/scripts/{dependencies,extra,publishing}.gradle`), including how the
  Unimined/Blossom machinery stays confined to `:mod`, jar packaging (does `:engine` shade
  into the mod jar via the template's `contain` configuration?), and the seam-enforcement
  mechanism (e.g. no-MC-on-compile-classpath by construction, plus an architecture test).
- **`engine.gl` facade design**: interface granularity (thin GL-verb layer vs. grouped
  services), the `GLCapabilityProfile` value object (GL version, max draw buffers, max
  color attachments, max texture units — the §4.1 probe set — plus the extension set,
  whose contract surface is §3.5's on-demand `MC_<GL_extension>` macros; **REV2**
  citation split resolving PHASE_1_DOC §11.5 item 3: §4.1 lists four probes and does not
  include the extension set), and the recording/replay implementation for headless tests.
- **`mod.glue` provider-interface completeness check** (REV1): PD §2's service inventory —
  `MinecraftVersionShimService`'s method list (camera/world/dimension/framebuffer/biome/
  weather accessors) plus the glsm state services — is a field-tested checklist of what a
  version-facing glue seam must cover. Use it to completeness-check the facade/provider
  design; note deliberately-excluded items (we have one backend, not several — **REV2**
  count per §G3.1's note).
- **Mixin wiring**: `MixinConfigs` jar-manifest declaration (the current canon — legacy
  MixinBooter interfaces are deprecated, §5.1), mixin-config JSON layout (one per phase
  needed?), SRG-name targeting policy, refmap handling under Unimined, dev ergonomics
  (`crl.dev.mixin`, `-Dmixin.debug.export=true`). **REV1:** evaluate Pintonium's
  class-scan mixin plugin (class-scans a package — no per-mixin JSON upkeep, PD §16)
  against the manifest canon; adopt or reject with reasons.
- **Bootstrap sequencing** (REV1): Pintonium's proven 1.12.2 three-stage bring-up —
  `GameSettings.loadOptions` (early init) → `OpenGlHelper.initializeTextures` RETURN (GL
  caps ready → engine init + pack load) → `GuiMainMenu.initGui` RETURN (loading complete)
  (PD §16). Adopt as the engine bring-up skeleton or deviate with reasons; Phase 7's hook
  catalog consumes the frame-adjacent parts.
- **lwjglx posture** (OQ-21): our code compiles against LWJGL3 proper only; document what
  `enable_lwjglx=true` in the template means for runtime and whether we keep it. **REV1
  evidence:** Pintonium runs 1.12.2 on LWJGL3 via lwjgl3ify or Cleanroom, with a coremod
  relocating LWJGL2 references in mod classes (PD §16) — the "LWJGL3-native + runtime
  shim for others" posture is proven on this exact platform.
- Headless JUnit baseline in `:engine`/`:conformance` (JUnit is template-wired via the
  `enable_junit_testing=true` build flag; **REV2:** no `src/test/` exists in the pristine
  template — the baseline creates it).
- Logging channels (§G4.5 list), debug-flag names (reserve
  `-Dschmaloogium.debug.saveSources`), user-facing error channel conventions.
- `mod.compat` **bail registry stub**: the mechanism for "detected incompatible
  chunk-renderer replacement → disable shaders with a clear message" (policy content comes
  from Phase 10 / OQ-5; the mechanism slot exists from day one). **REV1:** the registry's
  detection-anchor table has its first concrete entries already (PD §9, listed in Phase
  10's spec) — design the stub so anchors are data, not code.
- CI workflow adjustments (the template's three GitHub workflows) for the module split and
  `:conformance` (test stages land with Phase 2's design; leave extension points).
- OQ-12 note: a short considered paragraph on GPL-3.0-or-later mod + LGPL-2.1 platform +
  LGPL-3.0 ModularUI (jar-in-jar), citing ecosystem precedent. **REV1:** add one sentence
  on the LGPL-3.0 Pintonium reference relationship (reading + possible incorporation with
  compliance; the AGPL-carve-out discipline of §G11.2).

**Scope — out:** harness content (Phase 2); anything pack-format (Phase 3); all GL policy
beyond the facade shape (Phases 5+); GUI framework evaluation (Phase 12).

**Required inputs:**
- RESEARCH.md §1, §5.1–§5.3, §6.1, §7.2, §12.2.
- **PD §2 (architecture/seam comparison), §16 (platform notes).** (§G11 rules apply.)
- Template ground truth: `Schmaloogium/{build.gradle,gradle.properties,settings.gradle}`,
  `Schmaloogium/gradle/scripts/*.gradle`, `Schmaloogium/src/**`, `Schmaloogium/.github/workflows/*`,
  `README.md`.
- Skim only: `cleanroom-src/src/main/java/com/cleanroommc/` (boot/mixin bootstrap layout).
- MCP recipes: `get_porting_guide("mixin-setup")`, `get_project_template(...)` (§12.4).

**Architecture requirements:** the seam is non-negotiable (§G3.1) and must be stated as a
testable constraint; everything else in G3 is yours to refine (and later phases inherit
your refinement). Honor D-5/D-6/D-7 explicitly. Kirino (§5.2) is the reason seam-hardness
is a requirement, not hygiene — record it. **REV1:** PD §2 is supporting evidence — a
service-based seam drove multiple radically different MC backends from one shader core
(**REV2** count: three platform modules in the checkout — §G3.1 note).

**Doc gate:** module/package layout finalized with dependency rules as testable
constraints; every D-1..D-10 either satisfied by this phase or explicitly deferred with its
owner phase named; pin table complete with re-verification procedure; **REV1:** glue-seam
completeness check against PD §2's inventory present; bootstrap sequence adopted or
deviation justified.

**Impl gate:** project builds empty modules + passes an architecture test proving `:engine`
has no MC/loader/mixin/LWJGL classpath; CI green.

**Context budget:** mandatory reading ≈ 38k tokens (Part I + spec + template files +
RESEARCH.md sections + PD). Ample headroom; do not spend it exploring cleanroom-src or
`Pintonium/` beyond the listed inputs.

---

### Phase 2 — Conformance harness

**Milestone:** v0.1 (design; implementation starts week one) · **Depends on:** 1 · **OQs:** OQ-10

**REV1 changes:** camera-path motion scenes become mandatory; scene-family coverage is
explicitly calibrated against what the Pintonium reference cannot validate.

**Objective.** Design the conformance machinery that defines "done" for every other phase
(D-3, D-10): tiers, scenes, capture, diffing, fixtures, and the headless-core test harness.

**Deliverable.** `PHASE_2_DOC.md` per §G9.

**Scope — in:**
- **Tier machinery** (T0–T3, §8.2): what each tier measures, per-pack tier state tracking,
  reporting format.
- **Fixed-scene specification**: a scene = seed + coordinates + time + weather + camera
  path (§8.3); the file format for scene definitions; the initial scene set (terrain,
  water/translucency, night/shadows, weather, hand/held-item — one scene per behavioral
  family the matrix packs exercise). **REV1:** **camera-path motion is mandatory** — at
  least one scene per motion-sensitive family must move the camera along a defined path.
  Temporal effects (TAA, bloom, everything consuming `previous*` matrices, depth copies,
  motion vectors) are the observed conformance long tail: Pintonium's "blurring" bug class
  (TAA/Bloom/FAA/Chromatic Aberration, PD §19.1) is fed by inconsistent previous-frame
  inputs, and static-frame testing would miss the entire class.
- **Reference-gap calibration** (REV1): Pintonium's working packs are no-shadow or
  shadow-tolerant, and it leaves sky/weather/clouds on fixed function (PD §4) — the scene
  set must cover exactly the families the reference cannot validate (shadows, sky,
  weather), since those are our highest-uncertainty hook surfaces. Pintonium's three
  working-pack configs may inform but never define coverage (D-3).
- **Capture automation**: world load, deterministic setup (fixed seed/time/weather,
  disable randomness sources), camera placement, screenshot capture — designed against
  vanilla/Forge client APIs (designable now; runnable once v0.1 renders).
- **Image diff**: tolerance model (per-pixel + aggregate thresholds; GPU/driver variance
  allowance), baseline storage/versioning (T1 self-baselines are hand-approved once, then
  regression oracles), diff-report artifacts.
- **T2 protocol**: capturing OptiFine G6 reference renders on the same scenes — manual,
  outside CI, documented step-by-step (OF is not redistributable; the oracle images are
  local artifacts).
- **Fixture system** (resolved OQ-11 policy, §G6): download-at-test-time via Modrinth API
  (stable version IDs for BSL/Complementary/Sildur's), canonical-URL manual placement for
  SEUS/Chocapic/projectLUMA, local cache layout, never-rehost rule enforced structurally
  (cache outside the repo, CI cache keys).
- **Headless-core harness**: golden-run tests feeding matrix-pack sources through the
  Phase 3 front-end against recorded `GLCapabilityProfile`s; validating resource-sizing
  decisions with no GL context (§8.3). Define the golden-file format and update workflow.
  **REV1:** Pintonium's front-end is the de-facto competitor — its parse successes and
  failures on Iris-era packs (PD §7) are a calibration target for what "parse all seven
  matrix packs end-to-end" means.
- **RenderBook JUnit OpenGL Extension evaluation plan** + **OQ-10 spike spec**: does
  headless/EGL/virtual-display GL work on CI runners? Success/failure criteria; fallback =
  GL-context tests run locally/pre-release only, CI stays headless-core + parse tiers.
- **§9 exit-criteria mapping**: every milestone exit criterion in RESEARCH.md §9 → the
  concrete harness run(s) that gate it.
- Identify the **"runnable before any renderer exists" subset** — this is what the
  implementation effort builds in literal week one.

**Scope — out:** the front-end being tested (Phase 3); scene *content* tuning per pack
(implementation-time); performance benchmarking (Phase 14).

**Required inputs:**
- RESEARCH.md §8 (whole), §9 (exit-criteria column), App G, §5.1 (RenderBook bullet),
  §12.5 (Mc122RenderBook URL).
- **PD §4 (which scene families the reference can't render), §19 (the blurring bug
  class).** (§G11 rules apply.)
- `PHASE_1_DOC.md` (module layout, facade, `GLCapabilityProfile`).
- `Schmaloogium/.github/workflows/*` (CI ground truth).
- Web (only if needed): Modrinth API docs for version-pinned downloads;
  `github.com/tttsaurus/Mc122RenderBook` for the JUnit extension's actual API.

**Doc gate:** every §9 exit criterion traceable to a specified harness run; fixture
licensing policy encoded structurally; the before-renderer subset explicitly listed;
OQ-10 spike spec complete with fallback; **REV1:** motion scenes present per
motion-sensitive family; shadow/sky/weather families explicitly covered despite having
no working reference.

**Impl gate:** fixture downloader + headless golden-run skeleton + scene-spec parser
implemented and green in CI before Phase 7's implementation lands (D-10).

**Context budget:** ≈ 40k tokens mandatory reading. Web fetches are bounded (two sites).

---

### Phase 3 — Pack front-end: ingestion, preprocessing & configuration model

**Milestone:** v0.1 · **Depends on:** 1 · **OQs:** OQ-7 (architecture only)

**REV1 changes:** jcpp adopted as the preprocessor engine (with two proven techniques);
four Pintonium pitfalls (B1/B2/B3/B12) become explicit conformance rows; the
`StandardMacros` identity set is the OQ-7 reference; the macro header reserves the
injection point Phase 6's centerDepthSmooth `#define` needs.

**Objective.** Everything from disk to a validated in-memory `PackConfiguration`: pack
discovery, source preprocessing, option discovery, directive scanning, and the complete
`shaders.properties` model. One coherent subsystem because the pieces are circular: the
properties file is itself macro-preprocessed, and option discovery happens in shader
sources. Pure `:engine` code — no MC types anywhere in this phase.

**Deliverable.** `PHASE_3_DOC.md` per §G9.

**Scope — in:**
- **Pack discovery** (§3.1, §4.1): `shaderpacks/` enumeration; folder and zip packs
  (nested-root tolerance, path sanitization); "OFF"/"(internal)" sentinels; per-dimension
  `world<id>` folders (world −128..128 scan; when present, shaders load *only* from there;
  empty folder disables shaders for that dimension; only `.vsh`/`.fsh` read from dimension
  folders). **REV1:** Pintonium implements the *Iris* dimension model (`dimension.properties`
  + world0/-1/1, full program sets, no base merge) — a pre-decided rejection (§G11.4);
  its zip-FS lifecycle management (one static FS, closed on destroy) and per-dimension
  `ProgramSet` cache are details worth copying (PD §7.1).
- **Source model & `#include`** (§3.2): ≤10 deep, relative and `/`-absolute-from-shaders
  forms, `#line` bookkeeping for correct error attribution. **REV1:** Pintonium resolves
  `#include` before all other preprocessing (comment-unaware line scan — documented
  upstream behavior, matches OF) with graph-DFS cycle detection and rustc-style
  diagnostics (PD §7.1); it has **no depth cap** — our ≤10 rule is contract-side; enforce
  it anyway (§3.2).
- **Standard macro header** (§3.5): the full identity set (`MC_VERSION` 10904-format,
  `MC_GL_VERSION`, `MC_GLSL_VERSION`, OS/vendor/renderer macros, on-demand
  `MC_<GL_extension>`, option macros) injected after `#version`. **Identity-set
  architecture for OQ-7**: the set is *configurable data*, shaped for §7.5 option 3
  (OF-era identity + capability macros + own `SCHMALOOGIUM` macro + per-pack overrides),
  with the final decision deferred to G8/S3 — design so the decision is a config change,
  not a rewrite. **REV1:** PD §7.6's `StandardMacros` is the implemented, pack-tested
  reference — reuse the `MC_VERSION` 10904-format shim mechanism and the driver-string
  regex parsing for `MC_GL_VERSION`/`MC_GLSL_VERSION`; note OF's use-based extension
  filtering differs from their enumerate-everything approach (their TODO). OQ-7 evidence:
  Pintonium defines **no** `IRIS_VERSION` at all and loses nothing with Iris-era packs
  (PD §7.6) — supports option 3's honest-flag posture. **Reserve the macro-header
  injection point** Phase 6's centerDepthSmooth `#define` redirect needs (PD §6.3),
  whether or not Phase 6 adopts it.
- **Preprocessor** (§3.5): `#define/#undef/#ifdef/#ifndef/#if/#elif/#else/#endif`,
  `defined X` / `defined(X)`, macro substitution; applied to shader sources,
  `shaders.properties`, and ID-mapping files (standard macros only for the latter, §3.7).
  **REV1 — implemented on jcpp (Apache-2.0, verified — PD §7.2)** unless the build
  session documents a deviation; jcpp is pack-tested in production on exactly this
  workload. Two Pintonium techniques are adopted: (a) `#version`/`#extension` lines are
  rewritten to `#warning` markers before jcpp (which tolerates them anywhere),
  recollected by a listener, and **hoisted back to the top** of the output (strict
  drivers/Mesa requirement) with an injection guard against pack-side marker spoofing;
  (b) environment defines are injected via jcpp's macro API (`pp.addMacro`), **not**
  textual `#define` lines, preserving line numbers for error attribution — exactly what
  the §3.2 `#line`-bookkeeping requirement needs. **Write our own properties-file
  preprocessing path** — Pintonium's strips `#` characters from non-directive lines,
  corrupting values containing `#` (PD B12), and carries vestigial dead listener
  machinery.
- **Option discovery** (§3.3, App F.3): switch options (`#define NAME` / commented form;
  recognized only when the same file `#ifdef`s them), variable options
  (`#define NAME value // tooltip [values]`), the const-option whitelist, the
  ambiguity-disables rule, lang-file decoration model (`shaders/lang/*.lang`).
  **REV1:** the `#ifdef`-reference confirmation must be per **weakly connected
  component** of the include graph — Pintonium stubs this to pack-global
  (`computeWeaklyConnectedComponents` returns the whole graph, PD B3), mis-handling packs
  where two files `#define` the same name but only one `#ifdef`s it. Implement the
  component analysis properly; conformance-map row required.
- **Option application** via source-line rewrite at compile time (§4.7). **REV1:**
  Pintonium's in-place rewrite with `// OptionAnnotatedSource: Changed option` markers,
  constraint-count profile precedence, cycle-checked includes, and `!program.x` disabling
  is working App F.3/F.4 machinery — a valid structural reference (PD §7.3).
- **Directive scanning** (complete App A.3 table): implicit resource declarations
  (uniform declarations sizing buffers — `shadowtex1` → second shadow depth buffer,
  `colortex7` → buffer count, `gdepth` → RGBA32F upgrade, `centerDepthSmooth` → readback
  enable), const directives, `DRAWBUFFERS`, vertex-attribute opt-ins, `countInstances`,
  geometry-shader legacy config — **in all three syntactic forms** (`const` declarations,
  `/* KEY:value */`, legacy `// KEY:value`). **REV1:** all three forms are mandatory and
  individually tested — Pintonium's comment-style directive handlers are **dead no-ops**
  (`acceptCommentStringDirective` et al. are TODO stubs, PD B2), survivable only because
  its working packs are modern const-style. Our classic matrix (SEUS Renewed, Chocapic13
  V9, projectLUMA) is exactly the era that uses `/* SHADOWRES */`, `/* SHADOWFOV */`,
  `/* SHADOWHPL */`, `/* GAUX4FORMAT */`, and the `gdepth` uniform upgrade trigger.
  **Every directive→field mapping row gets a conformance test** (PD B1: `drynessHalflife`
  writes into the `wetnessHalfLife` field in Pintonium — exactly the bug class the test
  net exists for).
- **Full `shaders.properties` parse** (App F.1–F.8): engine flags, `version.<mcver>` gate,
  custom-texture *specs* (all three F.5 source forms parsed into a model; loading is
  Phase 13), screens/profiles/sliders model, custom-uniform/variable declarations
  (captured as raw expression strings; the language is Phase 11), per-program render-state
  overrides (alphaTest/blend/scale/flip/enabled — stored; applied by Phase 4).
  **REV1 gaps Pintonium cannot validate** (each is a conformance row we own alone, PD
  §7.4): `sliders=` functionally dead there; **no `version.<mcver>` gate**;
  `texture.<stage>.<sampler>` filter/wrap suffixes stripped and ignored; `dynamicHandLight`
  parsed, never consumed.
- **ID-mapping file grammar** (§3.7): `block/item/entity.properties` parse (long/short/
  property-matched/legacy id:meta forms, `layer.*` keys) into an unresolved model —
  *resolution against registries is Phase 9*. **REV1:** Pintonium parses `layer.*` but
  consumption is TODO there (PD §8) — our P9-resolve/P7-dispatch split stands.
- **Persistence formats**: per-pack `shaderpacks/<pack>.txt` (changed options only),
  global `optionsshaders.txt` equivalent — formats and read/write model; GUI is Phase 12.
  Tag: v0.1 is GUI-less — parsing and applying persisted options only. **REV1:**
  Pintonium's format matches (ISO-8859-1 Properties, merged through a queue on reload,
  PD §14) — a working round-trip reference.
- Processed-source debug dump (`-Dschmaloogium.debug.saveSources`, App F.8 equivalent).
- **Required output — the engine-flag ownership map**: a table assigning every App F.1
  flag to the phase that implements its behavior (e.g. `clouds`/`backFace.*`/`sun`/`moon`/
  `vignette`/`underwaterOverlay`/`rain.depth`/`beacon.beam.depth` → Phase 7;
  `shadowTranslucent` → Phase 8; `oldHandLight`/`dynamicHandLight` → Phase 9;
  `oldLighting`/`separateAo`/`frustum.culling` → owner named by you). Later phases wire
  their slice; the map is the master list.

**Scope — out:** program compilation (Phase 4); buffer allocation (Phase 5 — you produce
sizing *requirements*); uniform values (Phase 6); expression evaluation (Phase 11); GUI
(Phase 12); texture loading (Phase 13); alias resolution (Phase 9).

**Required inputs:**
- RESEARCH.md §3.1–§3.3, §3.5, §3.7, §4.1 (steps 2–3), §4.7 (options), §7.5, App A.3,
  App F (whole), App H.
- **PD §7 (front-end, whole), §12 (why no AST transformer), §17 rows B1–B3 & B12.**
  Pintonium `common-shaders/src/main/java/net/irisshaders/iris/shaderpack/` (LGPL
  structure reference — §G11 rules; exclude `celeritas-shader-refactor/` per §G11.3).
- `schlorbium-project/doc/shaders.properties` (whole file, 489 ln — the annotated key
  reference).
- `schlorbium-project/doc/shaders.txt` — the sections on directives, macros, options, and
  ID mapping (skip program/uniform tables; those are Phases 4/6 inputs).
- `PHASE_1_DOC.md` (module layout, logging, debug flags).

**Architecture requirements:** pure `:engine`; every parse error follows the G2.4 ladder
(malformed directive → warn + ignore line, never abort the pack unless structurally
unusable); the `PackConfiguration` output object is the single source downstream phases
consume (registry configs, buffer requirements, uniform declarations, option state).

**Doc gate:** conformance map covers every App F key and App A.3 directive with zero
unassigned rows; flag-ownership map complete; identity-set architecture demonstrably
option-3-shaped with the decision still open; pure-core placement confirmed. **REV1:**
jcpp adoption stated (or deviation justified with reasons); the four PD pitfalls (B1
miswired halflife, B2 dead comment directives, B3 stubbed component analysis, B12
`#`-stripping) each trace to a conformance-map row **and** a named test; the
centerDepthSmooth `#define` injection point is reserved in the macro-header design.

**Impl gate:** headless golden runs (Phase 2 harness) parse all seven matrix packs' sources
end-to-end without error; resource-sizing decisions match hand-verified expectations for
at least one classic pack.

**Context budget:** ≈ 55k tokens mandatory reading (largest front-end surface, now incl.
PD + reference tree). Stay out of `SHADER_ENGINE_IMPL.md` — §3/App F + shipped docs are
the contract; you do not need behavioral internals. Fallback if sprawling: none — this
phase merged two concerns deliberately; if the doc grows large, compress prose, not
coverage.

---

### Phase 4 — Stage/program registry & compilation

**Milestone:** v0.1 · **Depends on:** 1, 3 · **OQs:** —

**REV1 changes:** the backup-chain model, barrier shape, and per-pass state bundle gain a
working reference; the attribute pre-bind divergence is flagged as a do-not-copy;
version-counter invalidation is added for reload safety.

**Objective.** The engine's spine: a stage registry modeling the full modern pass sequence
(D-4) with the G6 five stages as instances, the classic program registry with backup
chains, and the compile/link pipeline.

**Deliverable.** `PHASE_4_DOC.md` per §G9.

**Scope — in:**
- **Stage registry** (§7.3, §3.6.1 shape only): stage identity for the full superset
  (`setup → begin → shadow → shadowcomp → prepare → gbuffers → deferred → composite →
  final`); sparse pass arrays up to index 99; per-pass buffer read/write sets; flip
  bookkeeping slots; compute-dispatch placeholders (`.csh` + `_a…_z` — slots exist, wiring
  is G8/S2). **No hardcoded 16s or 8s anywhere.** The G6 era is a *configuration* of this
  mechanism: 1.12.2 wires shadow/gbuffers/deferred/composite/final with arrays populated
  to 15. **REV1:** Pintonium's 26-value `WorldRenderingPhase` enum (with `phase` +
  `overridePhase` + deferred-pop semantics) is the Iris-superset shape, working — a
  structural cross-check for the registry's phase model (PD §3.1).
- **Program registry** (App A.1): all classic catalog slots incl. virtual `deferred_pre`/`composite_pre`
  (flip-control only); per-slot stage, backup parent, and per-program state (draw-buffer
  routing, composite-mipmap bitmask, instance count, alpha/blend overrides, render scale,
  flip config) — populated from Phase 3's `PackConfiguration`. **REV1:** PD §3.3's
  `CompositeRenderer.Pass` bundle (program, framebuffer, drawBuffers, `BlendModeOverride`,
  per-buffer `BufferBlendOverride` via GL 4.0 `glEnablei`/`ARBDrawBuffersBlend`,
  `viewportScale`, mipmap bitmask, flip snapshot `stageReadsFromAlt`, compute companions)
  is a working App A.1 per-slot state inventory — check the slot model against it field
  by field.
- **Backup-chain resolution** (App A.2): empty program inherits the *entire configuration*
  of the nearest non-empty ancestor; `shadow` never inherits; profile-disabled /
  `program.<name>.enabled=false` programs are treated as absent. **REV1:** PD §3.1's
  `ProgramFallbackResolver` is App A.2 ported and working — `TerrainSolid/TerrainCutout →
  Terrain → TexturedLit → Textured → Basic`, `Water → Terrain`, `HandWater → Hand`,
  `ShadowSolid/ShadowCutout → Shadow`, memoized recursive resolution, **shadow never
  inherits**. The closest clean reference that exists; validate our table against it.
- **Compile/link flow** (§4.2): per-file — Phase 3 front-end output → compile → attach →
  pre-link attribute binding at fixed locations (`mc_Entity`=10, `mc_midTexCoord`=11,
  `at_tangent`=12) → link → validate. Core GL objects (`glCreateProgram` family) via the
  facade, not ARB entry points (§6.2). **REV1 warning:** Pintonium pre-binds at
  **11/12/13/14** (Iris numbering, `ProgramCreator.java:21-25` — **REV2** re-verified at
  the line, range unchanged, detail added: location 11 is bound *twice*, `iris_Entity`
  and `mc_Entity`, then 12 `mc_midTexCoord`, 13 `at_tangent`, 14 `at_midBlock`, with 0/1
  taken by `Position`/`UV0` at `:27-28`; PD §3.2/§18) — that works
  only because it owns its chunk renderer's VAO layout. A contract-faithful engine binds
  **10/11/12**. Copying Pintonium's numbering is a contract violation; do not let
  reference-reading drift it.
- **Geometry shaders, dual-form** (§3.2, §6.2): GL 3.2 layout qualifiers *and* the legacy
  `GL_ARB_geometry_shader4` + `maxVerticesOut` declaration form — the preprocessor/scanner
  accepts both; internal translation strategy specified here.
- **Failure handling** (§4.7): validation failure → delete program, user-visible error,
  resolve through backup chain (ladder step 3).
- **"Use program" state-barrier contract** (§4.2): define the barrier's obligations
  (sampler re-point, uniform refresh, custom-uniform evaluation, per-program alpha/blend
  lock, shadow-pass force-selection override) as an *interface* — Phase 6 supplies the
  uniform machinery, Phase 7/8 invoke it. Hook sites stay dumb. **REV1:** the barrier's
  shape is externally validated — Pintonium's `Program.use()` performs exactly
  memory-barrier → `glUseProgram` → uniforms/samplers/images re-push (PD §3.2).
- **Reload-safety invalidation** (REV1): adopt PD §3.1's version-counter scheme — a
  monotonically increasing pipeline version counter (`versionCounterForSodiumShaderReload`
  analog) that downstream program/uniform caches poll to invalidate on pack reload.
  Phase 12's reload paths depend on this; expose it in doc §5.
- Per-program state application semantics: DRAWBUFFERS routing validation, scale/flip
  storage, `countInstances` exposure to the pass executor (execution is Phase 7, tag v0.5).

**Scope — out:** FBO/texture objects (Phase 5); uniform values and upload (Phase 6); pass
*execution* and frame orchestration (Phase 7); shadow-pass camera (Phase 8).

**Required inputs:**
- RESEARCH.md §3.1, §3.6.1 (shape only), §4.1 (steps 4–5), §4.2, §7.3, App A (whole).
- **PD §3 (pipeline core), §13 (legacy-compat technique + its warning).** (§G11 rules.)
- `schlorbium-project/doc/shaders.txt` — the "Shader Programs" table section (program
  names, renders, fallback column).
- `PHASE_1_DOC.md` (facade, module layout), `PHASE_3_DOC.md` (`PackConfiguration`,
  scanned directives, per-program overrides).

**Architecture requirements:** registry lives in `engine.registry` (pure); GL touchpoints
only through the facade so registry logic is headless-testable against a
`GLCapabilityProfile`. Contract-visible: program names, fallback chains, attribute
locations, barrier semantics. **REV1:** per G2.4-rung-3 note, generated passthrough
programs are a legitimate degradation design space, but heuristic pack-layout detection
(Pintonium's Solas-brightness root cause, PD §19.2) is never acceptable on a contract
path — the backup chain is the principled mechanism.

**Doc gate:** the registry demonstrably instantiates both the G6 table and the §3.6.1
superset without structural change (show both configurations); every App A.1 row mapped;
barrier contract fully specified as an interface; **REV1:** backup-chain table
cross-validated against PD §3.1's resolver; version-counter invalidation exposed in §5.

**Impl gate:** headless tests — backup-chain resolution over synthetic packs matches App
A.2 semantics; a recorded-GL compile flow runs a classic pack's program set to "linked"
with zero unmapped directives.

**Context budget:** ≈ 41k tokens mandatory reading.

---

### Phase 5 — Framebuffer & buffer architecture

**Milestone:** v0.1 (shadow FBO structure now, wired at v0.2) · **Depends on:** 1, 3, 4 · **OQs:** —

**REV1 changes:** the depthtex0 depth-texture swap gains a proven, now-mandatory
reference; flip/clear semantics are externally confirmed (incl. the fog-alpha-1.0 quirk);
frame-end flip reconciliation becomes an explicit recorded decision; the tiered
depth-copy strategy and resize lifecycle checklist are adopted.

**Objective.** The buffer estate: main FBO with ping-pong/flip semantics, clear rules,
formats, depth textures, the shadow FBO structure, sizing, and resize lifecycle.
Contract-visible almost end to end — packs depend on exact flip behavior.

**Deliverable.** `PHASE_5_DOC.md` per §G9.

**Scope — in:**
- **Main FBO ("dfb")** (§4.3, App B.1): up to 8 logical color buffers at G6 wiring, each
  backed by two textures (main/alt) for ping-pong; gbuffers read/write "main";
  deferred/composite read "main", write "alt", then **flip the buffers they wrote**;
  per-program flip overrides incl. the virtual `*_pre` programs (config from Phases 3/4);
  the last-writer-leaves-flip-enabled convention (App F.7). **REV1 semantics, verified in
  production** (PD §5.1): *not flipped → write alt, read main; flipped → write main, read
  alt*; `flip()` toggles; per-pass snapshots; explicit `flip.<pass>.<buffer>` directives
  applied at pass construction; FBO creation per pass attaches main-or-alt per drawbuffer
  index; samplers close over the same snapshot. Allocation is main/alt pairs up front
  (LINEAR filter, NEAREST for integer formats, CLAMP_TO_EDGE).
- **Frame-end flip reconciliation — recorded decision required** (REV1, §G11.4): Iris
  runs frame-end `SwapPass` copies propagating alt contents back to main (proven working;
  costs bandwidth), while OF-faithful behavior is last-writer-leaves-flip-enabled
  carryover (App F.7). The contract (gbuffers read/write main) implies state must be
  unflipped at frame start; RESEARCH.md's ruling governs. Whichever mechanism satisfies
  it, the decision (`D-P5-k`), the contract check, and the complete state machine are
  documented — this is exactly the "long tail" behavior conformance testing exists to
  catch (§2.4).
- **Clear rules** (§4.3, App B.1): buffer 0 → fog color; buffer 1 → solid white (+
  `gdepth`-declared → RGBA32F upgrade); buffers 2–7 → transparent black; per-buffer
  `<buf>Clear`/`ClearColor` overrides; **clearing honors flip state (clears both sides
  when flipped)**. **REV1 confirmations** (PD §5.1): the both-sides-when-flipped ruling is
  confirmed by `ClearPassCreator`'s main/alt clear-FBO pairs; **buffer 0's fog clear color
  forces alpha to 1.0** — record this quirk verbatim; the deployed comment notes Sildur's
  Vibrant Shaders produce pink reflections otherwise; clears batch in groups of
  `GL_MAX_DRAW_BUFFERS`.
- **Formats** (App B.4): the 37 internal formats, pixel formats/types, the integer
  pixel-transfer path for integer formats, incomplete-framebuffer fallback → recreate
  everything as plain RGBA (ladder-conformant, user-visible warning).
- **Depth textures** (App B.2): depthtex0 (real attachment), depthtex1/2 as copy targets —
  the copy *moments* are Phase 7's; you own the textures, copy mechanics, and their
  lifecycle. **REV1 — depthtex0's 1.12.2 problem is solved in the reference and the
  solution is mandatory reading:** vanilla 1.12.2 `Framebuffer` uses a depth
  **renderbuffer**, which cannot be sampled; Pintonium's `MixinFramebuffer_Shaders`
  replaces it with a depth texture at `createFramebuffer` (`GL_DEPTH_COMPONENT`, or
  `GL_DEPTH24_STENCIL8` + stencil attachment when stencil is enabled), tracks it via an
  extension interface + version counter, and re-attaches all owned FBOs' depth
  attachments whenever the version bumps (PD §5.2). Phase 5 cannot ship without an
  equivalent — specify ours against this proven design. **Copy mechanics:** adopt the
  capability-tiered strategy — GL 4.3 `glCopyImageSubData` (**function-pointer checked
  because caps lie**) → GL 3.0 blit (combined depth-stencil) → GL 2.0
  `glCopyTexSubImage2D`; first frame `glCopyTexImage2D` (PD §5.2).
- **Shadow FBO ("sfb")** (§4.3): created only when shadow buffers are used (Phase 3 sizing
  output); ≤2 depth (+ optional hardware-PCF compare mode), ≤2 color, at
  `shadowMapResolution` × shadow-quality multiplier; per-texture nearest/mipmap filter
  config. Structure and lifecycle here; pass wiring is Phase 8. **REV1:** Pintonium's
  shadow targets are a **structure-only** reference — hardware-PCF compare mode
  (`GL_COMPARE_REF_TO_TEXTURE`) and the old-pack swizzle `R,R,R,1` are right, but its
  `ShadowRenderTargets.flip()` is a stub and shadow samplers always read main (PD B4).
  Our shadow flip semantics must be real and state-machine-tested.
- **Sizing** (§4.3): display size × render-quality multiplier; `superSamplingLevel`;
  resize/recreate lifecycle (display resize, multiplier change, pack change → uninit/init,
  §4.1 step 5's uninit triggers as they affect buffers). **REV1:** keep scan-driven
  sizing — Pintonium allocates all 16 colortex unconditionally (PD B13); our
  Phase-3-scan-driven model is strictly better. The resize lifecycle checklist exists at
  PD §5.3 (recreate colortex, realloc depthtex1/2, force full clear, recalculate all pass
  + swap sizes, resize relative SSBOs/custom images) — adopt it wholesale. Note:
  Pintonium has **no global render-quality multiplier** (`MC_RENDER_QUALITY` hardcoded
  1.0) — `superSamplingLevel` is ours alone (§G11.5 "no help available").
- **Growth posture** (§3.6.3, D-4): data model addresses buffers by index without
  hardcoded 8/16 caps (16/32 colortex, shadowcolor2–7 are G8 wiring, not new architecture);
  `Final` renders to the vanilla framebuffer (handoff contract with Phase 7).
- Reproduce the **App B.3 fixed texture-unit map** as this phase's binding table
  (ownership shared with Phase 6: you own which texture object backs each unit per stage;
  Phase 6 owns pointing sampler uniforms at units) — including the documented
  **depthtex1-at-unit-11 ruling** (App B.3 note: the shipped doc's ID table says 12;
  behavior and uniform table say 11; **11 is authoritative**). **REV1:** Pintonium's
  dynamic per-program unit allocation (PD §6.5) is a pre-decided rejection (§G11.4) —
  App B.3 stays fixed.

**Scope — out:** when copies/clears *happen* in the frame (Phase 7); sampler uniform
re-pointing (Phase 6); shadow camera/pass (Phase 8); custom texture binding (Phase 13);
sampler objects & async transfers (Phase 14).

**Required inputs:**
- RESEARCH.md §4.3, App B (whole), §3.6.3 (growth shape only), §4.1 (init/uninit steps).
- **PD §5 (buffer estate, whole), §17 row B4, §18 flip row.** Pintonium
  `forge122/src/shaders/.../MixinFramebuffer_Shaders.java` + `common-shaders/.../targets/`
  (LGPL — §G11 rules).
- `schlorbium-project/doc/shaders.txt` — buffer/format/pixel-type sections.
- `PHASE_1_DOC.md` (facade), `PHASE_3_DOC.md` (sizing requirements, format directives,
  clear overrides), `PHASE_4_DOC.md` (flip config storage, DRAWBUFFERS routing).

**Architecture requirements:** policy in `engine.buffers` (pure, testable as a state
machine over facade calls); GL objects behind the facade. Flip/clear semantics are
contract-visible — specify them as explicit state machines with transition tables, because
they are exactly the "long tail" behavior conformance testing exists to catch (§2.4).

**Doc gate:** App B.3 reproduced exactly incl. the unit-11 ruling; clear/flip specified as
testable state machines; every App B.1/B.2/B.4 row in the conformance map. **REV1:** the
frame-end flip reconciliation decision recorded with provenance and contract check; the
clear-color table includes the fog-alpha-1.0 rule; the depthtex0 swap design present and
traced to the PD §5.2 reference; shadow flip semantics real (not the PD B4 stub).

**Impl gate:** headless state-machine tests cover flip/clear/override permutations; a
recorded-GL run creates/destroys the full buffer estate for a classic pack without leaks.

**Context budget:** ≈ 43k tokens mandatory reading.

---

### Phase 6 — Uniform & sampler system

**Milestone:** v0.1 (shadow-set *values* wired at v0.2) · **Depends on:** 1, 3, 4 · **OQs:** —

**REV1 changes:** the cadence model and FF-matrix capture are externally validated; a
GPU-side `centerDepthSmooth` design becomes a gated candidate against the planned
synchronous readback; a notifier→producer audit is mandated; smoothing math gains
closed-form references (read the math, not Pintonium's wiring — B1).

**Objective.** The ~50 built-in uniforms with their exact semantics and update cadences,
the sampler-unit re-pointing that fulfills Phase 4's state barrier, world-state smoothing
math, and the value-provider seam that keeps all of it headless-testable.

**Deliverable.** `PHASE_6_DOC.md` per §G9.

**Scope — in:**
- **Full App D inventory** (D.1–D.4): every uniform with type, semantics, and provider;
  the shadow matrix/celestial set designed now, its *values* wired when Phase 8 lands
  (tag v0.2).
- **Cadence model** (App D cadence note, §4.2): everything refreshes on program switch;
  per-program location cache; redundant-upload skipping (matrices always upload);
  celestial vectors update at the sky-rotation moment (hook is Phase 7's; the update
  entry point is yours); shadow matrices during shadow-camera setup (Phase 8 invokes);
  per-draw dynamics (`entityColor`, `entityId`, `blockEntityId`, `blendFunc`,
  `instanceId`) at their hooks (Phases 7/9/10 invoke); custom uniforms evaluated after
  built-ins on every switch (Phase 11 plugs in here — define the extension point).
  **REV1 — externally validated model** (PD §6.1): Pintonium's buckets are ONCE /
  PER_TICK (tick compare) / PER_FRAME (frame counter) / **dynamic** (every program
  switch); each `Uniform` caches its last value and skips redundant GL calls; matrices
  always upload; `CommonUniforms`' non-dynamic/dynamic split (camera/viewport/time/biome/
  celestial/matrices/id-map vs `entityId`/`atlasSize`/`blendFunc`/`renderStage`/fog) is
  the working checklist — use it when writing the cadence table.
- **Frame-begin ordering constraint** (REV1): world-state sampling / uniform-update
  notification (incl. the center-depth sample) fires **before** any buffer resize or
  clear — PD §6.1's `FrameUpdateNotifier` exists precisely "so that the center depth
  sample is retrieved properly". Encode this in the frame-begin contract Phase 7
  consumes (flag in doc §5).
- **Sampler re-pointing**: on every use-program, sampler uniforms re-point to the App B.3
  fixed unit map (per-stage variants — gbuffers/shadow vs deferred/composite/final).
  **REV1:** implementation reference — `ProgramSamplers.update()`'s rebind + queued
  `glUniform1i` calls with dedup (PD §6.5); mechanics only, the fixed map stands
  (§G11.4).
- **World-state sampling & smoothing** (§4.4 frame-begin, App D.1/D.3):
  `wetness`/`drynessHalflife` decay, `eyeBrightnessSmooth`, `centerDepthSmooth` — specify
  the smoothing math exactly (halflife → per-tick exponential decay formula, time-corrected);
  `isEyeInWater`, night vision/blindness, sky/fog color sampling. **REV1 closed-form
  references** (PD §6.4): `SmoothedFloat` (asymmetric exponential decay, `k = ln2 /
  halfLife`, half-lives in deciseconds, separate wet/dry rates) and `SmoothedVec2f`
  (`eyeBrightnessSmooth`, `eyeBrightnessHalflife` default 10). **Read the math, not the
  wiring** — PD B1 misassigns `drynessHalflife` into the wetness field.
- **`centerDepthSmooth` — two candidate designs, decision recorded** (REV1, §G11.4):
  (a) the App-D-faithful **synchronous per-frame `glReadPixels`** + CPU-side exponential
  smoothing (the v1.1 plan; PBO async replacement at Phase 14); (b) the REV1 candidate —
  **GPU-side smoothing**: a 1×1 R32F ping-pong pass blends the current center depth
  against the previous frame's value with a `centerDepthHalflife` decay uniform, and
  pack references to `centerDepthSmooth` redirect through a `#define` injected by Phase
  3's macro header (`#define centerDepthSmooth texture2D(<reserved-sampler>,
  vec2(0.5)).r`) — legal without an AST transformer because the uniform is only ever
  *read* (PD §6.3). **Mandatory contract check against App D semantics:** the GPU EMA is
  behaviorally equivalent but not bit-identical to CPU-readback smoothing; record the
  evidence and the decision (`D-P6-k`). If adopted, Phase 14's PBO item is obviated —
  record the ledger note for §G5.3's integration review. **Default if the check is
  inconclusive: (a).**
- **Previous-frame snapshots**: camera position + modelview/projection captured per frame
  for the `previous*` uniforms (TAA-style contract, §4.4). **REV1:** temporal consumers
  are the conformance long tail (PD §19.1) — snapshot *moments* and overwrite semantics
  must be exact, and doc §8's tests must cover them.
- **FF-matrix capture points**: `gbufferModelView`/`gbufferProjection` (+inverses)
  captured from the fixed-function stack at defined moments (§4.4; the moments are Phase
  7 hooks; the capture/inverse machinery is yours). **REV1 — proven in production on
  1.12.2** (PD §6.2): copy from vanilla's `ActiveRenderInfo.PROJECTION`/`MODELVIEW`
  FloatBuffers (accessor mixin) at the first-clear moment — i.e. the matrices vanilla
  itself captured during `orientCamera`; derive inverses and `gbufferPrevious*` in the
  uniform layer. Shadow matrices likewise ride the FF stack (`glMatrixMode`/`glPushMatrix`/
  `glLoadMatrix` swaps) — D-9 compat cooperation demonstrated.
- **`blendFunc` observation** via GlStateManager cooperation (§G4.6, App E row 16).
  **REV1 — the notifier must be wired, and the wiring must be audited:** Pintonium never
  assigns its `blendFunc` notifier on 1.12.2; a pack declaring the `blendFunc` uniform
  NPEs at program build (PD B6). Doc §4 includes a **notifier→producer table**: every
  state notifier (fog start/end, blend func, texture bind, normal/specular change, phase
  change, fallback entity — PD §6.6's hub list is the shape to adopt) mapped to the hook
  that feeds it; the integration review cross-checks it against Phase 7's hook catalog.
- **Value-provider interfaces** in `engine.uniforms`: every uniform's value comes through
  a provider interface implemented by `mod.glue` (world sampling) — so the whole system
  unit-tests headless with scripted providers.
- Per-uniform GL-error isolation (ladder step 2).

**Scope — out:** custom-uniform expressions (Phase 11); the hooks that *invoke* updates
(Phase 7/8); alias-derived id values (Phase 9); `atlasSize` value source (Phase 13).

**Required inputs:**
- RESEARCH.md §3.4, §4.2 (barrier), §4.4 (frame-begin/uniform lines), App B.3, App D
  (whole).
- **PD §6 (whole), §17 rows B1 & B6.** (§G11 rules.)
- `schlorbium-project/doc/shaders.txt` — the "Uniforms" section.
- `PHASE_1_DOC.md`, `PHASE_3_DOC.md` (declared-uniform scan → which uniforms are active),
  `PHASE_4_DOC.md` (state-barrier interface you fulfill).

**Architecture requirements:** contract-visible: names, types, semantics, cadences (App
D); implementation may batch/cache differently than the reference (§4.8) but observable
behavior must match. Pure-core with provider seam is mandatory.

**Doc gate:** every App D row mapped to provider + cadence + milestone tag; smoothing
formulas written out; barrier fulfillment traced to Phase 4's interface point by point.
**REV1:** the centerDepthSmooth decision recorded with contract-check evidence; the
notifier→producer audit table present; the frame-begin ordering constraint exported in
§5.

**Impl gate:** headless tests — cadence engine with scripted providers reproduces
documented update patterns; smoothing math matches closed-form expectations; location
cache/redundant-skip behavior verified against a recording facade.

**Context budget:** ≈ 43k tokens mandatory reading.

---

### Phase 7 — Render-loop integration & frame orchestration

**Milestone:** v0.1 exit (assembles the first end-to-end render) · **Depends on:** 2, 3, 4, 5, 6 · **OQs:** OQ-3, OQ-4

**REV1 changes:** the hook catalog gains a verified 7-row injection timeline as its
starting hypothesis; the frame-begin ordering constraint lands; the composite-guarantee
is noted as strictly stronger than the reference's; sky/weather/clouds are flagged as
reference-free rows; OQ-3/OQ-4 evidence annotations.

**Objective.** The frame driver that orchestrates every prior phase into a rendered frame,
and the complete Mixin hook catalog that connects it to the vanilla 1.12.2 render loop.
This is the phase where Schmaloogium becomes a shader engine.

**Deliverable.** `PHASE_7_DOC.md` per §G9, with **two mandated parts** (a: frame driver;
b: hook catalog). **Fallback:** if the session nears its context ceiling, part (b) splits
off as Phase 7b — finish part (a)'s doc completely, and write a Phase 7b assignment
paragraph into doc §11 (part (a)'s doc becomes 7b's input).

**Scope — in, part (a) — engine-side frame driver** (§4.4):
- Frame begin/end lifecycle: world-state sampling trigger (Phase 6), previous-frame
  snapshot, gbuffers texture-set bind, main-FBO bind + clears (Phase 5), **the
  composite-guarantee** (composites/final run even on early frame exits). **REV1:** the
  frame-begin sequence preserves the Phase 6 ordering constraint — sampling/notification
  **before** resize/clear (PD §6.1). The composite-guarantee is **strictly stronger than
  the reference**: Pintonium runs composite/final at `renderWorldPass` TAIL and does not
  handle early frame exits specially (PD §4) — design ours beyond the reference
  (try/finally-style finalization or equivalent), and say so.
- **The phase-dispatch table** (App A.1): program selection around each render phase —
  sky (incl. the celestial-rotation moment → Phase 6 celestial update), terrain
  solid/cutout-mipped/cutout, damaged-block, entities, glowing outline, block entities,
  beacon beam, armor glint, spider eyes, particles (lit/unlit), clouds, weather; **push/pop
  program semantics** around leash/glint rendering; shadow-pass force-selection deference
  (Phase 4 barrier rule).
- **Deferred trigger** between solid and translucent terrain: depth copy → depthtex1,
  deferred ping-pong execution [copy at v0.5; architecture + pass execution now];
  translucent terrain (water program) → hand solid → hand translucent with the
  depth-scale matrix trick; weather depth copy → depthtex2 [v0.5]; first-person overlay
  → draw-buffers-none routing. **REV1:** the translucent-layer-HEAD → depth-copy →
  deferred sequence is cross-validated by the reference (PD §4 row 5) — RESEARCH.md
  §4.4's placement is confirmed.
- **Composite/final execution**: fullscreen quad (triangle-strip fallback), identity
  ortho, fog/depth/blend disabled, per-pass mipmap generation (composite-mipmap bitmask),
  `scale.<prog>` sub-viewports [v0.5], `countInstances` instancing loop [v0.5],
  anaglyph-aware final to the vanilla framebuffer (Phase 5 handoff).
- **Internal default pack** (§4.1 sentinels are Phase 3's; the built-in passthrough pack
  content is yours) and the shaders-off path (G2.4 rule 5). **REV1:** no reference —
  Pintonium hardcodes `isInternal()` false (PD §7.1); design from §4.1 alone.
- **Dimension-switch lifecycle**: uninit/reinit when per-dimension packs exist (§4.1 step
  5); resolution-multiplier and pack-change uninit triggers coordinated with Phase 5.
  **REV1:** adopt the per-dimension pipeline-cache + version-counter shape (PD §3.1) as
  the mechanism; the *semantics* stay the OF world-folder contract (§G11.4).
- **Engine-flag wiring** for this phase's slice of the Phase 3 flag-ownership map
  (`clouds`, `backFace.*`, `sun`, `moon`, `vignette`, `underwaterOverlay`, `rain.depth`,
  `beacon.beam.depth`, `frustum.culling` as applicable).

**Scope — in, part (b) — Mixin hook catalog** (§7.1, App E):
- **The verified injection timeline (REV1 — start here).** Pintonium ships a battle-tested
  1.12.2 injection map that survives Complementary Unbound/Solas-class packs (PD §4,
  verified by direct read of `forge122/.../mixin/shaders/MixinEntityRenderer_Shaders.java`
  and `MixinRenderGlobal_Shaders.java`). Each row is a **starting hypothesis to be
  validated against Cleanroom's patches and App E SRG names** — the *moments* are proven,
  the exact targets are ours to confirm:

  | # | Injection point (all `EntityRenderer` unless noted) | What runs there |
  |---|---|---|
  | 1 | `renderWorldPass(IFJ)V` **HEAD** | tick-delta/time uniforms; per-dimension pipeline prepare; phase reset |
  | 2 | `renderWorldPass`, INVOKE `GlStateManager.clear(I)` ordinal 0 (before) | reset vanilla GL state to known values |
  | 3 | same site, **AFTER** the first clear | **capture gbuffer matrices** from `ActiveRenderInfo` → Phase 6; begin-frame uniforms; frame begin (clears, resize check, shadow clear, frame-update notify, custom uniforms, `begin_*` passes); rebind default FB |
  | 4 | `renderWorldPass`, INVOKE `RenderGlobal.setupTerrain(...)` AFTER | shadow pass / `prepare_*` trigger slot; rebind default FB |
  | 5 | `RenderGlobal.renderBlockLayer` HEAD when layer == TRANSLUCENT | depth copy → depthtex1, then all deferred passes; re-enable blend |
  | 6 | `EntityRenderer.renderHand(FI)V` around first-person item render | hand bridge incl. depthtex2 copy and center-depth sampling |
  | 7 | `renderWorldPass` **TAIL** | composite-all then final to the MC framebuffer |

  Supporting hooks: `RenderGlobal.renderEntities` region (entity/TE bridges), particle
  render HEAD/RETURN, dragon crystal beams HEAD/RETURN (beacon beam), selection-box
  HEAD/RETURN (line program), `updateFogColor`/`setupFog` (fog uniforms via state
  notifiers), plus the bootstrap hooks (Phase 1's three-stage sequence). **The
  ordinal-0 `GlStateManager.clear` INVOKE site is a proven stable anchor** for "world
  render begins here" in 1.12.2 — matrix capture and frame begin both hang off it.
- **Reference-free rows — flagged higher-risk** (REV1): Pintonium does **not** hook sky
  (`renderHorizon` is an empty TODO there), weather, or clouds (PD §4). Those App E rows
  are designed from the App E catalog + Cleanroom patches + MCP symbol resolution alone;
  mark each such row "no working reference" in the doc and order the v0.1 assembly to
  prove them early where feasible.
- Per-site injection specs for **all App E classes**: for each hook — target class/method
  (SRG name + descriptor from App E), injection type (`@Inject`/`@Redirect`/`@ModifyArg`/
  `@At` strategy inside long methods), what engine entry point it calls, and its failure
  posture (a hook that fails to apply must degrade per G2.4, not crash).
- The celestial-rotation sub-method site inside `renderSky` (`func_174976_a`) —
  identified concretely (App E coverage note); use MCP `resolve_symbol` +
  `get_method_signature` to validate every App E row you consume.
- Display-resize/framebuffer-size interception and GL-context posture: **OQ-3 spike spec**
  (context creation mechanics, compat-profile request, HiDPI framebuffer-size vs window
  size, what lwjglx intercepts — procedure: inspect Cleanroom's window/context layer in a
  dev env; fallback: hook `Framebuffer`/resize sites only, no context-flag changes).
  **REV1:** Pintonium never touches context creation (PD §16) — evidence supports the
  fallback as the default plan.
- **OQ-4 spike spec** (CleanMix divergences on hot-path injections — procedure: apply a
  representative injection per category — `@Inject` head/return, `@Redirect`, constant
  `@At` — into `EntityRenderer`/`RenderGlobal`/`BufferBuilder` in dev, verify
  application + measure overhead; fallback: alternative injection forms per site,
  documented per hook). **REV1:** Pintonium proves head/INVOKE/TAIL injections on these
  exact hot paths apply and run on a MixinBooter-family 1.12.2 loader (PD §4) — the spike
  is now about CleanMix *specifically*.
- Coexistence bail hook (Phase 1's registry; policy from Phase 10).
- The v0.1 assembly narrative: which hooks + driver pieces constitute the first
  end-to-end render, in dependency order.

**Scope — out:** shadow-pass content (Phase 8 — you leave its invocation slot before the
world render); vertex-write/draw-path hooks (Phase 10 — App E rows 5–8 are catalogued as
Phase 10 consumers, not specified here); per-entity/TE id values (Phase 9); atlas hooks
(Phase 13 — App E rows 10–11 likewise deferred).

**Required inputs:**
- RESEARCH.md §4.4 (whole), §7.1, §5.3, App A.1, App E (whole).
- **PD §4 (the injection timeline), §6.1 (ordering), §16 (bootstrap).** Pintonium
  `forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java`
  and `.../MixinRenderGlobal_Shaders.java` (LGPL field evidence — §G11 rules).
- `cleanroom-src/patches/minecraft/net/minecraft/client/renderer/EntityRenderer.java.patch`
  and `RenderGlobal.java.patch` (Cleanroom's own deltas to your two main target classes —
  what Cleanroom already changed, you must not fight).
- `schlorbium-project/SHADER_ENGINE_IMPL.md` — frame-flow and lifecycle sections only,
  under §G7 rules (restate behavior, never structure).
- `schlorbium-project/files.txt` (cross-check: every hook class appears in OF's
  replacement list).
- MCP recipes: `resolve_symbol`, `get_method_signature` (validate App E), 
  `search_cleanroom_api(kind=event)` (prefer a Forge/Cleanroom event over a mixin where
  one exists — e.g. render-world events).
- `PHASE_2_DOC.md` (exit-criteria runs), `PHASE_3_DOC.md`–`PHASE_6_DOC.md`.

**Architecture requirements:** hooks stay dumb (§G3.3); the frame driver lives engine-side
with a narrow glue interface so a future Kirino backend (G8/S5) replaces part (b) without
touching part (a) — state this seam explicitly in doc §5. Where a Forge event covers a
hook need at acceptable fidelity, prefer it over a mixin (fewer injections = smaller OQ-4
surface); document each such choice.

**Doc gate:** every §7.1 hook need 1–11 traced to a hook-site spec, a Forge event, or an
explicit deferral (with owner phase and milestone tag); v0.1 assembly narrative complete;
both spike specs complete with fallbacks. **REV1:** the 7-row Pintonium timeline adopted
per row (with Cleanroom-patch/SRG validation cited) or deviation justified; rows with no
working reference (sky/weather/clouds) explicitly flagged.

**Impl gate:** RESEARCH.md §9 v0.1 exit — ≥1 classic pack at T1 on the fixed scenes; T0
across the classic matrix (Phase 2 harness runs).

**Context budget:** the heaviest phase: ≈ 68k tokens mandatory reading + MCP resolution
traffic. Watch the ceiling; the 7b fallback exists for a reason. Do not read decompiled
engine sources; `SHADER_ENGINE_IMPL.md`'s frame-flow section + §4.4 suffice. Bound
Pintonium reading to the two listed mixin files.

---

### Phase 8 — Shadow pass

**Milestone:** v0.2 · **Depends on:** 4, 5, 6, 7 · **OQs:** —

**REV1 changes:** the shadow camera/snap math gains a portable reference; the phase's
least-de-risked status is confirmed and unchanged (no 1.12.2 traversal reference exists).

**Objective.** The complete shadow pass: its camera, culling, traversal, render order,
depth split, and uniform wiring — running inside frame begin, before the world render,
in the slot Phase 7 reserved.

**Deliverable.** `PHASE_8_DOC.md` per §G9.

**Scope — in** (all §4.5 unless noted):
- **Shadow camera**: forced third-person; ortho projection (±`shadowDistance` half-plane,
  near 0.05 / far 256) or perspective when `shadowMapFov` is set; modelview = celestial
  rotation (sun by day, moon by night) + `sunPathRotation`; **texel snapping** by
  `shadowIntervalSize` (default 2.0) so shadow texels don't crawl. **REV1 — portable math
  reference:** `ShadowMatrices.createOrthoMatrix` and
  `createShadowModelView(sunPathRotation, intervalSize)` (texel snapping lives there) in
  Pintonium's modern-module `ModernShadowRenderer` — the only working implementation in
  that tree (PD §10). Reuse structure, re-derive values (§G11.5).
- **Culling & traversal**: shadow-frustum planes derived from the shadow MVP **plus
  synthesized side planes along the light direction**; sun-aligned-box chunk traversal
  between camera and light when shadow render distance < view distance;
  `shadowDistanceRenderMul` optimization semantics. **REV1:** **no traversal reference
  exists** — Pintonium's re-runs its own chunk collector, and its 1.12.2 shadow renderer
  is `null` (`// TODO: Port the 1.12 shadow terrain renderer`, PD §10). We traverse
  vanilla's `RenderGlobal` per App E rows 1–2, from RESEARCH.md alone. This remains one
  of the least de-risked phases; PD does not change that.
- **Render order**: terrain solid → cutout-mipped → cutout; entities via Forge
  render-pass interop (free on a Forge-lineage loader — verify the Cleanroom equivalent);
  depth copy → shadowtex1 (**water-shadow split**, before shadow translucents); optional
  translucent terrain (`shadowTranslucent` flag — this phase's slice of the Phase 3
  flag-ownership map, plus clouds-in-shadow per config); per-config mipmap generation on
  shadow textures (Phase 5 texture config).
- **Hardware PCF** (compare-mode setup on shadow depth textures, `shadowHardwareFiltering`).
- **Vanilla blob-shadow suppression** while a shadow pass exists.
- **Uniform wiring**: shadow matrices (`shadowProjection`/`shadowModelView` + inverses)
  captured during shadow-camera setup into Phase 6's slots; `shadowAngle`;
  shadowtex/shadowcolor sampler activation per App B.3. **REV1:** FF-stack shadow
  matrices are demonstrated on 1.12.2 (`glMatrixMode`/`glPushMatrix`/`glLoadMatrix`
  swaps during the pass, PD §6.2) — the compat-cooperation pattern to follow.
- The force-shadow-program rule during the pass (Phase 4's barrier override — you define
  when the pass begins/ends; the barrier enforces it).
- Additional hook sites needed beyond Phase 7's catalog (e.g. blob-shadow suppression,
  `setupTerrain` reuse for shadow traversal) — specified in the same App E format,
  flagged as Phase 8 additions.

**Scope — out:** shadow FBO structure (Phase 5); barrier mechanics (Phase 4);
shadowcomp passes (G8/S1).

**Required inputs:**
- RESEARCH.md §4.5 (whole), App A.3 (shadow directives), App B.2/B.3, App D.3 (shadow
  rows), App E rows 1–2 (renderWorldPass/setupTerrain context).
- **PD §10 (shadow pass — math reference only).** (§G11 rules.)
- `schlorbium-project/doc/shaders.txt` — shadow section.
- `schlorbium-project/SHADER_ENGINE_IMPL.md` — shadow-pass section only (§G7 rules).
- MCP: `search_cleanroom_api("render pass entity", kind=event)` for the Forge
  render-pass interop equivalent.
- `PHASE_4_DOC.md`–`PHASE_7_DOC.md`.

**Architecture requirements:** contract-visible via shadow uniforms/buffers (§4.8): the
celestial camera math, texel snap, and depth-split moments must match observed behavior —
packs compute shadow-space positions from these matrices, so any deviation shows as
misplaced shadows in T1/T2.

**Doc gate:** camera math written out (matrices, snapping formula); traversal algorithm
specified; every shadow-related App A.3/B.2/D.3 row in the conformance map; added hook
sites in App E format. **REV1:** camera/snap math cross-checked against the PD §10
reference; the absence of a traversal reference acknowledged in the risk notes.

**Impl gate:** RESEARCH.md §9 v0.2 — classic packs with shadows at T1; first T2 runs.

**Context budget:** ≈ 44k tokens mandatory reading.

---

### Phase 9 — ID aliasing & per-draw dynamics

**Milestone:** v0.3 · **Depends on:** 3, 6, 7 · **OQs:** —

**REV1 changes:** the dual-spec tier gains two proven mechanisms (the modern→1.12 alias
table and the `MC_VERSION`→11300 re-parse trick); resolution-semantics references land;
the held-item and `entityColor` gaps are confirmed as reference-free.

**Objective.** The identity layer: resolving pack-facing stable IDs against live Forge
registries, merging mod-provided extensions, and driving the per-draw id/held-item
uniforms. Runs *before* Phase 10 because alias resolution is upstream of `mc_Entity`
vertex stamping.

**Deliverable.** `PHASE_9_DOC.md` per §G9.

**Scope — in:**
- **Alias resolution** (§3.7): Phase 3's parsed grammar → resolved lookup tables against
  Forge block/item/entity registries: short names (`red_flower`), namespaced
  (`minecraft:red_flower`), property-matched forms, legacy id:meta. Reload on
  registry/resource changes. **REV1 — resolution-semantics reference** (PD §8.1):
  metadata ids, property predicates, tag expansion, still↔flowing fluid aliases,
  **first-writer-wins precedence** (`putIfAbsent`, order-significant), and OF-legacy
  numeric fallback when no `block.properties` exists — all working in production.
- **Dual-spec mechanisms (REV1, adopt):**
  1. **The modern→1.12 name alias table** — `grass_block→grass`, `short_grass→tallgrass`,
     `tall_grass→double_plant`, `dead_bush→deadbush`, `sugar_cane→reeds`,
     `lily_pad→waterlily`, `cobweb→web`, `redstone_lamp→lit_redstone_lamp`, plus a
     special case for modern packs' `minecraft:grass` (PD §8.1,
     `VintageBlockMaterialMapping`). Current BSL/Complementary/Sildur's releases need
     this. Adopt the mechanism; re-derive and extend the values against live 1.12.2
     registries (reuse structure, re-derive values — §G11.5).
  2. **The entity.properties era-bridge:** if parsing under the ≤1.12 rules yields an
     empty map, **re-parse with `MC_VERSION` forced to 11300** so modern packs' named
     entity ranges still load (PD §8.1). Small, proven — adopt.
- **Per-mod merge**: `assets/<modid>/shaders/{block,item,entity}.properties` from every
  loaded mod jar, merged with pack-provided files (define precedence: pack wins over mod,
  document it) — the mod-facing extension point RESEARCH.md flags as must-preserve.
- **Custom render layers** (`layer.solid/cutout/cutout_mipped/translucent=<blocks>`;
  solid-opaque cubes excluded) — resolution here; the terrain dispatch that honors them is
  Phase 7's (flag the cross-reference in doc §5). **REV1:** Pintonium parses `layer.*`
  but consumption is TODO there (PD §8) — our split stands; no dispatch reference.
- **Held-item tracking**: `heldItemId`/`heldItemId2` (main/off hand),
  `heldBlockLightValue`/`2` including the `oldHandLight` brighter-hand-wins mode and
  `dynamicHandLight` (this phase's slice of the flag-ownership map). **REV1:** **no
  reference exists** — Pintonium's 1.12.2 held-item uniforms are empty stubs
  (`VintageIdMapUniforms` TODO, PD §8.1). Design from App D alone.
- **Per-draw dynamics**: `entityId` (per entity, via the RenderManager hook),
  `blockEntityId` (per TE, via TileEntityRendererDispatcher), `entityColor` (hurt/flash
  tint) — value computation + the Phase 6 per-draw upload path; hook-site specs in App E
  format (rows 13–14) if Phase 7 catalogued but did not specify them. **REV1 warnings**
  (PD §8.2): `blockEntityId` stamping has no 1.12.2 caller in the reference (uniform
  constant 0); and 1.12's hurt-flash is a **fixed-function TexEnv effect invisible to
  bound GLSL programs** — `entityColor` delivery must be real (computed at the entity
  hook and pushed through the per-draw path), never observed from FF state. This is a
  root cause of Pintonium's "entities brighter" bug (PD §19.2).
- The **alias-lookup service** interface Phase 10 consumes for `mc_Entity` stamping
  (`renderType<<16 | aliasedBlockId`, metadata) — the entity-data *stack* itself is
  Phase 10's; the id computation is yours.

**Scope — out:** vertex stamping and the chunk-build stack (Phase 10); uniform upload
mechanics (Phase 6); grammar parsing (Phase 3).

**Required inputs:**
- RESEARCH.md §3.7, §4.7 (aliases), App D.1/D.4, App E rows 13–14.
- **PD §8 (whole).** Pintonium `common-shaders/.../shaderpack/IdMap.java` +
  `forge122/src/shaders/java/net/irisshaders/iris/shaderpack/materialmap/VintageBlockMaterialMapping.java`
  (LGPL — §G11 rules; **REV2:** full path — the forge122 class lives in the *Iris*
  package under the shaders source set, not an `org.taumc.celeritas` one).
- `schlorbium-project/doc/shaders.txt` — ID-mapping section.
- Skim: `cleanroom-src/src/main/java/net/minecraftforge/` registry API
  (`registries/`, `fml/common/registry/`) — resolution targets.
- MCP: `resolve_symbol` for registry classes as needed.
- `PHASE_3_DOC.md` (grammar model), `PHASE_6_DOC.md` (per-draw upload), `PHASE_7_DOC.md`
  (hook catalog state).

**Architecture requirements:** resolution tables are rebuilt, never mutated in place
(reload safety); unknown names degrade per G2.4 (warn once, id absent) — packs tolerate
missing ids, they do not tolerate crashes.

**Doc gate:** every §3.7 form + the per-mod merge + precedence rules in the conformance
map; alias-service interface fully specified for Phase 10. **REV1:** the dual-spec alias
table mechanism and the 11300 re-parse trick appear as designed features with PD
provenance; the held-item/`entityColor` reference-free status acknowledged.

**Impl gate:** headless tests resolve fixture properties files against a scripted
registry; in-game, `heldItemId`/`entityId` uniforms verified on the Phase 2 scene set
(v0.3 harness runs).

**Context budget:** ≈ 36k tokens mandatory reading.

---

### Phase 10 — Extended vertex pipeline

**Milestone:** v0.3 · **Depends on:** 4, 7, 9 · **OQs:** OQ-5, OQ-14

**REV1 changes:** a constant-attribute delivery candidate for entity/TE ids (contract-
checked decision); numeric per-quad math and thread-safety references land; OQ-5 gains
concrete detection anchors (Pintonium is the canonical adversary); OQ-14 gets a "no
evidence" note; `at_midBlock` gains a working precedent.

**Objective.** The 56-byte extended vertex format and everything that feeds it: the
vertex-builder side channel, per-quad attribute math, the chunk-build entity stack, and
attribute delivery in both draw paths. The most invasive subsystem — and the one that
owns the chunk-renderer coexistence policy.

**Deliverable.** `PHASE_10_DOC.md` per §G9.

**Scope — in:**
- **The format** (App C.1): vanilla 28-byte block vertex → 56 bytes / 14 ints;
  `mc_midTexCoord` (2×float @ 32), `at_tangent` (4×short @ 40), `mc_Entity` (shorts @ 48);
  attribute locations 10/11/12 (Phase 4 pre-binds them).
- **Population rules** (App C.2): `mc_Entity` = current top of the chunk-build entity
  stack (values from Phase 9's alias service), stamped into every vertex at write time;
  per quad (every 4 vertices): face normal = normalize((v2−v0)×(v3−v1)), UV-delta
  tangent/bitangent with handedness `w = sign(dot(bitangent, normal×tangent))`,
  `mc_midTexCoord` = average of the quad's UVs. **REV1 — numeric reference:** Pintonium's
  `ChunkVertexExtendedData`/`QuadUtil` implement exactly these formulas (tangent via the
  UV-edge method with handedness in w; midTexCoord as quad UV average ×32768 as u16 in
  their packing) — cite them in the worked example (PD §9; §G11.5 "reuse structure,
  re-derive values").
- **The vertex-builder side channel**: attached to every buffer builder; stamping +
  per-quad computation; interaction with `begin`/`endVertex`/`addVertexData` (App E row 5).
- **Chunk-build entity push/pop** around per-block model rendering (App E rows 3–4, 9):
  block state pushes `(renderType<<16 | aliasedBlockId, metadata)`; async chunk-build
  coordination (ChunkRenderDispatcher worker threads — thread-safety of the stack is
  yours to design, doc §7). **REV1:** Pintonium's extended data rides a **thread-local
  side channel** safe across chunk-build workers (PD §9) — the proven pattern for the
  thread-safety design. (Note theirs records sequential ranges rather than a push/pop
  stack; our contract needs the stack — App E rows 3–4, 9.)
- **Both draw paths** (App E rows 6–8): VBO (`VertexBuffer.bufferData`/`drawArrays`) and
  client-array (`WorldVertexBufferUploader.draw`, `Tessellator.draw`) — attribute
  pointers stride 56 at offsets 32/40/48, arrays 10–12 enabled only around shader-active
  draws.
- **Constant-attribute delivery candidate (REV1 — decision recorded, §G11.4):** Pintonium
  delivers entity ids **without touching the vertex format**:
  `GL30.glVertexAttribI3i(11, shaderEntityId, 0, 0)` sets `mc_Entity` as a generic
  vertex-attribute *constant* for the draw (verified at
  `forge122/src/main/java/org/taumc/celeritas/mixin/core/terrain/RenderGlobalMixin.java:459`
  — **REV2 re-verified exact**; the literal 11 is the
  `celeritas$IRIS_ENTITY_ATTRIBUTE_INDEX` constant declared at l. 72, the zero-reset
  companion call sits at l. 454, and the file is in the *core* source set, not the
  shaders one; PD §8.2). On a compat context, any attribute whose array is disabled reads the
  constant — so entity/TE draws can carry ids with zero vertex-format intrusion,
  reserving the 56-byte extended format for terrain where per-vertex data is actually
  needed. **Mandatory contract check** against App C/D's delivery semantics (does OF
  deliver per-vertex for entities? does any pack observable differ?), weighed against
  OF's exact mechanism; record the decision (`D-P10-k`). If adopted: the id value still
  comes from Phase 9's alias service, the `entityId` uniform fallback stays for
  non-attribute draws, and Phase 4's pre-bind numbering is unaffected.
- **Format swap lifecycle**: extended format swapped in/out on pack toggle; world-renderer
  reload; **OQ-14 spike spec** (Forge baked-quad/LightUtil cache interplay under
  Cleanroom — procedure: toggle formats in a dev world with Forge lighting pipeline
  on/off, observe cache corruption; fallback: force-invalidate caches on swap / pin the
  Forge lighting path). **REV1:** no evidence from Pintonium — it sidesteps the question
  by owning the mesh path (PD §9); the spike stands unchanged.
- **Per-program attribute enablement** from Phase 3's scan (a program declaring
  `mc_Entity` opts in; others get pointers disabled).
- **Growth design** (§7.4, App C.3): attribute slots addressable by name; layout growable
  without touching every consumer; `at_midBlock` as the canonical first addition
  (post-v0.5 tag — design the slot, don't wire it). **REV1:** `at_midBlock` (plus block
  light) is already implemented in Pintonium's chunk formats (PD §9) — G8/S4's canonical
  growth item now has a free, working math reference.
- **OQ-5 coexistence policy**: detect replaced chunk pipelines → **detect-and-bail with a
  clear user message** as the v0.3 policy (RESEARCH.md §7.4 `[A]`), via Phase 1's bail
  registry; spike spec for detection mechanics (classloading probes vs mod-id checks);
  integration is explicitly future work (G8). **REV1 — concrete detection anchors**
  (PD §9): Pintonium *is* the canonical adversary — it `@Overwrite`s
  `RenderGlobal.renderBlockLayer`/`setupTerrain` and zeroes vanilla render distance.
  Anchors: mod ids `embeddium` / `celeritas_shaders`; root packages
  `org.embeddedt.embeddium`, `org.taumc.celeritas`; presence of
  `CeleritasWorldRenderer`; Vintagium/Nothirium share the lineage. The spike's dev test:
  run Schmaloogium alongside Pintonium and verify detection fires.
- Hook-site specs (App E format) for rows 3–9 — Phase 7 catalogued them as your
  consumers; you specify them.

**Scope — out:** alias id computation (Phase 9); `_n`/`_s` atlases (Phase 13 — the
tangent frame you compute is what makes them useful, note the dependency); compile-time
attribute binding (Phase 4).

**Required inputs:**
- RESEARCH.md §4.6 (vertex part), §7.4, App C (whole), App E rows 3–9.
- **PD §8.2 (constant-attribute delivery), §9 (vertex-pipeline strategy contrast +
  math).** (§G11 rules.)
- `schlorbium-project/SHADER_ENGINE_IMPL.md` — vertex-pipeline section only (§G7 rules).
- `cleanroom-src/patches/minecraft/net/minecraft/client/renderer/BufferBuilder.java.patch`
  (+ `chunk/` patches if present) — Cleanroom's own deltas to your target classes.
- MCP: `resolve_symbol`/`get_method_signature` for App E rows 3–9 validation.
- `PHASE_4_DOC.md` (attribute binding), `PHASE_7_DOC.md` (hook catalog conventions),
  `PHASE_9_DOC.md` (alias service).

**Architecture requirements:** per-quad math is contract-visible (packs read the tangent
frame); specify it numerically (formulas + worked example). The side channel must be
zero-cost when shaders are off (G2.4 rule 5). Thread-safety across chunk-build workers is
a first-class design concern, not a footnote.

**Doc gate:** App C fully mapped; both draw paths specified; both spike specs complete
with fallbacks; growth design shows `at_midBlock` addable without consumer edits;
coexistence policy + message text drafted. **REV1:** the constant-attribute decision
recorded with its contract check; the OQ-5 detection table includes the Pintonium
anchors; per-quad formulas cross-checked against the PD §9 numeric reference.

**Impl gate:** RESEARCH.md §9 v0.3 — classic packs at T2 within tolerance on terrain
scenes.

**Context budget:** ≈ 51k tokens mandatory reading + MCP traffic.

---

### Phase 11 — Custom-uniform expression engine

**Milestone:** v0.4 · **Depends on:** 3, 6 (no Phase 7 dependency — parallel-friendly) · **OQs:** —

**REV1 changes:** an evaluator-architecture reference lands (license-gated); the App F.6
function set gains a concrete checklist.

**Objective.** The complete `uniform.<type>.<name>=<expr>` / `variable.<type>.<name>`
expression language: grammar, functions, input binding, evaluation cadence, and an
evaluator architecture chosen for per-frame cost. Pure `:engine` code.

**Deliverable.** `PHASE_11_DOC.md` per §G9.

**Scope — in** (App F.6 is the contract):
- **Grammar**: numeric literals, `pi true false`, operators
  (`+ - * / %`, `! && ||`, comparisons), member access (vector `.x/.y/.z`, color
  `.r/.g/.b`, matrix `name.<row>.<col>`), the full function set (`sin cos asin acos tan
  atan atan2 torad todeg min max clamp abs floor ceil exp frac log pow random round
  signum sqrt fmod`), conditional `if(cond,val,…,val_else)`, **`smooth([id,]val[,fadeIn
  [,fadeOut]])`** with per-id persistent state (time-corrected exponential, default 1s),
  boolean helpers `between/equals/in`, constructors `vec2/vec3/vec4`.
- **Input binding**: every fixed scalar built-in uniform (Phase 6's value model), biome
  params (`biome`, `temperature`, `rainfall`, `BIOME_*` constants), view-entity booleans
  (`is_alive` … `is_wet`); the **per-draw exclusion rule** (`entityColor entityId
  blockEntityId fogMode fogColor` are not expression inputs — App F.6).
- **`variable.*` intermediates**: evaluated once per update, referenced by uniforms, not
  uploaded.
- **Typing**: declared type (`float/int/bool/vec2/3/4`) vs expression result — coercion
  and mismatch rules.
- **Cadence**: evaluated on every program switch, after built-ins (Phase 6's extension
  point); `smooth()` state keyed by id, surviving across switches, time-corrected.
- **Evaluator architecture**: parsed-AST interpreter vs compiled (MethodHandle/bytecode,
  §6.3) — design the interface so both fit, specify the decision criteria (per-switch
  cost budget across a real pack's uniform count), recommend the v0.4 choice (interpreter
  first unless evidence says otherwise — G2.5), note the compiled path as an OQ-22 ledger
  item for Phase 14's methodology. **REV1 — architecture reference (license-gated):**
  Pintonium's vendored `stareval` + `IrisFunctions` implement the full pipeline — parse,
  topological sort, cycle detection, dead-uniform elimination
  (`CustomUniforms.optimise()`), evaluation on program switch after built-ins (PD §14).
  The AST→evaluator-with-resolver-indirection shape matches the interpreter-first plan;
  `IrisFunctions` is a concrete App F.6 function-set checklist. **License gate
  (§G11.2 rule 3):** stareval's upstream repo no longer resolves (historically
  MIT-credited) — verify before any reuse; if unverifiable, clean-room implement from
  App F.6, which we own regardless.
- **Error isolation**: parse errors disable that uniform at load (chat-visible warning);
  runtime errors disable that uniform only (ladder step 1); division-by-zero/NaN
  propagation semantics defined.
- Biome/view-entity value-provider interfaces (implemented by `mod.glue`, headless-tested
  with scripts).

**Scope — out:** where values come from (Phase 6 providers); the properties-file capture
of declarations (Phase 3); GUI display of profiles referencing options (Phase 12).

**Required inputs:**
- RESEARCH.md §3.4 (item 4), App F.6, §6.3 (expression row).
- **PD §14 (expression-engine paragraphs).** (§G11 rules.)
- `schlorbium-project/doc/shaders.properties` — the custom-uniform/variable section
  (the annotated expression-language reference).
- `PHASE_3_DOC.md` (declaration capture), `PHASE_6_DOC.md` (value model, extension point).

**Architecture requirements:** pure `engine.expr`; contract-visible function semantics
(packs ship expressions tuned against OF's evaluator — match documented behavior,
including `smooth()`'s time correction); headless test vectors are the primary
verification (golden expression → value tables).

**Doc gate:** every App F.6 token/function/operator in the conformance map; `smooth()`
state machine specified; evaluator interface + decision criteria written; error semantics
per the ladder. **REV1:** the stareval license-verification outcome recorded (verify →
reuse-with-notice, or unverifiable → clean-room), with the `IrisFunctions` checklist
cross-referenced either way.

**Impl gate:** headless golden-vector suite passes; matrix packs' custom uniforms parse
and evaluate against scripted providers (Phase 2 golden runs extended).

**Context budget:** ≈ 29k tokens mandatory reading — the lightest phase; keep it tight.

---

### Phase 12 — Options GUI, persistence & reload

**Milestone:** v0.4 · **Depends on:** 1, 3 (soft: 7 — reload-lifecycle section only) · **OQs:** OQ-9

**REV1 changes:** the OQ-9 fallback is de-risked to near-zero (a working vanilla-GuiScreen
shader GUI exists in Pintonium); persistence gains a working round-trip reference.

**Objective.** The user-facing surface: pack selection, generated option screens,
profiles, sliders, persistence round-trip, and the reload paths — on ModularUI if the
spike sustains it.

**Deliverable.** `PHASE_12_DOC.md` per §G9.

**Scope — in:**
- **Pack-selection screen**: pack list (folder/zip, sentinels), current selection,
  engine-settings entries (Schmaloogium's equivalents of OF's 8: normal/specular map
  toggles, render/shadow quality multipliers, hand depth, old hand light, old lighting —
  AA/AF explicitly do not exist per §1.2).
- **Generated option screens** from Phase 3's model (App F.4): `screen=`/`screen.NAME=`
  with `[SUBSCREEN]`, `<profile>`, `<empty>`, `*` (unplaced options), `columns=N`
  (default 2, auto-widen past 18); sliders (`sliders=` list); profile inference from
  current option values ("Custom" otherwise) and click-to-cycle; tooltips from lang files
  (split on ". ", trailing "!" renders red); value prefix/suffix decoration.
- **Persistence round-trip**: only changed options to `shaderpacks/<pack>.txt`; global
  engine settings file; write-through on change; Phase 3 owns formats — you own when/how
  they're read/written from the GUI. **REV1 — working reference:** Pintonium's
  changed-options `<pack>.txt` (ISO-8859-1 Properties) merged through a queue on reload +
  global config alongside (PD §14) — round-trip proven on 1.12.2.
- **Reload paths**: F3+R keybind, `/reloadShaders` command, resource-reload integration —
  each mapped to the correct lifecycle (full pack reload vs option re-apply vs
  world-renderer reload), coordinated with the Phase 7 lifecycle (soft dependency: if
  `PHASE_7_DOC.md` is absent, design against Phase 7's spec here and flag the assumption
  in doc §5). **REV1:** consume Phase 4's version-counter invalidation (PD §3.1 analog)
  as the reload-safety mechanism; flag the consumption in doc §5.
- **OQ-9 spike spec**: ModularUI fitness — procedure: prototype a generated screen with
  a slider, a subscreen, and profile cycling from a real pack's `screen.*` config;
  success: all three bind cleanly; failure fallback: vanilla-GuiScreen-based minimal UI
  (uglier, zero-dependency) — design the screen *model* UI-framework-agnostically so the
  fallback swaps the view layer only. **REV1 — fallback de-risked to near-zero:** the
  fallback already exists and works on 1.12.2 — Pintonium's
  `VintageShaderPackSelectionScreen` + `VintageShaderPackOptionsScreen` are hand-written
  vanilla `GuiScreen`s with pack list, scrolling option screens, sub-screen navigation,
  profile cycling, lang-file tooltips with `en_us` fallback, and name prettification
  (PD §14). Use them as the screen-model binding reference either way; the LGPL code may
  inform the fallback view directly (§G11 rules). The spike now purely judges ModularUI
  upside.
- GUI-side error surfacing (per-program compile errors, capability-gate messages — G4.5
  channels).

**Scope — out:** option semantics/parsing (Phase 3); what reload does internally
(Phases 4–7); ModularUI licensing note (Phase 1 / G7).

**Required inputs:**
- RESEARCH.md §4.7 (options/GUI), §7.6, App F.3/F.4.
- **PD §14 (GUI + persistence paragraphs).** Pintonium
  `forge122/src/shaders/java/net/irisshaders/iris/gui/VintageShaderPackSelectionScreen.java` +
  `VintageShaderPackOptionsScreen.java` (LGPL — §G11 rules; **REV2:** full path — Iris
  package under the forge122 shaders source set).
- MCP: `search_mod_examples(query="ModularUI", category=gui)` — real usage patterns.
- `PHASE_1_DOC.md` (module layout, ModularUI dependency mechanics), `PHASE_3_DOC.md`
  (options/screens/profiles model, persistence formats).

**Architecture requirements:** `mod.gui` only; the screen *model* (tree of options/
subscreens/profiles with bindings) lives engine-side and is headless-testable; the
ModularUI (or fallback) view is a thin adapter — this is the OQ-9 hedge.

**Doc gate:** every App F.3/F.4 construct in the conformance map (incl. `*`, `<empty>`,
red-"!", auto-widen); reload-path × lifecycle matrix complete; OQ-9 spike + fallback
designed. **REV1:** the fallback references the existing Pintonium screens (no longer a
from-scratch design); slider handling is noted as having **no** reference (Pintonium's
`sliders=` is functionally dead, PD §7.4).

**Impl gate:** RESEARCH.md §9 v0.4 — options round-trip persistence; classic matrix at
T2/T3 (jointly with Phase 11).

**Context budget:** ≈ 34k tokens mandatory reading + MCP examples.

---

### Phase 13 — Texture systems

**Milestone:** v0.5 · **Depends on:** 3, 5, 7 · **OQs:** —

**REV1 changes:** the custom-texture model gains an end-to-end validated checklist; the
noise-RNG divergence is flagged; companion atlases are confirmed reference-free; the
filter/wrap suffix gap is added to the do-not-inherit list.

**Objective.** The texture estate beyond the FBOs: normal/specular companion atlases,
the noise texture, pack custom textures in all three source forms, and the `atlasSize`
uniform.

**Deliverable.** `PHASE_13_DOC.md` per §G9.

**Scope — in:**
- **`_n`/`_s` companion atlases** (§4.6): every atlas sprite may have `_n`/`_s`
  companions; full companion atlases with matching mip chains; missing sprites →
  flat-normal `0xFF7F7FFF` / zero-specular defaults; bound on units 2/3 during world
  rendering (App B.3); stitch/load hooks on `TextureMap`/`TextureAtlasSprite` (App E rows
  10–11 — hook-site specs in the Phase 7 format); `MC_NORMAL_MAP`/`MC_SPECULAR_MAP`
  macro wiring back into Phase 3's identity set. **REV1:** **no atlas reference
  exists** — Pintonium's `PBRTextureManager` keys per *bound texture id*, not an atlas
  stitch (PD §11); it solves neither sprite-animation sync nor per-sprite companions.
  Design from §4.6/App E rows 10–11 alone. (The macro wiring *does* have a reference —
  PD §7.6's `MC_NORMAL_MAP`/`MC_SPECULAR_MAP` rows.)
- **Noise texture**: `noiseTextureResolution²` RGB, xorshift-generated (specify the
  generator so it's reproducible), unit 15, `texture.noise=<path>` override. **REV1
  divergence flagged:** Pintonium uses `java.util.Random(0)`-seeded bytes (PD §11) — a
  pre-decided rejection (§G11.4) *unless* the App/observed behavior shows packs depend
  on OF's exact noise values, in which case the contract question is reopened through
  the G0.1 conflict rule, never silently.
- **Custom textures** (App F.5, model from Phase 3): all three source forms —
  pack-relative PNG; `minecraft:` asset locations (incl. `dynamic/lightmap_1`, atlas
  paths, `_n`/`_s` variant selection); raw binary
  (`<path> <target> <internalFormat> <dims…> <pixelFormat> <pixelType>`, 1D/2D/3D/RECT);
  `.mcmeta` blur/clamp sidecars; per-stage binding (gbuffers → gbuffers+shadow programs;
  deferred; composite → composite+final); multiple texture types per unit disambiguated
  by sampler type (one type per unit per program); lifecycle across pack/resource
  reloads. **REV1 — validated checklist:** all of the above works in production in the
  reference (PD §11), incl. auto-generated `customtexN` names patched into programs and
  override-of-colortex disambiguation. **Do not inherit:** the filter/wrap suffix gap —
  `texture.<stage>.<sampler>` filter/wrap suffixes are stripped and ignored there
  (PD §7.4); ours must honor them (conformance row).
- **`atlasSize`** uniform (set while the atlas is bound — value source here, upload via
  Phase 6). **REV1:** no reference — Pintonium's atlasSize notifier is a no-op TODO
  (PD §11); design from App D alone.
- Sprite-animation interaction (companions must animate with their base sprite — design
  the tick hookup).

**Scope — out:** unit-map ownership (Phases 5/6); tangent math that consumes the normals
(Phase 10); labPBR semantics (pack-side convention — engine-neutral, G8 advertises it).

**Required inputs:**
- RESEARCH.md §4.6 (texture part), App B.3 (units 2/3/15), App E rows 10–11, App F.5.
- **PD §11 (textures, whole), §7.6 (macro rows).** (§G11 rules.)
- `schlorbium-project/SHADER_ENGINE_IMPL.md` — texture-system section only (§G7 rules).
- MCP: `resolve_symbol` for TextureMap/TextureAtlasSprite/stitch-event symbols;
  `search_cleanroom_api("texture stitch", kind=event)` (prefer the Forge stitch events
  over mixins where fidelity allows).
- `PHASE_3_DOC.md` (custom-texture model, macro set), `PHASE_5_DOC.md` (unit binding
  tables), `PHASE_7_DOC.md` (hook conventions).

**Architecture requirements:** companion loading must not regress vanilla atlas stitching
when shaders are off (G2.4 rule 5); memory posture documented (two extra full atlases is
the accepted cost, §4.8 Keep).

**Doc gate:** all three custom-texture forms + `.mcmeta` + stage mapping in the
conformance map; companion-atlas lifecycle (load/stitch/reload/animate) specified;
hook sites in App E format. **REV1:** filter/wrap suffix handling present (vs PD §7.4
gap); noise-generator decision recorded with provenance.

**Impl gate:** RESEARCH.md §9 v0.5 (jointly with Phase 14) — full classic matrix at T3;
packs using `MC_NORMAL_MAP` render correctly on the fixed scenes.

**Context budget:** ≈ 39k tokens mandatory reading.

---

### Phase 14 — GL modernization & performance

**Milestone:** v0.5 + quality-of-life · **Depends on:** 5, 6, 7, 13 · **OQs:** OQ-15, OQ-22

**REV1 changes:** the PBO async center-depth item becomes conditional on Phase 6's
decision; sampler-object, DSA-tiering, and KHR_debug designs gain deployed references;
the OQ-22 ledger gains concrete evidence rows.

**Objective.** The §6.2/§4.8 "Adapt" set as concrete designs: replace the reference's
legacy per-frame costs with modern-GL equivalents inside our own pipeline — the only
performance work the mission permits (§1.2, G2.5).

**Deliverable.** `PHASE_14_DOC.md` per §G9.

**Scope — in:**
- **GL 3.3 sampler objects** per stage, replacing per-frame re-parameterization of flip
  textures (filter state decoupled from texture objects; mapping onto Phase 5's estate).
  **REV1 — deployed reference:** `GlSampler` with per-unit bind caches and GL 4.5
  `glBindSamplers` batching, in production on the 1.12.2 compat context (PD §15).
- **DSA tiering (REV1 — new scope row):** adopt PD §15's pattern — GL 4.5 `DSACore` →
  `DSAARB` → bind-to-edit fallback, chosen at init — as the facade's internal
  object-creation strategy wherever it stays behavior-invisible (G4.2).
- **PBO + fence-sync async center-depth readback** — **conditional** (REV1): if Phase 6
  adopted GPU-side smoothing (PD §6.3), this item is **obviated** — record an OQ-22
  ledger note instead and drop the work item. If Phase 6 kept the sync readback, the
  original design stands: one-frame latency on an already-smoothed value;
  imperceptibility verification (compare `centerDepthSmooth` traces sync vs async on the
  Phase 2 scenes); the sync path kept as fallback/config. **No reference exists either
  way** — Pintonium has no PBO/async readback anywhere (PD §15).
- **Shared-context async shader compile** + async `_n`/`_s` atlas upload (**OQ-15 spike
  spec**: GLFW shared compat contexts across drivers — procedure: prototype on ≥2 driver
  families, measure pack-switch stall; success: no corruption + stall < threshold;
  **mandatory synchronous fallback designed regardless of outcome**, selected at runtime
  per driver).
- **KHR_debug** labels/groups + debug-context dev mode (pairs with RenderBook's Nsight
  workflow, §6.2); integration with Phase 1's debug flags. **REV1:** copy the **pattern**
  (object labels + per-phase push/pop groups behind a debug flag, PD §15), not the
  wiring — Pintonium's `setPhase` has a push/pop imbalance bug (PD B7).
- **Allocation/GC posture** + measurement methodology: how the implementation effort
  validates that clean-code-first holds (allocation profiling on the Phase 2 scenes;
  criteria for when an optimization is justified) — the **OQ-22 spot-check ledger**:
  each §6.2/§6.3 `[U]` claim this phase relies on gets a row (claim → cheap experiment →
  decision point). **REV1 evidence rows available** (PD §15): DSA tiers, sampler objects,
  and compute/SSBO/image load-store/indirect dispatch all run pack-exercised on the
  1.12.2 compat context — the strongest available evidence for the corresponding §6.2
  claims (and for G8/S2 feasibility).
- **Redundant-state audit** methodology: identifying per-frame GL churn in our own
  pipeline (never vanilla's — §1.2).

**Scope — out:** any vanilla-pipeline optimization (non-goal, §1.2); chunk pipelines
(non-goal); the sync designs being replaced (Phases 5/6 own them; you supersede with
their docs as input).

**Required inputs:**
- RESEARCH.md §6.2, §6.3, §4.8 (Adapt rows), §11 (OQ-15/OQ-22 rows).
- **PD §15 (GL modernization, whole), §6.3 (centerDepthSmooth conditionality).** (§G11
  rules.)
- `PHASE_5_DOC.md`, `PHASE_6_DOC.md`, `PHASE_7_DOC.md`, `PHASE_13_DOC.md`.

**Architecture requirements:** every modernization is a strict behavioral no-op from the
pack's perspective (contract-visible behavior unchanged — G4.2); each ships with a
fallback to the reference-faithful path; facade extensions (new GL entry points) are
additive.

**Doc gate:** each Adapt row → design + fallback + ledger entry; OQ-15 spike complete;
imperceptibility test for async readback specified. **REV1:** the PBO item's
conditional status resolved against Phase 6's recorded decision; DSA tiering integrated
as a facade-internal strategy row.

**Impl gate:** RESEARCH.md §9 v0.5 (jointly with Phase 13) — full classic matrix at T3;
pack-switch stall measurably reduced vs the synchronous baseline without T1 regressions.

**Context budget:** ≈ 34k tokens mandatory reading.

---

## Appendix: coverage cross-checks

*For the document maintainer, not phase sessions.*

**RESEARCH.md §9 milestone scope → phase mapping:**

| §9 scope item | Phase |
|---|---|
| gbuffers + composite + final; program registry w/ backup chains | 4, 7 |
| preprocessor + macros + includes; source-scan resource sizing | 3 |
| main FBO ping-pong + flips + clears | 5 |
| built-in uniforms (no shadow set); fixed unit map | 6 |
| options parsing (no GUI); internal default pack | 3; 7 |
| Shadow pass (all components) | 8 |
| Extended vertex format + per-quad attributes + entity-data stack | 10 |
| block/item/entity aliases + per-mod merge; per-entity/TE id uniforms | 9 |
| Custom uniforms/variables expression engine | 11 |
| profiles/screens/sliders model; options GUI; persistence | 3 (model); 12 (GUI) |
| per-program alphaTest/blend/scale/flip overrides | 3 (parse), 4 (apply), 7 (execute) |
| `_n`/`_s` atlases + noise + custom textures | 13 |
| depth copies incl. async center-depth; render scale; instancing | 7 (arch, v0.5 tags); 6→14 (center-depth — REV1: conditional on P6's GPU-smoothing decision) |
| conformance harness | 2 |
| post-v0.5 row | G8 |

**Dropped-item audit** (items with no obvious home, verified placed): wetness/eyeBrightness/
centerDepth smoothing → P6; dimension-switch lifecycle → P3 (discovery) + P7 (lifecycle);
graceful degradation → G2.4 + every doc §6; geometry-shader dual-form → P4; version gate →
P3; blob-shadow suppression + clouds-in-shadow → P8; `blendFunc`/GlStateManager → P6 + G4.6;
composite guarantee/anaglyph/overlay-drawbuffers-none/push-pop → P7; `layer.*` → P9 (resolve)
+ P7 (dispatch); debug source dump → P3; engine-flag ownership → P3 map, wired by owners;
Kirino/upstream engagement → G8 + P1 seam.

**REV1 additions audit** (every Pintonium-sourced change, verified placed):

| PD finding | Placed in |
|---|---|
| Rules of engagement; licensing carve-outs; repo traps; trust tiers; reading map | G11; G7 item 7; G0.1a; G0.2 |
| jcpp preprocessor + hoisting/line-preservation techniques | P3 |
| B1/B2/B3/B12 pitfalls as conformance rows | P3 (doc gate), G11.4 |
| ProgramFallbackResolver backup-chain model; Program.use() barrier; Pass state bundle; attribute-location warning; version-counter invalidation | P4 |
| Framebuffer depth-texture swap (depthtex0); flip/clear semantics + fog-alpha-1.0; frame-end reconciliation decision; tiered depth copy; resize checklist; shadow-flip stub warning; 16-colortex anti-pattern | P5 |
| Cadence buckets; frame-begin ordering; FF-matrix capture; centerDepthSmooth GPU candidate; smoothing closed forms; blendFunc notifier audit; sampler dedup mechanics | P6 |
| 7-row injection timeline; clear-ordinal-0 anchor; deferred-trigger cross-validation; composite-at-TAIL + stricter guarantee; sky/weather/clouds reference-free; OQ-3/OQ-4 annotations | P7 |
| Shadow camera/snap math portable; traversal absent | P8 |
| Modern→1.12 alias table; MC_VERSION→11300 re-parse; first-writer-wins; held-item/entityColor gaps | P9 |
| Constant-attribute candidate; per-quad math reference; thread-local pattern; OQ-5 anchors; OQ-14 no-evidence; at_midBlock precedent | P10 |
| stareval architecture (license gate); IrisFunctions checklist | P11 |
| OQ-9 fallback built; persistence round-trip reference; sliders no-reference | P12 |
| Custom-texture checklist; noise divergence; companion-atlas gap; filter/wrap gap; atlasSize gap | P13 |
| PBO conditionality; sampler objects; DSA tiers; KHR_debug pattern-not-wiring; OQ-22 evidence | P14 |
| Motion scenes; reference-gap scene calibration | P2, G6 |
| Seam-inventory checklist; bootstrap sequence; mixin plugin option; OQ-20/OQ-21 evidence | P1 |
| G8/S1 shadow-flip warning; S2 feasibility upgrade; S3 observations; S4 at_midBlock reference | G8 |
| Integration-review seams (macro-header injection point; notifier table; version counter) | G5.3 item 4 |

**REV2 changes audit** (every RC1→RC2 change, verified placed; each is `**REV2**`-marked
at its site except the front-matter/table items, which are self-describing):

| REV2 change | Placed in |
|---|---|
| Front matter: RC2 identity; supersedes v1.1 + RC1; P1/P2 verification state; REV1→REV2 what-changed | header block |
| Revision-highlights table (RC1 → RC2) | after header |
| Adoption procedure (line pins, operator docs, phase-doc migration, relabel rule) | G0.4 (new) |
| reference-src alias table; RESEARCH §12.1 conflict report; PD-location correction | G0.2 |
| Phase-doc / review / brief locations; version-roll convention; harness + briefs-doc pointers | G1.1 step 3; G1.2 step 3 |
| P2 §11.5 item 3 resolution recorded (its `artifacts/` proposal superseded by the reorg) | G1.3 provenance note |
| Do-not-modify list extended to all three design revisions | G1.1 hard rules |
| Forbidden-sources rule (chatlogs pattern, root `*.txt`) | G1.1 hard rules |
| Rung 2a — feature-level GL failure (P1 §11.5 item 4, `[V7-7]`) with five-rung grandfathering | G2.4 |
| Backend-count correction (3 platform + 2 shared; PD §2's `:babric` absent) | G3.1 note; G10 OQ-20 row; P1 scope + arch requirements |
| Derived-artifacts clause — `[D-P2-5]`/`[D-P2-6]`, manifests-not-images, `-PupdateGoldens` (P2 §11.5 item 4) | G6 |
| License swap recorded as executed (`aa917a6`); residual scope narrowed | G7 item 1; P1 scope |
| Kirino = empty uninitialized submodule | G8/S5 |
| Repo traps re-verified: deleted tracked zip; VintageFix leftovers; stale v1.1 copy | G11.3 items 1–3 |
| §4.1 probe-set / §3.5 extension-set citation split (P1 §11.5 item 3) | P1 scope (facade bullet) |
| Template ground truth: pin at `build.gradle:60`; `java-templates`/`resource-templates`; JUnit flag + no `src/test/` | P1 scope |
| `ProgramCreator.java:21-25` re-verified at the line (range unchanged) + double-bound-11 binding detail | P4 compile/link |
| `VintageBlockMaterialMapping` full Iris-package path | P9 required inputs |
| `RenderGlobalMixin` full path (core source set) + `:72`/`:454` companion facts; `:459` re-verified exact | P10 constant-attribute |
| Vintage GUI screens full Iris-package path | P12 required inputs |

**REV3:** additions audit (every RC2→RC3 change, verified placed; each site carries a
literal `**REV3:**` marker):

| **REV3:** Addition/correction | Placed in |
|---|---|
| RC3 identity and own path; candidate-overall status; RC2 governs Phase 1 from §0.11 onward with thirteen reviews; v1.1 governs Phase 2 | **REV3:** header/source/status block |
| Revision highlights (RC2 → RC3) matched to the actual additions | **REV3:** top highlights table |
| Oculus shorthand resolves into `reference-src/`; OD location and no-hook boundary | **REV3:** G0.2 alias row |
| Stale all-v1.1 prose corrected; literal mapping counts corrected to 12 for v1.1 and 13 for RC2; no RC3 pins derived | **REV3:** G0.4 |
| Part I boundary extended to G12; do-not-modify list extended to RC3 and OD | **REV3:** G1.1 |
| Oculus LGPL/sub-license boundary; unresolved taumc prohibition; stareval clean-room outcome; inactive JCPP warning | **REV3:** G7 item 8 |
| Stareval architecture restricted to clean-room shape; sliders answered; held-item, `blockEntityId`, and companion-atlas policy partial; exact six unanswered gaps retained | **REV3:** G11.5 |
| What/why and the loader-independent-only, zero-hook provenance boundary | **REV3:** G12.1 |
| Licensing, verbatim hard blocklist, JOML/digraph carve-outs, taumc/stareval/JCPP outcomes | **REV3:** G12.2 |
| Declared-target, closed-tree, modern-hook, JCPP, JOML, and analogy traps | **REV3:** G12.3 |
| Evidence/adoption rules; exact six dropped findings; exact pack vocabulary; all five RESEARCH conflicts and dispositions, including failed PB07/TX01 quotes | **REV3:** G12.4 |
| Exact four-tier trust vocabulary with answered/partial/unanswered boundary | **REV3:** G12.5 |
| Non-governing phase→OD reading map covering all fourteen phases | **REV3:** G12.6 |
| Closing additions audit and RC3 end marker | **REV3:** appendix close |

*End of design document (**REV3:** Oculus Revision, v2.0-RC3).*
