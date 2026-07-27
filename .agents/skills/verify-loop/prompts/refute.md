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
