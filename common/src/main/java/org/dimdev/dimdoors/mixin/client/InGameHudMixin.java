package org.dimdev.dimdoors.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public abstract class InGameHudMixin{
	private int frame = 0;
	private static final float OVERLAY_OPACITY_ADJUSTEMENT = 1.5F;
	@Shadow
	private int screenHeight;
	@Shadow
	private int screenWidth;

	@Shadow
	protected abstract Player getCameraPlayer();

	//	@Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShader(Ljava/util/function/Supplier;)V"), method = "renderVignetteOverlay(Lnet/minecraft/entity/Entity;)V")
//	public void renderVignetteOverlay(Entity entity, CallbackInfo info) {
//		if (ModDimensions.isLimboDimension(entity.world)) {
//			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//		}
//	}
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void renderOverlayMixin(PoseStack matrices, float tickDelta, CallbackInfo ci) {
	}
}
