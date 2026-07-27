export const meta = {
  name: 'phase-verify-loop',
  description: 'Loop §G1.2 adversarial verify → §G1.3 fix-up on a PHASE_<N>_DOC.md until a review returns a literal PASS',
  whenToUse: 'When a phase doc should be driven to a PASS verdict without hand-writing a brief per round. Pass `phase: N` to choose the phase. Exits only on PASS (zero blocking, zero corrections), on FAIL, or on the round cap.',
  phases: [
    { title: 'Attack', detail: 'read-only finders, one per §G1.2 lens' },
    { title: 'Refute', detail: 'skeptics per candidate, prompted to kill it' },
    { title: 'Steelman', detail: 'opposed lens on surviving corrections' },
    { title: 'Gate', detail: 're-resolve every citation at the line' },
    { title: 'Adjudicate', detail: 'write the round review file, exactly one verdict' },
    { title: 'Fix up', detail: 'apply corrections, record Resolutions, prove §5 invariance' },
  ],
}

// ----------------------------------------------------------------- configuration

const REPO = '/home/nick/IdeaProjects/schmaloogium-project/Schmaloogium'

const PRESETS = {
  lean: { finders: 3, refuters: 2, steelman: false },
  thorough: { finders: 5, refuters: 3, steelman: true },
}

// The Workflow runtime delivers `args` as a JSON-encoded **string**, not an object — verified by
// probe: both `{maxRounds:0}` and `[1,2,3]` arrive with `typeof args === 'string'`. Without this
// parse every field below reads `undefined` and the script silently falls back to its full
// unattended defaults (lean, round 11, six rounds, reviewOnly off), which is exactly the run the
// pre-flight exists to avoid. Parse defensively; an unparseable value means defaults, as before.
function readArgs(a) {
  if (!a) return {}
  if (typeof a !== 'string') return a
  try { return JSON.parse(a) } catch (e) { return {} }
}

const A = readArgs(args)
const cfg = PRESETS[A.preset] || PRESETS.lean
const PHASE = A.phase === undefined ? 2 : A.phase
const START = A.startRound || 1
const MAX_ROUNDS = A.maxRounds === undefined ? 6 : A.maxRounds
const REVIEW_ONLY = A.reviewOnly === true
const ADDENDUM_LINES = A.addendumLines || 40

const RANK = { none: 0, note: 1, correction: 2, blocking: 3 }
const NAME = ['none', 'note', 'correction', 'blocking']

// ----------------------------------------------------------------- per-phase facts, derived paths
// Everything phase-specific lives in this table. No prompt below names a phase number directly —
// they read from `F` and the derived paths, which is what makes this harness reusable for phases
// 3-14 without another round of surgery.
//
// `spec` and `docGate` are line ranges into DESIGN.md Part II. They are the one kind of fact here
// that rots silently: DESIGN.md is edited by fix-up sessions on the governance document, not by
// this loop, so re-check them if a doc-gate finding ever looks like it is reading the wrong text.
//
// THERE IS NO SINGLE `DESIGN.md`, AND NO SINGLE SET OF LINE NUMBERS. Three files share the
// basename since the reorg put the version in the directory, they are different documents, and the
// two phase docs are anchored to DIFFERENT ones:
//   `docs/design/v1.1/DESIGN.md`     1,586 lines — PHASE_2_DOC's anchor (its §0.1 cites the
//                                   Phase 2 spec at v1.1 ll. 662-720), and every review to round 11
//   `docs/design/v2.0-RC2/DESIGN.md` 2,478 lines — PHASE_1_DOC's anchor from §0.11 onward (its
//                                   l. 12 says so), adopted at the round-eleven fix-up, §G0.4 step 3
//   `docs/design/v2.0-RC1/DESIGN.md` 2,304 lines — read by nothing; kept for history
// So the revision is a declared PER-PHASE fact, exactly like `docVersion` below, and its pin set
// lives in `DESIGN_PINS`. Pointing a phase at the wrong revision does not error — it silently feeds
// every agent the wrong text at plausible-looking coordinates, which is the whole trap §G0.4 and
// `docs/MOVES.md` both warn about.
//
// ADDING OR MOVING A REVISION means deriving a complete pin set from THAT FILE'S OWN HEADINGS
// (`grep -n '^#'`, then confirm each range's first and last line by printing it). NEVER shift
// another revision's numbers by an offset — §G0.4 step 1 forbids exactly that, and the offsets this
// comment used to carry were wrong when they were written.
//
// `docVersion` names the directory the phase doc lives in. It is NOT derivable from the phase
// number — phase 1 is `v14` after fourteen fix-up rounds, phase 2 is `v1`, freshly built — so it is a
// declared fact here like every other phase-specific value. See `docPath` below for the roll
// procedure when a fix-up produces a new revision.

// Pin sets, one per design revision. `sections` is composed whole rather than assembled from parts
// because the revisions do not carry the same sections — v1.1 has no §G11 — and a conditional
// clause per section would be a bug farm. Every RC2 number here was derived from RC2's headings and
// confirmed by printing the range; none was shifted from v1.1's.
const DESIGN_PINS = {
  'v1.1': {
    path: 'docs/design/v1.1/DESIGN.md',
    sections: '§G0.3 ll. 48-56; §G1.1 68-116; §G1.2 118-149; §G1.3 151-162; §G4.2 310-316; ' +
      '§G4.3 318-322; §G4.4 324-331; §G4.6 341-348; §G5.1 phase table 356-377; §G5.3 400-425; ' +
      '§G9 doc template 508-542; §G10 OQ table 544-572',
    g9: '508-542',
  },
  'v2.0-RC2': {
    path: 'docs/design/v2.0-RC2/DESIGN.md',
    sections: '§G0.3 ll. 135-143; §G1.1 188-255; §G1.2 257-300; §G1.3 302-320; §G4.2 506-519; ' +
      '§G4.3 521-525; §G4.4 527-534; §G4.6 545-557; §G5.1 phase table 565-588; §G5.3 611-638; ' +
      '§G9 doc template 761-798; §G10 OQ table 800-829; ' +
      '§G11 Pintonium rules of engagement 833-943',
    g9: '761-798',
  },
}

// Every revision is evidence, whichever one a given round reads. The do-not-modify lists name all
// of them rather than only `DESIGN`, so the protection does not silently narrow to one file the
// moment a phase moves revisions. `PHASE_1_REVIEW_11.md`'s Resolutions states the same convention:
// "`DESIGN.md` in all three revisions … are unmodified".
const DESIGN_ALL = [
  'docs/design/v1.1/DESIGN.md',
  'docs/design/v2.0-RC1/DESIGN.md',
  'docs/design/v2.0-RC2/DESIGN.md',
  'docs/design/v2.0-RC3/DESIGN.md',
]

const PHASE_FACTS = {
  1: {
    name: 'Foundation & project architecture',
    docVersion: 'v14',
    design: 'v2.0-RC2',
    spec: '957-1067', docGate: '1056-1060',
    oqs: 'OQ-2, OQ-12, OQ-20, OQ-21',
    deps: [],
  },
  2: {
    name: 'Conformance harness',
    docVersion: 'v1',
    design: 'v1.1',
    spec: '662-723', docGate: '713-715',
    oqs: 'OQ-10',
    deps: [1],
  },
}

const F = PHASE_FACTS[PHASE]
if (!F) {
  throw new Error(
    'No PHASE_FACTS entry for phase ' + PHASE + '. Add one — its name, its doc-version directory, ' +
    'the design revision its doc is anchored to (a key of DESIGN_PINS), the Part II line ranges ' +
    'for its spec and Doc gate STATED IN THAT REVISION, its assigned OQs, and its dependency ' +
    'phases (all in the §G5.1 table) — rather than letting the prompts assert another phase\'s ' +
    'facts.')
}

