package org.dimdev.dimdoors.mixin.client.accessor;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelRenderer.class)
public interface WorldRendererAccessor {
	@Accessor
	int getTicks();
}
