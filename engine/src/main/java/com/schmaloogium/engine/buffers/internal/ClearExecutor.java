// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import com.schmaloogium.engine.buffers.ClearExecutionPlan;
import com.schmaloogium.engine.buffers.ClearExecutionResult;
import com.schmaloogium.engine.buffers.ClearRequest;
import com.schmaloogium.engine.buffers.ResourceClearPolicy;
import com.schmaloogium.engine.gl.ColorClearValue;
import com.schmaloogium.engine.gl.FramebufferDrawSlot;
import com.schmaloogium.engine.gl.FramebufferHandle;
import com.schmaloogium.engine.gl.FramebufferStatus;
import com.schmaloogium.engine.gl.GLError;
import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.gl.TextureHandle;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Typed clear planning and execution (PHASE_5_DOC §4.6): plan groups buffers by equal
 * extent + converted typed value + physical side and chunks at
 * {@code min(maxDrawBuffers, maxColorAttachments)}; execute binds each batch once and clears
 * each occupied route position in ascending order through the P1 typed facade. A clear never
 * toggles flip. Side rules: full clear clears sides A AND B; a normal clear with
 * {@code flipped=false} clears only the current read/main side; a normal clear with
 * {@code flipped=true} clears both sides.
 */
public final class ClearExecutor {

    /** Record of one side scheduled for clearing inside one plan. */
    private record SideTarget(EstateCore.ColorPair pair, TextureHandle texture) {
    }

    /** The per-buffer clear enablement from the declarative plan, by row index. */
    private static boolean clearEnabled(EstateCore core, int row) {
        return core.plan.colors().get(row).clear();
    }

    ClearExecutionPlan plan(EstateCore core, ClearRequest request, long openFrameId) {
        Objects.requireNonNull(request);
        if (openFrameId != core.openFrameId || core.openFrameId == -1) {
            return new ClearExecutionPlan(core.generation, core.depthAttachmentEpoch,
                openFrameId, List.of());
        }
        boolean full = request.fullClear() || core.fullClearRequired;
        List<SideTarget> targets = new ArrayList<>();
        for (int row = 0; row < core.colorPairs.size(); row++) {
            EstateCore.ColorPair pair = core.colorPairs.get(row);
            if (!clearEnabled(core, row)) {
                continue;
            }
            if (full) {
                targets.add(new SideTarget(pair, pair.sideA));
                targets.add(new SideTarget(pair, pair.sideB));
            } else if (pair.flipped) {
                targets.add(new SideTarget(pair, pair.sideA));
                targets.add(new SideTarget(pair, pair.sideB));
            } else {
                targets.add(new SideTarget(pair, pair.readSide()));
            }
        }
        if (targets.isEmpty()) {
            return new ClearExecutionPlan(core.generation, core.depthAttachmentEpoch,
                core.openFrameId, List.of());
        }
        // Group by converted typed value; keep first-occurrence order; chunk.
        int chunkLimit = Math.min(core.device.capabilities().maxDrawBuffers(),
            core.device.capabilities().maxColorAttachments());
        Map<String, List<SideTarget>> groups = new LinkedHashMap<>();
        for (SideTarget target : targets) {
            ColorClearValue value = convertedValue(core, target.pair(), request);
            String key = groupKey(value);
            groups.computeIfAbsent(key, ignored -> new ArrayList<>()).add(target);
        }
        List<ClearExecutionPlan.Batch> batches = new ArrayList<>();
        for (List<SideTarget> group : groups.values()) {
            ColorClearValue value = convertedValue(core, group.get(0).pair(), request);
            for (int start = 0; start < group.size(); start += chunkLimit) {
                List<SideTarget> chunk = group.subList(start, Math.min(group.size(),
                    start + chunkLimit));
                List<ColorClearValue> values = new ArrayList<>();
                List<TextureHandle> textures = new ArrayList<>();
                for (SideTarget sideTarget : chunk) {
                    values.add(value);
                    textures.add(sideTarget.texture());
                }
                batches.add(new ClearExecutionPlan.Batch(values, textures));
            }
        }
        return new ClearExecutionPlan(core.generation, core.depthAttachmentEpoch,
            core.openFrameId, batches);
    }