// The phase's design revision, resolved to its pin set. A row naming a revision with no pin set is
// a hard stop, not a fallback: falling back to another revision's numbers is precisely the silent
// wrong-text failure the table exists to prevent.
const D = DESIGN_PINS[F.design]
if (!D) {
  throw new Error(
    'PHASE_FACTS[' + PHASE + '].design is ' + JSON.stringify(F.design) + ', which has no ' +
    'DESIGN_PINS entry (have: ' + Object.keys(DESIGN_PINS).join(', ') + '). Add one — the path, ' +
    'the section->line list and the §G9 range — all derived from that file\'s own headings, never ' +
    'shifted from another revision\'s numbers (§G0.4 step 1).')
}

// The documents moved out of the repo root in commit 9df5f05 ("docs: fix non-existent document
// organization") and were reorganized again into versioned directories. Every reference below is
// repo-relative from REPO; a bare filename no longer resolves, which is exactly how the Phase 1
// wiring broke.

// A phase doc lives in a directory named for its revision — `docs/phase1/v14/PHASE_1_DOC.md`.
// Resolution goes through PHASE_FACTS rather than string-building from `n`, because a dependency's
// version is *that* phase's fact, never this one's.
//
// ROLLING A VERSION (v14 -> v15 once a fifteenth fix-up lands) is two steps, run together, and
// always AFTER a loop exits:
//     git mv docs/phase<N>/v<K> docs/phase<N>/v<K+1>
//     then bump docVersion in PHASE_FACTS above.
// `v<K>` = the highest `§0.K` fix-up addendum in the doc. Never roll mid-loop: DOC is computed once
// at startup, so a rename between rounds leaves every later round pointed at a directory that no
// longer exists, and each agent silently reads nothing.
function docPath(n) {
  const f = PHASE_FACTS[n]
  if (!f || !f.docVersion) {
    throw new Error(
      'No PHASE_FACTS.docVersion for phase ' + n + '. Every phase row must name the version ' +
      'directory its doc lives in (e.g. `v14`) — it cannot be derived from the phase number.')
  }
  return 'docs/phase' + n + '/' + f.docVersion + '/PHASE_' + n + '_DOC.md'
}

const REVIEWS = 'docs/phase' + PHASE + '/reviews'
const DOC = docPath(PHASE)
const DESIGN = D.path
const RESEARCH = 'docs/research/v1/RESEARCH.md'
const DEP_DOCS = F.deps.map(function (n) {
  return docPath(n)
})

function reviewPath(r) {
  return REVIEWS + '/PHASE_' + PHASE + '_REVIEW_' + r + '.md'
}

// Phase 1 reached this harness after ten hand-run verify rounds, and several prompts below were
// written to counter *that* document's failure mode — a reviewer manufacturing findings to keep a
// mature loop alive. On a document that has never been reviewed those same words are a lie, and a
// damaging one: they bias the finders toward silence and the adjudicator toward a false PASS.
// Starting at round 1 means no review exists yet, so the maturity claims are suppressed.
const FIRST_EVER_REVIEW = START === 1

// ----------------------------------------------------------------- shared prompt blocks
// COMMON and POSTURE are lifted from the round-4 adversarial workflow, which ran 15 agents
// with 0 errors. Do not "improve" them without a reason you can name.

const DEP_MANY = DEP_DOCS.length > 1
const DEP_BULLET = DEP_DOCS.length
  ? '- ' + DEP_DOCS.map(function (d) { return '`' + d + '`' }).join(', ') + ' — **the dependency ' +
    'phase doc' + (DEP_MANY ? 's' : '') + '**, ' + (DEP_MANY ? '' : 'a ') + 'declared §G1.2 input' +
    (DEP_MANY ? 's' : '') + ' (step 1 of\n  the reading list). ' + (DEP_MANY ? 'Their' : 'Its') +
    ' **§5** is the binding contract this document consumes\n  (§G1.1), and it is what an ' +
    '*Interface honesty* finding is measured against. Reading ' + (DEP_MANY ? 'them' : 'it') + '\n' +
    '  is required, not optional.'
  : '- This phase has no dependency phase docs (§G5.1), so *Interface honesty* runs in one direction\n' +
    '  only: whether what this document promises its dependents is specified rather than gestured at.'

// The maturity paragraph is the one place the harness makes a claim about the document's history.
// Getting it wrong in either direction is costly, so it is stated from the actual round number.
const EMPTY_IS_HONEST = FIRST_EVER_REVIEW
  ? `## Empty is honest — and so is a long list
An empty findings array is a perfectly good outcome, and **you must not manufacture findings.** But
be clear where this document stands: **it has never been reviewed.** No adversarial reader has opened
it before you, one session wrote it, and no one else has read that prose closely. The whole document
is unreviewed surface, and on a first reading of a document this size a substantial findings list is
expected rather than suspicious.

The anti-inflation discipline therefore applies to **severity**, not to volume: do not call a note a
correction to make it land. Name what you checked and found sound in \`clean_areas\` either way — the
clean-to-dirty ratio is part of the verdict.`
  : `## Empty is honest
An empty findings array is a perfectly good outcome. **Do not manufacture findings.** ${START - 1}
verify round${START - 1 === 1 ? ' has' : 's have'} already run on this document; the failure mode at
this maturity is no longer "the reviewer missed something" but "the reviewer kept the loop alive".
Name what you checked and found sound in \`clean_areas\` — the clean-to-dirty ratio is part of the
verdict.`

const COMMON = `
You are one agent in an automated **§G1.2 verify round** on the Schmaloogium project.

Your working directory is \`${REPO}/\`, and **every path below is relative to it.** The project's
documents live under \`docs/\`, not at the repo root: a reference without its \`docs/…\` prefix will
not resolve, and silently finding nothing is the failure mode to avoid here.

## HARD RULES — non-negotiable
- **READ-ONLY.** Do not create, edit, or delete any file. Do not run builds, tests, or gradle.
  Do not run git commands that mutate state (\`git log\`/\`git status\`/\`git show\`/\`git diff\` are fine).
  Do not write via shell redirection, \`tee\`, \`sed -i\`, or any other route. You may use Read, Grep,
  Glob, and read-only Bash. That is all.
- **FORBIDDEN SOURCES.** Do **not** read any prior session's terminal transcript, at any path.
  Concretely, and both patterns matter:
  - anything under **a directory named \`chatlogs/\` anywhere below \`docs/\`** — **any** extension.
    \`docs/phase1/chatlogs/\`, \`docs/phase2/chatlogs/\` and \`docs/reference/pintonium/chatlogs/\` all
    hold transcripts, in both \`.txt\` and \`.md\`;
  - **any \`*.txt\` at the repo root.** \`/export\` drops session transcripts there under a dated
    filename, so this set grows without anyone editing this rule. Do not read them, and do not treat
    an unfamiliar root-level \`.txt\` as safe merely because it is not named here.

  §G1.2 bars a reviewer from the author's conversation context because it transmits the author's
  blind spots, and for this document that context is the most tempting file in the repository — the
  transcripts include the sessions that designed both the phase doc and this harness. A sub-agent
  broke exactly this rule in round nine of Phase 1; its conclusion was discarded and re-derived from
  permitted sources (\`docs/phase1/reviews/PHASE_1_REVIEW_9.md\` §0.2). Do not repeat it. **The rule
  is about provenance, not location or file type:** a transcript found somewhere not listed here is
  still barred, and if you are unsure whether a file is one, do not open it.
- **No scope creep.** Answer only what you are asked below.
- Your final text is a return value consumed by a program, not a message to a human.

## Ground truth (read what you need, not everything)
- \`${DESIGN}\` — the governance document, **design revision ${F.design}**, which is the revision
  \`${DOC}\` is anchored to. Three files in this repository are named \`DESIGN.md\` and they are
  different documents; every line number below is stated against *this* one, and a coordinate from
  another revision will resolve to plausible-looking wrong text rather than erroring. Sections:
  ${D.sections}. The Part II **Phase ${PHASE} spec** is at ll. ~${F.spec}, and its **Doc gate** at
  ll. ~${F.docGate}. If a coordinate you are given disagrees with what you find at that line, **stop
  and report it** rather than guessing which is right (§G0.4).
- \`${RESEARCH}\` — the contract ground truth. §0 reading guide and confidence tags; §1 mission,
  non-goals and the D-1..D-10 decision log; §4.2 the ~90 built-in uniforms and the 43 program slots;
  §4.4 the frame model; §11 the open-question register; App D and App F.
- \`${DOC}\` — **the document under review.**
${DEP_BULLET}
- Template ground truth, at the repo root (these did **not** move): \`build.gradle\`,
  \`settings.gradle\`, \`gradle.properties\`, \`gradle/scripts/{dependencies,extra,publishing}.gradle\`,
  \`src/**\`, \`.github/workflows/*\`, \`README.md\`.

## CRITICAL — what does NOT count as work
**You get no credit for confirming that a quote matches a line.** Verifying the anchor is the
starting point, not the finding. Your job is the *interpretive* question underneath it. A separate
Gate agent re-resolves every citation you produce, so a finding that is only an anchor check will be
dropped and will have cost the round nothing but tokens.

${EMPTY_IS_HONEST}
`

