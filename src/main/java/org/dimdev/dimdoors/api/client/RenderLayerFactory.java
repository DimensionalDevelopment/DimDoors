package org.dimdev.dimdoors.api.client;

import org.dimdev.dimdoors.mixin.client.accessor.RenderLayerAccessor;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;

public class RenderLayerFactory {
	public static RenderLayer create(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, RenderLayer.MultiPhaseParameters phases) {
		return RenderLayerAccessor.callOf(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, phases);
	}
}
