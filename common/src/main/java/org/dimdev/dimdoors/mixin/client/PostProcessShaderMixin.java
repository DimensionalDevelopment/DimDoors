package org.dimdev.dimdoors.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PostPass.class)
public class PostProcessShaderMixin {

	@Shadow @Final private EffectInstance effect;

	@Inject(method = "process(F)V", at = @At("HEAD"), cancellable = true)
	public void render(float time, CallbackInfo cir) {
		effect.safeGetUniform("GameTime").set(RenderSystem.getShaderGameTime());
	}

	private Player getCameraPlayer() {
		return !(Minecraft.getInstance().getCameraEntity() instanceof Player player) ? null : player;
	}
}
