// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Assembles the aggregate section 4.7 resource requirements from the properties
 * model and the const-declared resource values, every baseline record present.
 */
public final class ResourceRequirementsBuilder {

    private ResourceRequirementsBuilder() {
    }

    public static ResourceRequirements build(ShaderPropertiesModel properties,
            Map<String, ConstScanner.Finding> consts) {
        return build(properties, consts, Map.of());
    }

    /**
     * @param programs the per-program requirements the front end scanned from each
     *                 program's own sources ({@code DRAWBUFFERS} routing, {@code countInstances})
     */
    public static ResourceRequirements build(ShaderPropertiesModel properties,
            Map<String, ConstScanner.Finding> consts,
            Map<ProgramRequirementKey, ProgramRequirements> programs) {
        return build(properties, consts, programs, consts);
    }

    /**
     * @param fullscreenConsts the consts declared by deferred/composite sources only.
     *                         {@code colortexNClear} and {@code colortexNClearColor} are
     *                         honoured for that family alone (PHASE_3_DOC §3.3 :1828-1829),
     *                         so a clear directive in a gbuffers source is ignored rather
     *                         than silently applied to the whole estate.
     */
    public static ResourceRequirements build(ShaderPropertiesModel properties,
            Map<String, ConstScanner.Finding> consts,
            Map<ProgramRequirementKey, ProgramRequirements> programs,
            Map<String, ConstScanner.Finding> fullscreenConsts) {
        BufferMinima minima = new BufferMinima(8, 1, 1,
            boolConst(consts, "generateShadowColorMipmap", false) ? 1 : 0);
        Map<ColorAttachmentKey, ColorAttachmentRequirement> color =
            colorAttachments(consts, fullscreenConsts);
        ShadowRequirements shadow = shadow(consts);
        CenterDepthRequirements centerDepth = new CenterDepthRequirements(false);
        SmoothingConstants smoothing = new SmoothingConstants(
            floatConst(consts, "wetnessHalflife", 600.0f),
            floatConst(consts, "drynessHalflife", 600.0f),
            floatConst(consts, "eyeBrightnessHalflife", 10.0f),
            floatConst(consts, "centerDepthHalflife", 1.0f));
        WorldRenderConstants world = new WorldRenderConstants(
            floatConst(consts, "sunPathRotation", 0.0f),
            floatConst(consts, "ambientOcclusionLevel", 0.5f));
        NoiseRequirement noise = new NoiseRequirement(
            properties.noise() instanceof NoiseTextureSpec.Override, noiseResolution(consts));
        return new ResourceRequirements(minima, color, shadow, centerDepth, programs,
            smoothing, world, noise);
    }

