package org.dimdev.dimdoors.api.client;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;

@Environment(EnvType.CLIENT)
public class RenderLayerFactory {
	public static RenderType create(String name, VertexFormat vertexFormat, VertexFormat.Mode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, RenderType.CompositeState phases) {
		return RenderType.create(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, phases);
	}
}
