package org.dimdev.dimdoors.api.client;

import java.util.Collections;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.resources.ResourceLocation;

import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.client.ModShaders;

@OnlyIn(Dist.CLIENT)
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
		model.render(matrixStack, vertexConsumerProvider.getBuffer(RENDER_LAYER), light, overlay);
	}

	static {
		WARP_PATH = DimensionalDoors.resource("textures/other/warp.png");
		DIMENSIONAL_PORTAL_SHADER = new RenderStateShard.ShaderStateShard(ModShaders::getDimensionalPortal);
		RENDER_LAYER = RenderLayerFactory.create(
				"dimensional_portal",
				DefaultVertexFormat.POSITION,
				VertexFormat.Mode.QUADS,
				256,
				false,
				false,
				RenderType.CompositeState.builder()
						.setShaderState(DIMENSIONAL_PORTAL_SHADER)
						.setTextureState(
								RenderStateShard.MultiTextureStateShard.builder()
										.add(TheEndPortalRenderer.END_SKY_LOCATION, false, false)
										.add(WARP_PATH, false, false)
										.build()
						)
						.createCompositeState(false)
		);
		ModelPart.Cube small = new ModelPart.Cube(0, 0, 0.2f, 0.2f, -0.1f, 15.8f, 15.8f, 0.01F, 0, 0, 0, false, 1024, 1024);
		MODEL = new ModelPart(Collections.singletonList(small), Collections.emptyMap());
		ModelPart.Cube big = new ModelPart.Cube(0, 0, 0.2f, 0.2f, -0.1f, 15.8f, 31.8f, 0.01F, 0, 0, 0, false, 1024, 1024);
		TALL_MODEL = new ModelPart(Collections.singletonList(big), Collections.emptyMap());
	}
}