    private ColorClearValue convertedValue(EstateCore core, EstateCore.ColorPair pair,
            ClearRequest request) {
        int row = rowIndex(core, pair.logical);
        ResourceClearPolicy policy = core.plan.colors().get(row).clearPolicy();
        double[] payload = FormatTable.resolvePayload(policy, request);
        FormatTable.ConvertedClear converted = FormatTable.convert(realizedFormat(core, row),
            payload[0], payload[1], payload[2], payload[3]);
        if (converted instanceof FormatTable.ConvertedClear.Floating floating) {
            return new ColorClearValue.Floating(floating.r(), floating.g(), floating.b(),
                floating.a());
        }
        if (converted instanceof FormatTable.ConvertedClear.Signed signed) {
            return new ColorClearValue.Signed(signed.r(), signed.g(), signed.b(), signed.a());
        }
        FormatTable.ConvertedClear.Unsigned unsigned =
            (FormatTable.ConvertedClear.Unsigned) converted;
        return new ColorClearValue.Unsigned(unsigned.r(), unsigned.g(), unsigned.b(),
            unsigned.a());
    }

    private static String groupKey(ColorClearValue value) {
        if (value instanceof ColorClearValue.Floating floating) {
            return "F:" + floating.r() + "," + floating.g() + "," + floating.b() + ","
                + floating.a();
        }
        if (value instanceof ColorClearValue.Signed signed) {
            return "S:" + signed.r() + "," + signed.g() + "," + signed.b() + "," + signed.a();
        }
        ColorClearValue.Unsigned unsigned = (ColorClearValue.Unsigned) value;
        return "U:" + unsigned.r() + "," + unsigned.g() + "," + unsigned.b() + ","
            + unsigned.a();
    }

    private int rowIndex(EstateCore core,
        com.schmaloogium.engine.buffers.LogicalBuffer logical) {
        for (int index = 0; index < core.colorPairs.size(); index++) {
            if (core.colorPairs.get(index).logical.equals(logical)) {
                return index;
            }
        }
        throw new IllegalArgumentException("unknown logical buffer: " + logical);
    }

    static com.schmaloogium.engine.gl.ColorInternalFormat realizedFormat(EstateCore core,
            int row) {
        String name = core.realized.projection().colorBuffers().get(row).allocation()
            .orElseThrow().format();
        return com.schmaloogium.engine.gl.ColorInternalFormat.valueOf(name);
    }

    ClearExecutionResult execute(EstateCore core, ClearExecutionPlan plan) {
        GLDevice device = core.device;
        for (ClearExecutionPlan.Batch batch : plan.batches()) {
            List<TextureHandle> textures = batch.textures();
            String key = clearKey(textures);
            FramebufferHandle fbo = core.clearFbos.get(key);
            if (fbo == null) {
                fbo = device.framebuffers().create(key);
                for (int position = 0; position < textures.size(); position++) {
                    device.framebuffers().attachColor(fbo, position, textures.get(position));
                }
                device.framebuffers().drawBuffers(fbo, denseSlots(textures.size()));
                if (device.framebuffers().check(fbo) != FramebufferStatus.COMPLETE) {
                    core.diagnostics.report(BufferDiagnostics.backendFailure(
                        "schmaloogium.buffers.error.clear.framebuffer", key));
                    return ClearExecutionResult.BACKEND_FAILED;
                }
                core.clearFbos.put(key, fbo);
            }
            device.framebuffers().bind(com.schmaloogium.engine.gl.FramebufferTarget.DRAW, fbo);
            List<ColorClearValue> values = batch.values();
            for (int position = 0; position < textures.size(); position++) {
                device.framebuffers().clearColorAttachment(fbo, position, values.get(position));
            }
            List<GLError> errors = device.drainErrors();
            if (!errors.isEmpty()) {
                core.diagnostics.report(BufferDiagnostics.backendFailure(
                    "schmaloogium.buffers.error.clear.backend", errors.get(0).detail()));
                return ClearExecutionResult.BACKEND_FAILED;
            }
        }
        core.fullClearRequired = false;
        return ClearExecutionResult.SUCCESS;
    }

    private static List<FramebufferDrawSlot> denseSlots(int count) {
        List<FramebufferDrawSlot> slots = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            slots.add(new FramebufferDrawSlot.Attachment(index));
        }
        return slots;
    }

    private static String clearKey(List<TextureHandle> textures) {
        StringBuilder key = new StringBuilder("clear:");
        for (TextureHandle texture : textures) {
            key.append(System.identityHashCode(texture)).append('|');
        }
        return key.toString();
    }
}
