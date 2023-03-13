package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.Dist;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimdoors.item.DimensionalDoorItemRegistrar;

@Environment(Dist.CLIENT)
public class UnderlaidChildItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
	private final ItemStack underlay;

	public UnderlaidChildItemRenderer(Item underlay) {
		this.underlay = new ItemStack(underlay);
	}

	public UnderlaidChildItemRenderer(ItemStack underlay) {
		this.underlay = underlay;
	}

	@Override
	public void render(ItemStack stack, ItemTransforms.TransformType mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
		if (!(stack.getItem() instanceof DimensionalDoorItemRegistrar.ChildItem)) throw new UnsupportedOperationException("Can only use UnderlaidChildItemRenderer for ChildItems");
		DimensionalDoorItemRegistrar.ChildItem childItem = (DimensionalDoorItemRegistrar.ChildItem) stack.getItem();

		matrices.pushPose();
		matrices.translate(0.5D, 0.5D, 0.5D);

		ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

		matrices.pushPose();
		matrices.scale(1, 1, 0.5f);
		itemRenderer.renderStatic(underlay, ItemTransforms.TransformType.NONE, light, overlay, matrices, vertexConsumers, 0);
		matrices.popPose();

		ItemStack originalItemStack = new ItemStack(
				childItem.getOriginalItem(),
				stack.getCount());
		originalItemStack.setTag(stack.getTag());

		matrices.pushPose();
		childItem.transform(matrices);
		itemRenderer.renderStatic(originalItemStack, ItemTransforms.TransformType.NONE, light, overlay, matrices, vertexConsumers, 0);
		matrices.popPose();

		matrices.popPose();
	}
}
