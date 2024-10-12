package org.dimdev.dimdoors.client;

import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.client.effect.DimensionSpecialEffectsExtensions;
import org.dimdev.dimdoors.client.effect.DungeonDimensionEffect;
import org.dimdev.dimdoors.client.effect.LimboDimensionEffect;
import org.dimdev.dimdoors.world.ModDimensions;

import java.util.function.Function;

public class DimensionRenderering {

    public static void initClient() {
        DimensionRenderingRegistry.CloudRenderer noCloudRenderer = context -> {
        };
        DimensionRenderingRegistry.registerCloudRenderer(ModDimensions.LIMBO, noCloudRenderer);
        DimensionRenderingRegistry.registerCloudRenderer(ModDimensions.DUNGEON, noCloudRenderer);
        DimensionRenderingRegistry.registerCloudRenderer(ModDimensions.PERSONAL, noCloudRenderer);
        DimensionRenderingRegistry.registerCloudRenderer(ModDimensions.PUBLIC, noCloudRenderer);

        Function<DimensionSpecialEffectsExtensions, DimensionRenderingRegistry.SkyRenderer> rendererFactory = dimensionSpecialEffectsExtensions -> new DimensionRenderingRegistry.SkyRenderer() {
            @Override
            public void render(WorldRenderContext context) {
                dimensionSpecialEffectsExtensions.renderSky(context.world(), 0, context.tickCounter().getGameTimeDeltaTicks(), context.matrixStack(), context.camera(), context.projectionMatrix(), false, () -> {});
            }
        };

        DimensionRenderingRegistry.registerSkyRenderer(ModDimensions.LIMBO, rendererFactory.apply(LimboDimensionEffect.INSTANCE));

        var pocketRenderer = rendererFactory.apply(DungeonDimensionEffect.INSTANCE);

        DimensionRenderingRegistry.registerSkyRenderer(ModDimensions.DUNGEON, pocketRenderer);
        DimensionRenderingRegistry.registerSkyRenderer(ModDimensions.PERSONAL, pocketRenderer);
        DimensionRenderingRegistry.registerSkyRenderer(ModDimensions.PUBLIC, pocketRenderer);

        DimensionRenderingRegistry.registerDimensionEffects(DimensionalDoors.id("limbo"), LimboDimensionEffect.INSTANCE);
        DimensionRenderingRegistry.registerDimensionEffects(DimensionalDoors.id("dungeon"), DungeonDimensionEffect.INSTANCE);
    }

}
