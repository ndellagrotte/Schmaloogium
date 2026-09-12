// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.conformance;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The structural hook-application report the run manifest's {@code hooks} block copies
 * (PHASE_2_DOC §4.5.4, R18): one row per mixin class in the mod's configs with its target,
 * the injector count declared in its bytecode (read with ASM, never by loading the mixin
 * class), the count observed applied through {@code IMixinConfigPlugin.postApply}, its
 * class ({@code CORE|FEATURE|OBSERVER|DEFERRED}) and fallback. Nothing here is inferred from
 * runtime rendering behaviour; a row whose mixin never applied stays at zero.
 *
 * <p>The mixin plugin lives in the coremod's classloader-excluded package, so it hands the
 * applied (mixin → target) facts over through JVM system properties
 * ({@code schmaloogium.hooks.applied.<mixin>}), which every classloader shares.
 */
public final class HookApplicationReport {

    /** One primary row. */
    public record Row(String catalogId, String target, int expectedCount, int actualCount,
            List<String> classes, String fallback) {
        public Row {
            classes = List.copyOf(classes);
        }
    }

    private static final Set<String> INJECTOR_ANNOTATIONS = Set.of(
            "Lorg/spongepowered/asm/mixin/injection/Inject;",
            "Lorg/spongepowered/asm/mixin/injection/Redirect;",
            "Lorg/spongepowered/asm/mixin/injection/ModifyArg;",
            "Lorg/spongepowered/asm/mixin/injection/ModifyArgs;",
            "Lorg/spongepowered/asm/mixin/injection/ModifyConstant;",
            "Lorg/spongepowered/asm/mixin/injection/ModifyVariable;",
            "Lorg/spongepowered/asm/mixin/Overwrite;");

    /** The frozen catalogue: mixin class → (class, fallback). Order is the config order. */
    private static final Map<String, String[]> CATALOG = catalog();

    private static Map<String, String[]> catalog() {
        Map<String, String[]> m = new LinkedHashMap<>();
        for (String core : new String[] {"frame.MixinEntityRenderer", "frame.MixinFramebuffer",
            "frame.MixinGlStateManager", "frame.MixinItemRenderer", "frame.MixinMinecraft",
            "frame.MixinParticleManager", "frame.MixinRenderGlobal",
            "frame.MixinSimpleReloadableResourceManager", "frame.MixinTileEntityRendererDispatcher"}) {
            m.put(core, new String[] {"CORE", "SHADERS_OFF"});
        }
        m.put("frame.MixinTimer", new String[] {"FEATURE", "NONE"});
        m.put("frame.MixinIntegratedServer", new String[] {"FEATURE", "NONE"});
        m.put("frame.MixinNetHandlerPlayClient", new String[] {"FEATURE", "NONE"});
        m.put("frame.MixinTextureManager", new String[] {"FEATURE", "NONE"});
        return Collections.unmodifiableMap(m);
    }

    public static final String APPLIED_PROPERTY_PREFIX = "schmaloogium.hooks.applied.";

    private HookApplicationReport() {
    }

    private static String applied(String mixinBinaryName) {
        return System.getProperty(APPLIED_PROPERTY_PREFIX + mixinBinaryName);
    }

    public static List<Row> rows() {
        List<Row> rows = new ArrayList<>();
        for (Map.Entry<String, String[]> e : CATALOG.entrySet()) {
            String binary = "com.schmaloogium.mod.mixin." + e.getKey();
            ClassNode node;
            try {
                node = read(binary);
            } catch (Throwable unreadable) {
                node = null; // an unreadable mixin class is reported as zero expected, never guessed
            }
            String target = node == null ? "" : mixinTarget(node);
            int expected = node == null ? 0 : injectorCount(node);
            String applied = applied(binary);
            int actual = applied == null ? 0 : expected;
            rows.add(new Row(e.getKey(), applied != null ? applied : target, expected, actual,
                    List.of(e.getValue()[0]), e.getValue()[1]));
        }
        return rows;
    }

    private static ClassNode read(String binaryName) {
        try (InputStream in = HookApplicationReport.class.getClassLoader()
                .getResourceAsStream(binaryName.replace('.', '/') + ".class")) {
            if (in == null) {
                return null;
            }
            ClassNode node = new ClassNode();
            new ClassReader(in).accept(node, ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES);
            return node;
        } catch (IOException e) {
            return null;
        }
    }

    private static int injectorCount(ClassNode node) {
        int count = 0;
        for (MethodNode method : node.methods) {
            if (method.visibleAnnotations == null) {
                continue;
            }
            for (AnnotationNode a : method.visibleAnnotations) {
                if (INJECTOR_ANNOTATIONS.contains(a.desc)) {
                    count++;
                }
            }
        }
        return count;
    }

    private static String mixinTarget(ClassNode node) {
        if (node.invisibleAnnotations != null) {
            for (AnnotationNode a : node.invisibleAnnotations) {
                if ("Lorg/spongepowered/asm/mixin/Mixin;".equals(a.desc) && a.values != null) {
                    for (int i = 0; i + 1 < a.values.size(); i += 2) {
                        Object value = a.values.get(i + 1);
                        if ("value".equals(a.values.get(i)) && value instanceof List<?> list && !list.isEmpty()) {
                            return Objects.toString(((org.objectweb.asm.Type) list.get(0)).getClassName());
                        }
                        if ("targets".equals(a.values.get(i)) && value instanceof List<?> list && !list.isEmpty()) {
                            return Objects.toString(list.get(0));
                        }
                    }
                }
            }
        }
        return "";
    }
}
