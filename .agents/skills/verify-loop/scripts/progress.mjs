/**
 * Live progress reporting for the verification loop.
 *
 * A paid round is hours of silence otherwise: the engine's stages are barriers
 * around fan-outs of long isolated model sessions, so "nothing printed" and
 * "wedged" look identical from outside. This module turns the run into a
 * stream of structured events — stage entry/exit, every role session's start
 * and finish with its duration, and a heartbeat naming what is still in
 * flight — and renders each one as a single greppable line.
 *
 * Pure: no filesystem, no clock beyond the injectable `now`, no process state.
 * The engine owns the sinks (stderr, run log file, machine consumers); this
 * module only decides what an event is and what it reads like.
 */

export const PROGRESS_MODES = new Set(["human", "json", "none"]);
export const DEFAULT_HEARTBEAT_MS = 30 * 1000;

/** Compact, fixed-width-ish duration: 840ms, 47s, 4m21s, 1h02m. */
export function formatDuration(ms) {
  if (!Number.isFinite(ms) || ms < 0) return "0s";
  if (ms < 1000) return `${Math.round(ms)}ms`;
  const seconds = Math.round(ms / 1000);
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  const rest = seconds % 60;
  if (hours) return `${hours}h${String(minutes).padStart(2, "0")}m`;
  if (minutes) return `${minutes}m${String(rest).padStart(2, "0")}s`;
  return `${rest}s`;
}

/**
 * Display name for a role label. Labels are engine-internal and carry the
 * stage plus the round (`refute:candidate-004:2:R34:correction`), both of
 * which the line prefix already states.
 */
export function shortRoleName(label) {
  const parts = String(label).split(":").filter(Boolean);
  if (!parts.length) return String(label);
  const correction = parts.at(-1) === "correction";
  if (correction) parts.pop();
  if (/^R\d+$/.test(parts.at(-1) ?? "")) parts.pop();
  const [stage, ...rest] = parts;
  const name = rest.length ? rest.join(" #") : stage;
  return correction ? `${name} (correction)` : name;
}

function prefix(event) {
  const round = Number.isInteger(event.round) ? `r${event.round}` : null;
  const scope = [round, event.stage].filter(Boolean).join(" ");
  return scope || "verify";
}

function counts({ done = 0, planned = 0, inFlight = 0 }) {
  const parts = [];
  if (planned > 0) parts.push(`${done}/${planned} done`);
  else if (done > 0) parts.push(`${done} done`);
  parts.push(`${inFlight} in flight`);
  return `[${parts.join(", ")}]`;
}

function withDetail(line, detail) {
  return detail ? `${line} - ${detail}` : line;
}

function withNext(line, next) {
  return next ? `${line}; next ${next}` : line;
}

/** One event -> one line. Returns "" for an event that should not be printed. */
export function renderProgressEvent(event) {
  const at = prefix(event);
  switch (event.kind) {
    case "run-start":
      return `run start: ${event.target} preset ${event.preset}, ${event.mode}, `
        + `${event.rounds} round(s) max from r${event.startRound}`;
    case "run-artifact":
      return `${event.label}: ${event.path}`;
    case "run-end":
      return withDetail(
        `run end: ${event.outcome} after ${event.rounds} round(s) in ${formatDuration(event.ms)}`,
        event.detail,
      );
    case "run-error":
      return `run failed after ${formatDuration(event.ms)}: ${event.message}`;
    case "round-start":
      return `${at}: round ${event.index}/${event.of} starting`;
    case "round-end":
      return withDetail(`${at}: round complete in ${formatDuration(event.ms)}`, event.detail);
    case "stage-start":
      return withDetail(
        `${at}: entering`,
        [
          event.planned === undefined ? null : `${event.planned} session(s)`,
          event.concurrency ? `concurrency ${event.concurrency}` : null,
          event.detail,
        ].filter(Boolean).join(", ") || null,
      );
    case "stage-end":
      return withNext(
        withDetail(`${at}: complete in ${formatDuration(event.ms)}`, event.summary),
        event.next,
      );
    case "stage-skip":
      return withNext(withDetail(`${at}: skipped`, event.reason), event.next);
    case "role-start":
      return `${at}: start ${shortRoleName(event.role)} ${counts(event)}`;
    case "role-end":
      return `${at}: done ${shortRoleName(event.role)} in ${formatDuration(event.ms)}`
        + `${event.detail ? ` - ${event.detail}` : ""} ${counts(event)}`;
    case "role-fail":
      return `${at}: FAILED ${shortRoleName(event.role)} after ${formatDuration(event.ms)} - ${event.message}`;
    case "role-retry":
      // Full label, not the short name: a retry line is the one an operator
      // greps for against the journal's role_retries entries.
      return `${at}: role ${event.role} failed (${event.message}); retrying once with a fresh session`;
    case "role-note":
      return `${at}: ${shortRoleName(event.role)} - ${event.message}`;
    case "heartbeat": {
      const running = event.running ?? [];
      const inFlight = running.length
        ? `${running.length} in flight (${running.map(shortRoleName).join(", ")})`
        : "no session in flight";
      const progressed = event.planned > 0 ? `, ${event.done}/${event.planned} done` : "";
      return `${at}: still running ${formatDuration(event.elapsedMs)} - ${inFlight}${progressed}`;
    }
    case "note":
      return `${at}: ${event.message}`;
    case "warn":
      return `${at}: WARNING ${event.message}`;
    default:
      return "";
  }
}

