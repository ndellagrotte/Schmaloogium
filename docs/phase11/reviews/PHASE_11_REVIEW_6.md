# Phase 11 — Verification Review, Round 6

Target: `docs/phase11/v1/PHASE_11_DOC.md` (whole document)
Manifest: `verification/targets/phase-11.json`
Governing design revision: `docs/design/v3/DESIGN.md` (v3, override selection)

## 0. Method and reading order

Sources read before judgment: the target document (§0 header and round addenda, §3 conformance
map, §4 detailed design regions cited by candidates, §5 cross-phase interfaces at 947–1039, §11
rulings); `docs/design/v3/DESIGN.md` Part I, Phase 11 target spec, doc gate, and the G9 mandatory
template; `docs/research/v1/RESEARCH.md` Appendix F.6 (raw, line-exact) plus the uniform-contract
and built-in-input selectors; the binding dependency regions of
`docs/phase3/v1/PHASE_3_DOC.md` (§5.1 publication surface) and `docs/phase6/v1/PHASE_6_DOC.md`
(§4.12 audit and §4.13 custom-uniform extension point); and the Pintonium/OptiFine evidence
selectors as needed for the conformance lens.

Every candidate's claim was re-resolved independently against the primary text with raw,
line-numbered reads before any prior review was opened. Prior reviews (Rounds 1–5) were read last,
adjudicator-last, only to check settled material and recurrence.

Deviations: none. No forbidden source was read — no `docs/**/chatlogs/**`, no `*.txt`, no prior
agent or session transcript. No network use. No subagent fan-out from this session; the candidate
set was supplied by the engine. Gate drops: none. Candidates eliminated before adjudication: none.

## 1. Findings

### Finding 1 (candidate-001) — §0.2 states a verification chronology contradicted by §0.6–§0.8 and §11.2

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:67`–`:70`.
- **Claim under test:** the Round-5 fix-up "synchronized current verification state" (§0.8), yet
  §0.2 still asserts a Round-2/Round-3 chronology.
- **Evidence:** §0.2 says "Rounds 1 and 2 have completed; the Round-2 fix-up made the latest §5
  change, and Round 3 is the fresh verification it triggered" (`:67`–`:70`). §0.6 records Round 3
  as completed with a §5 change (`:109`–`:113`), §0.7 records Round 4 likewise (`:115`–`:118`),
  §0.8 records Round 5 and claims verification state was synchronized (`:120`–`:123`), and §11.2
  item 4 states Rounds 1–4 preceded the review with the Round-4 fix-up as the latest prior §5
  change (`:1264`–`:1266`). The two statements of current verification state are mutually
  exclusive; no passage supersedes or annotates the §0.2 sentence.
- **Severity:** correction. The steelman that this is non-normative status metadata outside the
  doc gate is partly right about the gate, but it is a directly false self-statement about the
  closure gate, it falsifies §0.8's own specific claim, and this document's own precedent —
  Round 2 "corrected stale authority/status prose" and Round 5 was ordered to fix exactly this
  class of defect in §11.2 — classes it as a contract correction, not a note. This is a
  recurrence of the same defect relocated from §11.2 to §0.2.
- **Fix ordered:** rewrite §0.2's final sentence to match the addenda and §11.2 (Rounds 1–5
  completed; the Round-5 fix-up made the latest §5 change; Round 6 is the fresh verification it
  triggered), or state the chronology in exactly one place and cross-reference it from the other.
- **Touches interface/change-trigger region:** no. §0.2 lies outside 947–1039 and the ordered
  edit changes no §5 row.

### Finding 2 (candidate-002) — every consumed Phase 6 grant in §5.3, plus §4.8 and §3.3, is anchored to a range that does not contain it

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:999`–`:1007` (§5.3), `:686`–`:688` (§4.8),
  `:397`–`:399` (§3.3).
- **Claim under test:** Phase 11 "consumes exactly the verified Phase 6 contracts" and pins each
  to a Phase 6 line range a reader can check.
