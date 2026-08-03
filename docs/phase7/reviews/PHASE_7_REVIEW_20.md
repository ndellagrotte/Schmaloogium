## 0. Method and reading order

I independently re-derived both Gate-surviving candidates from the Phase 7 target, the
manifest-selected RC3 governing sections (including the Phase 7 assignment, document gate, and
mandatory template), RESEARCH.md, the binding §5 contracts of Phases 2–6, and the permitted
supporting evidence relevant to the claims. Only after settling those judgments did I read
`docs/phase7/reviews/PHASE_7_REVIEW_1.md` through
`docs/phase7/reviews/PHASE_7_REVIEW_19.md`, in order and last.

The only reading-list deviation was `reference-src/schlorbium-HD_U_G6_pre1/files.txt`: the
manifest also forbids every `*.txt` source, so I did not read it. It was immaterial to the two
internal-contract candidates. There was no network use, forbidden-source use, or agent fan-out.
This was the canonical engine's already-dispatched atomic adjudication role, so the supplied
`verify-loop` skill required completing only this role without invoking the loop or delegating.
No candidates were eliminated before adjudication, and Gate dropped none.

## 1. Findings

### candidate-001 — Phase 9 hook-report counts undercount declared injection anchors

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1159`–`:1165`
- **Claim:** The active Phase 9 rows in `HookApplicationReport` do not use the document's binding
  counting unit consistently with their declared injection sites.
- **Evidence:** H9-ENTITY-ID-01 declares HEAD and RETURN injections on each of two `RenderManager`
  methods, while H9-BLOCK-ENTITY-ID-01 declares HEAD and RETURN on one
  `TileEntityRendererDispatcher` method (`docs/phase7/v1/PHASE_7_DOC.md:1019`–`:1020`). The active
  report table nevertheless assigns those rows 2/2 and 1/1 respectively
  (`docs/phase7/v1/PHASE_7_DOC.md:1159`–`:1165`). The binding interface defines Mixin counts as
  successfully applied injection anchors and requires these five Phase 9 rows to obey that table
  (`docs/phase7/v1/PHASE_7_DOC.md:1637`–`:1642`). Under that declared unit, the two rows require
  4/4 and 2/2. Counting methods or logical hook pairs cannot reconcile the present values with the
  explicit interface rule.
- **Required correction:** Change the active counts in §4.12 and the corresponding §5 contract to
  4/4 for H9-ENTITY-ID-01 and 2/2 for H9-BLOCK-ENTITY-ID-01. If a different unit is intended,
  redesign the hook declarations and binding count definition consistently instead.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — §5 expressly binds the counting unit and the
  Phase 9 table, so correcting that contract fires the fresh-verification trigger.

## 2. Checked and clean

The finder-reported clean areas survived re-derivation apart from the admitted count mismatch. The
Phase 9 publication order, held/reset sequencing, reload reasons, geometry invalidation, five-row
identifier inventory, milestone labels, and checklist remain internally consistent. The target
otherwise distinguishes pending dependency changes from granted contracts, keeps blocked features
gated, supplies substantive downstream hand-offs and compensation rules, and maps the governing
frame flow, program families, hook needs, reference timeline, engine flags, Appendix E rows, and
OQ-3/OQ-4 work without another candidate-backed defect.

`candidate-002` is dropped against settled material. Round 10 already adjudicated the same
`Opened.draw` challenge and found the owner uniquely resolvable: `FrameOpenResult.Opened` carries
only a frame token, while `ScopeOpenResult.Opened` alone declares `DrawDisposition draw`
(`docs/phase7/v1/PHASE_7_DOC.md:1248`–`:1266`). The challenged sentence describes drawing through
an acquired shader scope (`docs/phase7/v1/PHASE_7_DOC.md:1585`–`:1593`), so the member reference
can only denote `ScopeOpenResult.Opened.draw`. Qualification would improve editorial precision but
is not a required contract correction. Nothing in the post-Round-10 maintenance changes that
variant ownership or creates new dispatch ambiguity, and Round 19 subsequently reached literal
PASS.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The admitted defect is a localized diagnostic-contract inconsistency, not a structural miss that
requires rebuilding Phase 7. Round 19 reached zero corrections, but the post-PASS Phase 9
maintenance surface introduced this new correction, so convergence is no longer established for
the current target. The next required action is a scoped fix-up of candidate-001, including this
review's `## Resolutions` record and the Phase 7 addendum. Because the correction changes §5, a
fresh whole-document and interface verification round is required before Phase 7 can close.

## Resolutions

### candidate-001 — applied

Re-derived from the catalog rather than the review's conclusion: H9-ENTITY-ID-01 covers two
`RenderManager` methods with one HEAD and one RETURN injection on each, hence four successfully
applied Mixin anchors; H9-BLOCK-ENTITY-ID-01 covers one dispatcher method with HEAD and RETURN,
hence two. Section 4.12 now reports the active rows as 4/4 and 2/2. Section 5 continues to bind
Mixin counts to successfully applied injection anchors and now states the corrected active sequence
explicitly. Section 0.24 records the compact fix-up. Because §5
changed, a fresh verification round is required before Phase 7 can close.

### Notes deferred

None.
