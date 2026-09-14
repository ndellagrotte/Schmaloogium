// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.pack.DimensionKey;
import com.schmaloogium.engine.pack.NormalizedPackPath;
import com.schmaloogium.engine.preprocess.DeclaredUniformCatalog;
import com.schmaloogium.engine.preprocess.GeometrySourceForm;
import com.schmaloogium.engine.preprocess.MaterializationFingerprint;
import com.schmaloogium.engine.preprocess.MaterializedSource;
import com.schmaloogium.engine.preprocess.ShaderLanguage;
import com.schmaloogium.engine.preprocess.ShaderSourceStage;
import com.schmaloogium.engine.preprocess.SourceId;
import com.schmaloogium.engine.preprocess.SourceKey;
import com.schmaloogium.engine.preprocess.SourceMap;
import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.registry.ClassicProgramCatalog;
import com.schmaloogium.engine.registry.DrawRouting;
import com.schmaloogium.engine.registry.ExtendedAttribute;
import com.schmaloogium.engine.registry.GeometryInputRequirement;
import com.schmaloogium.engine.registry.ProgramSlotDescriptor;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.ProgramStateBundle;
import com.schmaloogium.engine.registry.support.ScriptedGLDevice;
import com.schmaloogium.engine.gl.ProgramHandle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

/** Task F: declared classic attributes are bound by name at 10/11/12 before link. */
class GlProgramBuilderAttributeBindTest {

    @Test
    void declaredAttributesAreBoundBeforeLink() {
        ScriptedGLDevice device = new ScriptedGLDevice();
        device.failLink = true; // stop after link: the assertion is the pre-link order
        Planner.PlannedSlot planned = planned(
            Set.of(ExtendedAttribute.AT_TANGENT, ExtendedAttribute.MC_ENTITY));

        GlProgramBuilder.build(planned, device, new HashMap<ProgramSlotId, CompiledProgramBinding>(),
            new ArrayList<ProgramHandle>());

        List<String> binds = device.calls.stream()
            .filter(c -> c.startsWith("bindAttributeLocation:")).toList();
        int link = device.calls.indexOf(device.calls.stream()
            .filter(c -> c.startsWith("link:")).findFirst().orElseThrow());
        assertEquals(2, binds.size(), device.calls.toString());
        assertTrue(binds.get(0).endsWith(":10:mc_Entity"), binds.toString());
        assertTrue(binds.get(1).endsWith(":12:at_tangent"), binds.toString());
        assertTrue(device.calls.indexOf(binds.get(1)) < link, "binds precede link");
    }

    @Test
    void noDeclaredAttributesMeansNoBind() {
        ScriptedGLDevice device = new ScriptedGLDevice();
        device.failLink = true;
        GlProgramBuilder.build(planned(Set.of()), device, new HashMap<>(), new ArrayList<>());
        assertTrue(device.calls.stream().noneMatch(c -> c.startsWith("bindAttributeLocation:")));
    }

    private static Planner.PlannedSlot planned(Set<ExtendedAttribute> attributes) {
        ProgramSlotDescriptor descriptor = ClassicProgramCatalog.rows().stream()
            .filter(d -> d.id().packName().equals("gbuffers_terrain")).findFirst().orElseThrow();
        Planner.PlannedSlot planned = new Planner.PlannedSlot(descriptor);
        MaterializationFingerprint fp = new MaterializationFingerprint("fp-vertex");
        SourceKey root = new SourceKey(DimensionKey.BASE, "gbuffers_terrain",
            ShaderSourceStage.VERTEX,
            new SourceId(new NormalizedPackPath("shaders/gbuffers_terrain.vsh")));
        planned.materialized = List.of(new Planner.MaterializedStage(ShaderSourceStage.VERTEX,
            new MaterializedSource(root, "void main(){}", new SourceMap(Map.of(), List.of()),
                new DeclaredUniformCatalog(fp, List.of()),
                new ShaderLanguage(120, false, Optional.empty(), List.of()),
                new GeometrySourceForm.None(), List.of(), fp)));
        planned.stateBundle = new ProgramStateBundle(
            new DrawRouting.AllUsedBuffers(BufferDomain.COLORTEX),
            Set.of(), 1, attributes,
            Optional.empty(), Optional.empty(), Optional.empty(),
            Map.of(), Optional.empty(), GeometryInputRequirement.NONE);
        return planned;
    }
}