- **Evidence:** I re-read Phase 6 directly. Lines 1130–1170 are §4.11 replay prose and the §4.12
  notifier-to-producer audit table — not the bridge signature (cited `:1135`–`:1147`) nor the
  lookup/value algebra (cited `:1148`–`:1162`) nor upload submission (cited `:1163`–`:1187`).
  The custom-uniform extension point §4.13 begins at Phase 6 line 1186: `CustomUniformBridge` at
  1200–1205, the lookup/value algebra at ~1206–1224, sink/command/result types at ~1226–1251, the
  install-lifecycle rule at 1253–1259 (cited by §4.8 as `:1190`–`:1196`), the seven-name
  exclusion union at 1266–1267 (cited by §3.3 as `:1197`–`:1200`), built-ins-first execution at
  1283–1286 (cited as `:1201`–`:1207`), and definition-order/duplicate/accepted-prefix semantics
  from 1296 (cited as `:1209`–`:1223`). Every anchor is displaced by roughly 65–90 lines.
- **Severity:** correction. The substantive grants are stated faithfully and do exist in Phase 6
  — the conformance and interface lenses independently confirmed semantic agreement — so this is
  not blocking; but a consumer following the binding citations lands on an unrelated audit table
  and cannot verify the consumed dependency contract, which is more than wording preference.
- **Fix ordered:** repoint each citation at the range that actually contains its grant, deriving
  every boundary freshly from the current Phase 6 text (the candidate's proposed `:1279`–`:1283`
  for built-ins-first is itself short of the 1283–1286 paragraph). While editing, also re-derive
  §5.4's `:1389`–`:1391` anchor against the current Phase 6 §5.1 rows.
- **Touches interface/change-trigger region:** yes. §5.3 sits at 995–1007 inside the declared
  `cross-phase-interfaces` region (947–1039) and the ordered edit changes text there.

### Finding 3 (candidate-003) — §5.2 anchors the consumed Phase 3 algebra to a range that stops before the granting row

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:989`–`:993`.
- **Claim under test:** `docs/phase3/v1/PHASE_3_DOC.md:1415`–`:1428` is the binding source of the
  consumed `customExpressions()` algebra.
- **Evidence:** I read Phase 3 1415–1445 raw. The cited window ends on the §5.1 table's header and
  separator rows (`| Exposed contract | Content | Consumer(s) |`, `|---|---|---|`) at 1427–1428;
  it contains only the §4.10 tail, the `## 5. Cross-phase interfaces` heading, the §5.1 heading,
  and the preamble. The granting row —
  `PackConfiguration.customExpressions()`, `CustomExpressionDecl`, `CustomExpressionKind`,
  `CustomExpressionType`, `SourceAttribution` — is at line 1442, and the binding algebra block
  begins at `The binding custom-expression algebra is:` (line 1459) running to roughly 1482. The
  only other Phase 3 citations in the target (`:1145`–`:1147`, `:690`–`:693`) cover ownership, not
  the published algebra, so no equivalent anchor exists.
- **Severity:** correction. The transcribed algebra in §5.2 is faithful to Phase 3, so this is a
  mis-anchored binding citation rather than a wrong contract claim.
- **Fix ordered:** repoint §5.2 at `docs/phase3/v1/PHASE_3_DOC.md:1442` for the publication row
  and `:1459`–`:1482` for the binding algebra block and its ordering/duplicate/fingerprint prose.
- **Touches interface/change-trigger region:** yes. §5.2 line 993 is inside 947–1039 and the
  ordered edit changes text there.

### Finding 4 (candidate-004) — §3.2 cites the operator bullet as the authoritative function list

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:323`–`:324`.
- **Claim under test:** the function table's exactness anchor `RESEARCH.md:1503`–`:1506` contains
  the published function list.
- **Evidence:** raw read of `docs/research/v1/RESEARCH.md:1490`–`:1515` places the per-draw
  exclusion tail at 1503–1505 and `- Operators: ...` at 1506; the function bullet
  (`- Functions: sin cos asin ...`, `if`, `smooth`, boolean helpers, `vec2 vec3 vec4`) occupies
  1507–1510. The cited range and the function list do not overlap. §3.2's 33 table rows cite only
  the unnumbered "RESEARCH App F.6", so this is the sole line-level authority for the
  completeness claim, and the neighbouring §3 citation at `:398` resolves correctly, ruling out a
  systematic offset convention.
- **Severity:** correction. The function table itself is complete and correct against F.6; only
  the completeness anchor misresolves.
- **Fix ordered:** change the citation to `docs/research/v1/RESEARCH.md:1507`–`:1510`.
- **Touches interface/change-trigger region:** no.

### Finding 5 (candidate-005) — §3.1's declaration-form anchor excludes the `variable.*` clause it paraphrases, and §0.3's surface anchor truncates the function bullet

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:295`–`:296` and `:94`–`:95`.
- **Claim under test:** `RESEARCH.md:1492`–`:1495` supports "declares both forms and says
  variables are reusable but not uploaded"; `:1492`–`:1508` is the published expression surface
  underlying the clean-room claim.
