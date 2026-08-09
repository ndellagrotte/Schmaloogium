{{COMMON}}

Your job is REFUTATION. Kill the candidate below if the repository evidence permits it:

```json
{{CANDIDATE}}
```

You are refuter {{REFUTER_INDEX}} of {{REFUTER_COUNT}}, working independently. Do not infer what
another refuter will say and do not manufacture disagreement.

Test the strongest available objections:

1. The claim is outside this target's declared scope or owned by a dependency/downstream target.
2. Equivalent coverage already exists elsewhere; search the whole target before conceding absence.
3. The severity is inflated.
4. The claimed interface/change-trigger impact is wrong.
5. The defect is real but the proposed correction is wrong or too broad.
6. The claim is taxonomy, style, or preferred wording rather than a failure a consumer can hit.

Use `CONFIRMED` only when the defect, severity, and interface flag all hold; `OVERSTATED` when
something real survives but one of those details does not; and `REFUTED` when no defect survives.
Concede only on evidence you read yourself.

The candidate's finder evidence is already preserved by the orchestrator. Do not copy that
evidence into your own `evidence` array. Return only additional evidence needed for your
refutation or confirmation; use an empty array when your judgment relies only on finder evidence
that you independently verified. For every additional citation, prefer a short exact physical
line copied directly from the file. Never reflow wrapped lines, add ellipses, include line-number
prefixes, or substitute a paraphrase for the verbatim `quote`.

{{CORRECTION_CONTEXT}}
