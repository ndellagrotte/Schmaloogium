// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.gl.VertexBindMode;
import com.schmaloogium.engine.gl.VertexBindRejection;
import com.schmaloogium.engine.gl.VertexBindResult;
import com.schmaloogium.engine.gl.VertexBinding;
import com.schmaloogium.engine.gl.VertexInputService;
import com.schmaloogium.engine.gl.VertexSource;
import com.schmaloogium.engine.vertex.AttributePointer;
import com.schmaloogium.engine.vertex.VertexGeometryInput;
import com.schmaloogium.engine.vertex.VertexInputPlan;
import com.schmaloogium.engine.vertex.VertexLayout;
import com.schmaloogium.engine.vertex.ConventionalInput;
import com.schmaloogium.engine.vertex.Delivery;
import com.schmaloogium.engine.vertex.StorageType;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * The bounded vertex-input service (PHASE_1_DOC §4.7.6, R10-1/D-P1-50). Requests an
 * input-state boundary, not buffer ownership: field offsets, component counts and the
 * net byte width must carry overflow-safe bounds against the source range. Rejections
 * ({@link VertexBindRejection}) are mutation-free and leave no native state; a
 * {@link VertexBindResult.Failed} carries a diagnostic id and implies the backend already
 * restored the saved predecessor.
 *
 * <p>Opaque LIFO render-thread restoration obligation: {@code restore} accepts only the
 * same device's live top binding. One binding is valid until it is consumed by exactly
 * one successful restore; replay does not repeat it.
 */
final class Lwjgl3VertexInputService implements VertexInputService {

    private final Lwjgl3GLDevice device;

    /** The single live top binding, LIFO by contract - one slot is the whole stack top. */
    private Lwjgl3VertexBinding top;

    Lwjgl3VertexInputService(Lwjgl3GLDevice device) {
        this.device = device;
    }

    // ------------------------------------------------------------- bind

    @Override
    public VertexBindResult bind(VertexSource source, VertexLayout layout,
                                 VertexInputPlan plan, VertexBindMode mode) {
        if (!onRenderThread()) {
            return new VertexBindResult.Rejected(VertexBindRejection.WRONG_THREAD);
        }
        if (!(source instanceof IssuedSource issued) || issued.owner != device) {
            return new VertexBindResult.Rejected(VertexBindRejection.INVALID_SOURCE);
        }
        if (issued.retired) {
            return new VertexBindResult.Rejected(VertexBindRejection.STALE_SOURCE);
        }
        if (layout == null) {
            return new VertexBindResult.Rejected(VertexBindRejection.INVALID_LAYOUT);
        }
        if (plan == null) {
            return new VertexBindResult.Rejected(VertexBindRejection.INVALID_PLAN);
        }
        if (!layoutCompatible(layout, plan)) {
            return new VertexBindResult.Rejected(VertexBindRejection.INVALID_PLAN);
        }
        if (!sourceFitsRange(issued, layout, plan)) {
            return new VertexBindResult.Rejected(VertexBindRejection.OUT_OF_RANGE);
        }
        if (!modeCompatible(issued, mode)) {
            return new VertexBindResult.Rejected(VertexBindRejection.UNSUPPORTED_INPUT);
        }
        if (!geometryCompatible(plan, mode)) {
            return new VertexBindResult.Rejected(VertexBindRejection.UNSUPPORTED_INPUT);
        }
        try {
            Lwjgl3VertexBinding binding = issue(issued, layout, plan, mode);
            top = binding;
            return new VertexBindResult.Bound(binding);
        } catch (RuntimeException e) {
            // Transaction failure: the backend already restored its saved predecessor
            // internally; report and leave the predecessor in place.
            return new VertexBindResult.Failed("gl.vertex.bind." + System.identityHashCode(e));
        }
    }

    // ------------------------------------------------------------- restore