    private static ColorInternalFormat colorInternalFormat(String token) {
        try {
            return ColorInternalFormat.valueOf(token);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static int noiseResolution(Map<String, ConstScanner.Finding> consts) {
        return intConst(consts, "noiseTextureResolution", 256);
    }

    private static int intConst(Map<String, ConstScanner.Finding> consts, String name,
            int baseline) {
        ConstScanner.Finding f = consts.get(name);
        if (f == null) {
            return baseline;
        }
        try {
            return Integer.parseInt(f.value().trim());
        } catch (NumberFormatException e) {
            return baseline;
        }
    }

    private static float floatConst(Map<String, ConstScanner.Finding> consts, String name,
            float baseline) {
        ConstScanner.Finding f = consts.get(name);
        if (f == null) {
            return baseline;
        }
        try {
            return Float.parseFloat(f.value().trim());
        } catch (NumberFormatException e) {
            return baseline;
        }
    }

    private static boolean boolConst(Map<String, ConstScanner.Finding> consts, String name,
            boolean baseline) {
        ConstScanner.Finding f = consts.get(name);
        return f == null ? baseline : Boolean.parseBoolean(f.value().trim());
    }

    /** The attachment-scoped directive suffixes this builder reads (§4.7). */
    private static final Set<String> ATTACHMENT_SUFFIXES =
        Set.of("Format", "Clear", "ClearColor");

    /**
     * The section 4.7 attachment directives, resolved through the one shared buffer-name
     * normalizer so the legacy spellings ({@code gcolorFormat}, {@code gaux3Format},
     * {@code gaux4Clear} …) are read exactly like their {@code colortexN} equivalents.
     * Packs that ship only legacy names — SEUS Renewed declares nothing else — otherwise
     * fall through to the unsized RGBA fallback with every directive silently unread.
     * A canonical spelling wins over a legacy alias for the same attachment.
     */
    private static Map<ColorAttachmentKey, ColorAttachmentRequirement> colorAttachments(
            Map<String, ConstScanner.Finding> consts,
            Map<String, ConstScanner.Finding> fullscreenConsts) {
        Map<Integer, ConstScanner.Finding> formats = collectScoped(consts, "Format");
        Map<Integer, ConstScanner.Finding> clears = collectScoped(fullscreenConsts, "Clear");
        Map<Integer, ConstScanner.Finding> clearColors =
            collectScoped(fullscreenConsts, "ClearColor");
        Map<ColorAttachmentKey, ColorAttachmentRequirement> out = new LinkedHashMap<>();
        for (int i = 0; i < 8; i++) {
            ConstScanner.Finding fmt = formats.get(i);
            if (fmt == null) {
                continue;
            }
            ColorInternalFormat internalFormat = colorInternalFormat(fmt.value().trim());
            if (internalFormat == null) {
                continue;
            }
            ColorAttachmentFormat format = new ColorAttachmentFormat.Explicit(internalFormat);
            ConstScanner.Finding clearFinding = clears.get(i);
            boolean clear = clearFinding == null
                || Boolean.parseBoolean(clearFinding.value().trim());
            Optional<Vec4f> clearOverride = Optional.empty();
            ConstScanner.Finding clearColor = clearColors.get(i);
            if (clearColor != null) {
                Vec4f v = Vec4Parser.parse(clearColor.value());
                if (v != null) {
                    clearOverride = Optional.of(v);
                }
            }
            out.put(new ColorAttachmentKey(i),
                new ColorAttachmentRequirement(format, clear, clearOverride));
        }
        return out;
    }

    /**
     * Every declaration of one directive suffix, keyed by attachment index. The canonical
     * {@code colortexN} spelling displaces a legacy alias; between two legacy spellings of
     * the same attachment the first scanned wins, matching the scanner's own
     * first-occurrence rule.
     */
    private static Map<Integer, ConstScanner.Finding> collectScoped(
            Map<String, ConstScanner.Finding> consts, String suffix) {
        Map<Integer, ConstScanner.Finding> byIndex = new LinkedHashMap<>();
        Set<Integer> canonical = new java.util.HashSet<>();
        for (Map.Entry<String, ConstScanner.Finding> entry : consts.entrySet()) {
            ColorBufferNames.ScopedDirective scoped =
                ColorBufferNames.scoped(entry.getKey(), ATTACHMENT_SUFFIXES);
            if (scoped == null || !scoped.suffix().equals(suffix)) {
                continue;
            }
            if (scoped.canonical()) {
                byIndex.put(scoped.index(), entry.getValue());
                canonical.add(scoped.index());
            } else if (!canonical.contains(scoped.index())) {
                byIndex.putIfAbsent(scoped.index(), entry.getValue());
            }
        }
        return byIndex;
    }

    private static ShadowRequirements shadow(Map<String, ConstScanner.Finding> consts) {
        Set<ShadowTextureKey> mipmapped = EnumSet.noneOf(ShadowTextureKey.class);
        Set<ShadowTextureKey> nearest = EnumSet.noneOf(ShadowTextureKey.class);
        Set<ShadowDepthKey> hardwarePcf = EnumSet.noneOf(ShadowDepthKey.class);
        if (boolConst(consts, "generateShadowMipmap", false)) {
            mipmapped.addAll(EnumSet.of(ShadowTextureKey.DEPTH_0, ShadowTextureKey.DEPTH_1));
        }
        if (boolConst(consts, "generateShadowColorMipmap", false)) {
            mipmapped.addAll(EnumSet.of(ShadowTextureKey.COLOR_0, ShadowTextureKey.COLOR_1));
        }
        if (boolConst(consts, "shadowtex0Mipmap", false)) {
            mipmapped.add(ShadowTextureKey.DEPTH_0);
        }
        if (boolConst(consts, "shadowtex1Mipmap", false)) {
            mipmapped.add(ShadowTextureKey.DEPTH_1);
        }
        if (boolConst(consts, "shadowcolor0Mipmap", false)) {
            mipmapped.add(ShadowTextureKey.COLOR_0);
        }
        if (boolConst(consts, "shadowcolor1Mipmap", false)) {
            mipmapped.add(ShadowTextureKey.COLOR_1);
        }
        if (boolConst(consts, "shadowtex0Nearest", false)) {
            nearest.add(ShadowTextureKey.DEPTH_0);
        }
        if (boolConst(consts, "shadowtex1Nearest", false)) {
            nearest.add(ShadowTextureKey.DEPTH_1);
        }
        if (boolConst(consts, "shadowHardwareFiltering", false)) {
            hardwarePcf.addAll(EnumSet.of(ShadowDepthKey.DEPTH_0, ShadowDepthKey.DEPTH_1));
        }
        if (boolConst(consts, "shadowHardwareFiltering0", false)) {
            hardwarePcf.add(ShadowDepthKey.DEPTH_0);
        }
        if (boolConst(consts, "shadowHardwareFiltering1", false)) {
            hardwarePcf.add(ShadowDepthKey.DEPTH_1);
        }
        return new ShadowRequirements(
            intConst(consts, "shadowMapResolution", 1024),
            Optional.ofNullable(consts.get("shadowMapFov"))
                .map(f -> floatConst(consts, "shadowMapFov", 90.0f)),
            floatConst(consts, "shadowDistance", 160.0f),
            floatConst(consts, "shadowDistanceRenderMul", -1.0f),
            floatConst(consts, "shadowIntervalSize", 4.0f),
            mipmapped, nearest, hardwarePcf);
    }
}
