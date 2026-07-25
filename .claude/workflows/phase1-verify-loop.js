export const meta = {
  name: 'phase1-verify-loop',
  description: 'Loop §G1.2 adversarial verify → §G1.3 fix-up on PHASE_1_DOC.md until a review returns a literal PASS',
  whenToUse: 'When Phase 1 should be driven to a PASS verdict without hand-writing a brief per round. Exits only on PASS (zero blocking, zero corrections), on FAIL, or on the round cap.',
  phases: [
    { title: 'Attack', detail: 'read-only finders, one per §G1.2 lens' },
    { title: 'Refute', detail: 'skeptics per candidate, prompted to kill it' },
    { title: 'Steelman', detail: 'opposed lens on surviving corrections' },
    { title: 'Gate', detail: 're-resolve every citation at the line' },
    { title: 'Adjudicate', detail: 'write PHASE_1_REVIEW_<R>.md, exactly one verdict' },
    { title: 'Fix up', detail: 'apply corrections, record Resolutions, prove §5 invariance' },
  ],
}

// ----------------------------------------------------------------- configuration

const DIR = '/home/nick/IdeaProjects/schmaloogium-project/Schmaloogium'

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
const START = A.startRound || 11
const MAX_ROUNDS = A.maxRounds === undefined ? 6 : A.maxRounds
const REVIEW_ONLY = A.reviewOnly === true
const ADDENDUM_LINES = A.addendumLines || 40

const RANK = { none: 0, note: 1, correction: 2, blocking: 3 }
const NAME = ['none', 'note', 'correction', 'blocking']

// ----------------------------------------------------------------- shared prompt blocks
// COMMON and POSTURE are lifted from the round-4 adversarial workflow, which ran 15 agents
// with 0 errors. Do not "improve" them without a reason you can name.

const COMMON = `
You are one agent in an automated **§G1.2 verify round** on the Schmaloogium project.
All files are under \`${DIR}/\`.

## HARD RULES — non-negotiable
- **READ-ONLY.** Do not create, edit, or delete any file. Do not run builds, tests, or gradle.
  Do not run git commands that mutate state (\`git log\`/\`git status\`/\`git show\`/\`git diff\` are fine).
  Do not write via shell redirection, \`tee\`, \`sed -i\`, or any other route. You may use Read, Grep,
  Glob, and read-only Bash. That is all.
- **FORBIDDEN SOURCE.** Do **not** read \`${DIR}/*.txt\`. Those are prior sessions' terminal
  transcripts. §G1.2 bars a reviewer from the author's conversation context because it transmits the
  author's blind spots. A sub-agent broke exactly this rule in round nine; its conclusion was
  discarded and re-derived from permitted sources (\`PHASE_1_REVIEW_9.md\` §0.2). Do not repeat it.
- **No scope creep.** Answer only what you are asked below.
- Your final text is a return value consumed by a program, not a message to a human.

## Ground truth (read what you need, not everything)
- \`DESIGN.md\` — the governance document. §G0.3 ll. 48-56; §G1.1 68-116; §G1.2 118-149;
  §G1.3 151-162; §G4.2 310-316; §G4.3 318-322; §G4.4 324-331; §G4.6 341-348; §G5.1 phase table
  356-377; §G5.3 400-425; §G9 doc template 508-542; §G10 OQ table 544-572. The Part II **Phase 1
  spec** is at ll. ~585-658, and its **Doc gate** at ll. ~649-652.
- \`RESEARCH.md\` — the contract ground truth. §0 reading guide and confidence tags; §1 mission,
  non-goals and the D-1..D-10 decision log; §4.2 the ~90 built-in uniforms and the 43 program slots;
  §4.4 the frame model; §11 the open-question register; App D and App F.
- \`PHASE_1_DOC.md\` — **the document under review.**
- Template ground truth, same directory: \`build.gradle\`, \`settings.gradle\`, \`gradle.properties\`,
  \`gradle/scripts/{dependencies,extra,publishing}.gradle\`, \`src/**\`, \`.github/workflows/*\`,
  \`README.md\`.

## CRITICAL — what does NOT count as work
**You get no credit for confirming that a quote matches a line.** Verifying the anchor is the
starting point, not the finding. Your job is the *interpretive* question underneath it. A separate
Gate agent re-resolves every citation you produce, so a finding that is only an anchor check will be
dropped and will have cost the round nothing but tokens.

## Empty is honest
An empty findings array is a perfectly good outcome. **Do not manufacture findings.** Ten verify
rounds have already run on this document; the failure mode at this maturity is no longer "the
reviewer missed something" but "the reviewer kept the loop alive". Name what you checked and found
sound in \`clean_areas\` — the clean-to-dirty ratio is part of the verdict.
`

