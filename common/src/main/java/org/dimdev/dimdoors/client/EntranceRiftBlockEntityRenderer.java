package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import org.dimdev.dimdoors.api.client.DimensionalPortalRenderer;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.rift.targets.IdMarker;

@Environment(EnvType.CLIENT)
public class EntranceRiftBlockEntityRenderer implements BlockEntityRenderer<EntranceRiftBlockEntity> {
	@Override
	public void render(EntranceRiftBlockEntity blockEntity, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, int overlay) {
		if (Minecraft.getInstance().player != null)
			if (Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.RIFT_CONFIGURATION_TOOL.get()))
				if (blockEntity.getData().getDestination() instanceof IdMarker idMarker) {
					matrixStack.pushPose();
					matrixStack.translate(0.5, 0.5, 0.5);

					Minecraft.getInstance().font.drawInBatch(Component.literal(String.valueOf(idMarker.getId())), 0f, 0f, 0xffffffff, false, matrixStack.last().pose(), vertexConsumerProvider, false, 0x000000, LightTexture.FULL_BRIGHT);

					matrixStack.popPose();
				}

		DimensionalPortalRenderer.renderDimensionalPortal(matrixStack, vertexConsumerProvider, blockEntity.getTransformer(), tickDelta, light, overlay, blockEntity.isTall());
	}
}
