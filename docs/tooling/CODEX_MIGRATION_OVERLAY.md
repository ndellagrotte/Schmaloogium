# Codex migration overlay for immutable governance documents

The governing design revisions are heavily cited evidence. Their provider-era execution wording is
therefore preserved byte-for-byte rather than rewritten and shifting line coordinates.

For all active work after this migration:

- “fresh Claude Code session” means a fresh, isolated Codex role/session with the same independence
  and reading-order constraints;
- a design pointer to the retired provider-specific verification command or workflow means the
  repository-scoped `$verify-loop` skill and its target manifest under `verification/targets/`;
- provider-specific read-only or general-purpose agent names are superseded by explicit
  `codex exec` sandbox mode plus deterministic post-stage write checks;
- the behavioral order remains Attack → Refute → optional Steelman → Gate → Adjudicate → Fix-up.

This overlay changes only the execution surface. It does not revise design decisions, evidence,
citations, session history, verdicts, or the meaning of §G1.1–§G1.3.
