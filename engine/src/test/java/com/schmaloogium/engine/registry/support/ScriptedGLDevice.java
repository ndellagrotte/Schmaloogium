// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.support;

import com.schmaloogium.engine.gl.AlphaBlendOverride;
import com.schmaloogium.engine.gl.AlphaTestState;
import com.schmaloogium.engine.gl.BlendState;
import com.schmaloogium.engine.gl.CompileResult;
import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.gl.GLHandle;
import com.schmaloogium.engine.gl.LegacyGeometryInputPrimitive;
import com.schmaloogium.engine.gl.LegacyGeometryOutputPrimitive;
import com.schmaloogium.engine.gl.LinkResult;
import com.schmaloogium.engine.gl.LinkedGeometryInputPrimitive;
import com.schmaloogium.engine.gl.ProgramHandle;
import com.schmaloogium.engine.gl.SamplerInitializationResult;
import com.schmaloogium.engine.gl.ShaderHandle;
import com.schmaloogium.engine.gl.ShaderService;
import com.schmaloogium.engine.gl.ShaderStage;
import com.schmaloogium.engine.gl.StateService;
import com.schmaloogium.engine.gl.UniformLocation;
import com.schmaloogium.engine.gl.UniformService;
import com.schmaloogium.engine.gl.ValidateResult;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Headless scripted GLDevice (PHASE_4_DOC §4.7 test seam). The shader service records every
 * lifecycle call in order; compile/link/validate succeed unless scripted otherwise; sampler
 * initialization completes; the state service hands out closable alpha/blend locks.
 */
public final class ScriptedGLDevice implements GLDevice {

    /** Ordered record of every shader/state call a test may assert on. */
    public final List<String> calls = new ArrayList<>();
    public final List<Integer> programHandlesDeleted = new ArrayList<>();
    public final List<Integer> samplerUnitsInitialized = new ArrayList<>();
    public boolean failCompile;
    public boolean failLink;
    public boolean failValidate;
    public boolean failSamplerInit;
    public boolean samplerInitRestored = true;
    public LinkedGeometryInputPrimitive linkedGeometryInput;

    private int nextId = 1;

    private int take() {
        return nextId++;
    }

    @Override
    public List<com.schmaloogium.engine.gl.GLError> drainErrors() {
        return List.of();
    }

    @Override
    public com.schmaloogium.engine.gl.GLCapabilityProfile capabilities() {
        return new com.schmaloogium.engine.gl.GLCapabilityProfile(
            4, 6, "4.60", "scripted", "scripted",
            8, 8, 16, 16, 16384, 0, 0,
            java.util.Set.of("GL_ARB_geometry_shader4"));
    }

    public ScriptedGLDevice() {
        this.linkedGeometryInput = null;
    }

    public boolean usedFixedFunctionLast() {
        for (int i = calls.size() - 1; i >= 0; i--) {
            String call = calls.get(i);
            if (call.equals("useFixedFunction")) {
                return true;
            }
            if (call.startsWith("use:")) {
                return false;
            }
        }
        return false;
    }

    final class TestProgramHandle implements ProgramHandle {
        final int value = take();

        int value() {
            return value;
        }
    }

    final class TestShaderHandle implements ShaderHandle {
        final int value = take();

        int value() {
            return value;
        }
    }

