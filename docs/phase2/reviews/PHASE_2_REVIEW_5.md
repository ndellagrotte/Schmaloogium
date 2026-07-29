# Schmaloogium — Phase 2: Conformance harness — Review Round 5

## 0. Method and reading order

I independently re-derived all four gated candidates before reading any prior review. The reading
order was:

1. `docs/design/v1.1/DESIGN.md` Part I, the Phase 2 target specification, document gate, and
   mandatory template.
2. `docs/research/v1/RESEARCH.md`, including §9 and Appendix G.
3. The manifest-selected binding dependency, `docs/phase1/v14/PHASE_1_DOC.md`, especially §5.
4. The whole selected target, `docs/phase2/v1/PHASE_2_DOC.md`.
5. Only after settling every candidate, `docs/phase2/reviews/PHASE_2_REVIEW_1.md` through
   `docs/phase2/reviews/PHASE_2_REVIEW_4.md`, in round order.

There were no reading-list deviations and no network use. This already-dispatched atomic
adjudication role started no subagents or other agent fan-out and did not invoke the verification
harness. The canonical engine supplied the finder, refuter, steelman, and Gate material. The Gate
dropped no candidates, and no candidates were eliminated before adjudication. Forbidden sources
were not read.

The prior reviews do not settle the present defects. Round 3 established the versioned schemas and
round 4 added T0 evidence and baseline identities, but neither resolution defines the omitted pack
scalars or the admitted filesystem-entry domain of the new world identity. Earlier clean-area
statements about the conformance map do not address Appendix G's explicit timing conflict with §9.

## 1. Findings

### candidate-001 — World identity leaves non-regular filesystem entries undefined

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md` §§4.5.1, 4.5.4–4.5.5, 4.7.4, and 5.1.
- **Claim:** The canonical world hash uniquely and deterministically identifies every copied world
  used for baseline approval and invalidation.
- **Evidence:** The lifecycle recursively copies the cached world into the run directory and the
  client loads that copy (`docs/phase2/v1/PHASE_2_DOC.md:609–619`). The canonical digest includes
  only regular files yet is declared the sole identity used for approval and invalidation
  (`docs/phase2/v1/PHASE_2_DOC.md:724–729`). The world-cache section relies on hashing to turn world
  changes into comparisons (`docs/phase2/v1/PHASE_2_DOC.md:747–752`), and invalidation compares
  `environment.worldSha256` directly (`docs/phase2/v1/PHASE_2_DOC.md:887–892`). No target rule
  rejects symbolic links or other non-regular entries, says whether traversal follows links, or
  defines their copy and identity treatment. Distinct client-consumable trees can therefore share
  the sole identity, and copy behavior can vary by implementation.
- **Disposition:** Admitted. Define one canonical copy-and-hash domain, preferably allowing only
  directories and regular files and rejecting every other entry without following links before
  copying or hashing. Add focused identity tests for links and representative unsupported entries,
  including containment behavior.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

Candidate-003 is dropped as an exact substantive duplicate of this finding. Its additional
containment emphasis and test suggestions are incorporated above; it is not counted separately.

### candidate-002 — Required run-manifest pack scalar keys and domains are undefined

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md` §4.5.4 and §5.1.
- **Claim:** A consumer can implement the exposed `schmaloogium.run-manifest/1` schema without
  guessing field names or value domains.
- **Evidence:** The semantic table requires pack id, version key, acquisition mode, archive
  SHA-512, licence line, and options (`docs/phase2/v1/PHASE_2_DOC.md:686–697`). The normative schema
  then enumerates run/environment scalars and repeated `pack.options`, but never enumerates the
  non-repeated pack fields (`docs/phase2/v1/PHASE_2_DOC.md:699–719`). Deriving a “lower-camel dotted
  name” from prose does not settle exact names, scalar types, archive-digest encoding, or the
  acquisition-mode value set. Section 5 exports this wire schema to Phases 4 and 7
  (`docs/phase2/v1/PHASE_2_DOC.md:1320–1324`).
- **Disposition:** Admitted. Enumerate every non-repeated pack scalar's exact dotted key, scalar
  type, required or conditional status, constrained encoding, and the complete acquisition-mode
  enum. Align the canonical fixture and §5 reference.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

