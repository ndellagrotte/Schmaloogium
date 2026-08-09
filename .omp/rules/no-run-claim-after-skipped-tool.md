---
name: no-run-claim-after-skipped-tool
description: "Never report a skipped/queued tool result as in-flight or as unstarted-without-alarm — immediately retry the skipped call in the same turn"
condition: ["[Ss]kipped due to queued user message", "[Nn]o run in flight", "[Nn]ext action, unstarted", "was skipped before it started", "\\b[Ss]ay go and I'?ll start it\\b"]
scope: "text"
---

A tool result of `Skipped due to queued user message` means the command **never executed**. That is not a status to narrate — it is work you still owe.

When the user's last authorization still stands (they already answered the gating question, or explicitly issued the command), you MUST retry the skipped tool call in the very next turn, in the same message as the status update. Do NOT:

- Describe a long-running job as "unstarted" and then stop.
- Ask for authorization a second time for work already authorized.
- Let the user believe time passed with work in progress.

Lead the status update with the fact that nothing ran and that you are restarting it **now**, then issue the call. If the queued user message actually revokes or changes the authorization, say so explicitly and do not silently drop the command.

Silence about a non-execution reads as execution. Never leave a user waiting on a process that does not exist.