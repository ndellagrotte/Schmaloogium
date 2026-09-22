// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors
package com.schmaloogium.mod.glue.textures;

import com.schmaloogium.engine.buffers.BaseAtlasContext;
import com.schmaloogium.engine.buffers.TextureHandleRef;
import com.schmaloogium.engine.frame.PipelineVersion;
import com.schmaloogium.engine.frame.spi.AtlasBindingEvidence;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.textures.AtlasBindingObservation;
import com.schmaloogium.engine.textures.AtlasBindingObserver;
import com.schmaloogium.engine.textures.AtlasBindingObservers;
import com.schmaloogium.mod.glue.VanillaForeignTextures;
import com.schmaloogium.mod.glue.frame.ResourceReloadBoundary;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/** Render-thread issuer for actual unit-zero bindings; evidence never exposes credentials. */
public final class TextureBindingRuntime {
    private static final Map<Integer, Incarnation> OBJECTS = new HashMap<>();
    private static long objectSerial;
    private static long bindSerial;
    private static long resourceEpoch = -1;
    private static int actualBase;
    private static Evidence latest;
    private static int expectedBase = -1;
    private static com.schmaloogium.engine.frame.spi.AtlasBindingSink bindingSink;
    private static Supplier<Optional<PipelineVersion>> composition = Optional::empty;
    private static final AtlasBindingObserver OBSERVER = new AtlasBindingObserver() {
        @Override
        public AtlasBindingObservation authenticate(AtlasBindingEvidence evidence) {
            if (!(evidence instanceof Evidence issued) || !onRenderThread()) {
                return AtlasBindingObservation.InvalidBase.INSTANCE;
            }
            if (!isLatest(issued)) return AtlasBindingObservation.StaleBase.INSTANCE;
            return new AtlasBindingObservation.Authenticated(issued.association, issued.base, issued);
        }

        @Override
        public boolean isLatest(Object token) {
            if (!onRenderThread() || !(token instanceof Evidence issued)) return false;
            return issued == latest && issued.serial == bindSerial
                && issued.epoch == ResourceReloadBoundary.epoch()
                && composition.get().filter(issued.version::equals).isPresent()
                && currentBaseName() == actualBase;
        }
    };
    private static final TextureSourcePreparer.ForeignObjects FOREIGN = new TextureSourcePreparer.ForeignObjects() {
        @Override
        public Optional<ForeignLive> resolve(String identity, long epoch) {
            return resource(identity, epoch).map(value -> new ForeignLive(true, value.serial));
        }

        @Override
        public Optional<TextureHandle> handle(String identity, long epoch, long objectEpoch) {
            return resource(identity, epoch).filter(value -> value.serial == objectEpoch)
                .flatMap(value -> value.handle);
        }
    };

    private TextureBindingRuntime() { }

    public static void install(Supplier<Optional<PipelineVersion>> currentComposition) {
        composition = Objects.requireNonNull(currentComposition, "currentComposition");

        AtlasBindingObservers.install(OBSERVER);
    }
    public static void installBindingSink(com.schmaloogium.engine.frame.spi.AtlasBindingSink sink) {
        bindingSink = Objects.requireNonNull(sink, "sink");
    }

    public static TextureSourcePreparer.ForeignObjects foreignObjects() { return FOREIGN; }

    public static AtlasBindingEvidence observeBase(PipelineVersion version, long epoch) {
        return observe(version, epoch, true);
    }

    /** Fullscreen has no vanilla base input; this does not claim that unit zero is unbound. */
    public static AtlasBindingEvidence observeNoBase(PipelineVersion version, long epoch) {
        return observe(version, epoch, false);
    }

    private static AtlasBindingEvidence observe(PipelineVersion version, long epoch, boolean base) {
        if (!onRenderThread() || epoch != ResourceReloadBoundary.epoch()
                || composition.get().filter(version::equals).isEmpty()) {
            throw new IllegalStateException("texture evidence outside current composition");
        }
        refreshEpoch();
        actualBase = currentBaseName();
        Incarnation object = actualBase > 0 ? OBJECTS.get(actualBase) : null;
        if (base && actualBase > 0 && (object == null || object.texture == null)) {
            ITextureObject atlas = Minecraft.getMinecraft().getTextureMapBlocks();
            if (atlas instanceof com.schmaloogium.mod.mixin.textures.AbstractTextureAccessor raw
                    && raw.schmaloogium$textureName() == actualBase) {
                object = remember(actualBase, atlas);
            }
        }
        if (base && actualBase > 0 && (object == null || object.handle.isEmpty())) {
            object = remember(actualBase, object == null ? null : object.texture);
        }
        BaseAtlasContext association = new BaseAtlasContext.Unavailable();
        Optional<TextureHandleRef> handle = Optional.empty();
        if (base && object != null) {
            handle = object.handle.map(TextureHandleRef.Borrowed::new);
            if (object.texture != null) {
                association = MinecraftAtlasCapture.atlasFor(object.texture, epoch)
                    .<BaseAtlasContext>map(BaseAtlasContext.Atlas::new)
                    .orElse(objectAssociationUnavailable(object.texture));
            }
        }
        latest = new Evidence(version, epoch, ++bindSerial, association, handle);
        return latest;
    }