### candidate-004 — Dual-spec packs' through-v0.5 target is absent from the milestone map

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md` §§3.1, 3.5, 11.3, and 12.
- **Claim:** The conformance map covers every in-scope Appendix G requirement and reports
  authoritative-source conflicts rather than silently resolving them.
- **Evidence:** Appendix G makes the four dual-spec packs T0/T1 targets through v0.5
  (`docs/research/v1/RESEARCH.md:1538–1541`), while §9 places progression for current dual-spec
  releases post-v0.5 (`docs/research/v1/RESEARCH.md:949–951`). The target preserves the pack
  classifications but not Appendix G's timing (`docs/phase2/v1/PHASE_2_DOC.md:287–290`), and its
  milestone map supplies dual-spec T0/T1 runs only under post-v0.5
  (`docs/phase2/v1/PHASE_2_DOC.md:357–372`). Its dedicated contradictions section does not report
  this tension (`docs/phase2/v1/PHASE_2_DOC.md:1808–1815`). An implementer following the map would
  therefore silently adopt one side of conflicting source text.
- **Disposition:** Admitted. Extend the existing Appendix G mapping with the through-v0.5 T0/T1
  requirement, report its conflict with §9 in §11.3, and state a clearly provisional execution
  schedule pending upstream clarification. Align the milestone/run and implementation staging
  without changing §5 unless an exported contract is actually altered.
- **Severity:** correction
- **touches interface/change-trigger region: no**

## 2. Checked and clean

The round-4 availability flags, artifact-only T0 derivation, runner-synthesized failure manifests,
baseline propagation, and mod-set identity framing remain internally coherent. Phase 2's
consumption of Phase 1's module seam, `GLCapabilityProfile` serialization, recording/replay
facilities, fixture placement, debug flags, diagnostics, logging channel, and CI extension point
continues to align with the binding dependency contract. The capture-plan schema and the
Phase-3/Phase-4 golden-enrichment gate also remain consistent.

The seven Appendix G pack rows, T0–T3 semantics, fixed scene families, §8.3 harness requirements,
fixture licensing structure, and literal §9 exit-criterion rows were otherwise mapped with
substantive support. Candidate-003 was cleared only as a duplicate of admitted candidate-001, not
because its underlying defect failed re-derivation. Candidates 001, 002, and 004 survived.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

All three distinct defects are bounded fix-up work rather than structural misses requiring a
rebuild. Candidates 001 and 002 affect the exposed run-manifest/baseline contract; candidate-004 is
an internal conformance-map and conflict-disclosure correction.

The loop has not converged: round 4 had two corrections and changed the interface, and this fresh
review finds three corrections on that revised surface, two interface-affecting. The next required
action is a scoped fix-up resolving all three admitted findings and recording their resolutions in
this review. Because the cross-phase interface/change-trigger region must change, a fresh verify
round is required before Phase 2 can close.

## Resolutions

### candidate-001 — resolved

Re-derived from the copied-world lifecycle and sole baseline identity, the missing entry-domain
rule was real. Sections 4.5.1 and 4.5.4 now define one fail-closed domain: traversal never follows
links, only directories and regular files are admitted, unsupported entries fail before copy/hash,
and the digest is taken from the admitted copy. `BaselineIdentityTest` covers internal and escaping
links plus FIFO, socket, and device representatives. Section 5.1's exported schema reference
therefore inherits a deterministic copy-and-hash contract.

### candidate-002 — resolved

Section 4.5.4 now enumerates the five required non-repeated `pack` keys, their JSON-string type,
non-empty constraints, the complete `MODRINTH|MANUAL` acquisition enum, and the lowercase
128-hex-digit SHA-512 encoding. The existing canonical full-block fixture and §5.1 schema reference
remain the normative fixture and exported entry point; no second schema description was created.

### candidate-004 — resolved

Appendix G and §9 genuinely conflict on timing. Sections 3.5, 9.1, 11.3, 11.5, and 12 now preserve
both claims: dual-spec T0/T1 runs provisionally gate each v0.1–v0.5 release, modern-stage family
coverage stays post-v0.5, and an upstream reconciliation is requested. Section 5 was not changed
for this correction because it does not alter the named-run or tier contract.

### Notes deferred

None.

### §G1.3 status

All three admitted corrections are applied. The cross-phase interface region changed, so Phase 2
requires a fresh verify round and is not yet verified.
