package org.dimdev.dimdoors.client;

import org.dimdev.dimdoors.api.client.DimensionalPortalRenderer;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EntranceRiftBlockEntityRenderer implements BlockEntityRenderer<EntranceRiftBlockEntity> {
	@Override
	public void render(EntranceRiftBlockEntity blockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
		DimensionalPortalRenderer.renderDimensionalPortal(matrixStack, vertexConsumerProvider, blockEntity.getTransformer(), tickDelta, light, overlay, blockEntity.isTall());
	}
}
