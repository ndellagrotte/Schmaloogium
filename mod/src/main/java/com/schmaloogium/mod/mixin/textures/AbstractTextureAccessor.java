// SPDX-License-Identifier: GPL-3.0-or-later
package com.schmaloogium.mod.mixin.textures;

import net.minecraft.client.renderer.texture.AbstractTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractTexture.class)
public interface AbstractTextureAccessor {
    @Accessor("glTextureId") int schmaloogium$textureName();
}
