package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.client.effect.DimensionSpecialEffectsExtensions;
import org.dimdev.dimdoors.client.effect.LimboDimensionEffect;
import org.dimdev.dimdoors.listener.pocket.PocketListenerUtil;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.type.addon.SkyAddon;
import org.joml.Matrix4f;

import java.util.List;
import java.util.function.Function;

public class DimensionRenderering {

    public static void initClient() {
        DimensionRenderingRegistry.CloudRenderer noCloudRenderer = context -> {
        };
        DimensionRenderingRegistry.registerCloudRenderer(ModDimensions.LIMBO, noCloudRenderer);
        DimensionRenderingRegistry.registerCloudRenderer(ModDimensions.DUNGEON, noCloudRenderer);
        DimensionRenderingRegistry.registerCloudRenderer(ModDimensions.PERSONAL, noCloudRenderer);
        DimensionRenderingRegistry.registerCloudRenderer(ModDimensions.PUBLIC, noCloudRenderer);

        Function<DimensionSpecialEffectsExtensions, DimensionRenderingRegistry.SkyRenderer> rendererFactory = dimensionSpecialEffectsExtensions -> context -> dimensionSpecialEffectsExtensions.renderSky(context.world(), 0, context.tickDelta(), context.matrixStack(), context.camera(), context.projectionMatrix(), false, () -> {});

        DimensionRenderingRegistry.registerSkyRenderer(ModDimensions.LIMBO, rendererFactory.apply(LimboDimensionEffect.INSTANCE));

        DimensionRenderingRegistry.SkyRenderer pocketRenderer = context -> {
            ClientLevel world = context.world();
            List<SkyAddon> skyAddons = PocketListenerUtil.applicableAddonsClient(SkyAddon.class, world, context.camera().getBlockPosition());
            SkyAddon skyAddon = null;
            if (skyAddons.size() > 0) {
                // There should really only be one of these.
                // If anyone needs to use multiple SkyAddons then go ahead and change this.
                skyAddon = skyAddons.get(0);
            }

            if (skyAddon != null) {
                ResourceKey<Level> key = skyAddon.getWorld();

                DimensionRenderingRegistry.SkyRenderer skyRenderer = DimensionRenderingRegistry.getSkyRenderer(key);

                if (skyRenderer != null) {
                    skyRenderer.render(context);
                } else {

                    if (key.equals(Level.END)) {
//                        context.gameRenderer().getMinecraft().levelRenderer.renderEndSky(matrices);
                    }
                }
            }
        };

        DimensionRenderingRegistry.registerSkyRenderer(ModDimensions.DUNGEON, pocketRenderer);
        DimensionRenderingRegistry.registerSkyRenderer(ModDimensions.PERSONAL, pocketRenderer);
        DimensionRenderingRegistry.registerSkyRenderer(ModDimensions.PUBLIC, pocketRenderer);

        DimensionRenderingRegistry.registerDimensionEffects(DimensionalDoors.id("limbo"), LimboDimensionEffect.INSTANCE);
    }

}
