# Phase 3 verification review — round 46

## 0. Method and reading order

I independently re-derived every surviving candidate before consulting historical reviews. The
load-bearing source order was:

1. `docs/phase3/v1/PHASE_3_DOC.md`, with focused checks of the header and amendment history,
   canonical source and resource declarations, conformance map, detailed source/resource semantics,
   the complete manifest-declared §5 interface region, schema discipline, downstream hand-offs,
   implementation checklist, and closing ledger;
2. the resolved `docs/design/v3/DESIGN.md` authority, including Part I, §G0.4 adoption procedure,
   §G1.3 verification rule, the G8 provisional-roadmap status, the mandatory template, the Phase 3
   specification, and the document gate;
3. `docs/research/v1/RESEARCH.md` as contract ground truth, especially compute-root attachment,
   framebuffer default/fallback formats, Appendix A.3's `gdepth` upgrade, and Appendix B.4's 37
   explicit internal formats;
4. the binding §5 contract in `docs/phase1/v14/PHASE_1_DOC.md`; and
5. the supplied candidate, refutation, eliminated-candidate, and finder-clean-area records. The
   permitted Pintonium and Oculus reports were not needed to decide the surviving candidates.

I searched the target for equivalent `RGBA` aliases/default projections, compute-root publication,
current revision gates, and closing chronology before settling each candidate's interpretation,
severity, interface classification, and duplicate relationship. Only after those judgments were
settled did I read all discovered prior reviews `PHASE_3_REVIEW_1.md` through
`PHASE_3_REVIEW_45.md`, including their resolutions, as the final historical step.

There were no source-list deviations, no network use, and no agent fan-out. I did not invoke
`$verify-loop`, run a verification harness, start another session, or read a forbidden transcript,
chatlog, or `*.txt` source. The Gate reported no drops. Candidates 002, 006, and 008 had already
been eliminated before adjudication and were not revived or converted into findings.

## 1. Findings

### candidate-003 — The attachment baseline is not representable by the closed format type

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1851-1901,2378-2412`.
- **Claim:** Every created `ColorAttachmentRequirement` must carry a value representable by its
  mandatory `ColorInternalFormat format` component, while preserving the contract's distinction
  between default/plain RGBA, explicit sized formats, and the `gdepth` RGBA32F upgrade.
- **Evidence:** The canonical declaration makes `format` a non-optional `ColorInternalFormat`
  (`PHASE_3_DOC.md:1862-1864`) and closes that enum to exactly the 37 Appendix-B.4 tokens, including
  `RGBA8` and `RGBA32F` but not `RGBA` (`:1888-1901`). Binding §5 repeats the same exhaustive enum
  (`:2380-2390`) and then requires a first-created attachment to use baseline `RGBA`
  (`:2400-2406`). The target separately requires defaults to be explicit typed values rather than
  nulls or undeclared sentinels (`:1911-1914`). RESEARCH distinguishes the ordinary RGBA baseline
  and plain-RGBA fallback from the `gdepth` upgrade to RGBA32F
  (`docs/research/v1/RESEARCH.md:486-488,518-522,1155-1167`), while Appendix B.4 defines the 37
  sized directive values (`:1257-1269`). No target declaration supplies an `RGBA` member, alias, or
  conversion, so an attachment created by a clear, clear-color, mipmap, or other non-format
  directive cannot satisfy the published record constructor.
- **Severity:** correction. Introduce one executable attachment-format algebra with a distinct
  default/plain-RGBA case and an explicit-format case containing the existing 37-value
  `ColorInternalFormat` domain (or obtain and record an authoritative decision to use a different
  exact representation). Construct new entries with the default/plain case, keep explicit `RGBA8`
  distinct, and make active `gdepth` force explicit `RGBA32F`. Align §§2.2, 3.3, 4.7, and the sole
  binding §5 contract; update fingerprint encoding and exact constructor/fold tests; and advance
  `CURRENT_SCHEMA_VERSION` with producer/consumer compatibility tests under §5.3 because the
  published component shape or meaning/default changes.
- **Touches interface/change-trigger region:** yes. The repair must change the binding
  `ResourceRequirements` declaration/default in §5 and therefore fires the monitored interface
  trigger.

### candidate-004 — The Phase 11 gate and closing ledger stop before the current §0.45 surface

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:300-303,3078-3080,3195-3201`.
- **Claim:** Local downstream-readiness wording and the terminal chronology must identify the latest
  Phase 3 revision and §5 surface rather than an obsolete amendment.
- **Evidence:** The current addendum says Round 45 produced §0.45, including consumer-visible
  source-identity and schema-10 work (`PHASE_3_DOC.md:300-303`), and §5.3 confirms that Round 45's
  nested source-identity change made schema 10 mandatory (`:2572-2576`). The Phase 11 hand-off still
  conditions consumption on a fresh PASS for “this §0.37 interface” (`:3078-3080`). The terminal
  ledger then ends with Round 44 producing §0.44 and omits Round 45 entirely (`:3195-3201`). General
  statements that Phase 3 remains unverified and schema-9 consumers reject schema 10 keep the
  overall posture conservative, but they do not make these two explicit current-surface references
  accurate. The governing rule permits dependent consumption only after the latest surface is
  verified (`docs/design/v3/DESIGN.md:348-359,657-667`).
- **Severity:** correction. Replace the fixed §0.37 Phase 11 condition with a gate on the latest
  verified Phase 3 revision/latest §5 surface. Extend the terminal chronology to record that Round
  45 reviewed §0.44 and produced §0.45; when applying this review's fix-up, record Round 46's review
  of §0.45 and the resulting current amendment without claiming verification. Preserve the general
  rule that no dependent may consume the current document until its latest interface-changing
  surface receives the required fresh review.
