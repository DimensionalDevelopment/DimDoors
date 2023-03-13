package org.dimdev.dimdoors.client.dimensioneffects;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

import org.dimdev.dimdoors.listener.pocket.PocketListenerUtil;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.type.addon.SkyAddon;

public class PocketSpecialEffects extends BaseDimensonSpecialEffects{
	public PocketSpecialEffects() {
		super(false);
	}

	@Override
	public boolean renderSky(@NotNull ClientLevel level, int ticks, float partialTick, @NotNull PoseStack poseStack, Camera camera, @NotNull Matrix4f projectionMatrix, boolean isFoggy, @NotNull Runnable setupFog) {
		List<SkyAddon> skyAddons = PocketListenerUtil.applicableAddonsClient(SkyAddon.class, level, camera.getBlockPosition());
		SkyAddon skyAddon = null;
		if (skyAddons.size() > 0) {
			// There should really only be one of these.
			// If anyone needs to use multiple SkyAddons then go ahead and change this.
			skyAddon = skyAddons.get(0);
		}

		if (skyAddon != null) {
			ResourceKey<DimensionType> key = skyAddon.getWorld();

			return Objects.requireNonNull(Minecraft.getInstance().getConnection())
					.registryAccess()
					.registry(Registries.DIMENSION_TYPE).map(a -> a.get(key))
					.map(DimensionSpecialEffects::forType)
					.filter(skyRenderer -> skyRenderer.renderSky(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog))
					.isPresent();

//			if (skyRenderer != null) {
//				skyRenderer.render(context);
//			} else {
//
//				if (key.equals(Level.END)) {
//					context.gameRenderer().getMinecraft().levelRenderer.renderEndSky(poseStack);
//				} else if (key.equals(ModDimensions.LIMBO)) {
//					renderLimboSky(poseStack);
//				}
//			}
		}

		return false;
	}
}
