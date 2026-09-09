// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.Diagnostics;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.log.Logs;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

import org.lwjgl.opengl.GLCapabilities;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The native capability probe (PHASE_1_DOC §4.7.2). Runs on the render thread against
 * the live context after display init, queries limits through {@code glGetInteger},
 * enumerates extensions through the GL entry points (LWJGL3's capabilities object is a
 * flat field list, so it serves as the entry-point check instead), and error-checks
 * every query: a failed or advertised-but-broken query means capture fails and no
 * usable profile is published - no exception escapes, the failure is reported through
 * {@link Diagnostics} (boot log + chat), and the bootstrap bails.
 */
public final class CapabilityProbe {

    /** File name of the one-shot §4.7.2 text dump under {@code -Dschmaloogium.debug.dumpCapabilities}. */
    public static final String DUMP_FILE_NAME = "schmaloogium-capabilities.profile";

    private static final Pattern VERSION_PREFIX = Pattern.compile("(\\d+)\\.(\\d+)");

    private CapabilityProbe() {
    }

    /**
     * Captures the live profile, or returns null when capture fails for any reason.
     * Never throws.
     */
    public static GLCapabilityProfile capture() {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc == null || !mc.isCallingFromMinecraftThread() || org.lwjgl.opengl.GL.getCapabilities() == null) {
                Logs.channel(LogChannels.GL).error(
                        "capability probe requires the render thread and a current GL context; capture failed");
                return null;
            }
            GLCapabilityProfile profile = captureOrThrow(org.lwjgl.opengl.GL.getCapabilities());
            dumpIfRequested(profile);
            return profile;
        } catch (Throwable t) {
            Logs.channel(LogChannels.GL).error("capability probe failed: " + t);
            Diagnostics.report(new EngineDiagnostic(DiagnosticSeverity.ERROR, UserChannel.CHAT,
                    "gl.probe.failed", java.util.List.of(),
                    String.valueOf(t), LogChannels.BOOT));
            return null;
        }
    }

    private static GLCapabilityProfile captureOrThrow(GLCapabilities caps) {
        String version = requireString(GL11.glGetString(GL11.GL_VERSION));
        Matcher vm = VERSION_PREFIX.matcher(version);
        if (!vm.find()) {
            throw new IllegalStateException("unparseable GL_VERSION: " + version);
        }
        int major = Integer.parseInt(vm.group(1));
        int minor = Integer.parseInt(vm.group(2));
        String glsl = major >= 2
                ? requireString(GL20.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION))
                : "0.0";
        String vendor = requireString(GL11.glGetString(GL11.GL_VENDOR));
        String renderer = requireString(GL11.glGetString(GL11.GL_RENDERER));

        int maxDrawBuffers = major >= 2 ? queryLimit(GL20.GL_MAX_DRAW_BUFFERS) : 1;
        int maxColorAttachments = hasFbo(caps) ? queryLimit(GL30.GL_MAX_COLOR_ATTACHMENTS) : 1;
        int maxTextureImageUnits = queryLimit(GL20.GL_MAX_TEXTURE_IMAGE_UNITS);
        int maxVertexAttribs = major >= 2 ? queryLimit(GL20.GL_MAX_VERTEX_ATTRIBS) : 0;
        int maxTextureSize = queryLimit(GL11.GL_MAX_TEXTURE_SIZE);

        // [D-P1-66]: a tier is supported only when the advertisement, the limits and the
        // actual entry points all agree; anything else publishes zero.
        int max3D = 0;
        if (caps.OpenGL12 && caps.glTexImage3D != 0 && caps.glTexSubImage3D != 0) {
            max3D = queryLimit(GL12.GL_MAX_3D_TEXTURE_SIZE);
            if (max3D <= 0) {
                throw new IllegalStateException("broken GL_MAX_3D_TEXTURE_SIZE query");
            }
        }
        int maxRect = 0;
        if (caps.OpenGL31 || caps.GL_ARB_texture_rectangle) {
            maxRect = queryLimit(GL31.GL_MAX_RECTANGLE_TEXTURE_SIZE);
            if (maxRect <= 0) {
                throw new IllegalStateException("broken GL_MAX_RECTANGLE_TEXTURE_SIZE query");
            }
        }
        Set<String> extensions = enumerateExtensions();
        return new GLCapabilityProfile(major, minor, glsl, vendor, renderer,
                maxDrawBuffers, maxColorAttachments, maxTextureImageUnits, maxVertexAttribs,
                maxTextureSize, max3D, maxRect, extensions);
    }

    private static boolean hasFbo(GLCapabilities caps) {
        return caps.OpenGL30 || caps.GL_ARB_framebuffer_object || caps.GL_EXT_framebuffer_object;
    }

    /** One limit query; every glGetInteger is error-checked ([D-P1-66]). */
    private static int queryLimit(int pname) {
        int value = GL11.glGetInteger(pname);
        int err = GL11.glGetError();
        if (err != GL11.GL_NO_ERROR) {
            throw new IllegalStateException("limit query 0x" + Integer.toHexString(pname)
                    + " raised GL error 0x" + Integer.toHexString(err));
        }
        return value;
    }

    private static String requireString(String value) {
        if (value == null) {
            throw new IllegalStateException("required GL string query returned null");
        }
        return value;
    }

    /** Enumerates the extension set via glGetStringi on GL3+, else the legacy string. */
    private static Set<String> enumerateExtensions() {
        Set<String> out = new HashSet<>();
        if (GL11.glGetInteger(GL30.GL_MAJOR_VERSION) >= 3) {
            int count = GL11.glGetInteger(GL30.GL_NUM_EXTENSIONS);
            for (int i = 0; i < count; i++) {
                String ext = GL30.glGetStringi(GL30.GL_EXTENSIONS, i);
                if (ext != null) {
                    out.add(ext);
                }
            }
        } else {
            String all = GL11.glGetString(GL11.GL_EXTENSIONS);
            if (all != null) {
                out.addAll(java.util.Arrays.asList(all.split("\\s+")));
            }
        }
        int err = GL11.glGetError();
        if (err != GL11.GL_NO_ERROR) {
            throw new IllegalStateException("extension enumeration raised GL error 0x"
                    + Integer.toHexString(err));
        }
        return out;
    }

    /** §4.7.2 one-shot text dump, guarded by its own debug flag. */
    private static void dumpIfRequested(GLCapabilityProfile profile) {
        if (!Boolean.getBoolean("schmaloogium.debug.dumpCapabilities")) {
            return;
        }
        try {
            StringWriter out = new StringWriter();
            profile.write(out);
            Path target = Path.of(DUMP_FILE_NAME);
            Files.writeString(target, out.toString());
            Logs.channel(LogChannels.GL).info("capability profile dumped to " + target.toAbsolutePath());
        } catch (IOException e) {
            Logs.channel(LogChannels.GL).warn("capability dump failed: " + e);
        }
    }
}
