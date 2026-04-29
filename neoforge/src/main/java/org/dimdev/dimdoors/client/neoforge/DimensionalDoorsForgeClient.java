package org.dimdev.dimdoors.client.neoforge;

import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
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
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidType;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.door.WaterLoggableDoorBlock;
import org.dimdev.dimdoors.client.DimensionalDoorsClient;
import org.dimdev.dimdoors.client.ModRecipeBookGroups;
import org.dimdev.dimdoors.client.ModEntityModelLayers;
import org.dimdev.dimdoors.client.effect.DungeonDimensionEffect;
import org.dimdev.dimdoors.client.effect.LimboDimensionEffect;
import org.dimdev.dimdoors.client.screen.TesselatingLoomScreen;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.screen.ModScreenHandlerTypes;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static org.dimdev.dimdoors.block.UnravelUtil.copyState;
import static org.dimdev.dimdoors.item.door.DimensionalDoorItemRegistrar.PREFIX;

@EventBusSubscriber(modid = DimensionalDoors.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DimensionalDoorsForgeClient {

    @net.neoforged.bus.api.SubscribeEvent
    public static void setupClient(FMLClientSetupEvent event) {
        DimensionalDoorsClient.init();
    }

    @SubscribeEvent
    public static void registerRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
        ModRecipeBookGroups.init();
        org.dimdev.dimdoors.api.util.RegisterRecipeBookCategoriesEvent.EVENT.invoker().accept(
                new org.dimdev.dimdoors.api.util.RegisterRecipeBookCategoriesEvent(
                        event::registerAggregateCategory,
                        event::registerBookCategories,
                        event::registerRecipeCategoryFinder
                )
        );
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        DimensionalDoorsClient.initParticles(
                (particleType, particleProvider) -> event.registerSpecial((ParticleType) particleType, (ParticleProvider) particleProvider),
                (particleType, spriteSetFunction) -> event.registerSpriteSet(particleType, (ParticleEngine.SpriteParticleRegistration) spriteSetFunction::apply));

    }

    @SubscribeEvent
    private static void initalizeMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModScreenHandlerTypes.TESSELATING_LOOM, TesselatingLoomScreen::new);
    }

    @SubscribeEvent
    private static void initializeClient(RegisterClientExtensionsEvent event) throws Exception {
        event.registerFluidType(new FluidExtension(ModFluids.ETERNAL_FLUID_DETAILS), ModFluids.ETERNAL_FLUID.getFluidType());
        event.registerFluidType(new FluidExtension(ModFluids.LEAK_DETAILS), ModFluids.LEAK.getFluidType());
    }

    public record FluidExtension(ResourceLocation flowing, ResourceLocation still, ResourceLocation overlay) implements IClientFluidTypeExtensions {
        public FluidExtension(ModFluids.FluidDetails attributes) {
            this(attributes.flowing(), attributes.still(), attributes.overlay());
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

    /**
     * Replaces:
     *  - all generated DimDoors blockstate models with the baked template model
     *  - all items whose path starts with PREFIX (inventory variant) with the baked template model
     */
    @SubscribeEvent
    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        ModelBakery bakery = event.getModelBakery();
        var models = event.getModels();

        BakedModel childBaked = bakery.getBakedTopLevelModels().get(
                new ModelResourceLocation(DimensionalDoorsClient.childItem, ModelResourceLocation.STANDALONE_VARIANT)
        );
        if (childBaked == null) {
            DimensionalDoors.LOGGER.error("DimDoors: childItem model missing at bake time!");
            return;
        }

        // Blockstates: override every possible state model location for generated DimDoors blocks.
        DimensionalDoors.getDimensionalDoorBlockRegistrar()
                .getGennedIds().stream()
                .filter(BuiltInRegistries.BLOCK::containsKey)
                .map(BuiltInRegistries.BLOCK::get)
                .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
                .map(BlockModelShaper::stateToModelLocation)
                .forEach(location -> models.put(location, childBaked));

        // Items: override inventory model for any item whose id path starts with PREFIX.
        BuiltInRegistries.ITEM.keySet().stream()
                .filter(id -> id.getPath().startsWith(PREFIX))
                .forEach(id -> models.put(ModelResourceLocation.inventory(id), childBaked));
    }

    @SubscribeEvent
    public static void registerDimensionEffect(RegisterDimensionSpecialEffectsEvent event) {


        event.register(DimensionalDoors.id("limbo"), new NfVoidDimensionEffects(LimboDimensionEffect.INSTANCE));
        event.register(DimensionalDoors.id("dungeon"), new NfVoidDimensionEffects(DungeonDimensionEffect.INSTANCE));
    }
}
