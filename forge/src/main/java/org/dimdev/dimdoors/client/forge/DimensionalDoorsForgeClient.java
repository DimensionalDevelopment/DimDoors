package org.dimdev.dimdoors.client.forge;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.door.WaterLoggableDoorBlock;
import org.dimdev.dimdoors.client.DimensionalDoorsClient;
import org.dimdev.dimdoors.client.ModEntityModelLayers;
import org.dimdev.dimdoors.client.config.ModMenu;
import org.dimdev.dimdoors.client.effect.DungeonDimensionEffect;
import org.dimdev.dimdoors.client.effect.LimboDimensionEffect;

import static org.dimdev.dimdoors.block.UnravelUtil.copyState;
import static org.dimdev.dimdoors.item.door.DimensionalDoorItemRegistrar.PREFIX;

@Mod.EventBusSubscriber(modid = DimensionalDoors.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DimensionalDoorsForgeClient {

    @SubscribeEvent
    public static void setupClient(FMLClientSetupEvent event) {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> ModMenu.getConfigScreen(screen)));
//        FMLJavaModLoadingContext.get().getModEventBus().addListener((Consumer<RegisterRecipeBookCategoriesEvent>) event1 -> org.dimdev.dimdoors.api.util.RegisterRecipeBookCategoriesEvent.EVENT.invoker().accept(new org.dimdev.dimdoors.api.util.RegisterRecipeBookCategoriesEvent(event1::registerAggregateCategory, event1::registerBookCategories, event1::registerRecipeCategoryFinder)));
        DimensionalDoorsClient.init();
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        DimensionalDoorsClient.initParticles(
                (particleType, particleProvider) -> event.register((ParticleType) particleType, (ParticleProvider) particleProvider),
                (particleType, spriteSetFunction) -> event.register(particleType, (ParticleEngine.SpriteParticleRegistration) spriteSetFunction::apply));

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
    public static void modifyBake(ModelEvent.RegisterAdditional event) {
        event.register(DimensionalDoorsClient.childItem);
    }

    @SubscribeEvent
    public static void modifyBake(ModelEvent.BakingCompleted event) {
        var blockRegistrar = DimensionalDoors.getDimensionalDoorBlockRegistrar();

        DimensionalDoors.getDimensionalDoorBlockRegistrar().getGennedIds().stream().filter(DimensionalDoors.getDimensionalDoorBlockRegistrar()::isMapped).forEach(identifier -> {
            var block = ForgeRegistries.BLOCKS.getValue(identifier);

            if(block == null) return;

            var mapped = blockRegistrar.get(identifier);

            var original = ForgeRegistries.BLOCKS.getValue(mapped);

            if(original == null) return;
            original.getStateDefinition().getPossibleStates().forEach(blockState -> {
                var state = copyState(block, blockState);
                var model = event.getModelBakery().getBakedTopLevelModels().get(BlockModelShaper.stateToModelLocation(blockState));

                event.getModels().put(BlockModelShaper.stateToModelLocation(state.setValue(WaterLoggableDoorBlock.WATERLOGGED, true)), model);
                event.getModels().put(BlockModelShaper.stateToModelLocation(state), model);
            });
        });

        var childItem = event.getModelBakery().getBakedTopLevelModels().get(DimensionalDoorsClient.childItem);

        ForgeRegistries.ITEMS.getKeys().stream().filter(a -> a.getPath().startsWith(PREFIX)).forEach(location -> event.getModels().put(new ModelResourceLocation(location, "inventory"), childItem));
    }

    @SubscribeEvent
    public static void registerDimensionEffect(RegisterDimensionSpecialEffectsEvent event) {
        event.register(DimensionalDoors.id("limbo"), LimboDimensionEffect.INSTANCE);
        event.register(DimensionalDoors.id("dungeon"), DungeonDimensionEffect.INSTANCE);
    }
}
