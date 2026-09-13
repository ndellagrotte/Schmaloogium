// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.hooks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

/** The §4.13.1 counting rules over a synthetic transformed class (no Mixin runtime needed). */
class HookAnchorAuditTest {

    private static final String TARGET = "net/minecraft/client/renderer/RenderGlobal";
    private static final String TRAVERSAL = "com.schmaloogium.mod.mixin.frame.shadow.MixinRenderGlobalShadowTraversal";
    private static final String ACCESSOR = "com.schmaloogium.mod.mixin.frame.shadow.RenderGlobalShadowAccessor";

    @AfterEach
    void clearProperties() {
        for (String id : com.schmaloogium.engine.shadow.ShadowHookHealth.catalogue()) {
            System.clearProperty(HookAnchorAudit.PROPERTY_PREFIX + id);
        }
    }

    private static ClassNode target() {
        ClassNode node = new ClassNode();
        node.name = TARGET;
        node.fields.add(new FieldNode(Opcodes.ACC_PRIVATE, "renderInfos", "Ljava/util/List;", null, null));
        node.fields.add(new FieldNode(Opcodes.ACC_PRIVATE, "lastViewEntityX", "D", null, null));
        return node;
    }

    private static MethodNode method(ClassNode node, String name, String desc) {
        MethodNode m = new MethodNode(Opcodes.ACC_PUBLIC, name, desc, null, null);
        m.instructions = new InsnList();
        node.methods.add(m);
        return m;
    }

    private static void call(MethodNode m, String owner, String name) {
        m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        m.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, owner, name, "()V", false));
    }

    @Test
    void callSitesCountTheRenamedHandlerInvocationsOnly() {
        ClassNode node = target();
        MethodNode setup = method(node, "setupTerrain", "()V");
        // Mixin copied the handler under a session-mangled name and injected three call sites
        // for the three matched targets of one redirect, one for another.
        method(node, "redirect$zzz000$schmaloogium$visitSeed", "()V");
        call(setup, TARGET, "redirect$zzz000$schmaloogium$visitSeed");
        call(setup, TARGET, "redirect$zzz000$schmaloogium$visitSeed");
        call(setup, TARGET, "redirect$zzz000$schmaloogium$visitSeed");
        call(setup, TARGET, "handler$zzz000$schmaloogium$rebuildEntry");
        call(setup, "some/Other", "redirect$zzz000$schmaloogium$neighbor"); // foreign owner: not ours
        setup.instructions.add(new InsnNode(Opcodes.RETURN));

        HookAnchorAudit.publish(TRAVERSAL, node);

        assertEquals("3", System.getProperty(HookAnchorAudit.PROPERTY_PREFIX + "H8-TRAVERSE-01-VISIT-SEED"),
                "overmatch is preserved, never clamped");
        assertEquals("1", System.getProperty(HookAnchorAudit.PROPERTY_PREFIX + "H8-REBUILD-01-ENTRY"));
        assertEquals("0", System.getProperty(HookAnchorAudit.PROPERTY_PREFIX + "H8-TRAVERSE-01-NEIGHBOR"));
        assertEquals("0", System.getProperty(HookAnchorAudit.PROPERTY_PREFIX + "H8-TRAVERSE-01-VISIT-NEIGHBOR"));
        assertNull(System.getProperty(HookAnchorAudit.PROPERTY_PREFIX + "H8-BLOB-01-REDIRECT"),
                "rows of other mixins are untouched");
    }

    @Test
    void accessorRowsCountGeneratedMethodsAndResolveTheirFieldOperand() {
        ClassNode node = target();
        MethodNode getter = method(node, "schmaloogium$renderInfos", "()Ljava/util/List;");
        getter.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        getter.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, TARGET, "renderInfos", "Ljava/util/List;"));
        getter.instructions.add(new InsnNode(Opcodes.ARETURN));
        MethodNode setter = method(node, "schmaloogium$setRenderInfos", "(Ljava/util/List;)V");
        setter.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, TARGET, "renderInfos", "Ljava/util/List;"));
        // A getter whose operand does not exist in the class: generated but unresolved.
        MethodNode dangling = method(node, "schmaloogium$lastViewEntityY", "()D");
        dangling.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, TARGET, "lastViewEntityY", "D"));
        // An invoker whose target method exists.
        method(node, "setDisplayListEntitiesDirty", "()V");
        MethodNode invoker = method(node, "schmaloogium$invokeSetDisplayListEntitiesDirty", "()V");
        call(invoker, TARGET, "setDisplayListEntitiesDirty");

        HookAnchorAudit.publish(ACCESSOR, node);

        String p = HookAnchorAudit.PROPERTY_PREFIX;
        assertEquals("1", System.getProperty(p + "H8-RESTORE-01-FIELD-72755-R-GET"));
        assertEquals("1", System.getProperty(p + "H8-RESTORE-01-FIELD-72755-R-SET"));
        assertEquals("1", System.getProperty(p + "H8-RESTORE-01-FIELD-72755-R-RESOLVE"));
        assertEquals("1", System.getProperty(p + "H8-RESTORE-01-FIELD-174998-I-GET"), "generated");
        assertEquals("0", System.getProperty(p + "H8-RESTORE-01-FIELD-174998-I-RESOLVE"), "operand missing");
        assertEquals("0", System.getProperty(p + "H8-RESTORE-01-FIELD-174997-H-GET"), "never generated");
        assertEquals("1", System.getProperty(p + "H8-REBUILD-01-SETTER-RESOLVE"));
        assertEquals("0", System.getProperty(p + "H8-TERRAIN-01-RESOLVE"));
    }

    @Test
    void everyCatalogueRowExceptForgeIsOwnedByExactlyOneAuditedMixin() {
        // The catalogue rows the coremod audit publishes, plus the two Forge rows resolved
        // game-side, must cover the sixty-six ids exactly once (drift insurance for the table).
        java.util.Set<String> covered = new java.util.HashSet<>();
        ClassNode node = target();
        method(node, "x", "()V").instructions.add(new InsnNode(Opcodes.RETURN));
        for (String mixin : new String[] {"com.schmaloogium.mod.mixin.frame.MixinRenderGlobal",
            "com.schmaloogium.mod.mixin.frame.MixinEntityRenderer",
            "com.schmaloogium.mod.mixin.frame.shadow.MixinRender", TRAVERSAL, ACCESSOR}) {
            HookAnchorAudit.publish(mixin, node);
        }
        for (String id : com.schmaloogium.engine.shadow.ShadowHookHealth.catalogue()) {
            if (System.getProperty(HookAnchorAudit.PROPERTY_PREFIX + id) != null) {
                covered.add(id);
            }
        }
        java.util.Set<String> expected = new java.util.HashSet<>(com.schmaloogium.engine.shadow.ShadowHookHealth.catalogue());
        expected.remove("H8-FORGE-01-GET-RESOLVE");
        expected.remove("H8-FORGE-01-SET-RESOLVE");
        assertEquals(expected, covered);
    }
}
