package org.dimdev.dimdoors.client.forge;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.door.WaterLoggableDoorBlock;
import org.dimdev.dimdoors.client.DimensionalDoorsClient;
import org.dimdev.dimdoors.client.ModEntityModelLayers;
import org.dimdev.dimdoors.client.config.ModMenu;
import org.dimdev.dimdoors.client.effect.DungeonDimensionEffect;
import org.dimdev.dimdoors.client.effect.LimboDimensionEffect;

import static org.dimdev.dimdoors.block.UnravelUtil.copyState;
import static org.dimdev.dimdoors.item.door.DimensionalDoorItemRegistrar.PREFIX;

@EventBusSubscriber(modid = DimensionalDoors.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DimensionalDoorsForgeClient {

    @SubscribeEvent
    public static void setupClient(FMLClientSetupEvent event) {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (minecraft, screen) -> ModMenu.getConfigScreen(screen));
//        FMLJavaModLoadingContext.get().getModEventBus().addListener((Consumer<RegisterRecipeBookCategoriesEvent>) event1 -> org.dimdev.dimdoors.api.util.RegisterRecipeBookCategoriesEvent.EVENT.invoker().accept(new org.dimdev.dimdoors.api.util.RegisterRecipeBookCategoriesEvent(event1::registerAggregateCategory, event1::registerBookCategories, event1::registerRecipeCategoryFinder)));
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
    public static void modifyBake(ModelEvent.RegisterAdditional event) {
        event.register(DimensionalDoorsClient.childItem);
    }

    @SubscribeEvent
    public static void modifyBake(ModelEvent.ModifyBakingResult event) {
        var blockRegistrar = DimensionalDoors.getDimensionalDoorBlockRegistrar();

        DimensionalDoors.getDimensionalDoorBlockRegistrar().getGennedIds().stream().filter(DimensionalDoors.getDimensionalDoorBlockRegistrar()::isMapped).forEach(identifier -> {
            var block = BuiltInRegistries.BLOCK.get(identifier);

            if(block == null) return;

            var mapped = blockRegistrar.get(identifier);

            var original = BuiltInRegistries.BLOCK.get(mapped);

            if(original == null) return;
            original.getStateDefinition().getPossibleStates().forEach(blockState -> {
                var state = copyState(block, blockState);
                var model = event.getModelBakery().getBakedTopLevelModels().get(BlockModelShaper.stateToModelLocation(blockState));

                event.getModels().put(BlockModelShaper.stateToModelLocation(state.setValue(WaterLoggableDoorBlock.WATERLOGGED, true)), model);
                event.getModels().put(BlockModelShaper.stateToModelLocation(state), model);
            });
        });

        var childItem = event.getModelBakery().getBakedTopLevelModels().get(DimensionalDoorsClient.childItem);

        BuiltInRegistries.ITEM.keySet().stream().filter(a -> a.getPath().startsWith(PREFIX)).forEach(location -> event.getModels().put(new ModelResourceLocation(location, "inventory"), childItem));
    }

    @SubscribeEvent
    public static void registerDimensionEffect(RegisterDimensionSpecialEffectsEvent event) {
        event.register(DimensionalDoors.id("limbo"), LimboDimensionEffect.INSTANCE);
        event.register(DimensionalDoors.id("dungeon"), DungeonDimensionEffect.INSTANCE);
    }
}
