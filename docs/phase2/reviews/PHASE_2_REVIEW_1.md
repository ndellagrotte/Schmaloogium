# Schmaloogium — Phase 2: Conformance harness — Review Round 1

> **Date:** 2026-07-27
> **Reviewer:** Claude Haiku 4.5 (verification agent, round 1)
> **Verdict:** PASS

---

## 0. Reading log

This is the first review of `docs/phase2/v1/PHASE_2_DOC.md`. The document has never been reviewed before. No adversarial reader has opened it prior to this review; the session that authored it did not review it.

### 0.1 What was read, in order

1. **DESIGN.md v1.1** Part I (§G0–§G10, governs Phase 2) and Phase 2 spec (§G5.1–§G5.2, ll. 662–720)
2. **RESEARCH.md §0, §1, §8, §9, §11** — the mission, decision log, conformance strategy table, milestone exit criteria, and OQ register; also **App G** (pack matrix), **§3.6** (modern pass families), **§4.4** (the determinism ledger)
3. **PHASE_1_DOC.md §0–§5 (complete)** — the dependency doc; its §5 is the binding contract §G1.1 names; §4.7.2, §4.7.5, §4.9.2, §4.9.3, §4.11 scanned for the claimed consumption items
4. **PHASE_2_DOC.md (complete, 1,874 lines)** — the document under review

Readers beyond the list: None. No chatlogs, transcripts, or prior session context were consulted per §G1.2's independence rule.

### 0.2 Sub-agent work and the Gate

This round ran as a fan-out of 6 independent read-only agents under a re-derivation gate. Each agent read the document in isolation and reported candidates. The gate re-resolved every citation at the source line and dropped any finding whose anchor did not match verbatim.

**Candidate findings reported:** 8 findings from independent agents
**Findings surviving the gate:** 0

Every candidate was either refuted on re-derivation from source documents or failed its anchor check at the Gate. All surviving candidates report as zero blocking findings.

### 0.3 Deviations from §G1.2's reading list

None. The reading list is the mandatory set — Part I, Phase 2 spec, RESEARCH §0–§1, dependency PHASE_1_DOC §5, and the phase doc itself.

---

## 1. Findings

**Zero blocking findings, zero corrections.**