/**
 * Event emitter with stage/role bookkeeping and a heartbeat timer.
 *
 * `logger` receives rendered lines; `onEvent` receives the structured event.
 * `heartbeatMs <= 0` disables the timer (the test suite's fake runners return
 * instantly, so a heartbeat would only add nondeterminism there).
 */
export function createProgressReporter({
  logger = () => {},
  onEvent = null,
  heartbeatMs = DEFAULT_HEARTBEAT_MS,
  now = () => Date.now(),
} = {}) {
  const startedAt = now();
  let active = null;

  const emit = (event) => {
    const full = { ...event, at: new Date(now()).toISOString() };
    const line = renderProgressEvent(full);
    if (line) logger(line);
    if (onEvent) onEvent(full);
    return full;
  };

  const disarmActive = () => {
    if (active) {
      active.disarm();
      active = null;
    }
  };

  const stage = ({ round, stage: name, planned, concurrency, detail }) => {
    disarmActive();
    const stageStartedAt = now();
    const running = new Map();
    let done = 0;
    let timer = null;

    const handle = {
      round,
      stage: name,
      disarm() {
        if (timer) {
          clearInterval(timer);
          timer = null;
        }
      },
      roleStart(role) {
        running.set(role, now());
        emit({
          kind: "role-start",
          round,
          stage: name,
          role,
          inFlight: running.size,
          done,
          planned,
        });
      },
      roleEnd(role, roleDetail) {
        const from = running.get(role) ?? stageStartedAt;
        running.delete(role);
        done += 1;
        emit({
          kind: "role-end",
          round,
          stage: name,
          role,
          ms: now() - from,
          detail: roleDetail,
          inFlight: running.size,
          done,
          planned,
        });
      },
      roleFail(role, message) {
        const from = running.get(role) ?? stageStartedAt;
        running.delete(role);
        emit({ kind: "role-fail", round, stage: name, role, ms: now() - from, message });
      },
      roleRetry(role, message) {
        emit({ kind: "role-retry", round, stage: name, role, message });
      },
      roleNote(role, message) {
        emit({ kind: "role-note", round, stage: name, role, message });
      },
      note(message) {
        emit({ kind: "note", round, stage: name, message });
      },
      finish({ summary, next } = {}) {
        handle.disarm();
        if (active === handle) active = null;
        emit({ kind: "stage-end", round, stage: name, ms: now() - stageStartedAt, summary, next });
      },
    };

    emit({ kind: "stage-start", round, stage: name, planned, concurrency, detail });
    if (heartbeatMs > 0) {
      // Heartbeats fire even with nothing in flight: a writer stage spends
      // real time inside worktree snapshots and lease waits, which is exactly
      // when silence is least informative.
      timer = setInterval(() => {
        emit({
          kind: "heartbeat",
          round,
          stage: name,
          elapsedMs: now() - stageStartedAt,
          running: [...running.keys()],
          done,
          planned,
        });
      }, heartbeatMs);
      timer.unref?.();
    }
    active = handle;
    return handle;
  };

  return {
    emit,
    stage,
    elapsedMs: () => now() - startedAt,
    note(message, scope = {}) {
      emit({ kind: "note", ...scope, message });
    },
    warn(message, scope = {}) {
      emit({ kind: "warn", ...scope, message });
    },
    skipStage({ round, stage: name, reason, next }) {
      disarmActive();
      emit({ kind: "stage-skip", round, stage: name, reason, next });
    },
    roundStart({ round, index, of }) {
      emit({ kind: "round-start", round, index, of });
    },
    roundEnd({ round, ms, detail }) {
      disarmActive();
      emit({ kind: "round-end", round, ms, detail });
    },
    runStart(fields) {
      emit({ kind: "run-start", ...fields });
    },
    artifact(label, path) {
      emit({ kind: "run-artifact", label, path });
    },
    runEnd({ outcome, rounds, detail }) {
      disarmActive();
      emit({ kind: "run-end", outcome, rounds, ms: now() - startedAt, detail });
    },
    runError(message) {
      disarmActive();
      emit({ kind: "run-error", message, ms: now() - startedAt });
    },
    dispose: disarmActive,
  };
}

/**
 * Console sinks for one run, by `--progress` mode. `human` renders lines with
 * wall clock plus elapsed so a tail correlates with `~/.omp/logs`; `json`
 * emits the structured event. Both write to stderr, keeping stdout free for
 * the run's JSON result.
 */
export function progressSinks(mode, {
  write = (text) => process.stderr.write(text),
  now = () => Date.now(),
} = {}) {
  if (mode === "none") return { logger: () => {} };
  if (mode === "json") {
    return { logger: () => {}, onProgress: (event) => write(`${JSON.stringify(event)}\n`) };
  }
  const startedAt = now();
  return {
    logger: (line) => {
      const clock = new Date(now()).toTimeString().slice(0, 8);
      write(`[${clock} +${formatDuration(now() - startedAt)}] ${line}\n`);
    },
  };
}
