// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl.record;

import com.schmaloogium.engine.gl.FramebufferDrawSlot;
import com.schmaloogium.engine.gl.FramebufferTarget;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Headless assertions over a recorded call log (PHASE_1_DOC §4.7.5): the shared
 * assertions the seven named recorder tests are written against. Each check inspects
 * only the rendered-stable record — operation names, argument identities and pure
 * values — so it runs with no GL context and no device. All methods are fluent; the
 * first violated expectation throws {@link AssertionError}.
 *
 * <p>Semantics: {@link #calledInOrder} is a subsequence match (calls between the
 * matched ops are allowed); {@link #neverCalled} is exact operation absence;
 * {@link #noUseAfterDelete} inspects handle identity between {@code shaders.delete} and
 * later {@code shaders.use} events (handle-free fixed-function selection is always
 * legal); {@link #bindsBalanced} requires every {@code framebuffers.bind} obligation to
 * be closed by a matching {@code framebuffers.bindDefault} by log end;
 * {@link #noLeakedObjects} pairs creation verbs ({@code shaders.createProgram},
 * {@code shaders.createShader}, {@code textures.create}, {@code framebuffers.create})
 * against the delete verbs — borrowed-depth issuance is deliberately not a creation op
 * and borrowed markers are never deletable, so neither appears.
 */
public final class ReplayAssertions {

    private final List<GLCall> calls;

    private ReplayAssertions(List<GLCall> calls) {
        this.calls = List.copyOf(calls);
    }

    /** Assertions over a device's log. */
    public static ReplayAssertions of(GLCallLog log) {
        return new ReplayAssertions(log.calls());
    }

    /** Assertions over a hand-built or filtered call list (e.g. forged violating logs). */
    public static ReplayAssertions of(List<GLCall> calls) {
        return new ReplayAssertions(calls);
    }

    /** Every named operation appears in the given order (calls between are allowed). */
    public ReplayAssertions calledInOrder(String... opNames) {
        int at = 0;
        for (String want : opNames) {
            while (at < calls.size() && !calls.get(at).op().equals(want)) {
                at++;
            }
            if (at == calls.size()) {
                throw new AssertionError(
                        "expected a call to " + want + " after position " + at
                                + "; the log holds " + calls.size() + " calls");
            }
            at++;
        }
        return this;
    }

    /** The named operation never appears in the log, not even once. */
    public ReplayAssertions neverCalled(String opName) {
        for (GLCall call : calls) {
            if (call.op().equals(opName)) {
                throw new AssertionError(
                        opName + " was called at index " + calls.indexOf(call)
                                + " but the test requires it never be called");
            }
        }
        return this;
    }

    /** No {@code shaders.use} names a program handle deleted earlier in the log. */
    public ReplayAssertions noUseAfterDelete() {
        Set<Object> deleted = new HashSet<>();
        for (GLCall call : calls) {
            switch (call.op()) {
                case "shaders.delete" -> {
                    if (!call.args().isEmpty()) {
                        deleted.add(call.args().get(0));
                    }
                }
                case "shaders.use" -> {
                    for (Object arg : call.args()) {
                        if (deleted.contains(arg)) {
                            throw new AssertionError(
                                    "shaders.use carries a handle already deleted: "
                                            + String.valueOf(arg));
                        }
                    }
                }
                default -> {
                }
            }
        }
        return this;
    }

    /** Every framebuffer bind obligation is closed by a matching bindDefault by log end. */
    public ReplayAssertions bindsBalanced() {
        boolean readOpen = false;
        boolean drawOpen = false;
        for (GLCall call : calls) {
            switch (call.op()) {
                case "framebuffers.bind" -> {
                    FramebufferTarget target = (FramebufferTarget) call.args().get(0);
                    readOpen |= target == FramebufferTarget.READ
                            || target == FramebufferTarget.READ_AND_DRAW;
                    drawOpen |= target == FramebufferTarget.DRAW
                            || target == FramebufferTarget.READ_AND_DRAW;
                }
                case "framebuffers.bindDefault" -> {
                    FramebufferTarget target = (FramebufferTarget) call.args().get(0);
                    readOpen &= !(target == FramebufferTarget.READ
                            || target == FramebufferTarget.READ_AND_DRAW);
                    drawOpen &= !(target == FramebufferTarget.DRAW
                            || target == FramebufferTarget.READ_AND_DRAW);
                }
                default -> {
                }
            }
        }
        if (readOpen || drawOpen) {
            throw new AssertionError("unresolved framebuffer bind obligations at log end: "
                    + (readOpen ? "READ " : "") + (drawOpen ? "DRAW" : ""));
        }
        return this;
    }

    /** Every created object is deleted; nothing is deleted that was not created. */
    public ReplayAssertions noLeakedObjects() {
        Set<Object> created = new LinkedHashSet<>();
        Set<Object> deleted = new LinkedHashSet<>();
        for (GLCall call : calls) {
            switch (call.op()) {
                case "shaders.createProgram", "shaders.createShader", "textures.create",
                        "framebuffers.create" ->
                        created.add(call.args().get(0));
                case "shaders.delete", "textures.delete", "framebuffers.delete" -> {
                    if (!created.contains(call.args().get(0))) {
                        throw new AssertionError("delete of an object this recorder never "
                                + "created: " + String.valueOf(call.args().get(0)));
                    }
                    deleted.add(call.args().get(0));
                }
                default -> {
                }
            }
        }
        created.removeAll(deleted);
        if (!created.isEmpty()) {
            throw new AssertionError("leaked objects (created but never deleted): " + created);
        }
        return this;
    }

    /** The last {@code framebuffers.drawBuffers} call recorded exactly these slots. */
    public ReplayAssertions drawBuffersWere(List<FramebufferDrawSlot> expected) {
        GLCall last = null;
        for (GLCall call : calls) {
            if (call.op().equals("framebuffers.drawBuffers")) {
                last = call;
            }
        }
        if (last == null) {
            throw new AssertionError("framebuffers.drawBuffers was never called");
        }
        Object recorded = last.args().get(1);
        if (!expected.equals(recorded)) {
            throw new AssertionError(
                    "drawBuffers recorded " + recorded + " but expected " + expected);
        }
        return this;
    }

    /** The standard whole-render verdict: nothing leaked, balanced binds, legal use order. */
    public ReplayAssertions invariants() {
        return noLeakedObjects().bindsBalanced().noUseAfterDelete();
    }
}
