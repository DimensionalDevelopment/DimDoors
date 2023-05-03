package org.dimdev.dimdoors.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.dimdev.dimdoors.api.client.DimensionalPortalRenderer;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.rift.targets.IdMarker;

@Environment(EnvType.CLIENT)
public class EntranceRiftBlockEntityRenderer implements BlockEntityRenderer<EntranceRiftBlockEntity> {
	@Override
	public void render(EntranceRiftBlockEntity blockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
		if (MinecraftClient.getInstance().player != null)
			if (MinecraftClient.getInstance().player.getStackInHand(Hand.MAIN_HAND).isOf(ModItems.RIFT_CONFIGURATION_TOOL))
				if (blockEntity.getData().getDestination() instanceof IdMarker idMarker) {
					matrixStack.push();
					matrixStack.translate(0.5, 0.5, 0.5);

					MinecraftClient.getInstance().textRenderer.draw(Text.of(String.valueOf(idMarker.getId())), 0f, 0f, 0xffffffff, false, matrixStack.peek().getPositionMatrix(), vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0x000000, LightmapTextureManager.MAX_LIGHT_COORDINATE);

					matrixStack.pop();
				}

		DimensionalPortalRenderer.renderDimensionalPortal(matrixStack, vertexConsumerProvider, blockEntity.getTransformer(), tickDelta, light, overlay, blockEntity.isTall());
	}
}
