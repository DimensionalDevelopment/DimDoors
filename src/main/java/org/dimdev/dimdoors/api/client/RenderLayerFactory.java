package org.dimdev.dimdoors.api.client;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.renderer.RenderType;

import org.dimdev.dimdoors.mixin.client.accessor.RenderLayerAccessor;

@OnlyIn(Dist.CLIENT)
public class RenderLayerFactory {
	public static RenderType create(String name, VertexFormat vertexFormat, VertexFormat.Mode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, RenderType.CompositeState phases) {
		return RenderLayerAccessor.callCreate(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, phases);
	}
}