const POSTURE = `
## Your posture: REFUTATION
Your job is to **kill** the finding you are given. Assume it is wrong until the source files force
you to concede. Specifically, hunt for these ways a finding can be bad:

1. **Out of scope for this phase.** Phase ${PHASE} is "${F.name}" (§G5.1). Does the alleged defect
   actually belong to a different phase, making the doc's silence correct rather than defective?
   Check §G5.1's owner column and §G0.3's architected-now/implemented-later principle.
2. **Already covered elsewhere in the doc.** The finder read one region but may have missed a
   mechanism in §6, §7, §8, §9, §11 or §12 that closes the gap. **Grep the whole document** before
   conceding that something is absent.
3. **Severity inflation.** A "blocking" that is really a "correction"; a "correction" that is really
   a "note"; a "note" that is not a defect at all. §G1.2 reserves the strongest language for things
   that stop a dependent from building.
4. **The §5 claim is wrong.** \`touches_section_5\` is what forces another whole verify round
   (§G1.3). If the fix can be made without altering §5's text, say so — that is a materially
   different and much cheaper outcome.
5. **The proposed fix is wrong** even where the defect is real.
6. **It is a taxonomy or house-style quibble**, not a defect a dependent phase could trip over.

Concede only on evidence you read yourself in the source file. Never on the finder's say-so.

## Verdict vocabulary — use it precisely
- \`CONFIRMED\` — the defect is real **and** the stated severity and \`touches §5\` flag are right.
- \`OVERSTATED\` — something real is there, but the severity, the scope, the §5 claim, or the
  proposed fix is wrong. Say exactly which, and what it should be.
- \`REFUTED\` — not a defect. The doc is right, or the finder misread it.
`

// ----------------------------------------------------------------- schemas
// EVIDENCE_ITEM and the four finding schemas are lifted from the round-4 script verbatim.

const EVIDENCE_ITEM = {
  type: 'object',
  required: ['file', 'line', 'quote', 'shows'],
  additionalProperties: false,
  properties: {
    file: { type: 'string', description: 'repo-relative path from ' + REPO + ', e.g. ' + DOC + ' — NOT a bare filename; the Gate resolves this literally and drops what it cannot open' },
    line: { type: 'integer' },
    quote: { type: 'string', description: 'verbatim text at that line' },
    shows: { type: 'string', description: 'what this establishes, one sentence' },
  },
}

const FINDING_ITEM = {
  type: 'object',
  required: ['title', 'location', 'claim_under_test', 'evidence', 'severity', 'touches_section_5', 'not_already_covered_because'],
  additionalProperties: false,
  properties: {
    title: { type: 'string' },
    location: { type: 'string', description: 'doc section + line numbers' },
    claim_under_test: { type: 'string' },
    evidence: { type: 'array', items: EVIDENCE_ITEM },
    severity: { type: 'string', enum: ['blocking', 'correction', 'note'] },
    touches_section_5: { type: 'boolean', description: 'YOUR judgement, not anyone else’s' },
    not_already_covered_because: { type: 'string', description: 'why this is not a prior round’s finding restated' },
    recommended_fix: { type: 'string' },
  },
}

const ATTACK_SCHEMA = {
  type: 'object',
  required: ['findings', 'areas_examined', 'clean_areas'],
  additionalProperties: false,
  properties: {
    findings: { type: 'array', items: FINDING_ITEM },
    areas_examined: { type: 'array', items: { type: 'string' } },
    clean_areas: { type: 'string', description: 'what you checked and found sound — name it, so the clean ratio is on the record' },
  },
}

const REFUTE_SCHEMA = {
  type: 'object',
  required: ['verdict', 'severity_should_be', 'touches_section_5', 'strongest_refutation_attempted', 'why_it_held_or_failed', 'evidence'],
  additionalProperties: false,
  properties: {
    verdict: { type: 'string', enum: ['CONFIRMED', 'OVERSTATED', 'REFUTED'] },
    severity_should_be: { type: 'string', enum: ['blocking', 'correction', 'note', 'none'] },
    touches_section_5: { type: 'boolean', description: 'YOUR judgement, not the finder’s' },
    strongest_refutation_attempted: { type: 'string', description: 'the best argument you could build against the finding' },
    why_it_held_or_failed: { type: 'string', description: 'why that argument did or did not survive contact with the files' },
    evidence: { type: 'array', items: EVIDENCE_ITEM },
    recommended_fix: { type: 'string' },
  },
}

const STEELMAN_SCHEMA = {
  type: 'object',
  required: ['best_defence', 'defence_holds', 'residual_defect', 'final_severity', 'touches_section_5'],
  additionalProperties: false,
  properties: {
    best_defence: { type: 'string', description: 'the strongest case that the doc as written is correct' },
    defence_holds: { type: 'boolean' },
    residual_defect: { type: 'string', description: 'what survives the defence; empty string if nothing does' },
    final_severity: { type: 'string', enum: ['blocking', 'correction', 'note', 'none'] },
    touches_section_5: { type: 'boolean' },
    evidence: { type: 'array', items: EVIDENCE_ITEM },
    disagreement_with_refuter: { type: 'string', description: 'where you differ from the refuter and why; empty if you agree' },
  },
}

const GATE_SCHEMA = {
  type: 'object',
  required: ['results', 'summary'],
  additionalProperties: false,
  properties: {
    results: {
      type: 'array',
      items: {
        type: 'object',
        required: ['index', 'anchor_ok', 'detail'],
        additionalProperties: false,
        properties: {
          index: { type: 'integer', description: 'the finding index you were given' },
          anchor_ok: { type: 'boolean', description: 'every cited file:line resolves and the quote matches verbatim' },
          detail: { type: 'string', description: 'for a failure, which citation failed and what the line actually says' },
          corrected_location: { type: 'string', description: 'if the finding is real but its line number is stale, the correct one; else empty' },
        },
      },
    },
    summary: { type: 'string' },
  },
}

