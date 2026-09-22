// SPDX-License-Identifier: GPL-3.0-or-later
package com.schmaloogium.mod.mixin.textures;

import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextureMap.class)
public interface TextureMapAccessor {
    @Accessor("mapUploadedSprites") Map<String, TextureAtlasSprite> schmaloogium$uploadedSprites();
    @Accessor("listAnimatedSprites") List<TextureAtlasSprite> schmaloogium$animatedSprites();
    @Accessor("mipmapLevels") int schmaloogium$mipmapLevels();
}
