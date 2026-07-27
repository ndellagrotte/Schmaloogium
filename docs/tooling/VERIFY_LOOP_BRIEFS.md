# Verification prompt source map

The active verification workflow is Codex-native and has no manually synchronized readable prompt
copy. Its canonical sources are:

- `.agents/skills/verify-loop/prompts/*.md` — role prompts;
- `.agents/skills/verify-loop/schemas/*.schema.json` — target and structured-return contracts;
- `.agents/skills/verify-loop/scripts/engine.mjs` — stage barriers, deterministic aggregation,
  citation resolution, stop conditions, journals, and write/immutable enforcement;
- `verification/policy.json` — presets, survival/severity, PASS, convergence, and estimates;
- `verification/lenses/*.json` — reusable attack-lens sets;
- `verification/targets/*.json` — target-specific data.

Read [CODEX_VERIFICATION.md](CODEX_VERIFICATION.md) for arguments, examples, cost/concurrency
expectations, dry-run behavior, validation, and partial-failure recovery.

The retired provider-era prompt narrative is preserved with a migration annotation at
`docs/tooling/history/CLAUDE_VERIFY_LOOP_BRIEFS.md` as historical evidence. It is not executable,
operational, or canonical.
