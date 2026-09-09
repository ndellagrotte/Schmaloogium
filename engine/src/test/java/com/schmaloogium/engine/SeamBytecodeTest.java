// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Constraint C-1, bytecode half (PHASE_1_DOC §4.3 layer 3) — the test the Impl gate
 * names: no forbidden type referenced by any compiled {@code :engine} class. Walks every
 * {@code .class} file under {@code schmaloogium.test.classesDir} and collects the types
 * its constant pool actually names — the class itself, superclass, interfaces, field and
 * method descriptors, and declared exceptions. {@code SKIP_CODE} is safe: instructions
 * cannot name a type the pool does not.
 */
class SeamBytecodeTest {

    private static final String[] FORBIDDEN_TYPE_PREFIXES = {
            "net.minecraft.", "net.minecraftforge.", "com.cleanroommc.",
            "org.spongepowered.", "org.lwjgl", "org.lwjglx",
            "zone.rong.mixinbooter", "cpw.mods.",
    };

    @Test
    void engineBytecodeReferencesNoForbiddenType() throws IOException {
        String classesDirProperty = System.getProperty("schmaloogium.test.classesDir");
        assertTrue(classesDirProperty != null && !classesDirProperty.isBlank(),
                "the build did not inject schmaloogium.test.classesDir; "
                        + "SeamClasspathArguments is missing from this module's test jvmArgumentProviders");

        List<String> offenders = new ArrayList<>();
        for (String dirName : classesDirProperty.split(File.pathSeparator)) {
            Path dir = Path.of(dirName);
            if (!Files.isDirectory(dir)) {
                continue; // no compiled classes for this source set yet: nothing to scan
            }
            try (Stream<Path> classes = Files.walk(dir)) {
                classes.filter(p -> p.toString().endsWith(".class"))
                        .forEach(p -> collectOffenders(p, offenders));
            }
        }
        assertTrue(offenders.isEmpty(),
                "C-1 violation: :engine bytecode references forbidden types: " + offenders);
    }

    private static void collectOffenders(Path classFile, List<String> offenders) {
        Set<String> names = new LinkedHashSet<>();
        try (InputStream in = Files.newInputStream(classFile)) {
            ClassReader reader = new ClassReader(in);
            reader.accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public void visit(int version, int access, String name, String signature,
                                  String superName, String[] interfaces) {
                    add(offenders, names, name);
                    add(offenders, names, superName);
                    if (interfaces != null) {
                        for (String i : interfaces) {
                            add(offenders, names, i);
                        }
                    }
                }

                @Override
                public org.objectweb.asm.FieldVisitor visitField(int access, String name,
                        String descriptor, String signature, Object value) {
                    addDescriptor(offenders, names, descriptor);
                    return null;
                }

                @Override
                public org.objectweb.asm.MethodVisitor visitMethod(int access, String name,
                        String descriptor, String signature, String[] exceptions) {
                    addDescriptor(offenders, names, descriptor);
                    if (exceptions != null) {
                        for (String e : exceptions) {
                            add(offenders, names, e);
                        }
                    }
                    return null;
                }
            }, ClassReader.SKIP_CODE);
        } catch (IOException e) {
            offenders.add(classFile + " -> unreadable: " + e);
        }
    }

    private static void addDescriptor(List<String> offenders, Set<String> names, String descriptor) {
        if (descriptor == null) {
            return;
        }
        if (descriptor.startsWith("(")) {
            for (Type argument : Type.getArgumentTypes(descriptor)) {
                add(offenders, names, argument.getInternalName());
            }
            add(offenders, names, Type.getReturnType(descriptor).getInternalName());
        } else {
            add(offenders, names, Type.getType(descriptor).getInternalName());
        }
    }

    private static void add(List<String> offenders, Set<String> names, String internalName) {
        if (internalName == null) {
            return;
        }
        String dotted = internalName.replace('/', '.');
        if (!names.add(dotted)) {
            return;
        }
        for (String prefix : FORBIDDEN_TYPE_PREFIXES) {
            if (dotted.startsWith(prefix)) {
                offenders.add(dotted);
            }
        }
    }
}
