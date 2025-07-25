package org.dimdev.dimdoors.client.neoforge;

import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidType;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.door.WaterLoggableDoorBlock;
import org.dimdev.dimdoors.client.DimensionalDoorsClient;
import org.dimdev.dimdoors.client.ModEntityModelLayers;
import org.dimdev.dimdoors.client.effect.DungeonDimensionEffect;
import org.dimdev.dimdoors.client.effect.LimboDimensionEffect;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Consumer;

import static org.dimdev.dimdoors.block.UnravelUtil.copyState;
import static org.dimdev.dimdoors.item.door.DimensionalDoorItemRegistrar.PREFIX;

@EventBusSubscriber(modid = DimensionalDoors.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DimensionalDoorsForgeClient {

    @net.neoforged.bus.api.SubscribeEvent
    public static void setupClient(FMLClientSetupEvent event) {
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
    private static void initializeClient(RegisterClientExtensionsEvent event) throws Exception {
        event.registerFluidType(new FluidExtension(ModFluids.ETERNAL_FLUID_ATTRIBUTES), ModFluids.ETERNAL_FLUID_ATTRIBUTES.getFlowingFluid().getFluidType());
        event.registerFluidType(new FluidExtension(ModFluids.LEAK_ATTRIBUTES), ModFluids.LEAK_ATTRIBUTES.getFlowingFluid().getFluidType());
    }

    public record FluidExtension(ResourceLocation flowing, ResourceLocation still, ResourceLocation overlay) implements IClientFluidTypeExtensions {
        public FluidExtension(ArchitecturyFluidAttributes attributes) {
            this(attributes.getFlowingTexture(), attributes.getSourceTexture(), attributes.getOverlayTexture());
        }

        @Override
        public ResourceLocation getFlowingTexture() {
            return flowing;
        }

        @Override
        public @Nullable ResourceLocation getOverlayTexture() {
            return overlay;
        }

        @Override
        public ResourceLocation getStillTexture() {
            return still;
        }
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
    public static void onRegisterAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(new ModelResourceLocation(DimensionalDoorsClient.childItem, ModelResourceLocation.STANDALONE_VARIANT));
    }

    @SubscribeEvent
    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        var registrar = DimensionalDoors.getDimensionalDoorBlockRegistrar();
        var bakery    = event.getModelBakery();
        var models    = event.getModels();

        var childBaked = bakery.getBakedTopLevelModels().get(new ModelResourceLocation(DimensionalDoorsClient.childItem, ModelResourceLocation.STANDALONE_VARIANT));
        if (childBaked == null) {
            DimensionalDoors.LOGGER.error("DimDoors: childItem model missing at bake time!");
            return; // prevents a hard crash
        }

        DimensionalDoors.getDimensionalDoorBlockRegistrar().getGennedIds().stream().filter(BuiltInRegistries.BLOCK::containsKey).map(BuiltInRegistries.BLOCK::get)
                .map(Block::getStateDefinition).flatMap(a -> a.getPossibleStates().stream()).map(BlockModelShaper::stateToModelLocation).forEach(location -> models.put(location, childBaked));


        BuiltInRegistries.ITEM.keySet().stream()
                .filter(loc -> loc.getPath().startsWith(PREFIX))
                .forEach(loc -> models.put(new ModelResourceLocation(loc, "inventory"), childBaked));
    }

    @SubscribeEvent
    public static void registerDimensionEffect(RegisterDimensionSpecialEffectsEvent event) {
        event.register(DimensionalDoors.id("limbo"), LimboDimensionEffect.INSTANCE);
        event.register(DimensionalDoors.id("dungeon"), DungeonDimensionEffect.INSTANCE);
    }
}
