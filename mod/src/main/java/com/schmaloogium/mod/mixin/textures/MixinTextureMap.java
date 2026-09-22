// SPDX-License-Identifier: GPL-3.0-or-later
package com.schmaloogium.mod.mixin.textures;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.schmaloogium.mod.glue.textures.MinecraftAtlasCapture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.ITextureMapPopulator;
import net.minecraft.client.resources.IResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureMap.class)
public abstract class MixinTextureMap {
    @WrapMethod(method = "loadSprites(Lnet/minecraft/client/resources/IResourceManager;Lnet/minecraft/client/renderer/texture/ITextureMapPopulator;)V", require = 0, expect = 1)
    private void schmaloogium$outerLoad(IResourceManager resources, ITextureMapPopulator populator, Operation<Void> original) {
        TextureMap map = (TextureMap) (Object) this;
        boolean normal = false;
        MinecraftAtlasCapture.begin(map, resources);
        try {
            original.call(resources, populator);
            normal = true;
        } finally {
            MinecraftAtlasCapture.end(map, normal);
        }
    }

    @Inject(method = "loadTextureAtlas(Lnet/minecraft/client/resources/IResourceManager;)V", at = @At("HEAD"), require = 0, expect = 1)
    private void schmaloogium$innerLoad(IResourceManager resources, CallbackInfo ci) {
        MinecraftAtlasCapture.innerLoad((TextureMap) (Object) this);
    }

    @Inject(method = "updateAnimations()V", at = @At("TAIL"), require = 0, expect = 1)
    private void schmaloogium$animation(CallbackInfo ci) {
        MinecraftAtlasCapture.animations((TextureMap) (Object) this);
    }
}
