package org.dimdev.dimdoors.client;

import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.registry.Registry;
import org.dimdev.dimdoors.block.entity.FakeBlockEntity;

public class FakeBlockRenderer implements BlockEntityRenderer<FakeBlockEntity> {
	@Override
	public void render(FakeBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		if(entity.getWorld()!=null) {
			BakedModel mirroredModel = BakedModelManagerHelper.getModel(MinecraftClient.getInstance().getBakedModelManager(), Registry.BLOCK.getId(entity.getMirror()));
			MinecraftClient.getInstance().getBlockRenderManager().renderBlock(entity.getMirror().getDefaultState(), entity.getPos(),
					entity.getWorld(), matrices, vertexConsumers.getBuffer(RenderLayer.getSolid()), false, entity.getWorld().random);
		}
	}
}
