// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import com.schmaloogium.engine.config.MacroConfiguration;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.pack.PackLoadRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Builds the complete load-time macro environment: OF standard A-G identity,
 * option, capability-feature, engine-identity families, then per-pack overrides
 * in unsigned-UTF-8 name order. All jcpp installs flow from the returned map.
 */
public final class MacroEnvironmentBuilder {

    private MacroEnvironmentBuilder() {
    }

    private static final Pattern VENDOR_ATI = Pattern.compile("^ati.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern VENDOR_INTEL = Pattern.compile("^intel.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern VENDOR_NVIDIA = Pattern.compile("^nvidia.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern VENDOR_XORG = Pattern.compile("^x\\.org.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern RENDER_AMD = Pattern.compile("^(amd|ati|radeon).*", Pattern.CASE_INSENSITIVE);
    private static final Pattern RENDER_GALLIUM = Pattern.compile("^gallium.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern RENDER_INTEL = Pattern.compile("^intel.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern RENDER_GEFORCE = Pattern.compile("^(geforce|nvidia).*", Pattern.CASE_INSENSITIVE);
    private static final Pattern RENDER_QUADRO = Pattern.compile("^(quadro|nvs).*", Pattern.CASE_INSENSITIVE);
    private static final Pattern RENDER_MESA = Pattern.compile("^mesa.*", Pattern.CASE_INSENSITIVE);

    /** Deterministic A-G environment used by properties and ID-map parsing too. */
    public static Map<String, String> standardMacros(PackLoadRequest request) {
        Map<String, String> macros = new LinkedHashMap<>();
        macros.put("MC_VERSION", "11202");
        int gl = request.capabilities().glVersionMajor() * 100 + request.capabilities().glVersionMinor() * 10;
        macros.put("MC_GL_VERSION", Integer.toString(gl));
        macros.put("MC_GLSL_VERSION", glslVersionValue(request.capabilities().glslVersion()));
        macros.put(osMacro(request.runtimeIdentity().osFamily()), "");
        macros.put(vendorMacro(request.capabilities().vendor()), "");
        macros.put(rendererMacro(request.capabilities().renderer()), "");
        return macros;
    }

    public static Map<String, String> shaderMacros(PackLoadRequest request,
            MacroConfiguration configuration) {
        Map<String, String> macros = new LinkedHashMap<>(standardMacros(request));
        for (var definition : configuration.optionMacros()) {
            macros.put(definition.name(), definition.replacement());
        }
        for (var definition : configuration.capabilityFeatureMacros()) {
            macros.put(definition.name(), definition.replacement());
        }
        for (var definition : configuration.engineIdentityMacros()) {
            macros.put(definition.name(), definition.replacement());
        }
        // overrides in unsigned-UTF-8 name order
        new ArrayList<>(configuration.perPackOverrides().keySet())
            .sort(com.schmaloogium.engine.config.EngineOptionData::compareUnsignedUtf8);
        for (var e : configuration.perPackOverrides().entrySet()) {
            switch (e.getValue().action()) {
                case ADD, FORCE -> macros.put(e.getKey(), e.getValue().replacement().orElse(""));
                case SUPPRESS -> macros.remove(e.getKey());
            }
        }
        return macros;
    }

    /** The exact anchored glslVersion grammar and base-10 major*100+minor projection. */
    public static String glslVersionValue(String glslVersion) {
        java.util.regex.Matcher m = Pattern.compile(
            "^([1-9][0-9]*)\\.([0-9]{2})(?:[ \\t]+[^ \\t\\r\\n][^\\r\\n]*)?$")
            .matcher(glslVersion);
        if (!m.matches()) {
            throw new IllegalArgumentException("invalid glslVersion: " + glslVersion);
        }
        int major = Integer.parseInt(m.group(1));
        int minor = Integer.parseInt(m.group(2));
        long value = (long) major * 100 + minor;
        if (value > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("glslVersion overflows");
        }
        return Long.toString(value);
    }

    private static String osMacro(com.schmaloogium.engine.pack.OsFamily family) {
        return switch (family) {
            case WINDOWS -> "MC_OS_WINDOWS";
            case MACOS -> "MC_OS_MAC";
            case LINUX -> "MC_OS_LINUX";
            case OTHER -> "MC_OS_OTHER";
        };
    }

    private static String vendorMacro(String vendor) {
        if (VENDOR_ATI.matcher(vendor).matches()) {
            return "MC_GL_VENDOR_ATI";
        }
        if (VENDOR_INTEL.matcher(vendor).matches()) {
            return "MC_GL_VENDOR_INTEL";
        }
        if (VENDOR_NVIDIA.matcher(vendor).matches()) {
            return "MC_GL_VENDOR_NVIDIA";
        }
        if (VENDOR_XORG.matcher(vendor).matches()) {
            return "MC_GL_VENDOR_XORG";
        }
        return "MC_GL_VENDOR_OTHER";
    }

    private static String rendererMacro(String renderer) {
        if (RENDER_AMD.matcher(renderer).matches()) {
            return "MC_GL_RENDERER_RADEON";
        }
        if (RENDER_GALLIUM.matcher(renderer).matches()) {
            return "MC_GL_RENDERER_GALLIUM";
        }
        if (RENDER_INTEL.matcher(renderer).matches()) {
            return "MC_GL_RENDERER_INTEL";
        }
        if (RENDER_GEFORCE.matcher(renderer).matches()) {
            return "MC_GL_RENDERER_GEFORCE";
        }
        if (RENDER_QUADRO.matcher(renderer).matches()) {
            return "MC_GL_RENDERER_QUADRO";
        }
        if (RENDER_MESA.matcher(renderer).matches()) {
            return "MC_GL_RENDERER_MESA";
        }
        return "MC_GL_RENDERER_OTHER";
    }

    /** Honest capability feature macros from the Phase 1 profile only. */
    public static List<com.schmaloogium.engine.config.MacroDefinition> capabilityFeatureMacros(
            GLCapabilityProfile capabilities) {
        List<com.schmaloogium.engine.config.MacroDefinition> out = new ArrayList<>();
        if (capabilities.hasExtension("GL_ARB_shader_image_load_store")) {
            out.add(new com.schmaloogium.engine.config.MacroDefinition("IRIS_FEATURE_IMAGE_LOAD_STORE", "1"));
        }
        if (capabilities.hasExtension("GL_ARB_compute_shader")) {
            out.add(new com.schmaloogium.engine.config.MacroDefinition("IRIS_FEATURE_COMPUTE_SHADERS", "1"));
        }
        return out;
    }
}
