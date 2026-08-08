# Codex migration overlay for immutable governance documents

The governing design revisions are heavily cited evidence. Their provider-era execution wording is
therefore preserved byte-for-byte rather than rewritten and shifting line coordinates. This overlay
is the sanctioned interpreter for that wording: it supersedes the retired execution surface without
editing a single cited byte.

For all active work after this migration:

- “fresh Claude Code session” means a fresh, isolated agent session with the same independence and
  reading-order constraints as the design text requires.
- **The mechanized verification loop is retired (2026-08-08).** Any design pointer to the
  verification harness — the older `.claude/commands/verify-loop.md` operator runbook and the
  `/verify-loop` workflow, or the later omp-port invocation `scripts/verify` and the `$verify-loop`
  skill with its `verification/targets/`, `docs/tooling/VERIFY_LOOP_BRIEFS.md`, and
  `docs/tooling/CODEX_VERIFICATION.md` companions — refers to execution surface that no longer
  exists. It is not recreated. §G1.2 verify sessions and §G1.3 fix-up sessions are run by hand as
  fresh agent sessions, following the design text's own rules: the §G1.2 reading list and attack
  checks, repo-relative citations quoted at the line, the forbidden-sources pattern, one review
  file per round with exactly one verdict, and `## Resolutions` appended by the fix-up. The retired
  loop's internal stage decomposition (Attack → Refute → Steelman → Gate → Adjudicate → Fix-up)
  was harness machinery, not design text, and binds nothing.
- Provider-era role-executor names (Codex sandboxes, `omp-agent-runner.mjs`) are superseded by the
  current agent harness: read-only work runs under a write-free tool allowlist, writer work under
  the full tool set; the deterministic post-stage write checks were engine properties of the
  retired loop and are not reproduced by hand.
- The retired provider-era prompt narrative is preserved, quarantined, at
  `docs/tooling/history/CLAUDE_VERIFY_LOOP_BRIEFS.md` as historical evidence. It is not
  executable, operational, or canonical.

This overlay changes only the execution surface. It does not revise design decisions, evidence,
citations, session history, verdicts, or the meaning of §G1.1–§G1.3.