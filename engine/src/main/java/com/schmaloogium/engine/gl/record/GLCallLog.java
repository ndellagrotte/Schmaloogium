// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl.record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The deterministic per-frame call log (PHASE_1_DOC §4.7.5). One {@link GLCall} per
 * facade call, in call order. {@link #render()} is stable and deterministic — no
 * timestamps, no identity hash codes, no iteration-order dependence — which is what
 * makes it usable as a golden file and what makes byte-equality across frames the
 * stable-frame signal. That is a constraint, not an implementation note.
 *
 * <p>Bounded logs keep at most {@code capacity} calls and discard the OLDEST beyond it,
 * recording how many were dropped so {@link #render()} can say so rather than lie by
 * omission. Tests use {@link #unbounded()}; the live {@code -Dschmaloogium.debug.recordGL}
 * decorator (§4.9.3) uses a bounded ring, because a GL call log over a real session is
 * otherwise unbounded memory growth on the one hot path §7 identifies.
 */
public final class GLCallLog {

    /** Stable, renderer-visible name of a recorder-minted object (§4.7.5). */
    interface Named {
        String stableName();
    }

    private final int capacity;
    private final List<GLCall> calls = new ArrayList<>();
    private final Map<Object, String> aliases = new HashMap<>();
    private int dropped;

    private GLCallLog(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive: " + capacity);
        }
        this.capacity = capacity;
    }

    /** The counterpart tests use: an effectively-unbounded log. */
    public static GLCallLog unbounded() {
        return new GLCallLog(Integer.MAX_VALUE);
    }

    /** Bounded ring keeping the newest {@code capacity} calls (§4.7.5). */
    public static GLCallLog bounded(int capacity) {
        return new GLCallLog(capacity);
    }

    /** Every recorded call, oldest first; unmodifiable view. */
    public List<GLCall> calls() {
        return Collections.unmodifiableList(calls);
    }

    /** Every recorded call whose operation name starts with {@code opPrefix}. */
    public List<GLCall> callsMatching(String opPrefix) {
        List<GLCall> out = new ArrayList<>();
        for (GLCall call : calls) {
            if (call.op().startsWith(opPrefix)) {
                out.add(call);
            }
        }
        return List.copyOf(out);
    }

    /** How many older calls a bounded log has discarded so far. */
    public int droppedCallCount() {
        return dropped;
    }

    /**
     * One call per line, oldest first, each line terminated by LF. When calls were
     * dropped, a leading {@code # dropped N older calls} line says so rather than lying
     * by omission (§4.7.5). No timestamps, no identity hashes.
     */
    public String render() {
        StringBuilder out = new StringBuilder();
        if (dropped > 0) {
            out.append("# dropped ").append(dropped).append(" older calls\n");
        }
        for (GLCall call : calls) {
            out.append(call.render(this)).append('\n');
        }
        return out.toString();
    }

    void append(GLCall call) {
        if (calls.size() >= capacity) {
            calls.remove(0);
            dropped++;
        }
        calls.add(call);
    }

    /** Registers a stable renderer name for a foreign (non-minted) argument value. */
    void alias(Object value, String name) {
        aliases.put(value, name);
    }

    /** Deterministic argument rendering; never emits identity hashes (§4.7.5). */
    String renderArg(Object arg) {
        if (arg == null) {
            return "absent";
        }
        if (arg instanceof Named named) {
            return named.stableName();
        }
        String alias = aliases.get(arg);
        if (alias != null) {
            return alias;
        }
        if (arg instanceof String s) {
            return s;
        }
        if (arg instanceof Integer || arg instanceof Long || arg instanceof Boolean) {
            return arg.toString();
        }
        if (arg instanceof Float f) {
            return f.toString();
        }
        if (arg instanceof Enum<?> e) {
            return e.name();
        }
        if (arg instanceof Optional<?> optional) {
            return optional.isEmpty() ? "Optional.empty"
                    : "Optional[" + renderArg(optional.get()) + "]";
        }
        if (arg instanceof List<?> list) {
            StringBuilder out = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    out.append(", ");
                }
                out.append(renderArg(list.get(i)));
            }
            return out.append(']').toString();
        }
        // Engine records and other pure values: their toString is deterministic by
        // contract; the recorder never appends bulk bytes or unrenderable objects.
        return String.valueOf(arg);
    }
}
