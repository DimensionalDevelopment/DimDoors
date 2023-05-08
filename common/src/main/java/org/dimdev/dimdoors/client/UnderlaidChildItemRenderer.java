package org.dimdev.dimdoors.client;

//@Environment(EnvType.CLIENT)
//public class UnderlaidChildItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {//TODO: Move to fabric if needed still
//	private final ItemStack underlay;
//
//	public UnderlaidChildItemRenderer(Item underlay) {
//		this.underlay = new ItemStack(underlay);
//	}
//
//	public UnderlaidChildItemRenderer(ItemStack underlay) {
//		this.underlay = underlay;
//	}
//
//	@Override
//	public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
//		if (!(stack.getItem() instanceof DimensionalDoorItemRegistrar.ChildItem childItem)) throw new UnsupportedOperationException("Can only use UnderlaidChildItemRenderer for ChildItems");
//
//		matrices.push();
//		matrices.translate(0.5D, 0.5D, 0.5D);
//
//		ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
//
//		// TODO: refactor
//		matrices.push();
//		childItem.transform(matrices);
//		matrices.scale(1, 1, 0.5f);
//		itemRenderer.renderItem(underlay, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, null, 0);
//		matrices.pop();
//
//		ItemStack originalItemStack = new ItemStack(
//				childItem.getOriginalItem(),
//				stack.getCount());
//		originalItemStack.setNbt(stack.getNbt());
//
//		matrices.push();
////		childItem.transform(matrices);
//		itemRenderer.renderItem(originalItemStack, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, null, 0);
//		matrices.pop();
//
//		matrices.pop();
//	}
//}