const ADJUDICATE_SCHEMA = {
  type: 'object',
  required: ['verdict', 'blocking_count', 'correction_count', 'note_count', 'section5_touched', 'review_file', 'rationale'],
  additionalProperties: false,
  properties: {
    verdict: { type: 'string', enum: ['PASS', 'PASS-WITH-CORRECTIONS', 'FAIL'] },
    blocking_count: { type: 'integer' },
    correction_count: { type: 'integer' },
    note_count: { type: 'integer' },
    section5_touched: { type: 'boolean', description: 'would any correction alter §5 on the fix shape you recommend' },
    review_file: { type: 'string', description: 'path of the review file you wrote' },
    rationale: { type: 'string', description: 'one paragraph: why this verdict and not the adjacent one' },
    findings_dropped_on_derivation: { type: 'string', description: 'candidates you examined and cleared, recorded in §2' },
  },
}

const FIXUP_SCHEMA = {
  type: 'object',
  required: ['sites_edited', 'section5_unchanged', 'section5_hash_before', 'section5_hash_after', 'addendum_lines', 'weakest_points', 'do_not_refight', 'files_modified'],
  additionalProperties: false,
  properties: {
    sites_edited: { type: 'array', items: { type: 'string' }, description: 'every site edited, as "§X.Y ll. A-B — what changed"' },
    section5_unchanged: { type: 'boolean', description: 'result of the content-anchored diff you ran, not your belief' },
    section5_hash_before: { type: 'string' },
    section5_hash_after: { type: 'string' },
    addendum_lines: { type: 'integer', description: 'how many lines the new §0.N addendum occupies' },
    new_prose_lines: { type: 'integer', description: 'total lines added to the phase doc this round' },
    weakest_points: { type: 'array', items: { type: 'string' }, description: 'YOUR self-assessment: the judgement calls next round should test. Claims to test, never verdicts to reach.' },
    do_not_refight: { type: 'array', items: { type: 'string' }, description: 'what this round verified as settled, so the next round does not re-derive it' },
    files_modified: { type: 'array', items: { type: 'string' } },
    refused_with_cause: { type: 'string', description: 'any finding declined, and why; empty if none' },
  },
}

// ----------------------------------------------------------------- the five attack lenses
// Ordered by historical yield below; LENS_ORDER re-ranks them for a first-ever review, where
// `new-surface` is degenerate. `lean` takes the first three of whichever order applies.

const ALL_LENSES = [
  {
    key: 'new-surface',
    title: FIRST_EVER_REVIEW ? 'The whole document is the new surface' : 'The new surface',
    body: FIRST_EVER_REVIEW
      ? `This document has never been reviewed, so there is no "recently touched" region to
prioritise and no prior round has cleared anything for you: **the entire document is the unreviewed
surface.** The standing lesson still applies in its general form — *unreviewed material yields
findings in proportion to its size, not to the document's maturity* — and here that size is the
whole file.

Because the other lenses take the structured checks (§3's conformance map, §5's interfaces, the doc
gate, scope), take the parts they will skip: §4's detailed design at the level of internal
consistency, §6 failure modes, §7 threading and performance, §8 testability, §9 milestone staging,
§12's checklist. Attack, specifically: claims the document makes *about itself* ("X is specified in
§Y" — go read §Y); numbers, identifiers and file paths that appear more than once and must agree;
and any place §4 decides something §2's overview describes differently.`
      : `The previous fix-up's new prose is the largest unreviewed surface in the document, and
exactly one session has read it — the one that wrote it. **That is where your findings are.** Four
consecutive rounds have confirmed the standing lesson: *unreviewed material yields findings in
proportion to its size, not to the document's maturity.*

Attack, specifically: the sites the last fix-up edited; its §0.N addendum; any claim that addendum
makes *about the rest of the document* (a fix-up asserting "X is already correct at §Y" is a claim
you can check); and **neighbours** — the row, bullet or sentence adjacent to a site that was edited
correctly. Two of the last round's findings were neighbours a true claim elsewhere stopped anyone
looking at.`,
  },
  {
    key: 'interfaces',
    title: 'Interface honesty and §5',
    body: `§G1.2's *Interface honesty* check: everything the doc consumes from a dependency actually
exists in that dependency's §5; everything promised to a dependent is specified, not gestured at.
${DEP_DOCS.length ? `
**This phase has a dependency, so the inward half of that check is live and it is the highest-yield
work available to you.** Read ${DEP_DOCS.map(function (d) { return '`' + d + '` **§5**' }).join(' and ')}
— the binding contract under §G1.1 — and take every item this document says it consumes back to the
dependency's own text. A consumption row citing a section that does not say what the row claims is
precisely the class of error this lens exists to catch, and it cannot be found by reading this
document alone. Open the cited section; do not trust the citation.
` : ''}
You own the **\`touches §5\`** call, and it is the most consequential judgement in the round — it
decides under §G1.3 whether another verify session is owed. Read §5 (\`## 5. Cross-phase interfaces\`
through \`## 6. Failure modes\`) against §4's detailed design and ask whether §5 is still *sufficient
on its own*, which is what it claims to be. Check whether any §4 statement retracts or contradicts
something §5 still asserts.`,
  },
  {
    key: 'conformance',
    title: 'Conformance map',
    body: `§G1.2's *Conformance-map audit*: zero unmapped in-scope contract rows in §3, and
spot-check mapped rows **against the cited RESEARCH.md/App text** for semantic fidelity. The
criterion is *contract item → design element satisfying it* — a row that declines to name an owner
for something the criterion does not ask about is not an unmapped row, so do not inflate that.

The class of error you exist to catch is a row that cites a source which does not say what the row
claims. Open the cited text; do not trust the citation.`,
  },
  {
    key: 'doc-gate',
    title: 'Doc gate and template completeness',
    body: `Two §G1.2 checks. **Doc gate:** every criterion in the Phase ${PHASE} spec's *Doc gate*
(\`${DESIGN}\` ll. ~${F.docGate}) met **literally.** Read those lines yourself and take each criterion
as written — the gate is the one check where the spec's exact wording, not its evident intent, is
what the document is measured against.

**Template completeness:** all thirteen §G9 sections (ll. ${D.g9}) present *and substantive*, and
every assigned OQ (${F.oqs}) carrying a full §G4.4 spike spec —
question / procedure / success-failure criteria / fallback design. A section that exists but says
nothing is a finding; a section that is short but complete is not.`,
  },
  {
    key: 'scope-decisions',
    title: 'Scope discipline and binding decisions',
    body: `Two §G1.2 checks. **Scope discipline:** nothing designed that the spec's *Scope — out*
assigns to another phase, and nothing from *Scope — in* silently dropped. **Binding decisions:** no
RESEARCH.md D-1..D-10 contradicted, and no contract-visible component "improved" against §G4.2.

§G1.1 also requires that input contradictions be *reported* with a ruling and its provenance, never
silently smoothed over — a place where the doc smoothed one over is a finding.`,
  },
]

// `lean` takes the first three lenses, so the order decides what a cheap round actually checks.
// On a mature document the last fix-up's prose is the best-yielding target and leads. On a document
// that has never been reviewed there is no such region, so the structured checks lead instead and
// the (rewritten) whole-document lens sweeps up whatever they do not cover.
const LENS_ORDER = FIRST_EVER_REVIEW
  ? ['interfaces', 'conformance', 'doc-gate', 'scope-decisions', 'new-surface']
  : ['new-surface', 'interfaces', 'conformance', 'doc-gate', 'scope-decisions']

const LENSES = LENS_ORDER.map(function (k) {
  const lens = ALL_LENSES.find(function (l) { return l.key === k })
  if (!lens) throw new Error('LENS_ORDER names an unknown lens key: ' + k)
  return lens
})

// ----------------------------------------------------------------- helpers

function normKey(f) {
  const loc = String(f.location || '').toLowerCase().replace(/[^a-z0-9.]+/g, '')
  const t = String(f.title || '').toLowerCase().replace(/[^a-z0-9]+/g, '').slice(0, 60)
  return loc.slice(0, 40) + '|' + t
}

