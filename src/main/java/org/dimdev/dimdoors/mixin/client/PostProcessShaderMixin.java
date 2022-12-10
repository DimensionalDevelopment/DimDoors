package org.dimdev.dimdoors.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.JsonEffectShaderProgram;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PostEffectPass.class)
public class PostProcessShaderMixin {

	@Shadow @Final private JsonEffectShaderProgram program;

	@Inject(method = "render(F)V", at = @At("HEAD"), cancellable = true)
	public void render(float time, CallbackInfo cir) {
		program.getUniformByNameOrDummy("GameTime").set(RenderSystem.getShaderGameTime());
	}

	private PlayerEntity getCameraPlayer() {
		return !(MinecraftClient.getInstance().getCameraEntity() instanceof PlayerEntity) ? null : (PlayerEntity)MinecraftClient.getInstance().getCameraEntity();
	}
}
