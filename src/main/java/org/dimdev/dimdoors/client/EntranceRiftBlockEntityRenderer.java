package org.dimdev.dimdoors.client;

import java.util.Collections;
import java.util.Random;

import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.mixin.client.accessor.RenderLayerAccessor;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EntranceRiftBlockEntityRenderer implements BlockEntityRenderer<EntranceRiftBlockEntity> {
	public static final Identifier WARP_PATH;
	private static final RenderPhase.class_5942 DIMENSIONAL_PORTAL_SHADER;
	private static final RenderLayer RENDER_LAYER;
	private static final ModelPart MODEL;
	private static final ModelPart TALL_MODEL;
	private static final Random RANDOM = new Random();

	@Override
	public void render(EntranceRiftBlockEntity blockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
		float r = MathHelper.clamp((RANDOM.nextFloat() * 0.3F + 0.1F) * tickDelta, 0, 1);
		float g = MathHelper.clamp((RANDOM.nextFloat() * 0.4F + 0.1F) * tickDelta, 0, 1);
		float b = MathHelper.clamp((RANDOM.nextFloat() * 0.5F + 0.6F) * tickDelta, 0, 1);
		blockEntity.getTransformer().transform(matrixStack);
		if (blockEntity.isTall()) {
			TALL_MODEL.render(matrixStack, vertexConsumerProvider.getBuffer(RENDER_LAYER), light, overlay, r, g, b, 1);
		} else {
			MODEL.render(matrixStack, vertexConsumerProvider.getBuffer(RENDER_LAYER), light, overlay, r, g, b, 1);
		}
	}


	static {
		WARP_PATH = new Identifier("dimdoors:textures/other/warp.png");
		DIMENSIONAL_PORTAL_SHADER = new RenderPhase.class_5942(ModShaders::getDimensionalPortal);
		RENDER_LAYER = RenderLayerAccessor.callOf(
				"dimensional_portal",
				VertexFormats.POSITION,
				VertexFormat.DrawMode.QUADS,
				256,
				false,
				false,
				RenderLayer.MultiPhaseParameters.builder()
						.method_34578(DIMENSIONAL_PORTAL_SHADER)
						.method_34577(
								RenderPhase.class_5940.method_34560()
										.method_34563(EndPortalBlockEntityRenderer.SKY_TEXTURE, false, false)
										.method_34563(WARP_PATH, false, false)
										.method_34562()
						)
						.build(false)
		);
		ModelPart.Cuboid small = new ModelPart.Cuboid(0, 0, 0, 0, 0, 16, 16, 0.2F, 0, 0, 0, false, 1024, 1024);
		MODEL = new ModelPart(Collections.singletonList(small), Collections.emptyMap());
		ModelPart.Cuboid big = new ModelPart.Cuboid(0, 0, 0, 0, 0, 16, 32, 0.2F, 0, 0, 0, false, 1024, 1024);
		TALL_MODEL = new ModelPart(Collections.singletonList(big), Collections.emptyMap());
	}
}