function dedupe(list) {
  const seen = new Set()
  const out = []
  for (const f of list) {
    if (!f || !f.title) continue
    const k = normKey(f)
    if (seen.has(k)) continue
    seen.add(k)
    out.push(f)
  }
  return out
}

function median(ranks) {
  const s = ranks.slice().sort(function (a, b) { return a - b })
  return s[Math.floor(s.length / 2)]
}

function findingBlock(f) {
  const ev = (f.evidence || []).map(function (e) {
    return '  - `' + e.file + ':' + e.line + '` — "' + e.quote + '" → ' + e.shows
  }).join('\n')
  return [
    '**Title:** ' + f.title,
    '**Location:** ' + f.location,
    '**Claim under test:** ' + f.claim_under_test,
    '**Stated severity:** ' + f.severity + ' · **touches §5:** ' + (f.touches_section_5 ? 'yes' : 'no'),
    '**Evidence the finder offered:**',
    ev || '  (none offered — that is itself grounds for refutation)',
    f.recommended_fix ? '**Proposed fix:** ' + f.recommended_fix : '',
  ].filter(Boolean).join('\n')
}

// ----------------------------------------------------------------- the loop

const trend = []
let round = START
let outcome = 'CAP'
let lastVerdict = null
let priorFix = null

log('Phase ' + PHASE + ' ("' + F.name + '") verify loop on `' + DOC + '` (doc version ' +
    F.docVersion + ', design ' + F.design + ' = ' + DESIGN + ') — preset `' +
    (A.preset || 'lean') + '` (' + cfg.finders + ' finders, ' + cfg.refuters + ' refuters, steelman ' +
    (cfg.steelman ? 'on' : 'off') + '), starting at round ' + START + ', cap ' + MAX_ROUNDS +
    '. Exit on PASS = zero blocking AND zero corrections.')

if (FIRST_EVER_REVIEW) {
  log('Round 1: this document has no prior review. Maturity-based anti-inflation language is ' +
      'suppressed and the lens order leads with the structured checks — see FIRST_EVER_REVIEW.')
}
if (DEP_DOCS.length) {
  log('Dependency phase doc' + (DEP_DOCS.length > 1 ? 's' : '') + ' in the reading list: ' +
      DEP_DOCS.join(', ') + ' (§5 is the binding contract for Interface honesty).')
}

