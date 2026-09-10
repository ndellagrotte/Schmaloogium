// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.frame;

import com.schmaloogium.engine.frame.dispatch.RenderSection;
import com.schmaloogium.engine.frame.ScopeOpenResult;
import com.schmaloogium.mod.glue.frame.FrameHooks;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The particle scopes (PHASE_7_DOC §4.10.5, H-PARTICLE-01/02): unlit (gbuffers_textured)
 * and lit (gbuffers_textured_lit) particle rendering. MCP-named targets; remapJar generates the SRG refmap (D-5).
 */
@Mixin(ParticleManager.class)
public abstract class MixinParticleManager {

    /** H-PARTICLE-01: gbuffers_textured. */
    @Inject(method = "renderParticles(Lnet/minecraft/entity/Entity;F)V", at = @At("HEAD"),
            require = 0, expect = 1)
    private void schmaloogium$unlitEnter(Entity entityIn, float partialTicks, CallbackInfo ci) {
        this.unlitScope = FrameHooks.enterSection(RenderSection.PARTICLES_UNLIT);
    }

    @Inject(method = "renderParticles(Lnet/minecraft/entity/Entity;F)V", at = @At("RETURN"),
            require = 0, expect = 1)
    private void schmaloogium$unlitExit(Entity entityIn, float partialTicks, CallbackInfo ci) {
        FrameHooks.exitSection(RenderSection.PARTICLES_UNLIT, this.unlitScope);
        this.unlitScope = null;
    }

    private ScopeOpenResult unlitScope;

    /** H-PARTICLE-02: gbuffers_textured_lit. */
    @Inject(method = "renderLitParticles(Lnet/minecraft/entity/Entity;F)V", at = @At("HEAD"),
            require = 0, expect = 1)
    private void schmaloogium$litEnter(Entity entityIn, float partialTicks, CallbackInfo ci) {
        this.litScope = FrameHooks.enterSection(RenderSection.PARTICLES_LIT);
    }

    @Inject(method = "renderLitParticles(Lnet/minecraft/entity/Entity;F)V", at = @At("RETURN"),
            require = 0, expect = 1)
    private void schmaloogium$litExit(Entity entityIn, float partialTicks, CallbackInfo ci) {
        FrameHooks.exitSection(RenderSection.PARTICLES_LIT, this.litScope);
        this.litScope = null;
    }

    private ScopeOpenResult litScope;
}