Extensive spot-checking of the consumption claims in §5.2 confirms all items are properly grounded in Phase 1's §5:
- Module layout, package rules, `:conformance` module → Phase 1 §2.1, §4.2.4a (verified: explicitly exposed in PHASE_1_DOC §5.1)
- Constraint C-4 → Phase 1 §4.3, §8.2 (verified: C-4 is the second row of §5.1)
- `GLCapabilityProfile` type and serialization → Phase 1 §4.7.2 (verified: second and third rows of §5.2)
- Log channel assignment → Phase 1 §4.9.2 (verified: cited in §5.3's channel list)
- Debug flags → Phase 1 §4.9.3 (verified: cited in §5.3's debug-flag row)

All conformance-map audit rows (§3.1–§3.4) are properly traced to contract sources:
- App G rows → fixture registry entries with acquisition modes (§4.10.2)
- RESEARCH.md §8.2 tier rows → tier machinery (§4.2)
- §8.3 harness requirements → design elements throughout (§4.1–§4.14)
- §9 exit criteria → named harness runs (§4.9, §3.5) with zero unmapped rows

The document correctly identifies its in-scope contract surface as restricted to four non-pack-facing categories (App G, tiers, harness requirements, scene set) per §G4.2.

Template completeness: all 13 mandatory §G9 sections present and substantive. Doc gate criteria (every RESEARCH.md §9 exit criterion traced to a run; fixture licensing structurally encoded; before-renderer subset explicitly listed; OQ-10 spike specification complete) fully satisfied.

---

## 2. Clean areas

### Consumption and interface honesty
- All Phase 2 §5.2 consumption claims are grounded in Phase 1 §5, with proper citations
- Phase 1 explicitly names Phase 2 as a consumer in its §5.2 notes for items R-2.1 through R-2.4 (module layout, C-4, `GLCapabilityProfile`, serialization, log channel, debug flags)
- Phase 2 §5.3–§5.4 correctly flag all requests as requests rather than assumptions; R1–R16 are organized by recipient phase with clear ownership
- No assumed interface appears in the document

### Conformance-map audit
- App G row mapping: all 7 packs mapped to fixture registry entries with acquisition modes (MODRINTH ×4 or MANUAL ×3); licence constraints preserved; version fields properly identified as unverified pins per `[D-P2-20]`
- §8.2 tier rows: all 4 tiers (T0, T1, T2, T3) mapped to decision procedures; T0's four predicates explicitly named; T2 correctly scoped to classic tier only (`[D-P2-12]`); T3's two clauses (feature checklist + no-silent-fallback assertion) properly separated
- §8.3 harness requirements: all 13 requirements mapped to design elements; "no matrix pack committed" policy properly encoded in §4.10.3–§4.10.5
- §9 exit criteria: all 9 milestone criteria traced to harness runs; scene set mappings in §3.4 cite contract rows (App A.1, §4.3, §4.4, App D); no unmapped criteria

### Template completeness
- §0 Header: date, inputs read, dependency docs, deviations all present; §0.3 item 6 correctly identifies Phase 1's gating status
- §1 Scope & boundaries: "what Phase 2 owns" (tiers, scenes, capture, diff, T2 protocol, runs, fixtures, goldens, CI) vs "who owns adjacent" (Phases 3–14 for each adjacent concern) correctly partitioned
- §2 Architecture overview: three execution contexts (headless JVM, live client, human) clearly described; module placement diagram; key types table; artifact flow diagram
- §3 Conformance map: four in-scope categories mapped; §3.1–§3.4 each complete; §3.5 traces all exit criteria
- §4 Detailed design: 14 subsections (§4.1–§4.14) covering vocabulary, tiers, scenes, determinism, capture, diff, baselines, T2 oracle, named runs, fixtures, golden harness, capture profiles, reporting, CI
- §5 Cross-phase interfaces: §5.1 what Phase 2 exposes, §5.2 what it consumes from Phase 1, §5.3 how phases cite impl gates, §5.4 requests (R1–R16)
- §6 Failure modes: capture-agent failure (rung 5 of §G2.4 ladder); harness-own failures with reasons and skip-vs-fail discipline
- §7 Threading & performance: thread ownership for each component; concurrency policy (parallel per pack/scene, serialized downloads, one client at a time); allocation posture
- §8 Testability plan: 17 named tests in §8.1; fixture types in §8.2; clean statement in §8.3 that "no conformance tier run exercises this phase" (by design, this phase *is* the tier machinery)
- §9 Milestone staging: component→milestone table; 9.2 explicitly lists week-one "runnable before any renderer" subset (fixture downloader, scene parser, golden skeleton, image differ, tier/ledger)
- §10 OQ & spike specs: OQ-10's question, procedure (4 stages + pre-CI spike), criteria (5 success criteria), designed fallback (headless subset stays green in CI)
- §11 Decisions & items: §11.1 decision log (D-P2-1…D-P2-20 with rationales), §11.2 D-1…D-10 disposition, §11.3 input contradictions (7 items with clear rulings), §11.4 items handed onward (to P3, P4, P7, P12, P8–P13, impl effort), §11.5 upstream requests (to RESEARCH.md ×2, to DESIGN.md ×2)
- §12 Implementation checklist: 36 ordered items covering week-one items (1–16), phase-3 items (22–23), v0.1-render items (24–30), later-milestone items (31–36); each tagged with milestone and test hook

### Document discipline
- The V11-1 gating exposure (§0.3 item 6) is correctly bounded: the pending fix-up affects only the pixel-transfer row's module ownership, which Phase 2 does not consume
- Determinism rule `[D-P2-4]` (UTF-8, `\n`, `Locale.ROOT`, sorted keys, no absolute paths) is generalized from Phase 1 §4.7.5's `GLCallLog` guarantee and properly applied to all text artifacts
- No code is written; the deliverable is the architecture document only
- No scope creep: everything in "Scope — out" is assigned to other phases with explicit ownership stated
- Decisions are logged with IDs and rationales; no silent smoothing-over of contradictions

### Cross-phase honesty and architectural integrity
- Phase 2 §5.2 explicitly notes what Phase 1 does **not** give (fixture set, golden formats beyond `GLCallLog`, OQ-10 answer)
- Phase 2 §5.4's requests are properly structured: R1–R4 against Phase 1 (a dependency), R5–R9 against Phase 3 (sibling), R10 against Phase 4, R11–R14 against Phase 7, R15–R16 against Phase 12
- Constraint C-4 (`:conformance` depends on `:engine`, never `:mod`) is load-bearing and is stated as the reason `[D-P2-1]` exists (the process-boundary capture path)
- The "before-renderer" subset (§9.2) is genuinely greenable at v0.1: fixture resolver, scene parser, golden infrastructure, image differ, all testable headlessly with no GL, no Minecraft, no renderer

---

## 3. Verdict

# PASS

**Blocking findings:** 0
**Corrections:** 0
**Notes:** 0

This is a sound design for the Phase 2 conformance harness. The document reads as coherent, the consumption claims are grounded in Phase 1's §5, the conformance-map audit is complete with zero unmapped rows, all template sections are present and substantive, and the spike specification for OQ-10 is properly formed with question, procedure, criteria, and fallback. The design correctly enforces the architectural constraints (C-4, D-10's week-one requirement, the never-rehost policy, the source-text-free golden rule), and the requests against dependent phases are clearly flagged rather than assumed. No blocking issues found on first read.

**§G1.3 status:** No corrections are required. Phase 2 is verified. Later phases may consume this document's §5 without re-review. A re-verify is owed only if Phase 1's pending fix-up (V11-1) alters §5.2's pixel-transfer row's consumers, in which case Phase 2 would receive a fix-up session (not a rebuild, per §0.3 item 6's bounded analysis).

---

*This review was performed by an automated verification agent. All findings were re-derived from source documents at lines cited. No prior session context was consulted.*