    /** Called after vanilla's cached bind has completed, including restoration. */
    public static void onTextureBinding() {
        if (!onRenderThread() || GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE) != GL13.GL_TEXTURE0) return;
        refreshEpoch();
        int bound = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        if (expectedBase == bound) {
            actualBase = bound;
            return;
        }
        if (bound != actualBase) {
            actualBase = bound;
            bindSerial++;
            latest = null;
            if (bindingSink != null) {
                Optional<PipelineVersion> version = composition.get();
                if (version.isPresent()) {
                    var result = bindingSink.currentBinding(
                        observeBase(version.get(), ResourceReloadBoundary.epoch()));
                    if (!(result instanceof com.schmaloogium.engine.frame.spi.SignalResult.Accepted)) {
                        latest = null;
                        bindSerial++;
                        throw new IllegalStateException("actual base texture refresh rejected: " + result);
                    }
                }
            }
        }
    }

    /** Associates the successful TextureManager bind with its actual object incarnation. */
    public static void onTextureBound(ResourceLocation location) {
        if (!onRenderThread()) return;
        refreshEpoch();
        ITextureObject texture = Minecraft.getMinecraft().getTextureManager().getTexture(location);
        if (texture == null) return;
        int name = texture instanceof com.schmaloogium.mod.mixin.textures.AbstractTextureAccessor raw
            ? raw.schmaloogium$textureName() : texture.getGlTextureId();
        if (name > 0 && GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D) == name) remember(name, texture);
    }

    /** Native deletion invalidates an incarnation even when GL subsequently reuses its name. */
    public static void onTextureDeleted(int name) {
        if (!onRenderThread()) return;
        MinecraftAtlasCapture.textureDeleted(name);
        OBJECTS.remove(name);
        if (name == actualBase) {
            bindSerial++;
            latest = null;
        }
    }

    /** Brackets only the backend's resolved unit-zero operation, never a vanilla batch. */
    public static void beginExpectedBase(int name) {
        if (expectedBase != -1) throw new IllegalStateException("nested expected texture bind");
        if (currentBaseName() != actualBase) {
            bindSerial++;
            latest = null;
        }
        expectedBase = name;
    }

    public static void endExpectedBase(boolean completed) {
        int expected = expectedBase;
        expectedBase = -1;
        int observed = currentBaseName();
        actualBase = observed;
        if (!completed || observed != expected) {
            bindSerial++;
            latest = null;
            if (completed) throw new IllegalStateException("resolved texture bind did not take effect");
        }
    }

    private static Optional<Incarnation> resource(String identity, long epoch) {
        if (!onRenderThread() || epoch != ResourceReloadBoundary.epoch()) return Optional.empty();
        refreshEpoch();
        ITextureObject texture = Minecraft.getMinecraft().getTextureManager().getTexture(new ResourceLocation(identity));
        if (texture == null) return Optional.empty();
        int name = texture instanceof com.schmaloogium.mod.mixin.textures.AbstractTextureAccessor raw
            ? raw.schmaloogium$textureName() : texture.getGlTextureId();
        if (name <= 0 || !GL11.glIsTexture(name)) return Optional.empty();
        return Optional.of(remember(name, texture));
    }

    private static Incarnation remember(int name, ITextureObject texture) {
        Incarnation previous = OBJECTS.get(name);
        if (previous != null && previous.handle.isPresent()
                && (texture == null || previous.texture == texture)) return previous;
        if (previous != null && previous.texture == null && previous.handle.isPresent()) {
            Incarnation associated = new Incarnation(texture, previous.serial, previous.handle);
            OBJECTS.put(name, associated);
            return associated;
        }
        long serial = ++objectSerial;
        long epoch = resourceEpoch;
        Optional<TextureHandle> handle = VanillaForeignTextures.observedHandle(name,
            () -> ResourceReloadBoundary.epoch() == epoch && OBJECTS.containsKey(name)
                && OBJECTS.get(name).serial == serial);
        Incarnation current = new Incarnation(texture, serial, handle);
        OBJECTS.put(name, current);
        return current;
    }

    private static BaseAtlasContext objectAssociationUnavailable(ITextureObject texture) {
        return texture instanceof net.minecraft.client.renderer.texture.TextureMap
            ? new BaseAtlasContext.Unavailable() : new BaseAtlasContext.NonAtlas();
    }

    private static void refreshEpoch() {
        long epoch = ResourceReloadBoundary.epoch();
        if (resourceEpoch == epoch) return;
        resourceEpoch = epoch;
        OBJECTS.clear();
        latest = null;
        bindSerial++;
    }

    private static int currentBaseName() {
        int active = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        try {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            return GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        } finally {
            GL13.glActiveTexture(active);
        }
    }

    private static boolean onRenderThread() {
        Minecraft minecraft = Minecraft.getMinecraft();
        return minecraft != null && minecraft.isCallingFromMinecraftThread();
    }

    private record Incarnation(ITextureObject texture, long serial, Optional<TextureHandle> handle) { }
    private record Evidence(PipelineVersion version, long epoch, long serial,
                            BaseAtlasContext association, Optional<TextureHandleRef> base)
        implements AtlasBindingEvidence { }
}