- **Evidence:** F.6's heading is at RESEARCH line 1493, the `uniform.<...>` form at 1495, and the
  `variable.<type>.<name>=<expr>` / "(not uploaded)" clause at 1496 — one line past the cited
  range, whose start also precedes the heading. §0.3's `:1492`–`:1508` ends mid-function-bullet
  and excludes `smooth`, the boolean helpers `between/equals/in`, and the `vec2/vec3/vec4`
  constructors at 1509–1510, all of which the clean-room surface claim relies on. §3.1's table
  rows cite only unnumbered "App F.6", so no substitute line-level support exists.
- **Severity:** correction. I considered the steelman that this is a one-to-two-line offset inside
  the correctly named appendix and therefore a note. It fails on the §0.3 half: the truncated
  surface anchor omits precisely the constructs (`smooth`, boolean helpers, constructors) that the
  clean-room provenance argument must show were derived from published documentation rather than
  from stareval, and that provenance argument is load-bearing for the closed license gate.
- **Fix ordered:** cite `docs/research/v1/RESEARCH.md:1495`–`:1496` in §3.1 and widen §0.3's
  surface anchor to at least `:1493`–`:1512`.
- **Touches interface/change-trigger region:** no.

## 2. Checked and clean

The three finder lenses reported clean areas that I spot-verified and accept.

**New-surface lens.** Repeated identifiers are internally consistent: the `BIOME_*` family, the
fourteen `is_*` view booleans across §3.3, §4, §5.1, and §12, the backend semantic ID
`schmaloogium:typed-ast-interpreter-v1`, and the D-P11-1..13 decision references. RESEARCH
citations at target lines 241 and 1027 resolve to the cited content. The Round-5 edits in §4.12,
§5.1, and §5.5 are mutually consistent — reset deactivates the tuple, discards the random stream,
and yields `NoCustoms` until fresh activation, with close terminal — and agree with §2.4's
invariants and §6's failure table.

**Interfaces lens.** Substantively the consumed contracts match their sources: the six
upload-command mappings, `Bool1` ownership, the `Accepted`/`SkippedAbsent`/`Rejected` outcome set,
the three-counter refresh ledger, accepted-prefix-on-`Aborted`, definition-order submission,
single-bridge installation, and the closed reset-reason split (`PACK_REPLACEMENT`, `SHADERS_OFF`,
`GL_CONTEXT_LOSS` via adoption; `WORLD_EPOCH`, `CLOSE` via direct reset, with `FRAMEBUFFER_RESIZE`
correctly declared as having no Phase 6 counterpart) all agree with Phase 6 §4.13/§5.1, and the
consumed Phase 3 algebra matches the Phase 3 §5.1 row at line 1442. §5.1's exposed rows carry
closed variants, non-null/immutability, lifecycle ordering, and backend-ID rejection behavior in
enough detail to implement without guessing, and the §5.1 synchronization clause binds the
incorporated §§2.3, 4.1, and 4.9–4.12 declarations so the declared change-trigger region remains
self-sufficient. Findings 2 and 3 are citation defects only; no substantive interface mismatch
survived re-derivation.

**Conformance lens.** Every design-v3 Phase 11 in-scope requirement and every doc-gate item —
App F.6 tokens, operators, all named functions plus `if`, `smooth`, the boolean helpers and vector
constructors, input binding families, the per-draw exclusion union, variable intermediates, typing,
cadence, the smooth state machine, the evaluator interface, error semantics, and the stareval
license outcome — has a mapped row. The `shaders.properties` citations (326–330, 332–336, 341–351,
356–370, 401–405, 372–414) and the boolean-list and exclusion-union RESEARCH citations resolve and
semantically support their claims, including the correct seven-name exclusion union in preference
to the design row's stale five-name restatement. The mandatory thirteen-section structure is
present and the phase carries no assigned open questions.

