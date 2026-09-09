// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Mixin config ↔ package agreement (PHASE_1_DOC §4.5.2a, D-P1-38): for each config, the
 * {@code @Mixin}-annotated classes in its declared package <em>minus any sub-package
 * another config declares</em> are exactly the classes its arrays name. The sub-package
 * exclusion is part of the specification — the three declared packages are nested, so a
 * subtree-scoped predicate fails on a correct config set as soon as PRE_INIT or MOD
 * gains a tenant. This is the drift insurance taken instead of a class-scan plugin; it
 * evaluates the actual owner-populated configs, not assumed-empty arrays.
 */
class MixinConfigAgreementTest {

    private record Config(String name, String declaredPackage, List<String> declaredMixins) {
    }

    private static final String[] CONFIG_NAMES = {
            "schmaloogium.preinit.mixin.json",
            "schmaloogium.default.mixin.json",
            "schmaloogium.mod.mixin.json",
    };

    @Test
    void configsAgreeWithAnnotatedClasses() throws IOException {
        List<Config> configs = readConfigs();
        Map<String, Set<String>> annotated = scanAnnotatedClasses();

        for (Config config : configs) {
            List<String> otherDeclaredPackages = configs.stream()
                    .filter(c -> c != config)
                    .map(Config::declaredPackage)
                    .toList();

            List<String> found = annotated.entrySet().stream()
                    .filter(e -> inPackage(e.getKey(), config.declaredPackage())
                            && otherDeclaredPackages.stream().noneMatch(
                            other -> inPackage(e.getKey(), other)))
                    .map(Map.Entry::getValue)
                    .flatMap(Set::stream)
                    .sorted()
                    .toList();

            List<String> declared = config.declaredMixins().stream().sorted().toList();
            assertTrue(found.equals(declared),
                    "config " + config.name() + " disagrees with its package: "
                            + "annotated classes " + found + " vs declared mixins " + declared);
        }
    }

    private static boolean inPackage(String className, String declaredPackage) {
        // Exact package membership, not subtree: one config's declared package excludes
        // another config's declared package even where the latter nests under the former.
        if (declaredPackage.equals("com.schmaloogium.mod.mixin")) {
            return className.equals(declaredPackage)
                    || (className.startsWith(declaredPackage + ".")
                    && !className.startsWith("com.schmaloogium.mod.mixin.preinit.")
                    && !className.startsWith("com.schmaloogium.mod.mixin.compat."));
        }
        return className.equals(declaredPackage) || className.startsWith(declaredPackage + ".");
    }

    private static List<Config> readConfigs() throws IOException {
        List<Config> configs = new ArrayList<>();
        for (String name : CONFIG_NAMES) {
            try (InputStream in = MixinConfigAgreementTest.class.getClassLoader()
                    .getResourceAsStream(name)) {
                assertTrue(in != null, "mixin config missing from resources: " + name);
                String json = new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                configs.add(new Config(name, extractString(json, "package"), extractArray(json)));
            }
        }
        return configs;
    }

    /** Minimal structural extraction: the configs are fixed v0.1 shapes, not user input. */
    private static String extractString(String json, String key) {
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"").matcher(json);
        assertTrue(m.find(), "missing \"" + key + "\" in mixin config");
        return m.group(1);
    }

    private static List<String> extractArray(String json) {
        List<String> result = new ArrayList<>();
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("\"(?:client|mixins|server)\"\\s*:\\s*\\[([^\\]]*)\\]").matcher(json);
        while (m.find()) {
            java.util.regex.Matcher e = java.util.regex.Pattern
                    .compile("\"([^\"]+)\"").matcher(m.group(1));
            while (e.find()) {
                result.add(e.group(1));
            }
        }
        return result;
    }

    /** Walks this module's compiled classes, keeping those with a runtime-invisible @Mixin annotation. */
    private static Map<String, Set<String>> scanAnnotatedClasses() throws IOException {
        String classesDirProperty = System.getProperty("schmaloogium.test.classesDir");
        assertTrue(classesDirProperty != null && !classesDirProperty.isBlank(),
                "the build did not inject schmaloogium.test.classesDir");

        Map<String, Set<String>> annotated = new HashMap<>();
        for (String dirName : classesDirProperty.split(File.pathSeparator)) {
            Path dir = Path.of(dirName);
            if (!Files.isDirectory(dir)) {
                continue; // no compiled classes for this source set yet
            }
            try (Stream<Path> classes = Files.walk(dir)) {
                classes.filter(p -> p.toString().endsWith(".class"))
                        .forEach(p -> inspect(p, annotated));
            }
        }
        return annotated;
    }

    private static void inspect(Path classFile, Map<String, Set<String>> annotated) {
        String fileName = classFile.getFileName().toString();
        if (fileName.contains("$") || fileName.toLowerCase(Locale.ROOT).contains("mixinplugin")) {
            return;
        }
        try (InputStream in = Files.newInputStream(classFile)) {
            boolean[] isMixin = {false};
            new ClassReader(in).accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    if (descriptor.endsWith("Lorg/spongepowered/asm/mixin/Mixin;")) {
                        isMixin[0] = true;
                    }
                    return null;
                }
            }, ClassReader.SKIP_CODE);
            if (isMixin[0]) {
                String binaryName = classFile.toString()
                        .replace(File.separatorChar, '.');
                int classesIdx = binaryName.indexOf(".classes.");
                if (classesIdx >= 0) {
                    binaryName = binaryName.substring(classesIdx + ".classes.".length(),
                            binaryName.length() - ".class".length());
                }
                annotated.put(binaryName, Set.of(binaryName));
            }
        } catch (IOException e) {
            throw new RuntimeException("unreadable class: " + classFile, e);
        }
    }
}
