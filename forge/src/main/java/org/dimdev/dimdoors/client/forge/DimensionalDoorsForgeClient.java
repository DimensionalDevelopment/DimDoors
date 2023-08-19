package org.dimdev.dimdoors.client.forge;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.client.DimensionalDoorsClient;
import org.dimdev.dimdoors.client.ModEntityModelLayers;
import org.dimdev.dimdoors.client.ModRecipeBookGroups;
import org.dimdev.dimdoors.client.ModRecipeBookTypes;
import org.dimdev.dimdoors.client.config.ModMenu;
import org.dimdev.dimdoors.client.effect.LimboDimensionEffect;
import org.dimdev.dimdoors.recipe.ModRecipeTypes;

import java.util.List;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = DimensionalDoors.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DimensionalDoorsForgeClient {

    @SubscribeEvent
    public static void setupClient(FMLClientSetupEvent event) {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> ModMenu.getConfigScreen(screen)));
        DimensionalDoorsClient.init();
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        DimensionalDoorsClient.initParticles(
                (particleType, particleProvider) -> event.registerSpecial((ParticleType) particleType, (ParticleProvider) particleProvider),
                (particleType, spriteSetFunction) -> event.registerSpriteSet(particleType, (ParticleEngine.SpriteParticleRegistration) spriteSetFunction::apply));

    }

    @SubscribeEvent
    public static void registerEntities(EntityRenderersEvent.RegisterRenderers event) {
        DimensionalDoorsClient.initEntitiesClient(event::registerEntityRenderer);
    }

    @SubscribeEvent
    public static void registerModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        ModEntityModelLayers.initClient(event::registerLayerDefinition);
    }

    @SubscribeEvent
    public static void registerDimensionEffect(RegisterDimensionSpecialEffectsEvent event) {
        event.register(DimensionalDoors.id("limbo"), LimboDimensionEffect.INSTANCE);
    }
}
