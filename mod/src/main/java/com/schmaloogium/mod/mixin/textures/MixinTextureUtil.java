// SPDX-License-Identifier: GPL-3.0-or-later
package com.schmaloogium.mod.mixin.textures;

import com.schmaloogium.mod.glue.textures.MinecraftAtlasCapture;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureUtil.class)
public abstract class MixinTextureUtil {
    @Inject(method = "allocateTextureImpl(IIII)V", at = @At("RETURN"), require = 0, expect = 1)
    private static void schmaloogium$storage(int nativeName, int mipLevels, int width, int height, CallbackInfo ci) {
        MinecraftAtlasCapture.storage(nativeName, mipLevels, width, height);
    }
}
