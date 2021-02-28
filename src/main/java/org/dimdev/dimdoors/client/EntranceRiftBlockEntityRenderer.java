package org.dimdev.dimdoors.client;

import java.util.Random;

import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EntranceRiftBlockEntityRenderer extends BlockEntityRenderer<EntranceRiftBlockEntity> {
	private static final Random RANDOM = new Random(31100L);

	public EntranceRiftBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	@Override
	public void render(EntranceRiftBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		if (!blockEntity.isIpPortalLinked())
		DimensionalPortalRenderer.renderWithTransforms(
				matrices,
				blockEntity.getPos(),
				DefaultTransformation.fromDirection(blockEntity.getOrientation()),
				vertexConsumers,
				light,
				overlay,
				blockEntity.isTall()
		);
	}
}
