# Recommendation: Template Branch for Schmaloogium — mixin vs. scala vs. kotlin

> **Status:** 2026-07-25.
> **Inputs:** `README.md` (CleanroomModTemplate, §"Choose Branch"), `DESIGN.md` (v1.1),
> `RESEARCH.md` (§5.1, §6), and the verified `PHASE_1_DOC.md` (§4.5, §4.4.1).
> **Question:** which of the template's three non-main branches — `mixin`, `scala`, or
> `kotlin` — should Schmaloogium's mod development be based on?

---

## 1. Recommendation (TL;DR)

**Use the mixin branch's feature set: Java + Mixin.** Concretely for this repository:

- **Language: Java 25.** Already pinned in the toolchain (`java.toolchain.languageVersion
  = 25`, `build.gradle`) and in the verified `PHASE_1_DOC.md`. Not open for revision.
- **Hook mechanism: Mixin.** Mandatory under DESIGN.md decision **D-5** ("Mixin-based
  hooks only; no class replacement; ~25–30 targeted injections"). Not optional.
- **No branch switch is needed.** This checkout is on the template's `main` branch, which
  carries no mixin config, no `MixinConfigs` manifest attribute, and no `mixin { }` block
  — and the verified `PHASE_1_DOC.md` (§4.5) already specifies adding exactly that wiring
  (three config JSONs, one per CleanMix phase; `MixinConfigs` manifest attribute;
  `is_coremod=false`; SRG targeting; refmap duties). The mixin branch's substance is
  already designed in. The recommendation matters for the record and for anyone
  re-templating from scratch: start from `mixin`, not from `scala` or `kotlin`.

**Scala and Kotlin are rejected** — not because they are bad languages, but because they
answer a question this project has already settled (language = Java), while failing the
one question that is actually load-bearing (mature, verified Mixin tooling on the custom
Unimined fork).

---

## 2. What the three options actually are (README.md)

The README lists four branches (`main`, `mixin`, `scala`, `kotlin`) with the guidance:
"Choose mixin branch if you want to use Mixin. Use scala and kotlin branch if you want to
use those languages."

| Branch | What it adds over `main` | Kind of option |
|---|---|---|
| `mixin` | Mixin support: per-phase config JSONs (`PRE_INIT`/`DEFAULT`/`MOD`), Unimined-handled refmap, `IMixinConfigPlugin` hook | **Capability** (hook mechanism) |
| `scala` | Scala language compilation | **Language** |
| `kotlin` | Kotlin language compilation (community-contributed, credited to @ghostflyby) | **Language** |

The asymmetry matters: `mixin` answers "how do we hook vanilla rendering?", while
`scala`/`kotlin` answer "what language do we write in?". The template ships them as
mutually exclusive branches, so picking a language branch *and* getting Mixin would mean
merging branches by hand — an unverified, undocumented path on a fork the README itself
flags as unstable.

---

## 3. What Schmaloogium requires (DESIGN.md)

The choice is constrained by settled, binding decisions — it is not a free preference:

| Requirement | Source | Consequence |
|---|---|---|
| **D-5:** Mixin-based hooks only; ~25–30 targeted injections into the render loop | DESIGN.md §G2.1 | Mixin tooling is non-negotiable; every hook in Phases 7/8/10/13 rides on it |
| **D-6 + §G3.1:** `:engine` pure JVM, **Java 25**, zero MC/Forge/Mixin/LWJGL deps; `:mod` contains the `mod.mixin` package | DESIGN.md §G3.1 | The module layout names Java explicitly; mixins are a first-class, permanent package |
| **Java 25 toolchain pin**, re-verified deliberately, never floated | DESIGN.md §G2.2; PHASE_1_DOC.md §4.2 | Toolchain novelty is a measured risk, not a free choice |
| **Modern Java language features planned**: records/sealed types for option & uniform models, text blocks for embedded GLSL | RESEARCH.md §6 | The design already spends Java 25's feature budget |
| **Mixin wiring already designed**: `MixinConfigs` manifest attribute (current canon; MixinBooter interfaces deprecated), 3 config JSONs, SRG targeting, refmap under Unimined | DESIGN.md Phase 1 spec; PHASE_1_DOC.md §4.5 (`D-P1-9`…`D-P1-11`) | The mixin path is verified design work; a language switch would invalidate parts of it |
| **Mixins stay dumb**: observe state, delegate to engine, no policy | DESIGN.md §G3.3 | Mixin classes are thin, mechanical shims — the worst case for language-specific bytecode surprises, and the least rewarding place for language expressiveness |

---

## 4. Comparison

| Criterion | mixin (Java + Mixin) | scala | kotlin |
|---|---|---|---|
| Satisfies D-5 (Mixin hooks) | **Yes — out of the box** | Only via unverified manual merge with mixin wiring | Only via unverified manual merge with mixin wiring |
| Fits pinned Java 25 toolchain | **Yes — is the pinned toolchain** | No — adds Scala compiler + stdlib to a Java-25 pin | No — adds Kotlin compiler + stdlib to a Java-25 pin |
| Toolchain risk on the custom Unimined fork | Lowest; the documented path | **README's own warning names "impossible Scala compiler errors"** as a known failure mode | Community branch; less exercised than main/mixin |
| Mixin-from-the-language hazards | None — Mixin is Java-bytecode-native | Synthetic/bridge methods, trait encoding, boxing — all alien to Mixin's annotation processor and SRG remapping | Null-check intrinsics in hot render paths, synthetic accessors, `DefaultImpls`, metadata the remapper ignores |
| Refmap / SRG remapping (1.12.2) | Documented: "Don't worry about refmap, Unimined will handle it" | Remap tooling is Java-symbol-centric; Scala names (operators, mangled identifiers) are a known friction class | Kotlin metadata and inline/reified constructs are outside the Java remapping model |
| Runtime footprint | Baseline | Scala stdlib (~several MB) shipped or via the Scalar provider mod | Kotlin stdlib shipped in jar |
| Fit with project docs & AI-agent workflow | **Exact** — DESIGN.md, PHASE_1_DOC.md, MCP `cleanroom` recipes, and all examples are Java | Every doc example translates; agent sessions carry an extra mapping tax | Same translation tax, smaller than Scala's |
| Cost to adopt *now* | Zero — already designed into the verified Phase 1 doc | Reopens verified design work; adds risk for zero requirement gain | Same |
| Expressiveness payoff | Java 25 records/sealed/text blocks already budgeted (RESEARCH.md §6) | High in general; **near zero here** — mixins must stay dumb (§G3.3) and `:engine` must stay pure Java | Same reasoning |

---

## 5. Per-option assessment

### 5.1 mixin — **Recommended**

It is the only option that directly serves a binding requirement (D-5). The README
describes a complete, maintained path: one config JSON per CleanMix phase, automatic
refmap, `IMixinConfigPlugin` for conditional application. The verified Phase 1 doc studied
this branch's canon and adopted it (`MixinConfigs` manifest over deprecated MixinBooter
interfaces; three phase-split configs; `is_coremod=false` even though the mixin branch
ships `true`, because Cleanroom's built-in CleanMix makes the coremod vestigial and a
coremod would brush against D-5's "no class replacement"). Choosing this option costs
nothing and risks the least.

### 5.2 scala — **Rejected**

- The README itself warns of "impossible Scala compiler errors" on the custom Unimined
  fork — the only branch the template documentation singles out with a known-failure
  callout.
- Mixin's annotation processing and the 1.12.2 SRG-refmap machinery assume Java-shaped
  bytecode. Scala's trait encoding, synthetic methods, and name mangling sit squarely in
  the "impossible field names" failure class the README warns about — on the ~25–30
  render-loop injections that are the project's spine, that is the worst possible place
  for toolchain novelty.
- The payoff is illusory here: mixins must stay dumb shims (§G3.3), and the expressive
  core (`:engine`) is contract-bound to pure, dependency-free Java 25.
- Adopting it would contradict the pinned toolchain and invalidate verified Phase 1 design
  work.

### 5.3 kotlin — **Rejected (for this project)**

Kotlin is the more credible of the two language branches (good Java interop, active MC
ecosystem), and in a greenfield mod with no settled design it would be a defensible
choice. It still fails this project's tests:

- It is a language option, and the language question is already settled (Java 25, pinned
  and verified).
- Mixin-from-Kotlin carries real hazards on a remapped 1.12.2 toolchain: intrinsic
  null-checks injected into render-hot mixin handlers, synthetic accessors,
  `DefaultImpls` stubs, and Kotlin metadata the SRG remapper does not model. Each is
  individually survivable; collectively they are unpaid risk on the D-5 hook catalog.
- Like Scala, it requires hand-merging language support with mixin wiring on a fork the
  README flags as unstable — unverified, undocumented, and buying nothing the
  requirements ask for.

---

## 6. Decision and consequences

1. **Develop the mod in Java 25 with Mixin** — the mixin branch's feature set, as already
   embodied in the verified `PHASE_1_DOC.md` on this `main`-based checkout.
2. No Scala or Kotlin source roots in `:engine`, `:mod`, or `:conformance`.
3. Mixin classes stay dumb Java shims per DESIGN.md §G3.3; all policy lives in `:engine`.
4. This document is advisory context for the owner; where it touches anything a phase doc
   owns (PHASE_1_DOC.md §4.5), the phase doc remains normative.

## 7. Revisit triggers

Re-open this recommendation only if:

- the owner explicitly overturns the Java 25 pin (a D-1..D-10-level decision, never a
  session-local one — DESIGN.md §G1.1), or
- the mixin wiring itself proves unworkable on the platform (in which case the fallback
  is still Java — hand-wired Mixin per PHASE_1_DOC.md — not a language switch), or
- upstream Cleanroom ships first-class Kotlin/Scala Mixin tooling with refmap support,
  and a migration is costed against the then-existing codebase.
