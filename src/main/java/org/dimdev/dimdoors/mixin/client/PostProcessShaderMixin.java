package org.dimdev.dimdoors.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.JsonEffectGlShader;
import net.minecraft.client.gl.PostProcessShader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import com.mojang.blaze3d.systems.RenderSystem;

import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.world.level.component.PlayerModifiersComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PostProcessShader.class)
public class PostProcessShaderMixin {
	private ModConfig.Player config = DimensionalDoorsInitializer.getConfig().getPlayerConfig();

	@Shadow @Final private JsonEffectGlShader program;

	@Inject(method = "render(F)V", at = @At("HEAD"), cancellable = true)
	public void render(float time, CallbackInfo cir) {
		program.getUniformByNameOrDummy("GameTime").set(RenderSystem.getShaderGameTime());

		if (PlayerModifiersComponent.getFray(getCameraPlayer()) > config.fray.grayScreenFray) {
			float overlayOpacity = (config.fray.grayScreenFray - PlayerModifiersComponent.getFray(getCameraPlayer())) / (config.fray.grayScreenFray - (float) config.fray.maxFray);
			program.getUniformByNameOrDummy("FrayIntensity").set(overlayOpacity);
		} else {
			program.getUniformByNameOrDummy("FrayIntensity").set(0.0f);
		}
	}

	private PlayerEntity getCameraPlayer() {
		return !(MinecraftClient.getInstance().getCameraEntity() instanceof PlayerEntity) ? null : (PlayerEntity)MinecraftClient.getInstance().getCameraEntity();
	}
}