    @Override
    public void restore(VertexBinding binding) {
        device.requireRenderThread("vertexInput.restore");
        if (!(binding instanceof Lwjgl3VertexBinding b) || b.owner != device) {
            throw new IllegalArgumentException("vertexInput.restore: foreign binding (wrong device or backend)");
        }
        if (b != top) {
            throw new IllegalStateException(
                    "vertexInput.restore: binding is not the live top of the restore stack");
        }
        b.restoreOnce(); // consumed-once: repeated restore attempts throw
        top = null;
        device.noteMutation("vertexInput.restore", "(vertex binding)");
    }

    // ------------------------------------------------------------- issuance

    /**
     * The one place input state is installed. Snapshots the isolation set, enables and
     * installs the admitted descriptors, and rolls the predecessor back on any failure.
     */
    private Lwjgl3VertexBinding issue(IssuedSource issued, VertexLayout layout,
                                      VertexInputPlan plan, VertexBindMode mode) {
        Predecessor saved = capturePredecessor(plan);
        try {
            install(saved, issued, layout, plan, mode);
        } catch (RuntimeException e) {
            saved.restoreAll();
            throw e;
        }
        return new Lwjgl3VertexBinding(device, saved, plan);
    }

    private void install(Predecessor saved, IssuedSource issued, VertexLayout layout,
                         VertexInputPlan plan, VertexBindMode mode) {
        int savedClientUnit = GL11.glGetInteger(GL13.GL_CLIENT_ACTIVE_TEXTURE);
        try {
            if (mode == VertexBindMode.LIVE_DRAW) {
                for (AttributePointer p : plan.pointers()) {
                    enableGenericArray(p.location(), true);
                    GL20.glVertexAttribPointer(p.location(), p.components(),
                            glStorage(p.storage()), p.normalized(), layout.strideBytes(),
                            issued.rangeOffset() + p.byteOffset());
                }
                for (ConventionalInput input : plan.conventionalInputs()) {
                    installConventional(input, issued);
                }
            } else if (mode == VertexBindMode.LIST_CAPTURE) {
                for (ConventionalInput input : plan.conventionalInputs()) {
                    installConventional(input, issued);
                }
            } else { // LIST_REPLAY_GUARD: guard/current-value scope only, no pointers
                return;
            }
            disableUnadmitted(saved, plan);
        } finally {
            GL13.glClientActiveTexture(savedClientUnit);
        }
    }

