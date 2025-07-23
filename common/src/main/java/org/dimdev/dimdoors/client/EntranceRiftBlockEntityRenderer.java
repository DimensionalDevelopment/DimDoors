package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.dimdev.dimdoors.api.client.DimensionalPortalRenderer;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.rift.targets.IdMarker;

import java.util.List;

@Environment(EnvType.CLIENT)
public class EntranceRiftBlockEntityRenderer implements BlockEntityRenderer<EntranceRiftBlockEntity> {
	@Override
	public void render(EntranceRiftBlockEntity blockEntity, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, int overlay) {
		if (Minecraft.getInstance().player != null)
			if (Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.RIFT_CONFIGURATION_TOOL.get()))
				if (blockEntity.getData().getDestination() instanceof IdMarker idMarker) {
					matrixStack.pushPose();
					matrixStack.translate(0.5, 0.5, 0.5);

					Minecraft.getInstance().font.drawInBatch(Component.literal(String.valueOf(idMarker.getId())), 0f, 0f, 0xffffffff, false, matrixStack.last().pose(), vertexConsumerProvider, Font.DisplayMode.NORMAL, 0x000000, LightTexture.FULL_BRIGHT);

					matrixStack.popPose();
				}

		var state = blockEntity.getRenderBlockState();

		renderBlockState(state, blockEntity.getLevel().getRandom(), matrixStack, vertexConsumerProvider, light, overlay);

		matrixStack.pushPose();
		matrixStack.translate(0, 1, 0);
		renderBlockState(state.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), blockEntity.getLevel().getRandom(), matrixStack, vertexConsumerProvider, light, overlay);
		matrixStack.popPose();


		DimensionalPortalRenderer.renderDimensionalPortal(matrixStack, vertexConsumerProvider, blockEntity.getTransformer(), tickDelta, light, overlay, blockEntity.isTall());

	}

	private void renderBlockState(BlockState renderState, RandomSource random, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, int overlay) {
		var model = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(renderState);
		var renderType = ItemBlockRenderTypes.getRenderType(renderState, false);
		var vertexConsumer = vertexConsumerProvider.getBuffer(renderType);

		for (var direction : Direction.values()) {
			var quads = model.getQuads(renderState, direction, random);
			renderQuads(matrixStack, vertexConsumer, quads, light, overlay);
		}

		var quads = model.getQuads(renderState, null, random);
		renderQuads(matrixStack, vertexConsumer, quads, light, overlay);
	}

	private void renderQuads(PoseStack stack, VertexConsumer consumer, List<BakedQuad> quads, int light, int overlay) {
		var pose = stack.last();
		for (var quad : quads) {
			consumer.putBulkData(pose, quad, 1.0F, 1.0F, 1.0F, 1.0F, light, overlay);
		}
	}
}
