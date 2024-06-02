package org.dimdev.dimdoors.forge.client.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;

//Taken from porting lib
public interface DimensionSpecialEffectsExtensions {
	/**
	 * Renders the clouds of this dimension.
	 *
	 * @return true to prevent vanilla cloud rendering
	 */
	default boolean renderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix) {
		return false;
	}

	/**
	 * Renders the sky of this dimension.
	 *
	 * @return true to prevent vanilla sky rendering
	 */
	default boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
		return false;
	}

	/**
	 * Renders the snow and rain effects of this dimension.
	 *
	 * @return true to prevent vanilla snow and rain rendering
	 */
	default boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
		return false;
	}
}
