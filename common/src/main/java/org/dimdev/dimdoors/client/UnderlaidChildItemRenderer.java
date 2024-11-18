package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.door.DimensionalDoorItemRegistrar;

@Environment(EnvType.CLIENT)
public class UnderlaidChildItemRenderer {
	public static final UnderlaidChildItemRenderer INSTANCE = new UnderlaidChildItemRenderer(ModBlocks.DIMENSIONAL_PORTAL.get().asItem());

	private final ItemStack underlay;

	public UnderlaidChildItemRenderer(Item underlay) {
		this.underlay = new ItemStack(underlay);
	}

	public UnderlaidChildItemRenderer(ItemStack underlay) {
		this.underlay = underlay;
	}

//	@Override
	public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
		if (!(stack.getItem() instanceof DimensionalDoorItemRegistrar.ChildItem childItem)) throw new UnsupportedOperationException("Can only use UnderlaidChildItemRenderer for ChildItems");

		matrices.pushPose();
		matrices.translate(0.5D, 0.5D, 0.5D);

		ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

		// TODO: refactor
		matrices.pushPose();
		childItem.transform(matrices);
		matrices.scale(0.9f, 0.9f, 0.9f);
		itemRenderer.renderStatic(underlay, ItemDisplayContext.NONE, light, overlay, matrices, vertexConsumers, null, 0);
		matrices.popPose();

		ItemStack originalItemStack = new ItemStack(
				childItem.getOriginalItem(),
				stack.getCount());
		originalItemStack.applyComponents(stack.getComponents());

		matrices.pushPose();
		childItem.transform(matrices);
		itemRenderer.renderStatic(originalItemStack, ItemDisplayContext.NONE, light, overlay, matrices, vertexConsumers, null, 0);
		matrices.popPose();

		matrices.popPose();
	}
}