    private void installConventional(ConventionalInput input, IssuedSource issued) {
        long base = issued.rangeOffset();
        switch (input) {
            case POSITION -> {
                GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, base);
            }
            case COLOR -> {
                GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
                GL11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 4, base);
            }
            case NORMAL -> {
                GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
                GL11.glNormalPointer(GL11.GL_BYTE, 3, base);
            }
            case UV0 -> {
                GL13.glClientActiveTexture(GL13.GL_TEXTURE0);
                GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 8, base);
            }
            case UV1 -> {
                GL13.glClientActiveTexture(GL13.GL_TEXTURE1);
                GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                GL11.glTexCoordPointer(2, GL11.GL_SHORT, 4, base);
            }
        }
    }

    private void enableGenericArray(int location, boolean enable) {
        if (enable) {
            GL20.glEnableVertexAttribArray(location);
        } else {
            GL20.glDisableVertexAttribArray(location);
        }
    }

    /** Disables every enabled array the plan does not admit (the isolation set). */
    private void disableUnadmitted(Predecessor saved, VertexInputPlan plan) {
        Set<ConventionalInput> admitted = plan.conventionalInputs();
        for (ConventionalState s : saved.conventional) {
            if (s.enabled && !admitted.contains(s.input)) {
                applyConventionalEnable(s.input, false);
            }
        }
        for (GenericState s : saved.generic) {
            if (s.enabled && plan.pointers().stream().noneMatch(p -> p.location() == s.location)) {
                GL20.glDisableVertexAttribArray(s.location);
            }
        }
    }

    private static void applyConventionalEnable(ConventionalInput input, boolean enable) {
        switch (input) {
            case POSITION -> {
                if (enable) {
                    GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                } else {
                    GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
                }
            }
            case COLOR -> {
                if (enable) {
                    GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
                } else {
                    GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
                }
            }
            case NORMAL -> {
                if (enable) {
                    GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
                } else {
                    GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
                }
            }
            case UV0 -> {
                GL13.glClientActiveTexture(GL13.GL_TEXTURE0);
                if (enable) {
                    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                } else {
                    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                }
            }
            case UV1 -> {
                GL13.glClientActiveTexture(GL13.GL_TEXTURE1);
                if (enable) {
                    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                } else {
                    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                }
            }
        }
    }

    // ------------------------------------------------------------- predecessor

    /** The captured state one successful bind must be able to put back exactly. */
    static final class Predecessor {
        final List<ConventionalState> conventional = new ArrayList<>();
        final List<GenericState> generic = new ArrayList<>();
        final int clientUnit;

        Predecessor(List<ConventionalState> conventional, List<GenericState> generic, int clientUnit) {
            this.conventional.addAll(conventional);
            this.generic.addAll(generic);
            this.clientUnit = clientUnit;
        }

        void restoreAll() {
            for (ConventionalState s : conventional) {
                applyConventionalEnable(s.input, s.enabled);
            }
            for (GenericState s : generic) {
                if (s.enabled) {
                    GL20.glEnableVertexAttribArray(s.location);
                } else {
                    GL20.glDisableVertexAttribArray(s.location);
                }
            }
            GL13.glClientActiveTexture(clientUnit);
        }
    }

    private record ConventionalState(ConventionalInput input, boolean enabled) {
    }

    private record GenericState(int location, boolean enabled) {
    }

    private Predecessor capturePredecessor(VertexInputPlan plan) {
        List<ConventionalState> conv = new ArrayList<>();
        for (ConventionalInput input : EnumSet.allOf(ConventionalInput.class)) {
            conv.add(new ConventionalState(input, isEnabled(input)));
        }
        List<GenericState> gen = new ArrayList<>();
        int count = device.capabilities().maxVertexAttribs();
        for (int i = 0; i < count; i++) {
            gen.add(new GenericState(i, genericEnabled(i)));
        }
        return new Predecessor(conv, gen, GL11.glGetInteger(GL13.GL_CLIENT_ACTIVE_TEXTURE));
    }

    private static boolean genericEnabled(int location) {
        return GL20.glGetVertexAttribi(location, GL20.GL_VERTEX_ATTRIB_ARRAY_ENABLED) != 0;
    }

    private boolean isEnabled(ConventionalInput input) {
        return switch (input) {
            case POSITION -> GL11.glIsEnabled(GL11.GL_VERTEX_ARRAY);
            case COLOR -> GL11.glIsEnabled(GL11.GL_COLOR_ARRAY);
            case NORMAL -> GL11.glIsEnabled(GL11.GL_NORMAL_ARRAY);
            case UV0 -> {
                GL13.glClientActiveTexture(GL13.GL_TEXTURE0);
                yield GL11.glIsEnabled(GL11.GL_TEXTURE_COORD_ARRAY);
            }
            case UV1 -> {
                GL13.glClientActiveTexture(GL13.GL_TEXTURE1);
                yield GL11.glIsEnabled(GL11.GL_TEXTURE_COORD_ARRAY);
            }
        };
    }

    // ------------------------------------------------------------- admission checks

    private boolean layoutCompatible(VertexLayout layout, VertexInputPlan plan) {
        return layout.fingerprint().equals(plan.layoutFingerprint());
    }

    private boolean sourceFitsRange(IssuedSource issued, VertexLayout layout, VertexInputPlan plan) {
        long needed = 0;
        for (AttributePointer p : plan.pointers()) {
            long end = (long) p.byteOffset() + p.components() + (long) layout.strideBytes();
            needed = Math.max(needed, end);
        }
        needed += (long) Math.max(0, issued.vertexCount() - 1) * layout.strideBytes();
        return issued.rangeOffset() + needed <= issued.rangeLength();
    }

    private boolean modeCompatible(IssuedSource issued, VertexBindMode mode) {
        return switch (mode) {
            case LIVE_DRAW -> issued.kind == IssuedKind.CLIENT_RANGE || issued.kind == IssuedKind.BORROWED_VBO;
            case LIST_CAPTURE -> issued.kind == IssuedKind.CLIENT_RANGE;
            case LIST_REPLAY_GUARD -> issued.kind == IssuedKind.DISPLAY_LIST_REPLAY;
        };
    }

    private boolean geometryCompatible(VertexInputPlan plan, VertexBindMode mode) {
        if (mode != VertexBindMode.LIVE_DRAW && mode != VertexBindMode.LIST_REPLAY_GUARD) {
            return true; // capture-time has no active linked geometry requirement
        }
        return plan.expectedGeometryInput() == device.activeGeometryRequirement();
    }

    private boolean onRenderThread() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc != null && mc.isCallingFromMinecraftThread();
    }

    private static int glStorage(StorageType storage) {
        return switch (storage) {
            case FLOAT32 -> GL11.GL_FLOAT;
            case UINT8 -> GL11.GL_UNSIGNED_BYTE;
            case INT8 -> GL11.GL_BYTE;
            case INT16 -> GL11.GL_SHORT;
        };
    }

    // ------------------------------------------------------------- issued sources

    /** Marker-backed issuances; the engine's sub-interfaces are non-sealed on purpose. */
    static final class IssuedClientRange extends IssuedSource
            implements com.schmaloogium.engine.gl.VertexSource.ClientRange {
        IssuedClientRange(Lwjgl3GLDevice owner, IssuedKind kind, long rangeOffset,
                          long rangeLength, int vertexCount, ByteBuffer clientBytes) {
            super(owner, kind, rangeOffset, rangeLength, vertexCount, clientBytes);
        }
    }

    static final class IssuedBorrowedVbo extends IssuedSource
            implements com.schmaloogium.engine.gl.VertexSource.BorrowedVbo {
        IssuedBorrowedVbo(Lwjgl3GLDevice owner, IssuedKind kind, long rangeOffset,
                          long rangeLength, int vertexCount, ByteBuffer clientBytes) {
            super(owner, kind, rangeOffset, rangeLength, vertexCount, clientBytes);
        }
    }

    static final class IssuedDisplayListReplay extends IssuedSource
            implements com.schmaloogium.engine.gl.VertexSource.DisplayListReplay {
        IssuedDisplayListReplay(Lwjgl3GLDevice owner, IssuedKind kind, long rangeOffset,
                                long rangeLength, int vertexCount, ByteBuffer clientBytes) {
            super(owner, kind, rangeOffset, rangeLength, vertexCount, clientBytes);
        }
    }

    enum IssuedKind {
        CLIENT_RANGE, BORROWED_VBO, DISPLAY_LIST_REPLAY
    }

    /**
     * The backend's own issued source: carries the issuer token (D-P1-50) privately, so
     * the future P10 adapter (implementing the engine's sealed sub-interfaces) can
     * delegate. Never handed to the engine in this form at v0.1.
     */
    abstract static class IssuedSource {
        final Lwjgl3GLDevice owner;
        final IssuedKind kind;
        final long rangeOffset;
        final long rangeLength;
        final int vertexCount;
        final ByteBuffer clientBytes;
        boolean retired;

        IssuedSource(Lwjgl3GLDevice owner, IssuedKind kind, long rangeOffset,
                     long rangeLength, int vertexCount, ByteBuffer clientBytes) {
            this.owner = owner;
            this.kind = kind;
            this.rangeOffset = rangeOffset;
            this.rangeLength = rangeLength;
            this.vertexCount = vertexCount;
            this.clientBytes = clientBytes;
        }

        long rangeOffset() {
            return rangeOffset;
        }

        long rangeLength() {
            return rangeLength;
        }

        int vertexCount() {
            return vertexCount;
        }

        void retire() {
            retired = true;
        }
    }

}
