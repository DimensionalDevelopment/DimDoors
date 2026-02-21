package org.dimdev.dimdoors.client;

import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.client.renderer.LightTexture;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.client.effect.*;
import org.dimdev.dimdoors.world.ModDimensions;

import java.util.function.Function;

public class DimensionRenderering {

    public static void initClient() {
        Function<DimensionSpecialEffectsExtensions, DimensionRenderingRegistry.SkyRenderer> rendererFactory = dimensionSpecialEffectsExtensions -> context -> dimensionSpecialEffectsExtensions.renderSky(context.world(), 0, context.tickCounter().getGameTimeDeltaPartialTick(true), context.positionMatrix(), context.camera(), context.projectionMatrix(), false, () -> {});

        DimensionRenderingRegistry.registerSkyRenderer(ModDimensions.LIMBO, rendererFactory.apply(LimboDimensionEffect.INSTANCE));
        DimensionRenderingRegistry.registerCloudRenderer(ModDimensions.LIMBO, ctx -> {});
        DimensionRenderingRegistry.registerWeatherRenderer(ModDimensions.LIMBO, ctx -> {});


        DimensionRenderingRegistry.SkyRenderer pocketSkyRenderer = rendererFactory.apply(DungeonDimensionEffect.INSTANCE);
        DimensionRenderingRegistry.WeatherRenderer pocketWeatherRenderer = ctx -> {
            DungeonDimensionEffect.INSTANCE.renderWeather(ctx.world(), ((LevelRendererExtension) ctx.worldRenderer()).getTicks(), ctx.tickCounter().getRealtimeDeltaTicks(), ctx.gameRenderer().lightTexture(), ctx.camera().getPosition().x(), ctx.camera().getPosition().y(), ctx.camera().getPosition().z());
        };

        DimensionRenderingRegistry.CloudRenderer pocketCloudRenderer = ctx -> {
            DungeonDimensionEffect.INSTANCE.renderClouds(ctx.world(), ((LevelRendererExtension) ctx.worldRenderer()).getTicks(), ctx.tickCounter().getRealtimeDeltaTicks(), ctx.matrixStack(), ctx.camera().getPosition().x(), ctx.camera().getPosition().y(), ctx.camera().getPosition().z(), ctx.positionMatrix(), ctx.projectionMatrix());
        };

        DimensionRenderingRegistry.registerSkyRenderer(ModDimensions.DUNGEON, pocketSkyRenderer);
        DimensionRenderingRegistry.registerSkyRenderer(ModDimensions.PERSONAL, pocketSkyRenderer);
        DimensionRenderingRegistry.registerSkyRenderer(ModDimensions.PUBLIC, pocketSkyRenderer);

        DimensionRenderingRegistry.registerCloudRenderer(ModDimensions.DUNGEON, pocketCloudRenderer);
        DimensionRenderingRegistry.registerCloudRenderer(ModDimensions.PERSONAL, pocketCloudRenderer);
        DimensionRenderingRegistry.registerCloudRenderer(ModDimensions.PUBLIC, pocketCloudRenderer);

        DimensionRenderingRegistry.registerWeatherRenderer(ModDimensions.DUNGEON, pocketWeatherRenderer);
        DimensionRenderingRegistry.registerWeatherRenderer(ModDimensions.PERSONAL, pocketWeatherRenderer);
        DimensionRenderingRegistry.registerWeatherRenderer(ModDimensions.PUBLIC, pocketWeatherRenderer);

        var effects = new VoidDimensionSpecialEffects();

        DimensionRenderingRegistry.registerDimensionEffects(DimensionalDoors.id("limbo"), effects);
        DimensionRenderingRegistry.registerDimensionEffects(DimensionalDoors.id("dungeon"), effects);
    }

}
