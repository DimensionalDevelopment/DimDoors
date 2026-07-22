package org.dimdev.dimdoors.api.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.client.DimensionalDoorsClient;
import org.dimdev.dimdoors.client.ModShaders;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Environment(EnvType.CLIENT)
public final class DimensionalPortalRenderer {
	public static final ResourceLocation WARP_PATH;
	private static final RenderStateShard.ShaderStateShard DIMENSIONAL_PORTAL_SHADER;
	private static final RenderType RENDER_LAYER;
	private static final ModelPart MODEL;
	private static final ModelPart TALL_MODEL;

	public static void renderDimensionalPortal(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, Transformer transformer, float tickDelta, int light, int overlay) {
		renderDimensionalPortal(matrixStack, vertexConsumerProvider, transformer, tickDelta, light, overlay, true);
	}

	public static void renderDimensionalPortal(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, Transformer transformer, float tickDelta, int light, int overlay, boolean tall) {
		ModelPart model = tall ? TALL_MODEL : MODEL;
		renderModelWithPortalShader(model, matrixStack, vertexConsumerProvider, transformer, tickDelta, light, overlay);
	}

	public static void renderModelWithPortalShader(ModelPart model, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, Transformer transformer, float tickDelta, int light, int overlay) {
		transformer.transform(matrixStack);
		model.render(matrixStack, vertexConsumerProvider.getBuffer(DimensionalDoorsClient.detector.shaderPackOn() ? RenderType.entitySolid(WARP_PATH) : RENDER_LAYER), light, overlay);
	}

	static {
		WARP_PATH = DimensionalDoors.id("textures/other/warp.png");
		DIMENSIONAL_PORTAL_SHADER = new RenderStateShard.ShaderStateShard(ModShaders::getDimensionalPortal);
		RENDER_LAYER = RenderLayerFactory.create(
				"dimensional_portal",
				DefaultVertexFormat.POSITION,
				VertexFormat.Mode.QUADS,
				256,
				false,
				true,
				RenderType.CompositeState.builder()
						.setShaderState(DIMENSIONAL_PORTAL_SHADER)
						.setTextureState(
								RenderStateShard.MultiTextureStateShard.builder()
										.add(TheEndPortalRenderer.END_SKY_LOCATION, false, false)
										.add(WARP_PATH, false, false)
										.build()
						)
						.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
						.setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, false))
						.createCompositeState(false)
		);
		Set<Direction> directions = new HashSet<>(List.of(Direction.values()));
		ModelPart.Cube small = new ModelPart.Cube(0, 0, 0.201f, 0.201f, 0.001f, 15.798f, 15.798f, 1.0F, 0, 0, 0, false, 1024, 1024, directions);
		MODEL = new ModelPart(Collections.singletonList(small), Collections.emptyMap());
		ModelPart.Cube big = new ModelPart.Cube(0, 0, 0.201f, 0.201f, 0.001f, 15.798f, 31.798f, 1.0F, 0, 0, 0, false, 1024, 1024, directions);
		TALL_MODEL = new ModelPart(Collections.singletonList(big), Collections.emptyMap());
	}
}
