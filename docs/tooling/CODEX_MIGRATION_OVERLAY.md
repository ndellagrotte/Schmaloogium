# Codex migration overlay for immutable governance documents

The governing design revisions are heavily cited evidence. Their provider-era execution wording is
therefore preserved byte-for-byte rather than rewritten and shifting line coordinates. This overlay
is the sanctioned interpreter for that wording: it supersedes the retired execution surface without
editing a single cited byte.

For all active work after this migration:

- “fresh Claude Code session” means a fresh, isolated agent session with the same independence and
  reading-order constraints as the design text requires.
- **The mechanized verification loop is re-mechanized on omp (2026-08-08).** The Codex
  implementation was retired earlier that day; design pointers therefore split by age. Pointers to
  Codex-specific machinery — `codex exec` and its sandbox/output-schema flags,
  `.claude/commands/verify-loop.md`, and `docs/tooling/CODEX_VERIFICATION.md` — name execution
  surface that no longer exists. Pointers to `scripts/verify`, the `$verify-loop` skill, and
  `verification/targets/` resolve to the omp loop: `scripts/verify` +
  `.agents/skills/verify-loop/` + the operator runbook `docs/tooling/OMP_VERIFICATION.md`.
  (`docs/tooling/VERIFY_LOOP_BRIEFS.md` stays retired; the runbook subsumes it.) §G1.2 verify
  sessions and §G1.3 fix-up sessions are executed by that loop, or — as a documented fallback when
  the loop cannot run — by hand as fresh agent sessions, following the design text's own rules:
  the §G1.2 reading list and attack checks, repo-relative citations quoted at the line, the
  forbidden-sources pattern, one review file per round with exactly one verdict, and
  `## Resolutions` appended by the fix-up. The loop's internal stage decomposition (Attack →
  Refute → Steelman → Gate → Adjudicate → Fix-up) remains harness machinery, not design text, and
  binds nothing beyond the evidence rules themselves.
- Provider-era role-executor names (Codex sandboxes, `codex exec --output-schema`) are superseded
  by the omp harness: read-only roles run under a read/grep/glob tool allowlist with hook-enforced
  read denial of forbidden sources and prior reviews, writer roles add write/edit behind a
  pre-execution allowlist hook, and structured results arrive through one terminal `yield`
  re-validated against the role schema. The deterministic post-stage write checks (worktree-hash
  allowlist + immutable-evidence recheck) run inside the omp loop exactly as before; hand-run
  fallback sessions honor the same evidence rules by discipline rather than by engine enforcement.
- The retired provider-era prompt narrative is preserved, quarantined, at
  `docs/tooling/history/CLAUDE_VERIFY_LOOP_BRIEFS.md` as historical evidence. It is not
  executable, operational, or canonical.

This overlay changes only the execution surface. It does not revise design decisions, evidence,
citations, session history, verdicts, or the meaning of §G1.1–§G1.3.