No candidate was eliminated before adjudication, and there were no Gate drops, so there is no
pre-settled material to discuss here. No candidate was refuted on re-derivation; all five survived
independent re-resolution against the primary text.

## 3. Verdict

# PASS-WITH-CORRECTIONS

Counts: blocking=0; corrections=5; notes=0
Interface changed: yes

Interface disposition: Findings 2 and 3 order edits inside the manifest-declared
`cross-phase-interfaces` region (`docs/phase11/v1/PHASE_11_DOC.md:947`–`:1039`). Under that
region's change trigger, a fresh verification round is required before Phase 11 can close.

Trend and convergence: this is Round 6 with no recorded prior trend in the manifest input, but the
document's own addenda show five consecutive §5-touching fix-ups. Finding 1 is a recurrence of the
verification-state synchronization defect that Rounds 3 and 5 already corrected elsewhere in the
document, now relocated to §0.2 — the underlying cause is that per-round chronology is restated in
two places. Findings 2–5 are a single newly surfaced class: line-anchor drift against dependency
and authority documents, undetected in earlier rounds. No blocking or structural defect exists; the
contract's substance is sound and converging, and the remaining work is mechanical repointing plus
one status-prose consolidation.

Next required action: apply the five ordered corrections in a Round-6 fix-up, re-deriving every
replacement line range from the current dependency and authority text rather than adopting proposed
numbers verbatim; prefer stating the verification chronology in exactly one place. Because the
fix-up edits §5.2 and §5.3, a fresh verification round is required before closure.

## Resolutions

All five corrections applied to `docs/phase11/v1/PHASE_11_DOC.md`. Every replacement range was
re-derived from the current primary text; proposed numbers were not adopted verbatim.

1. **Finding 1 (§0.2 chronology).** Chose the single-statement option: §0.2 now says the per-round
   chronology lives only in the §0.4-onward addenda and that the latest addendum made the most
   recent §5 change. Neighbour sweep found §11.2 item 4 equally stale (it still named Rounds 1–4
   and "this Round-5 fix-up"), so it was rewritten to defer to the same addenda rather than restate
   a round count. New §0.9 records the Round-6 fix-up.
2. **Finding 2 (Phase 6 anchors).** Re-derived from Phase 6 §4.13: the code block runs 1188–1251,
   so `CustomUniformBridge.refresh` is 1200–1205, the `BuiltInExpressionView`/`ExpressionLookup`/
   `ExpressionValue` algebra 1207–1225, and the sink/command/`CustomRefreshResult` types 1226–1250.
   The install-lifecycle paragraph is 1253–1259 (§4.8), the seven-name exclusion union 1266–1267
   (§3.3), built-ins-first 1283–1286, and the definition-order/duplicate/accepted-prefix rules
   1296–1329 (the accepted-prefix and counter-ledger prose ends at 1329, past the review's
   suggested boundary). §5.3 and §4.8 and §3.3 updated accordingly.
3. **Finding 3 (Phase 3 anchor).** Verified the publication row at Phase 3 line 1442 and the
   binding algebra block at 1459; it ends at 1480 ("closed executable domains."), not 1482, which
   is the `TextureBindingKey` paragraph. §5.2 now cites `:1442` plus `:1459`–`:1480`.
4. **Finding 2 follow-up (§5.4).** Re-derived Phase 6 §5.1: counters at 1389, schema at 1390,
   `Bool1`/sink outcomes at 1391. The existing `:1389`–`:1391` anchor is correct and was left
   unchanged.
5. **Finding 4 (§3.2).** Confirmed the function bullet at RESEARCH 1507–1510; citation changed.
6. **Finding 5 (§3.1, §0.3).** Confirmed F.6 heading 1493, `uniform.*` 1495, `variable.*` 1496,
   function bullet through 1510, precipitation rule 1511–1512. §3.1 now cites 1495–1496; §0.3's
   surface anchor widened to 1493–1512.

Interface disposition: §5.2 and §5.3 inside the declared `cross-phase-interfaces` region were
intentionally edited. The edits change only dependency line anchors; no consumed grant, signature,
type, ordering, or outcome wording was altered. The region's change trigger fires and a fresh
verification round is required before Phase 11 can close.

### Notes deferred

None; the round recorded no notes.

No refusal.
