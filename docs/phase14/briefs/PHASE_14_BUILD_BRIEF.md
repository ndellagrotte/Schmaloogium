# Phase 14 build-session brief — commissioning record

**Session type:** §G1.1 build session (`docs/design/v3/DESIGN.md:232`–`:301`)
**Phase:** 14 — GL modernization & performance (`docs/design/v3/DESIGN.md:2514`–`:2588`)
**Governing design revision:** `docs/design/v3/DESIGN.md`
**Deliverable:** `docs/phase14/v1/PHASE_14_DOC.md` per §G9 (`docs/design/v3/DESIGN.md:817`–`:854`)
**Commissioned:** 2026-08-08
**Convention:** `docs/phase<N>/briefs/` per §G1.2 REV2 (`docs/design/v3/DESIGN.md:339`); the
existing example is `docs/phase1/briefs/`.

This file exists because the session it commissions departs from the §G5.3 gating invariant on
explicit maintainer instruction. It is recorded here so the §G1.2 verify session reads a
disclosed, authorized departure rather than an undisclosed violation.

---

## 1. Governing-revision selection

Phase 14 has never been built: there is no `docs/phase14/` in git history, and `docs/MOVES.md`
carries no Phase 13 or Phase 14 rows. This is therefore an **initial build against v3**, not a
§G0.4 re-pointing of an already-built phase, and §G0.4's four-step adoption procedure
(`docs/design/v3/DESIGN.md:195`–`:220`) does not apply.

Precedent for an initial v3 build: `docs/phase11/v1/PHASE_11_DOC.md` §0 and
`docs/phase2/v2/PHASE_2_DOC.md` §0.36 both declare `docs/design/v3/DESIGN.md`.

Per `AGENTS.md` §"Six different files are named `DESIGN.md`", the build session derives its own
line pins from v3's own headings and declares them in doc §0. It never transplants coordinates
from another revision.

---

## 2. Dependency state at commissioning

Phase 14 depends on 5, 6, 7, 13 (`docs/design/v3/DESIGN.md:626`). §G5.3 invariant 1
(`docs/design/v3/DESIGN.md:659`–`:663`) requires each to be **verified** per the §G1.3 definition
(`docs/design/v3/DESIGN.md:357`–`:359`) before a dependent build session reads it.

| Dep | Latest review | Verdict | Interface changed | Verified per §G1.3 |
|---|---|---|---|---|
| Phase 5 | `docs/phase5/reviews/PHASE_5_REVIEW_38.md` | PASS | no | **yes** |
| Phase 6 | `docs/phase6/reviews/PHASE_6_REVIEW_24.md` | PASS | no | **yes** |
| Phase 7 | `docs/phase7/reviews/PHASE_7_REVIEW_32.md` | PASS-WITH-CORRECTIONS | **yes** | **no** — round 33 owed |
| Phase 13 | — | — | — | **no — the phase is unbuilt** |

- **Phase 7.** Review 32 recorded `Counts: blocking=0; corrections=7; notes=3` and
  `Interface changed: yes`. Its `## Resolutions` section is complete for what was ordered, but
  §G1.3's "Re-verify only if §5 changed" clause is engaged. `docs/phase7/v1/PHASE_7_DOC.md` says
  so itself: *"v1 remains unverified pending a fresh whole-document review."*
- **Phase 13.** `docs/phase13/` does not exist. There is no `PHASE_13_DOC.md` to read at all —
  this is an unbuilt phase, not merely an unverified one.

---

## 3. Authorized departure

**Maintainer instruction, 2026-08-08:** build Phase 14 now; do not wait for Phase 7's round 33 or
for Phase 13 to be built; flag every relevant deviation in the phase doc.

Scope of the authorization, stated precisely so the verify session can bound it:

1. The build session **may read `docs/phase7/v1/PHASE_7_DOC.md` while it is unverified.**
   Everything consumed from its §5 is provisional and is re-checkable if round 33 changes it.
2. The build session **may proceed without `PHASE_13_DOC.md`.** It does *not* thereby gain
   permission to invent Phase 13's interfaces: §G1.1's "Dependency docs are contracts" rule
   (`docs/design/v3/DESIGN.md:296`–`:298`) still binds, so every Phase-13-sourced item is
   recorded in doc §5 as a **request** against a spec-derived assumption, never as an existing
   interface.

Nothing else in §G1.1's hard rules is relaxed. No code. No self-review. No scope creep. Context
discipline. Forbidden sources unchanged.

---

## 4. Doc-vs-doc contradiction the session must rule on

`docs/design/v3/DESIGN.md:647` places P13 and P14 in the same Wave 5, built in parallel, while
`:626` makes P13 a hard dependency of P14 and `:628`–`:632` states that "Depends on" is literal
and requires verified docs. The only sanctioned soft dependency in the design is Phase 12's on
Phase 7 (`docs/design/v3/DESIGN.md:668`–`:671`); no such exception exists for Phase 14.

Per §G1.1's input-contradictions rule (`docs/design/v3/DESIGN.md:282`–`:284`) this is reported in
doc §3/§11 with a ruling and its provenance, never silently smoothed over.

---

## 5. Already-resolved condition — do not re-litigate

The Phase 14 spec makes the PBO async center-depth item **conditional** on Phase 6's recorded
`centerDepthSmooth` decision (`docs/design/v3/DESIGN.md:2536`–`:2542`).

Phase 6 recorded **`D-P6-1`: select synchronous CPU `centerDepthSmooth`; return empty macro
contribution** — an explicit contract-visible *rejection* of PD §6.3's GPU-side smoothing
(`docs/phase6/v1/PHASE_6_DOC.md:1678`, decision text at `:965`, conformance-map rejection row at
`:468`).

Therefore the PBO + fence-sync async readback item is **not obviated**. The original design
stands in full, including the imperceptibility verification and the retained synchronous
fallback.

---

## 6. Execution-surface note

The mechanized verification loop was retired 2026-08-08 (commit `e173848`). There is no
`verification/targets/phase-14.json`; the directory is deleted. `docs/MOVES.md` states that a
phase doc's own §0 declaration is the single source of truth for its governing revision, and
`docs/tooling/CODEX_MIGRATION_OVERLAY.md` is the sanctioned interpreter for the immutable design
revisions' provider-era wording. §G1.2 and §G1.3 are hand-run fresh agent sessions per
`AGENTS.md` §"Running §G1.2/§G1.3".

---

## 7. What remains owed after this session

- `docs/phase14/reviews/PHASE_14_REVIEW_1.md` — a §G1.2 verify session, fresh and blind to this
  build session's context.
- `docs/phase7/reviews/PHASE_7_REVIEW_33.md` — the round Phase 7 already owed before this
  session began.
- Phase 13 — build, then verify. Phase 14's §5 Phase-13 requests are the input list for it.
