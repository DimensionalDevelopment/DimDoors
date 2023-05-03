package org.dimdev.dimdoors.mixin.client.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;

@Mixin(RenderLayer.class)
public interface RenderLayerAccessor {
	@Invoker
	static RenderLayer.MultiPhase callOf(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, RenderLayer.MultiPhaseParameters phases) {
		throw new UnsupportedOperationException();
	}
}
