package org.dimdev.dimdoors.mixin.client;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.hud.InGameHud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin{
	private int frame = 0;
	private static final float OVERLAY_OPACITY_ADJUSTEMENT = 1.5F;
	@Shadow
	private int scaledHeight;
	@Shadow
	private int scaledWidth;

	@Shadow
	protected abstract PlayerEntity getCameraPlayer();

	//	@Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShader(Ljava/util/function/Supplier;)V"), method = "renderVignetteOverlay(Lnet/minecraft/entity/Entity;)V")
//	public void renderVignetteOverlay(Entity entity, CallbackInfo info) {
//		if (ModDimensions.isLimboDimension(entity.world)) {
//			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//		}
//	}
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void renderOverlayMixin(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
	}
}
