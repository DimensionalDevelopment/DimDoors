package org.dimdev.dimdoors.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.JsonEffectGlShader;
import net.minecraft.client.gl.PostProcessShader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import com.mojang.blaze3d.systems.RenderSystem;

import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.entity.MaskEntity;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.entity.MonolithEntity;
import org.dimdev.dimdoors.world.ModDimensions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PostProcessShader.class)
public class PostProcessShaderMixin {

	@Shadow @Final private JsonEffectGlShader program;

	private static float overlayOpacity = 0.0F;

	@Inject(method = "render(F)V", at = @At("HEAD"), cancellable = true)
	public void render(float time, CallbackInfo cir) {
		program.getUniformByNameOrDummy("GameTime").set(RenderSystem.getShaderGameTime());
		if(program.getName().equals("static")) {
			System.out.println("Static");
			if (MinecraftClient.getInstance().getCameraEntity() instanceof LivingEntity livingEntity) {
				System.out.println("Living Entity");
				if(!(livingEntity instanceof PlayerEntity || livingEntity instanceof MonolithEntity || livingEntity instanceof MaskEntity)) {
					while (ModDimensions.isLimboDimension(livingEntity.world)) {
						System.out.println("Limbo");
						for (int i = 0; i < 20; i++) {
							overlayOpacity += 0.1F;
							program.getUniformByNameOrDummy("StaticIntensity" + i).set(overlayOpacity);
							if(overlayOpacity == 1.0F) {
								livingEntity.getEntityWorld().spawnEntity(new MonolithEntity(ModEntityTypes.MONOLITH, livingEntity.getEntityWorld()));
								livingEntity.remove(Entity.RemovalReason.KILLED);
							}
						}
					}
				}
			} else {
				program.getUniformByNameOrDummy("StaticIntensity").set(0.0f);
			}
		}
	}

	private PlayerEntity getCameraPlayer() {
		return !(MinecraftClient.getInstance().getCameraEntity() instanceof PlayerEntity) ? null : (PlayerEntity)MinecraftClient.getInstance().getCameraEntity();
	}
}