    private final ShaderService shaderService = new ShaderService() {
        @Override
        public ShaderHandle createShader(ShaderStage stage, String source) {
            calls.add("createShader:" + stage + ":" + stage.ordinal());
            return new TestShaderHandle();
        }

        @Override
        public CompileResult compile(ShaderHandle shader) {
            calls.add("compile:" + ((TestShaderHandle) shader).value());
            if (failCompile) {
                return new CompileResult(false, "scripted compile failure", null);
            }
            return new CompileResult(true, "", null);
        }

        @Override
        public ProgramHandle createProgram() {
            calls.add("createProgram");
            return new TestProgramHandle();
        }

        @Override
        public void attach(ProgramHandle p, ShaderHandle s) {
            calls.add("attach:" + ((TestProgramHandle) p).value() + ":" + ((TestShaderHandle) s).value());
        }

        @Override
        public void bindAttributeLocation(ProgramHandle p, int location, String name) {
            calls.add("bindAttributeLocation:" + ((TestProgramHandle) p).value() + ":" + location + ":" + name);
        }

        @Override
        public void configureLegacyGeometry(ProgramHandle p,
                LegacyGeometryInputPrimitive input,
                LegacyGeometryOutputPrimitive output, int maxVerticesOut) {
            calls.add("configureLegacyGeometry:" + ((TestProgramHandle) p).value() + ":" + input + ":" + output
                + ":" + maxVerticesOut);
        }

        @Override
        public LinkResult link(ProgramHandle p) {
            calls.add("link:" + ((TestProgramHandle) p).value());
            if (failLink) {
                return new LinkResult(false, "scripted link failure", null);
            }
            return new LinkResult(true, "", null);
        }

        @Override
        public Optional<LinkedGeometryInputPrimitive> linkedGeometryInput(ProgramHandle p) {
            calls.add("linkedGeometryInput:" + ((TestProgramHandle) p).value());
            return Optional.ofNullable(linkedGeometryInput);
        }

        @Override
        public SamplerInitializationResult initializeSamplerUnits(
                ProgramHandle p, List<com.schmaloogium.engine.gl.SamplerUnitAssignment> assignments) {
            calls.add("initializeSamplerUnits:" + ((TestProgramHandle) p).value() + ":" + assignments.size());
            for (var assignment : assignments) {
                samplerUnitsInitialized.add(assignment.unit());
            }
            if (failSamplerInit) {
                return new SamplerInitializationResult.Failed(
                    "scripted sampler failure", samplerInitRestored);
            }
            return new SamplerInitializationResult.Completed();
        }

        @Override
        public ValidateResult validate(ProgramHandle p) {
            calls.add("validate:" + ((TestProgramHandle) p).value());
            if (failValidate) {
                return new ValidateResult(false, "scripted validate failure", null);
            }
            return new ValidateResult(true, "", null);
        }

        @Override
        public void use(ProgramHandle p) {
            calls.add("use:" + ((TestProgramHandle) p).value());
        }

        @Override
        public void useFixedFunction() {
            calls.add("useFixedFunction");
        }

        @Override
        public void delete(ProgramHandle p) {
            calls.add("deleteProgram:" + ((TestProgramHandle) p).value());
            programHandlesDeleted.add(((TestProgramHandle) p).value());
        }

        @Override
        public void delete(ShaderHandle s) {
            calls.add("deleteShader:" + ((TestShaderHandle) s).value());
        }
    };

    private final class TestOverride implements AlphaBlendOverride {
        boolean closed;

        @Override
        public void close() {
            if (!closed) {
                closed = true;
                calls.add("closeOverride");
            }
        }
    }

    final List<TestOverride> overrides = new ArrayList<>();

    private final StateService stateService = new StateService() {
        @Override
        public void viewport(int x, int y, int w, int h) {
        }

        @Override
        public void clearColor(float r, float g, float b, float a) {
        }

        @Override
        public void clear(java.util.EnumSet<com.schmaloogium.engine.gl.ClearTarget> targets) {
        }

        @Override
        public void depthMask(boolean enabled) {
        }

        @Override
        public void depthTest(boolean enabled) {
        }

        @Override
        public void blend(BlendState state) {
        }

        @Override
        public void alphaTest(AlphaTestState state) {
        }

        @Override
        public AlphaBlendOverride lockAlphaBlend(
                Optional<AlphaTestState> alpha, Optional<BlendState> blend) {
            calls.add("lockAlphaBlend:" + alpha.isPresent() + ":" + blend.isPresent());
            TestOverride override = new TestOverride();
            overrides.add(override);
            return override;
        }

        @Override
        public BlendState effectiveBlend() {
            calls.add("effectiveBlend");
            return new BlendState(
                BlendState.BlendFactor.ONE, BlendState.BlendFactor.ZERO,
                BlendState.BlendFactor.ONE, BlendState.BlendFactor.ZERO);
        }

        @Override
        public void fog(com.schmaloogium.engine.gl.FogState state) {
        }

        @Override
        public com.schmaloogium.engine.gl.StateSnapshot snapshot(
                java.util.EnumSet<com.schmaloogium.engine.gl.StateAspect> aspects) {
            throw new UnsupportedOperationException("snapshot not scripted");
        }

        @Override
        public void restore(com.schmaloogium.engine.gl.StateSnapshot snapshot) {
        }
    };