for (let i = 0; i < MAX_ROUNDS; i++) {
  round = START + i

  if (budget.total && budget.remaining() < 60000) {
    log('Stopping before round ' + round + ': token budget nearly exhausted (' +
        Math.round(budget.remaining() / 1000) + 'k left). Not a PASS.')
    outcome = 'BUDGET'
    break
  }

  const REVIEW_FILE = reviewPath(round)
  const PRIOR_REVIEW = reviewPath(round - 1)
  // False only on round 1 of a document that has never been reviewed — there is no round 0 file.
  const HAS_PRIOR_REVIEW = !(FIRST_EVER_REVIEW && round === START)

  // ---- context carried forward from the previous fix-up: this is the hand-written brief,
  // ---- produced as data instead.
  const surface = priorFix
    ? [
        '## The new surface — what the previous fix-up changed, stated by the session that wrote it',
        '',
        'Sites edited:',
        priorFix.sites_edited.map(function (s) { return '- ' + s }).join('\n'),
        '',
        'Where that session says it is weakest — **these are claims to test, never verdicts to reach**:',
        priorFix.weakest_points.map(function (s) { return '- ' + s }).join('\n'),
        '',
        'What it verified as settled. Do not re-derive these unless you can show one wrong:',
        priorFix.do_not_refight.map(function (s) { return '- ' + s }).join('\n'),
        priorFix.refused_with_cause ? '\nRefused with cause: ' + priorFix.refused_with_cause : '',
      ].join('\n')
    : !HAS_PRIOR_REVIEW
    ? [
        '## The new surface',
        '',
        '**There is none, and that is the point.** This is the first adversarial review this',
        'document has ever had: no prior verify round, no fix-up, no §0.N addendum, no resolutions',
        'to inherit. One session wrote the whole file and no independent reader has opened it.',
        '',
        'So there is no recently-touched region to prioritise and nothing has been cleared for you.',
        'Work your lens across the whole document, and treat the document\'s own §0.3 (deviations,',
        'assumptions and open items the build session flagged about itself) as claims to test —',
        'never as a settled list.',
      ].join('\n')
    : [
        '## The new surface',
        '',
        'This is the first round of the automated loop, but not of this document: ' + (START - 1) +
          ' verify',
        'round' + (START - 1 === 1 ? '' : 's') + ' already ran on it by hand. The most recently',
        'written material is the last fix-up\'s — its §0.N addendum and the corrections it applied.',
        '**One session has read that prose — the one that wrote it.** Start there. Its own record of',
        'what it changed and where it thinks it is weakest is in that addendum and in ' + PRIOR_REVIEW,
        '\'s `## Resolutions` section — but read the latter only if this lens is the new-surface lens,',
        'and read the document itself first.',
      ].join('\n')

  // ---------------------------------------------------------------- Attack
  phase('R' + round + ' Attack')
  const lenses = LENSES.slice(0, cfg.finders)

  const attacks = (await parallel(lenses.map(function (L) {
    return function () {
      return agent(
        COMMON +
        '\n## Your lens: ' + L.title + '\n\n' + L.body +
        '\n\n' + surface +
        '\n\n## Reading order — this is a discipline, not a suggestion\n' +
        'Read `' + DESIGN + '` Part I and the Phase ' + PHASE + ' spec (ll. ~' + F.spec + '), then\n' +
        '`' + DOC + '`' +
        (DEP_DOCS.length ? ', then the dependency doc' + (DEP_DOCS.length > 1 ? 's' : '') + ' `' +
          DEP_DOCS.join('`, `') + '`' : '') +
        ', then only the sources your lens needs.\n\n' +
        '**Do not read any `PHASE_' + PHASE + '_REVIEW_*.md` file.** You are an independent reader,\n' +
        'and reading a prior round\'s findings on *this* document converts you into an auditor of\n' +
        'someone else\'s reasoning. A later Adjudicate agent reads them, last, and dispositions your\n' +
        'candidates against them.\n\n' +
        (DEP_DOCS.length
          ? 'The ban is scoped to this phase deliberately. A **dependency** phase\'s review files are\n' +
            'not your independence problem — they are evidence about the contract you are checking\n' +
            'against, and the document under review cites them. You may read them.\n\n'
          : '') +
        (HAS_PRIOR_REVIEW
          ? 'If your lens is the new-surface lens you may read ' + PRIOR_REVIEW + '\'s\n' +
            '`## Resolutions` section **only**, and only after your own findings are written down.\n\n'
          : 'There is no prior review of this document to be tempted by — round ' + round + ' is the\n' +
            'first.\n\n') +
        '## Return\n' +
        'Candidate findings under your lens, each with location, the claim under test, evidence you\n' +
        'read yourself at file:line, a severity, and your own `touches_section_5` judgement. Plus\n' +
        '`areas_examined` and `clean_areas`.',
        { label: 'attack:' + L.key, phase: 'R' + round + ' Attack', schema: ATTACK_SCHEMA, agentType: 'Explore' }
      )
    }
  }))).filter(Boolean)

  const raw = attacks.flatMap(function (a) { return a.findings || [] })
  const cleanAreas = attacks.map(function (a) { return a.clean_areas }).filter(Boolean)
  const candidates = dedupe(raw)
  log('R' + round + ' Attack: ' + raw.length + ' raw candidates from ' + attacks.length +
      ' lenses, ' + candidates.length + ' after dedup.')

  if (candidates.length === 0) {
    log('R' + round + ': no candidates survived dedup. The Adjudicate agent still runs and may ' +
        'return PASS on its own reading.')
  }

  // ---------------------------------------------------------------- Refute
  phase('R' + round + ' Refute')
  const judged = (await parallel(candidates.map(function (f, idx) {
    return function () {
      return parallel(Array.from({ length: cfg.refuters }, function (_, k) {
        return function () {
          return agent(
            COMMON + POSTURE +
            '\n## The finding you must kill\n\n' + findingBlock(f) +
            '\n\n## Note\n' +
            'You are refuter ' + (k + 1) + ' of ' + cfg.refuters + ' on this finding, working\n' +
            'independently. Do not try to guess what the others will say. Two agents converging on a\n' +
            'wrong answer is a real failure mode — but do not manufacture disagreement either.\n' +
            'Set `severity_should_be` and `touches_section_5` to YOUR judgement.',
            { label: 'refute:' + idx + '.' + (k + 1), phase: 'R' + round + ' Refute', schema: REFUTE_SCHEMA, agentType: 'Explore' }
          )
        }
      })).then(function (votes) {
        const v = votes.filter(Boolean)
        if (v.length === 0) return null
        const killed = v.filter(function (x) { return x.verdict === 'REFUTED' }).length
        const survives = killed < (v.length / 2) || (v.length === 2 && killed < 2)
        const ranks = v.filter(function (x) { return x.verdict !== 'REFUTED' })
                       .map(function (x) { return RANK[x.severity_should_be] || 0 })
        const sev = ranks.length ? median(ranks) : 0
        const s5 = v.filter(function (x) { return x.touches_section_5 }).length > (v.length / 2)
        return { f: f, survives: survives && sev > 0, rank: sev, touches5: s5, votes: v }
      })
    }
  }))).filter(Boolean)

  let surviving = judged.filter(function (j) { return j.survives })
  log('R' + round + ' Refute: ' + surviving.length + ' of ' + judged.length + ' candidates survived ' +
      cfg.refuters + ' independent refuters.')

  // ---------------------------------------------------------------- Steelman
  if (cfg.steelman) {
    const needsDefence = surviving.filter(function (j) { return j.rank >= RANK.correction || j.touches5 })
    if (needsDefence.length) {
      phase('R' + round + ' Steelman')
      const defended = await parallel(needsDefence.map(function (j, idx) {
        return function () {
          const refuterSummary = j.votes.map(function (v) {
            return '- verdict ' + v.verdict + ', severity ' + v.severity_should_be +
                   ', touches §5 ' + (v.touches_section_5 ? 'yes' : 'no') + ': ' + v.why_it_held_or_failed
          }).join('\n')
          return agent(
            COMMON +
            '\n## Your posture: DEFENCE\n' +
            'You are the opposed lens on the finding below. Other agents have just tried to refute it\n' +
            'and reached the conclusions shown. **Your job is the opposite of theirs.** Assume the\n' +
            'author of `' + DOC + '` was a careful architect who had a reason. Construct the\n' +
            '**strongest possible defence** of the document as written.\n\n' +
            'Then — honestly — say whether that defence actually holds when you check it against the\n' +
            'files. **A defence you cannot support with a quote is not a defence.** Set\n' +
            '`final_severity` and `touches_section_5` to YOUR judgement; you have read both sides and\n' +
            'your call is the one that stands.\n\n' +
            '## The finding\n\n' + findingBlock(j.f) +
            '\n\n### The refuters\' conclusions, for you to disagree with rather than defer to\n' +
            refuterSummary,
            { label: 'steelman:' + idx, phase: 'R' + round + ' Steelman', schema: STEELMAN_SCHEMA, agentType: 'Explore' }
          ).then(function (d) { return { j: j, d: d } })
        }
      }))
      for (const pair of defended.filter(Boolean)) {
        if (!pair.d) continue
        pair.j.rank = RANK[pair.d.final_severity] || 0
        pair.j.touches5 = pair.d.touches_section_5
        pair.j.steelman = pair.d
      }
      surviving = surviving.filter(function (j) { return j.rank > 0 })
      log('R' + round + ' Steelman: ' + defended.filter(Boolean).length + ' defended, ' +
          surviving.length + ' findings still standing.')
    }
  }

  // ---------------------------------------------------------------- Gate
  // The re-derivation gate, mechanised. Round nine ran it by hand and it caught a sub-agent
  // breach; it is the reason delegating here is safe at all.
  if (surviving.length) {
    phase('R' + round + ' Gate')
    const gateList = surviving.map(function (j, idx) {
      return '### Finding index ' + idx + '\n' + findingBlock(j.f)
    }).join('\n\n')

    const gate = await agent(
      COMMON +
      '\n## Your job: the re-derivation gate\n' +
      '**An agent\'s quote is not evidence until it matches the file.** For every finding below, open\n' +
      'every cited `file:line` yourself and confirm the quoted text appears there **verbatim**. This\n' +
      'is the one stage where anchor-checking IS the work.\n\n' +
      'Set `anchor_ok: false` when any citation fails — the file does not have that text at that\n' +
      'line, the quote is paraphrased, or words are dropped from the middle without an ellipsis.\n' +
      'If the substance is right but the line number is merely stale, set `anchor_ok: true` and put\n' +
      'the correct location in `corrected_location`. Findings you fail are dropped from the round.\n\n' +
      '## The findings\n\n' + gateList,
      { label: 'gate:R' + round, phase: 'R' + round + ' Gate', schema: GATE_SCHEMA, agentType: 'Explore', effort: 'low' }
    )

    if (gate && gate.results) {
      const bad = gate.results.filter(function (r) { return r.anchor_ok === false })
      for (const r of gate.results) {
        if (r.anchor_ok && r.corrected_location && surviving[r.index]) {
          surviving[r.index].f.location = r.corrected_location
        }
      }
      if (bad.length) {
        for (const b of bad) log('R' + round + ' Gate DROPPED finding ' + b.index + ': ' + b.detail)
        const badIdx = new Set(bad.map(function (b) { return b.index }))
        surviving = surviving.filter(function (_, idx) { return !badIdx.has(idx) })
      }
      log('R' + round + ' Gate: ' + bad.length + ' dropped on failed anchors, ' + surviving.length + ' verified.')
    }
  }

  // ---------------------------------------------------------------- Adjudicate
  phase('R' + round + ' Adjudicate')
  const finalList = surviving.map(function (j) {
    return findingBlock(j.f) +
           '\n**Post-refutation severity:** ' + NAME[j.rank] +
           ' · **touches §5:** ' + (j.touches5 ? 'yes' : 'no') +
           (j.steelman ? '\n**Steelman:** ' + (j.steelman.defence_holds ? 'the defence holds — ' : 'defence fails — ') + j.steelman.residual_defect : '')
  }).join('\n\n---\n\n')

  const adj = await agent(
    COMMON.replace('**READ-ONLY.**', '**You are the one agent this round permitted to write, and you may create exactly one file: `' + REVIEW_FILE + '`.** Otherwise READ-ONLY.') +
    '\n## Your job: write the round-' + round + ' review and return exactly one verdict\n\n' +
    'Candidate findings that survived ' + cfg.refuters + ' independent refuters' +
    (cfg.steelman ? ', a steelman defence,' : '') + ' and a re-derivation gate:\n\n' +
    (finalList || '**None. Every candidate was refuted or failed its anchor check.**') +
    '\n\n### What the finders reported as clean\n' + cleanAreas.join('\n\n') +
    (HAS_PRIOR_REVIEW
      ? '\n\n## Read the prior rounds LAST\n' +
        'Now — and only now, with the above in hand — read `' + PRIOR_REVIEW + '` including its\n' +
        '`## Resolutions` section, and grep earlier rounds for anything a candidate above restates.\n' +
        '§G1.2 exists to stop a reviewer inheriting the author\'s frame; reading the resolutions early\n' +
        'would make you an auditor of the last fix-up\'s reasoning instead of an independent reader of\n' +
        'the document it produced. **Disposition each candidate against the settled list: a finding a\n' +
        'prior round already cleared is not a finding, and an argument that holds on derivation is\n' +
        'itself worth recording in §2.**\n\n'
      : '\n\n## There are no prior rounds\n' +
        'This is the first review this document has ever received, so there is no settled list to\n' +
        'disposition against and nothing has been previously cleared. Every candidate above stands or\n' +
        'falls on your own derivation from the source files.\n\n') +
    '## Re-derive before you admit\n' +
    'The Gate confirmed the anchors. **You** must confirm the interpretation: for every finding you\n' +
    'admit to §1, open the cited text and satisfy yourself the claim is what the source supports.\n' +
    'Sub-agents generate candidates and citations; they do not generate findings. Findings,\n' +
    'severities and the verdict are yours.\n\n' +
    '## The verdict standard — read this twice\n' +
    (FIRST_EVER_REVIEW
      ? '**This is the first review this document has ever had.** Do not import the posture of a\n' +
        'late-stage round: nothing here has been argued over, softened, or cleared before, and a\n' +
        'first reading of a document this size returning a substantial findings list is the expected\n' +
        'outcome, not a sign the reviewer is inflating.\n\n' +
        '**PASS is available on the evidence and nothing else.** Return PASS only when there are no\n' +
        'blocking findings and no corrections — and be honest that on a never-reviewed document that\n' +
        'is a strong claim. Notes do not block PASS: record them in §1 with their severity and leave\n' +
        'them unapplied. Inventing a correction to look productive is worse than useless; softening a\n' +
        'real one to reach PASS is worse still, and at round one there is no loop-fatigue argument\n' +
        'for doing it. Judge the document in front of you.\n\n'
      : (START - 1) + ' verify rounds have already run on this document. **The failure mode at this\n' +
        'maturity is no longer "the reviewer misses things" — it is "the reviewer keeps the loop\n' +
        'alive."**\n\n' +
        '**PASS is available and it is the outcome that ends the cadence.** Return PASS when there\n' +
        'are no blocking findings and no corrections. Notes do not block PASS: record them in §1 with\n' +
        'their severity and leave them unapplied. A round that invents a correction to look\n' +
        'productive is worse than useless. **The converse holds just as hard: do not soften a real\n' +
        'correction to reach PASS.** Judge the document in front of you.\n\n') +
    '## Deliverable: `' + REVIEW_FILE + '`\n' +
    'Follow the four-section shape the Phase 1 review series settled on — see\n' +
    '`docs/phase1/reviews/PHASE_1_REVIEW_11.md` for the format if you need an exemplar (read it\n' +
    'for shape, not for findings; it is about a different document):\n' +
    '1. **§0** — what you read and in what order; reads beyond the list with the finding each turned\n' +
    '   on; deviations; network use; and a **sub-agent disclosure** stating that this round ran as an\n' +
    '   automated fan-out of ' + (cfg.finders + candidates.length * cfg.refuters + 1) + '-odd read-only\n' +
    '   agents under a mechanised re-derivation gate, and which candidates the gate dropped.\n' +
    '2. **§1 — Findings**, each with location, claim under test, evidence, severity, and an explicit\n' +
    '   **touches §5: yes/no** line.\n' +
    '3. **§2 — What was checked and came back clean.** Include the candidates that were refuted or\n' +
    '   cleared on your own derivation — a round reporting only findings misrepresents its coverage.\n' +
    '4. **§3 — Verdict**: exactly one of PASS / PASS-WITH-CORRECTIONS / FAIL, with the per-finding §5\n' +
    '   disposition table and the §G1.3 line. Reserve FAIL for structural misses requiring a rebuild.\n' +
    '   Put the verdict on a line of its own as a heading — `# PASS-WITH-CORRECTIONS` — as the Phase 1\n' +
    '   reviews do.\n\n' +
    '**A word on the tokens `PASS` and `FAIL` in this document.** `' + DOC + '` is a design for a\n' +
    'conformance harness, and it uses the bare words PASS, FAILED and SKIPPED as *run-outcome values*\n' +
    'in its own schemas and tables. Those are its subject matter, not verdicts, and they are not\n' +
    'evidence about your verdict. Do not let a grep for "PASS" over the document under review inform\n' +
    'the review of it.\n\n' +
    'Do not modify `' + DOC + '`, `' + DESIGN_ALL.join('`, `') + '`, `' + RESEARCH + '`' +
    (DEP_DOCS.length ? ', `' + DEP_DOCS.join('`, `') + '`' : '') + ', or any prior review file —\n' +
    'including their `## Resolutions` sections, which are evidence. **All three `DESIGN.md`\n' +
    'revisions are named deliberately:** this round reads `' + DESIGN + '`, but the others are the\n' +
    'anchors of other phase docs and of earlier reviews, and they are evidence too.\n' +
    'Create exactly one file: \n' +
    '`' + REVIEW_FILE + '`. Then stop.',
    { label: 'adjudicate:R' + round, phase: 'R' + round + ' Adjudicate', schema: ADJUDICATE_SCHEMA, agentType: 'general-purpose' }
  )

  if (!adj) {
    log('R' + round + ': the Adjudicate agent returned nothing. Stopping — no verdict exists.')
    outcome = 'ERROR'
    break
  }

  lastVerdict = adj.verdict
  trend.push({
    round: round,
    verdict: adj.verdict,
    blocking: adj.blocking_count,
    corrections: adj.correction_count,
    notes: adj.note_count,
    touches5: adj.section5_touched,
  })
  log('R' + round + ' VERDICT: ' + adj.verdict + ' — ' + adj.blocking_count + ' blocking, ' +
      adj.correction_count + ' corrections, ' + adj.note_count + ' notes, §5 touched: ' +
      (adj.section5_touched ? 'yes' : 'no'))

  if (adj.verdict === 'PASS' || (adj.blocking_count === 0 && adj.correction_count === 0)) {
    log('*** PASS at round ' + round + '. Zero blocking, zero corrections. The loop ends here. ***')
    outcome = 'PASS'
    break
  }

  if (adj.verdict === 'FAIL') {
    log('*** FAIL at round ' + round + '. §G1.3 routes FAIL to a rerun of the BUILD session with the ' +
        'review added to its Required inputs — a far larger operation and a human call. Stopping. ***')
    outcome = 'FAIL'
    break
  }

  if (REVIEW_ONLY) {
    log('reviewOnly set — stopping after the review, no fix-up run.')
    outcome = 'REVIEW-ONLY'
    break
  }

  // ---------------------------------------------------------------- Fix up
  phase('R' + round + ' Fix up')
  const fix = await agent(
    'You are a fresh **§G1.3 fix-up session** on the Schmaloogium project. Working directory:\n' +
    '`' + REPO + '`. **Every path below is relative to it** — the documents live under `docs/`, not\n' +
    'at the repo root, and a bare filename will not resolve.\n\n' +
    'Your contract is §G1.3\'s: read the Phase ' + PHASE + ' spec (`' + DESIGN + '` ll. ~' + F.spec +
    '), the phase doc and the review file; apply the corrections to `' + DOC + '`; and record each\n' +
    'resolution in the review file under a `## Resolutions` heading. Then stop. **Fix nothing the\n' +
    'review ruled clean, and verify nothing beyond ordinary care** — that is the next round\'s job.\n\n' +
    '## Apply `' + REVIEW_FILE + '`\n' +
    'Apply every correction. Notes are **not** applied this round: record them in the Resolutions\n' +
    'under a `### Notes deferred` heading with the reason, so the next round can tell a considered\n' +
    'deferral from an oversight. Refusal-with-cause is a first-class outcome for any finding — argue\n' +
    'it rather than applying it uncritically.\n\n' +
    '## Re-derive; do not adopt\n' +
    'A review\'s supporting argument is not evidence. Re-derive every load-bearing claim at its\n' +
    'source before you write it into the document. **And sweep over formulations, not over sites:**\n' +
    'grep the document for every wording you change and confirm no variant survives. A review names\n' +
    'the sites its finding turns on; a fix-up owes the sites its edit turns on, and that is always\n' +
    'the larger set. The sites that get missed are the ones a true claim elsewhere makes nobody look\n' +
    'at — check the **neighbour** of every site you edit.\n\n' +
    '## Convergence discipline — this is a hard constraint, not a style note\n' +
    'This document\'s own standing lesson is that *unreviewed material yields findings in proportion\n' +
    'to its size, not to the document\'s maturity*. Every addendum you write is next round\'s finding\n' +
    'surface. So:\n' +
    '- The new **§0.N addendum must be at most ' + ADDENDUM_LINES + ' lines**: what ran, the design\n' +
    '  calls as one-line rulings, and a `**§G1.3 status:**` paragraph. No extended argument.\n' +
    '- **The full argument goes in the review file\'s `## Resolutions`**, which is what §G1.3 actually\n' +
    '  requires. The §0.N addenda are this document\'s self-imposed convention and have themselves\n' +
    '  generated findings.\n' +
    '- Restamp the previous §0.N-1 `**§G1.3 status:**` header to `**§G1.3 status at the time:**`,\n' +
    '  convert it to past tense, and append the closing supersession sentence §0.4-§0.9 all carry.\n' +
    '- Update §0\'s `Last revised` stamp, the dated-claims note\'s §0.4-§0.N range, and the\n' +
    '  end-of-document closing paragraph\'s session and fix-up counts.\n\n' +
    '## The §5 gate — run it, do not assert it\n' +
    'Before you edit, snapshot §5 by **content anchor, never by line number** (your insertions will\n' +
    'shift every line below them):\n' +
    '```bash\n' +
    'cd ' + REPO + '\n' +
    'awk \'/^## 5\\. Cross-phase interfaces/,/^## 6\\. Failure modes/\' ' + DOC + ' | sha256sum\n' +
    '```\n' +
    'Run it again after every edit and return **both hashes** plus `section5_unchanged`. If §5 did\n' +
    'change, that is a legitimate outcome — say so truthfully; it means §G1.3\'s re-verify trigger\n' +
    'fires. Never report it unchanged without having run the comparison.\n\n' +
    'Also verify with `git status --short` that you touched **exactly two paths**: `' + DOC + '` and\n' +
    '`' + REVIEW_FILE + '`. If git reports `' + REVIEWS + '` as a single `??` entry instead of\n' +
    'listing the files inside it, that directory holds no tracked file and the check is degraded —\n' +
    'say so in `files_modified` and confirm by listing the directory instead. **Do not assume that\n' +
    'state.** A `reviews/` directory holding any tracked file — a prior review, or a `.gitkeep` — is\n' +
    'listed per-file and the check is exact; that is the case for every phase defined today, so a\n' +
    'degraded result means something is missing. The phase doc itself is tracked, so a modification\n' +
    'to it always shows.\n' +
    'Do not modify `' + DESIGN_ALL.join('`, `') + '`, `' + RESEARCH + '`' +
    (DEP_DOCS.length ? ', `' + DEP_DOCS.join('`, `') + '`' : '') + ', or any earlier review file —\n' +
    'including their `## Resolutions` sections, which are evidence. **All three `DESIGN.md`\n' +
    'revisions are named deliberately:** this round reads `' + DESIGN + '`, but the others are the\n' +
    'anchors of other phase docs and of earlier reviews, and they are evidence too. If an earlier\n' +
    'review contains an error, record the correction in your Resolutions; never edit the evidence.\n\n' +
    '## Then report, as data for the next round\n' +
    'Your structured return is what the next verify round is briefed from — it replaces the\n' +
    'hand-written brief. `weakest_points` is the important field: **the judgement calls you made that\n' +
    'an adversarial reader should test.** State them as claims to test, never as verdicts to reach.\n' +
    'A fix-up gets no adversarial review of its own; the next round can only attack reasoning you\n' +
    'wrote down.\n\n' +
    'Re-resolve every line number you cite against the finished file before you write it. Do not read\n' +
    'any prior session\'s terminal transcript — anything under a directory named `chatlogs/`\n' +
    'anywhere below `docs/` (any extension) or **any `*.txt` at the repo root** (`/export` keeps\n' +
    'adding them). They carry the\n' +
    'author\'s blind spots, which is why §G1.2 bars them.',
    { label: 'fixup:R' + round, phase: 'R' + round + ' Fix up', schema: FIXUP_SCHEMA, agentType: 'general-purpose' }
  )

  if (!fix) {
    log('R' + round + ': the fix-up agent returned nothing. Stopping rather than looping on an ' +
        'unknown document state.')
    outcome = 'ERROR'
    break
  }

  priorFix = fix
  const t = trend[trend.length - 1]
  t.newProse = fix.new_prose_lines
  t.addendum = fix.addendum_lines
  t.s5unchanged = fix.section5_unchanged

  log('R' + round + ' Fix up: ' + fix.sites_edited.length + ' sites, ' + fix.new_prose_lines +
      ' lines of new prose (§0.N addendum ' + fix.addendum_lines + '), §5 ' +
      (fix.section5_unchanged ? 'unchanged' : 'CHANGED') + ', files: ' + fix.files_modified.join(', '))

  if (fix.addendum_lines > ADDENDUM_LINES) {
    log('  WARNING: addendum is ' + fix.addendum_lines + ' lines against a cap of ' + ADDENDUM_LINES +
        '. That is next round\'s finding surface.')
  }

  // convergence check — three rounds without a fall in corrections means the loop is feeding itself
  if (trend.length >= 3) {
    const c = trend.slice(-3).map(function (x) { return x.corrections })
    if (!(c[0] > c[1] && c[1] > c[2])) {
      log('  CONVERGENCE WARNING: corrections over the last three rounds are ' + c.join(' → ') +
          ' — not strictly falling. The loop may be generating its own findings rather than ' +
          'converging. This is the signal to inspect, not to raise the cap.')
    }
  }
}

// ----------------------------------------------------------------- report

const summary = trend.map(function (t) {
  return '  round ' + t.round + ': ' + t.verdict + ' — ' + t.blocking + ' blocking, ' +
         t.corrections + ' corrections, ' + t.notes + ' notes' +
         (t.newProse === undefined ? '' : '; fix-up added ' + t.newProse + ' lines, §5 ' +
          (t.s5unchanged ? 'unchanged' : 'CHANGED'))
}).join('\n')

log('\n=== Phase ' + PHASE + ' verify loop finished: ' + outcome + ' ===\n' + summary)

if (outcome !== 'PASS') {
  log('\n**No PASS was reached.** This is reported as a fact, not softened. The last verdict was ' +
      lastVerdict + '. Read the trend above: if corrections are not falling round over round, the ' +
      'convergence levers are not working, and that is the finding — not a reason to raise the cap.')
}

return {
  outcome: outcome,
  phase: PHASE,
  doc: DOC,
  lastVerdict: lastVerdict,
  roundsRun: trend.length,
  lastRound: round,
  trend: trend,
  preset: A.preset || 'lean',
}
