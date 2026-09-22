// SPDX-License-Identifier: GPL-3.0-or-later
package com.schmaloogium.mod.mixin.textures;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextureAtlasSprite.class)
public interface TextureAtlasSpriteAccessor {
    @Accessor("animationMetadata") AnimationMetadataSection schmaloogium$animationMetadata();
    @Accessor("frameCounter") int schmaloogium$frameCounter();
    @Accessor("tickCounter") int schmaloogium$tickCounter();
}
