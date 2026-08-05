package org.dimdev.dimdoors.mixin.client.accessor;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {
    @Accessor("cullingFrustum")
    Frustum dimdoors$getCullingFrustum();

    @Accessor("capturedFrustum")
    Frustum dimdoors$getCapturedFrustum();
}
