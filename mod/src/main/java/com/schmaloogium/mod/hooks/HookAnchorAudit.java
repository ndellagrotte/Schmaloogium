// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.hooks;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The coremod-side audit behind {@code ShadowHookHealth} (PHASE_8_DOC §4.13.1): after Mixin
 * applies one of the catalogued shadow mixins, count in the <em>transformed</em> class node
 * <ul>
 *   <li>for INJECT/REDIRECT rows, the call sites of the copied handler (Mixin renames a handler
 *       to {@code handler$…$name} / {@code redirect$…$name}; the original name survives as a
 *       {@code $name} suffix, and one call site is emitted per matched injection point);</li>
 *   <li>for GET/SET rows, the generated accessor method by its exact name;</li>
 *   <li>for RESOLVE rows, the accessor's or invoker's target member as it exists in the class
 *       (the accessor body's GETFIELD/PUTFIELD/INVOKE operand), or the generated invoker.</li>
 * </ul>
 * Each count is published as {@code schmaloogium.hooks.anchor.<hookId>=<count>}; the game side
 * ({@code McShadowHookHealth}) reads every catalogue row (missing = 0) plus the two Forge
 * resolution rows it checks by reflection. This package is classloader-excluded, so system
 * properties are the transport, as for {@code HookApplicationReport}.
 */
public final class HookAnchorAudit {

    public static final String PROPERTY_PREFIX = "schmaloogium.hooks.anchor.";

    private enum Kind { CALL_SITES, ACCESSOR, RESOLVE }

    private record Row(String hookId, Kind kind, String member) {
    }

    /** mixin binary name → the rows it carries. */
    private static final Map<String, List<Row>> ROWS = rows();

    private HookAnchorAudit() {
    }

    private static Map<String, List<Row>> rows() {
        Map<String, List<Row>> m = new LinkedHashMap<>();
        String p = "com.schmaloogium.mod.mixin.";
        m.put(p + "frame.MixinRenderGlobal", List.of(
                new Row("H-SHADOW-OUTLINE-01", Kind.CALL_SITES, "schmaloogium$shadowOutlinePredicate")));
        m.put(p + "frame.MixinEntityRenderer", List.of(
                new Row("H8-SLOT-01-FRAME-05", Kind.CALL_SITES, "schmaloogium$aroundSetupTerrain")));
        m.put(p + "frame.shadow.MixinRender", List.of(
                new Row("H8-BLOB-01-REDIRECT", Kind.CALL_SITES, "schmaloogium$blobShadow")));
        m.put(p + "frame.shadow.MixinRenderGlobalShadowTraversal", List.of(
                new Row("H8-TRAVERSE-01-VISIT-SEED", Kind.CALL_SITES, "schmaloogium$visitSeed"),
                new Row("H8-TRAVERSE-01-VISIT-FALLBACK-SEED", Kind.CALL_SITES, "schmaloogium$visitFallbackSeed"),
                new Row("H8-TRAVERSE-01-VISIT-NEIGHBOR", Kind.CALL_SITES, "schmaloogium$visitNeighbor"),
                new Row("H8-TRAVERSE-01-RENDER-CHUNKS-MANY", Kind.CALL_SITES, "schmaloogium$renderChunksMany"),
                new Row("H8-TRAVERSE-01-SEED-DIRECTIONS", Kind.CALL_SITES, "schmaloogium$seedDirections"),
                new Row("H8-TRAVERSE-01-COMPILED-VISIBILITY", Kind.CALL_SITES, "schmaloogium$compiledVisibility"),
                new Row("H8-TRAVERSE-01-NEIGHBOR", Kind.CALL_SITES, "schmaloogium$neighbor"),
                new Row("H8-REBUILD-01-ENTRY", Kind.CALL_SITES, "schmaloogium$rebuildEntry")));
        List<Row> accessors = new java.util.ArrayList<>();
        field(accessors, "H8-RESTORE-01-FIELD-72755-R", "RenderInfos", true);
        field(accessors, "H8-RESTORE-01-FIELD-147595-R", "DisplayListEntitiesDirty", true);
        field(accessors, "H8-RESTORE-01-FIELD-174997-H", "LastViewEntityX", true);
        field(accessors, "H8-RESTORE-01-FIELD-174998-I", "LastViewEntityY", true);
        field(accessors, "H8-RESTORE-01-FIELD-174999-J", "LastViewEntityZ", true);
        field(accessors, "H8-RESTORE-01-FIELD-175000-K", "LastViewEntityPitch", true);
        field(accessors, "H8-RESTORE-01-FIELD-174994-L", "LastViewEntityYaw", true);
        field(accessors, "H8-RESTORE-01-FIELD-175001-U", "DebugFixedClippingHelper", true);
        field(accessors, "H8-RESTORE-01-FIELD-175002-T", "DebugFixTerrainFrustum", true);
        accessors.add(new Row("H8-RESTORE-01-FIELD-175008-N-GET", Kind.ACCESSOR, "schmaloogium$viewFrustum"));
        accessors.add(new Row("H8-RESTORE-01-FIELD-175008-N-RESOLVE", Kind.RESOLVE, "schmaloogium$viewFrustum"));
        accessors.add(new Row("H8-RESTORE-01-FIELD-175009-L-GET", Kind.ACCESSOR, "schmaloogium$chunksToUpdate"));
        accessors.add(new Row("H8-RESTORE-01-FIELD-175009-L-RESOLVE", Kind.RESOLVE, "schmaloogium$chunksToUpdate"));
        field(accessors, "H8-RESTORE-01-FIELD-147596-F", "PrevRenderSortX", false);
        field(accessors, "H8-RESTORE-01-FIELD-147597-G", "PrevRenderSortY", false);
        field(accessors, "H8-RESTORE-01-FIELD-147602-H", "PrevRenderSortZ", false);
        field(accessors, "H8-ENTITY-01-FIELD-72740-G", "RenderEntitiesStartupCounter", true);
        field(accessors, "H8-ENTITY-01-FIELD-72748-H", "CountEntitiesTotal", true);
        field(accessors, "H8-ENTITY-01-FIELD-72749-I", "CountEntitiesRendered", true);
        field(accessors, "H8-ENTITY-01-FIELD-72750-J", "CountEntitiesHidden", true);
        accessors.add(new Row("H8-REBUILD-01-SETTER-RESOLVE", Kind.RESOLVE, "schmaloogium$invokeSetDisplayListEntitiesDirty"));
        accessors.add(new Row("H8-TERRAIN-01-RESOLVE", Kind.RESOLVE, "schmaloogium$invokeRenderBlockLayer"));
        accessors.add(new Row("H8-CLOUD-01-RESOLVE", Kind.RESOLVE, "schmaloogium$invokeRenderClouds"));
        accessors.add(new Row("H8-ENTITY-01-METHOD-RESOLVE", Kind.RESOLVE, "schmaloogium$invokeRenderEntities"));
        m.put(p + "frame.shadow.RenderGlobalShadowAccessor", List.copyOf(accessors));
        return m;
    }

    private static void field(List<Row> out, String id, String suffix, boolean resolveRow) {
        String getter = "schmaloogium$" + Character.toLowerCase(suffix.charAt(0)) + suffix.substring(1);
        String setter = "schmaloogium$set" + suffix;
        out.add(new Row(id + "-GET", Kind.ACCESSOR, getter));
        out.add(new Row(id + "-SET", Kind.ACCESSOR, setter));
        if (resolveRow) {
            out.add(new Row(id + "-RESOLVE", Kind.RESOLVE, getter));
        }
    }

    /** The catalogued mixin binary names this audit knows (for the game-side reader). */
    public static boolean audits(String mixinBinaryName) {
        return ROWS.containsKey(mixinBinaryName);
    }

    public static void publish(String mixinBinaryName, ClassNode transformed) {
        List<Row> rows = ROWS.get(mixinBinaryName);
        if (rows == null) {
            return;
        }
        for (Row row : rows) {
            int count;
            try {
                count = switch (row.kind()) {
                    case CALL_SITES -> callSites(transformed, row.member());
                    case ACCESSOR -> methods(transformed, row.member());
                    case RESOLVE -> resolved(transformed, row.member());
                };
            } catch (RuntimeException unexpected) {
                count = 0;
            }
            System.setProperty(PROPERTY_PREFIX + row.hookId(), Integer.toString(count));
        }
    }

    /** Call sites (any method of the class) of a copied handler whose name ends with {@code $name}. */
    static int callSites(ClassNode node, String handlerName) {
        int count = 0;
        String suffix = "$" + handlerName;
        for (MethodNode method : node.methods) {
            if (method.instructions == null) {
                continue;
            }
            for (AbstractInsnNode insn : method.instructions) {
                if (insn instanceof MethodInsnNode call && call.owner.equals(node.name)
                        && (call.name.endsWith(suffix) || call.name.equals(handlerName))) {
                    count++;
                }
            }
        }
        return count;
    }

    /** Generated accessor/invoker methods by exact name. */
    static int methods(ClassNode node, String name) {
        int count = 0;
        for (MethodNode method : node.methods) {
            if (method.name.equals(name)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Resolution: the generated accessor exists and its body's member operand exists in the
     * class (a field it reads/writes, or a method it invokes on the same class).
     */
    static int resolved(ClassNode node, String accessorName) {
        for (MethodNode method : node.methods) {
            if (!method.name.equals(accessorName) || method.instructions == null) {
                continue;
            }
            for (AbstractInsnNode insn : method.instructions) {
                if (insn instanceof FieldInsnNode field && field.owner.equals(node.name)) {
                    return hasField(node, field.name, field.desc) ? 1 : 0;
                }
                if (insn instanceof MethodInsnNode call && call.owner.equals(node.name)
                        && !call.name.equals(accessorName)) {
                    return hasMethod(node, call.name, call.desc) ? 1 : 0;
                }
            }
        }
        return 0;
    }

    private static boolean hasField(ClassNode node, String name, String desc) {
        for (FieldNode field : node.fields) {
            if (field.name.equals(name) && field.desc.equals(desc)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasMethod(ClassNode node, String name, String desc) {
        for (MethodNode method : node.methods) {
            if (method.name.equals(name) && method.desc.equals(desc)) {
                return true;
            }
        }
        return false;
    }
}
