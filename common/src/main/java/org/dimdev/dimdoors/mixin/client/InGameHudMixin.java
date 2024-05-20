package org.dimdev.dimdoors.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public abstract class InGameHudMixin{
//	private int frame = 0;
//	private static final float OVERLAY_OPACITY_ADJUSTEMENT = 1.5F;
//	@Shadow
//	private int screenHeight;
//	@Shadow
//	private int screenWidth;
//
//	@Shadow
//	protected abstract Player getCameraPlayer();
//
//	//	@Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShader(Ljava/util/function/Supplier;)V"), method = "renderVignetteOverlay(Lnet/minecraft/entity/Entity;)V")
////	public void renderVignetteOverlay(Entity entity, CallbackInfo info) {
////		if (ModDimensions.isLimboDimension(entity.world)) {
////			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
////		}
////	}
//	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
//	public void renderOverlayMixin(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
//	}
}
