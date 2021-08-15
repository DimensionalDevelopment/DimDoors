package org.dimdev.dimdoors.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.dimdev.dimdoors.item.DimensionalDoorItemRegistrar;

@Environment(EnvType.CLIENT)
public class UnderlaidChildItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
	private final ItemStack underlay;

	public UnderlaidChildItemRenderer(Item underlay) {
		this.underlay = new ItemStack(underlay);
	}

	public UnderlaidChildItemRenderer(ItemStack underlay) {
		this.underlay = underlay;
	}

	@Override
	public void render(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		if (!(stack.getItem() instanceof DimensionalDoorItemRegistrar.ChildItem)) throw new UnsupportedOperationException("Can only use UnderlaidChildItemRenderer for ChildItems");
		DimensionalDoorItemRegistrar.ChildItem childItem = (DimensionalDoorItemRegistrar.ChildItem) stack.getItem();

		matrices.push();
		matrices.translate(0.5D, 0.5D, 0.5D);

		ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

		matrices.push();
		matrices.scale(1, 1, 0.5f);
		itemRenderer.renderItem(underlay, ModelTransformation.Mode.NONE, light, overlay, matrices, vertexConsumers, 0);
		matrices.pop();

		ItemStack originalItemStack = new ItemStack(
				childItem.getOriginalItem(),
				stack.getCount());
		originalItemStack.setNbt(stack.getNbt());

		matrices.push();
		childItem.transform(matrices);
		itemRenderer.renderItem(originalItemStack, ModelTransformation.Mode.NONE, light, overlay, matrices, vertexConsumers, 0);
		matrices.pop();

		matrices.pop();
	}
}