    private final UniformService uniformService = (UniformService) Proxy.newProxyInstance(
        getClass().getClassLoader(),
        new Class<?>[] {UniformService.class},
        (proxy, method, args) -> {
            if ("locate".equals(method.getName())) {
                if (args[1] instanceof String name && name.equals("tintColor")) {
                    return location((Integer) args[0], name);
                }
                return null;
            }
            return defaultValue(method.getReturnType());
        });

    private UniformLocation location(int programId, String name) {
        return (UniformLocation) Proxy.newProxyInstance(
            getClass().getClassLoader(),
            new Class<?>[] {UniformLocation.class},
            (proxy, method, args) -> defaultValue(method.getReturnType()));
    }

    private final com.schmaloogium.engine.gl.DebugService debugService =
        new com.schmaloogium.engine.gl.DebugService() {
            @Override
            public void pushGroup(String label) {
            }

            @Override
            public void popGroup() {
            }

            @Override
            public void label(GLHandle handle, String label) {
                calls.add("label:" + System.identityHashCode(handle) + ":" + label);
            }

            @Override
            public boolean isActive() {
                return false;
            }
        };

    private Object serviceOrThrow(Class<?> service) {
        if (service == ShaderService.class) {
            return shaderService;
        }
        if (service == StateService.class) {
            return stateService;
        }
        if (service == UniformService.class) {
            return uniformService;
        }
        if (service == com.schmaloogium.engine.gl.DebugService.class) {
            return debugService;
        }
        return Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {service},
            (proxy, method, args) -> {
                throw new UnsupportedOperationException(service.getSimpleName()
                    + "." + method.getName() + " not scripted");
            });
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        Map<Class<?>, Object> defaults = new LinkedHashMap<>();
        defaults.put(boolean.class, false);
        defaults.put(int.class, 0);
        defaults.put(long.class, 0L);
        defaults.put(float.class, 0f);
        defaults.put(double.class, 0d);
        defaults.put(void.class, null);
        return defaults.get(type);
    }

    @Override
    public ShaderService shaders() {
        return shaderService;
    }

    @Override
    public UniformService uniforms() {
        return uniformService;
    }

    @Override
    public com.schmaloogium.engine.gl.TextureService textures() {
        return (com.schmaloogium.engine.gl.TextureService) serviceOrThrow(
            com.schmaloogium.engine.gl.TextureService.class);
    }

    @Override
    public com.schmaloogium.engine.gl.FramebufferService framebuffers() {
        return (com.schmaloogium.engine.gl.FramebufferService) serviceOrThrow(
            com.schmaloogium.engine.gl.FramebufferService.class);
    }

    @Override
    public StateService state() {
        return stateService;
    }

    @Override
    public com.schmaloogium.engine.gl.DrawService draw() {
        return (com.schmaloogium.engine.gl.DrawService) serviceOrThrow(
            com.schmaloogium.engine.gl.DrawService.class);
    }

    @Override
    public com.schmaloogium.engine.gl.DebugService debug() {
        return debugService;
    }

    @Override
    public com.schmaloogium.engine.gl.VertexInputService vertexInputs() {
        return (com.schmaloogium.engine.gl.VertexInputService) serviceOrThrow(
            com.schmaloogium.engine.gl.VertexInputService.class);
    }
}