const POSTURE = `
## Your posture: REFUTATION
Your job is to **kill** the finding you are given. Assume it is wrong until the source files force
you to concede. Specifically, hunt for these ways a finding can be bad:

1. **Out of scope for this phase.** Phase 1 is "Foundation & project architecture" (§G5.1). Does the
   alleged defect actually belong to a later phase, making the doc's silence correct rather than
   defective? Check §G5.1's owner and §G0.3's architected-now/implemented-later principle.
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
    file: { type: 'string', description: 'filename only, e.g. PHASE_1_DOC.md' },
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
    new_prose_lines: { type: 'integer', description: 'total lines added to PHASE_1_DOC.md this round' },
    weakest_points: { type: 'array', items: { type: 'string' }, description: 'YOUR self-assessment: the judgement calls next round should test. Claims to test, never verdicts to reach.' },
    do_not_refight: { type: 'array', items: { type: 'string' }, description: 'what this round verified as settled, so the next round does not re-derive it' },
    files_modified: { type: 'array', items: { type: 'string' } },
    refused_with_cause: { type: 'string', description: 'any finding declined, and why; empty if none' },
  },
}

// ----------------------------------------------------------------- the five attack lenses
// Ordered by historical yield. `lean` takes the first three.

const LENSES = [
  {
    key: 'new-surface',
    title: 'The new surface',
    body: `The previous fix-up's new prose is the largest unreviewed surface in the document, and
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
    body: `Two §G1.2 checks. **Doc gate:** every criterion in the Phase 1 spec's *Doc gate*
(\`DESIGN.md\` ll. ~649-652) met **literally** — module/package layout finalized with dependency
rules as testable constraints; every D-1..D-10 either satisfied by this phase or explicitly deferred
with its owner phase named; pin table complete with re-verification procedure.

**Template completeness:** all thirteen §G9 sections (ll. 508-542) present *and substantive*, and
every assigned OQ (OQ-2, OQ-12, OQ-20, OQ-21) carrying a full §G4.4 spike spec —
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

log('Phase 1 verify loop — preset `' + (A.preset || 'lean') + '` (' + cfg.finders + ' finders, ' +
    cfg.refuters + ' refuters, steelman ' + (cfg.steelman ? 'on' : 'off') + '), starting at round ' +
    START + ', cap ' + MAX_ROUNDS + '. Exit on PASS = zero blocking AND zero corrections.')

for (let i = 0; i < MAX_ROUNDS; i++) {
  round = START + i

  if (budget.total && budget.remaining() < 60000) {
    log('Stopping before round ' + round + ': token budget nearly exhausted (' +
        Math.round(budget.remaining() / 1000) + 'k left). Not a PASS.')
    outcome = 'BUDGET'
    break
  }

  const REVIEW_FILE = 'PHASE_1_REVIEW_' + round + '.md'
  const PRIOR_REVIEW = 'PHASE_1_REVIEW_' + (round - 1) + '.md'

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
    : [
        '## The new surface',
        '',
        'This is the first round of the automated loop. The document has had ten verify rounds and',
        'seven fix-ups. The most recently written material is the seventh fix-up\'s: §0.10, and the',
        'corrections it applied at §4.2.3, §4.7.4 and §6. **One session has read that prose — the one',
        'that wrote it.** Start there. Its own record of what it changed and where it thinks it is',
        'weakest is in §0.10 and in ' + PRIOR_REVIEW + '\'s `## Resolutions` section — but read the',
        'latter only if this lens is the new-surface lens, and read the document itself first.',
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
        'Read `DESIGN.md` Part I and the Phase 1 spec, then `PHASE_1_DOC.md`, then only the sources\n' +
        'your lens needs. **Do not read any `PHASE_1_REVIEW_*.md` file.** You are an independent\n' +
        'reader, and reading a prior round\'s findings converts you into an auditor of someone else\'s\n' +
        'reasoning. A later Adjudicate agent reads them, last, and dispositions your candidates\n' +
        'against them. If your lens is the new-surface lens you may read ' + PRIOR_REVIEW + '\'s\n' +
        '`## Resolutions` section **only**, and only after your own findings are written down.\n\n' +
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
            'author of `PHASE_1_DOC.md` was a careful architect who had a reason. Construct the\n' +
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
    '\n\n## Read the prior rounds LAST\n' +
    'Now — and only now, with the above in hand — read `' + PRIOR_REVIEW + '` including its\n' +
    '`## Resolutions` section, and grep earlier rounds for anything a candidate above restates.\n' +
    '§G1.2 exists to stop a reviewer inheriting the author\'s frame; reading the resolutions early\n' +
    'would make you an auditor of the last fix-up\'s reasoning instead of an independent reader of\n' +
    'the document it produced. **Disposition each candidate against the settled list: a finding a\n' +
    'prior round already cleared is not a finding, and an argument that holds on derivation is\n' +
    'itself worth recording in §2.**\n\n' +
    '## Re-derive before you admit\n' +
    'The Gate confirmed the anchors. **You** must confirm the interpretation: for every finding you\n' +
    'admit to §1, open the cited text and satisfy yourself the claim is what the source supports.\n' +
    'Sub-agents generate candidates and citations; they do not generate findings. Findings,\n' +
    'severities and the verdict are yours.\n\n' +
    '## The verdict standard — read this twice\n' +
    'Ten verify rounds have run on this document, every one returning PASS-WITH-CORRECTIONS, and the\n' +
    'seventh fix-up already closed the phase under §G1.3\'s "no §5 change outstanding" clause. **The\n' +
    'failure mode at this maturity is no longer "the reviewer misses things" — it is "the reviewer\n' +
    'keeps the loop alive."**\n\n' +
    '**PASS is available and it is the outcome that ends the cadence.** Return PASS when there are\n' +
    'no blocking findings and no corrections. Notes do not block PASS: record them in §1 with their\n' +
    'severity and leave them unapplied. A round that invents a correction to look productive is\n' +
    'worse than useless. **The converse holds just as hard: do not soften a real correction to reach\n' +
    'PASS.** Judge the document in front of you.\n\n' +
    '## Deliverable: `' + REVIEW_FILE + '`\n' +
    'Follow rounds seven through ten\'s established shape:\n' +
    '1. **§0** — what you read and in what order; reads beyond the list with the finding each turned\n' +
    '   on; deviations; network use; and a **sub-agent disclosure** stating that this round ran as an\n' +
    '   automated fan-out of ' + (cfg.finders + candidates.length * cfg.refuters + 1) + '-odd read-only\n' +
    '   agents under a mechanised re-derivation gate, and which candidates the gate dropped.\n' +
    '2. **§1 — Findings**, each with location, claim under test, evidence, severity, and an explicit\n' +
    '   **touches §5: yes/no** line.\n' +
    '3. **§2 — What was checked and came back clean.** Include the candidates that were refuted or\n' +
    '   cleared on your own derivation — a round reporting only findings misrepresents its coverage.\n' +
    '4. **§3 — Verdict**: exactly one of PASS / PASS-WITH-CORRECTIONS / FAIL, with the per-finding §5\n' +
    '   disposition table and the §G1.3 line. Reserve FAIL for structural misses requiring a rebuild.\n\n' +
    'Do not modify `PHASE_1_DOC.md`, `DESIGN.md`, `RESEARCH.md`, or any prior review file — including\n' +
    'their `## Resolutions` sections, which are evidence. Create exactly one file. Then stop.',
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
    'You are a fresh **§G1.3 fix-up session** on the Schmaloogium project. Working directory: `' + DIR + '`.\n\n' +
    'Your contract is §G1.3\'s: read the phase spec, the phase doc and the review file; apply the\n' +
    'corrections to `PHASE_1_DOC.md`; and record each resolution in the review file under a\n' +
    '`## Resolutions` heading. Then stop. **Fix nothing the review ruled clean, and verify nothing\n' +
    'beyond ordinary care** — that is the next round\'s job.\n\n' +
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
    'cd ' + DIR + '\n' +
    'awk \'/^## 5\\. Cross-phase interfaces/,/^## 6\\. Failure modes/\' PHASE_1_DOC.md | sha256sum\n' +
    '```\n' +
    'Run it again after every edit and return **both hashes** plus `section5_unchanged`. If §5 did\n' +
    'change, that is a legitimate outcome — say so truthfully; it means §G1.3\'s re-verify trigger\n' +
    'fires. Never report it unchanged without having run the comparison.\n\n' +
    'Also verify with `git status --short` that you modified **exactly two files**: `PHASE_1_DOC.md`\n' +
    'and `' + REVIEW_FILE + '`. Do not modify `DESIGN.md`, `RESEARCH.md`, or any earlier review file —\n' +
    'including their `## Resolutions` sections, which are evidence. If an earlier review contains an\n' +
    'error, record the correction in your Resolutions; never edit the evidence.\n\n' +
    '## Then report, as data for the next round\n' +
    'Your structured return is what the next verify round is briefed from — it replaces the\n' +
    'hand-written brief. `weakest_points` is the important field: **the judgement calls you made that\n' +
    'an adversarial reader should test.** State them as claims to test, never as verdicts to reach.\n' +
    'A fix-up gets no adversarial review of its own; the next round can only attack reasoning you\n' +
    'wrote down.\n\n' +
    'Re-resolve every line number you cite against the finished file before you write it. Do not read\n' +
    '`' + DIR + '/*.txt` — they are prior sessions\' terminal transcripts.',
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

log('\n=== Phase 1 verify loop finished: ' + outcome + ' ===\n' + summary)

if (outcome !== 'PASS') {
  log('\n**No PASS was reached.** This is reported as a fact, not softened. The last verdict was ' +
      lastVerdict + '. Read the trend above: if corrections are not falling round over round, the ' +
      'convergence levers are not working, and that is the finding — not a reason to raise the cap.')
}

return {
  outcome: outcome,
  lastVerdict: lastVerdict,
  roundsRun: trend.length,
  lastRound: round,
  trend: trend,
  preset: A.preset || 'lean',
}