- **Touches interface/change-trigger region:** no. The ordered edits are confined to the §11 hand-off
  and terminal ledger; the already-current schema-10 §5 contract need not change for this finding.

## 2. Checked and clean

- `candidate-001` is dropped. The resolved v3 authority expressly says that v3 remains unadopted
  for Phase 3, that selecting/pointing a phase at v3 does not itself complete adoption, and that the
  phase keeps its declared governing design until all four ordered migration steps are complete
  (`docs/design/v3/DESIGN.md:195-220`). The review-time `selection_source: override` is therefore an
  assessment context, not proof that Phase 3's RC3 header and actual-input ledger are stale. Prior
  rounds 37, 39, 40, 42, and 44 specifically settled the same unchanged premise. No header or input
  relabeling is ordered by this review; a maintainer-directed v3 adoption remains a separate full
  §G0.4 operation.
- `candidate-007` is dropped as an exact substantive duplicate of admitted `candidate-003`. Both
  identify the same non-optional attachment-format field, the same closed 37-value enum, the same
  unrepresentable `RGBA` baseline, the same `gdepth` distinction, and the same §5/schema repair.
  Its substantive concern is represented once rather than double-counted.
- `candidate-005` is dropped on re-derivation. G8 is explicitly a deliberately non-binding
  provisional roadmap whose items are handed off in mandatory section §11, not a current dependent
  phase whose execution contract must be frozen in §5 (`docs/design/v3/DESIGN.md:774-815,837-853`).
  The target follows that allocation: P3-C19 records base then `_a`…`_z` recognition, shared
  associated `programName=p`, distinct physical IDs, COMPUTE materialization, and dimension-folder
  exclusion in the conformance map and implementation checklist
  (`PHASE_3_DOC.md:1383-1385,3188-3191`), while §11 hands it to G8/S2 (`:3086-3087`). The existing
  source API already publishes ordered roots with program, stage, and physical identity
  (`:990-1013,1140-1151,2432-2443`), and the existing same-edit rule requires any future
  consumer-visible change to update §5 (`:2287-2290`). Adding G8/S2 as a current binding consumer or
  binding execution-before-program timing now would contradict the roadmap's non-binding status and
  the allocation of future items to §11.
- The finder-reported clean areas were rechecked. The completion-LRU discovery outcomes, canonical
  physical `SourceId`/include-edge identity, `TextureSidecarRef.path()` projection, the declarations
  of all 37 explicit sized formats, shadow/`Vec4f` leaves, schema-10 rejection discipline, global
  compute filename recognition itself, mandatory thirteen-section structure, and OQ-7 spike remain
  coherent apart from the admitted typed-baseline defect.
- The selected Phase 1 module/package seam, complete `GLCapabilityProfile` macro input, fixed log
  channels, loader-neutral diagnostics, debug flag, and SPDX/third-party mechanism exist in the
  binding dependency contract. The missing jcpp build pin remains requested rather than assumed.
- Pre-adjudication candidates 002, 006, and 008 remain eliminated for their recorded Refute
  dispositions. No Gate-dropped candidate exists, and no finding was created outside the surviving
  candidate set.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both admitted findings are bounded fix-up work, not structural misses requiring a rebuild. One
repairs a consumer-visible resource-format algebra/default in the monitored §5 region; the other
repairs stale lifecycle references outside that region. Literal PASS is unavailable while either
correction remains.

The correction count falls from five in Round 45 to two after independent re-derivation and duplicate
consolidation. That is progress, but the recent history (`8 -> 8 -> 5 -> 5 -> 4 -> 5 -> 2`) is not
strictly decreasing and the current surface has not converged to zero corrections. The next required
action is a scoped fix-up resolving candidates 003 and 004 and recording their resolutions in this
review. Because candidate-003 necessarily changes the manifest-declared cross-phase interface and
its schema/default contract, a fresh whole-document verification round is required before Phase 3
can close or be consumed as a verified dependency.

## Resolutions

### candidate-003 — applied

RESEARCH independently fixes three distinct values: initialization uses plain `RGBA`
(`RESEARCH.md:486-488`), explicit directives use the 37 Appendix-B.4 internal formats
(`:1257-1269`), and active `gdepth` upgrades attachment 1 to `RGBA32F` (`:1155-1167`).
Accordingly, §§3.3, 4.7, and binding §5 now use the closed
`ColorAttachmentFormat.DefaultRgba|Explicit(ColorInternalFormat)` sum. New entries use
`DefaultRgba`, every sized directive—including `RGBA8`—uses `Explicit`, and `gdepth` forces
`Explicit(RGBA32F)` in either source order while preserving conflict diagnostics.

The canonical resource fingerprint now encodes the exact format variant tag and, for `Explicit`,
the exact enum name. Constructor, fold, fingerprint, and source-order tests cover the distinction.
Because the nested `PackConfiguration.resources` shape/default changed, the public constant,
current schema labels, compatibility assertions, milestone/checklist text, and binding §5.3 now use
schema 11 and require schema-10 consumers to reject it. This intentionally changes the
manifest-declared cross-phase interface and fires its fresh-whole-document-review trigger.

### candidate-004 — applied

The Phase 11 hand-off now gates consumption on a fresh literal PASS for the latest Phase 3 revision
and latest §5 surface, without freezing another amendment number. The terminal ledger now records
Round 45 reviewing §0.44 and producing §0.45 and Round 46 reviewing §0.45 and producing §0.46,
while continuing to state that Phase 3 v1 is unverified pending a fresh whole-document review.

### Notes deferred

None. The adjudicator admitted no notes.

### Refusals

None.
