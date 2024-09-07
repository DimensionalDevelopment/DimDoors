package org.dimdev.dimdoors.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimdoors.client.UnderlaidChildItemRenderer;
import org.dimdev.dimdoors.item.door.DimensionalDoorItemRegistrar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public abstract class BuiltinModelItemRendererMixin {
	@Inject(method = "renderByItem", at = @At("HEAD"), cancellable = true)
	private void dimdoors_renderByItem(ItemStack stack, ItemTransforms.TransformType mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, CallbackInfo ci) {
		if (stack.getItem() instanceof DimensionalDoorItemRegistrar.ChildItem) {
			UnderlaidChildItemRenderer.INSTANCE.render(stack, mode, matrices, vertexConsumers, light, overlay);
			ci.cancel();
		}
	}
}