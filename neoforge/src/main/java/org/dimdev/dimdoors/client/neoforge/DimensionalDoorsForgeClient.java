package org.dimdev.dimdoors.client.neoforge;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelIdentifier;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.NeoForge;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.client.DimensionalDoorsClient;
import org.dimdev.dimdoors.client.IClientSided;
import org.dimdev.dimdoors.client.ModEntityModelLayers;
import org.dimdev.dimdoors.client.ModRecipeBookGroups;
import org.dimdev.dimdoors.client.effect.DungeonDimensionEffect;
import org.dimdev.dimdoors.client.effect.LimboDimensionEffect;
import org.dimdev.dimdoors.client.screen.TesselatingLoomScreen;
import org.dimdev.dimdoors.compat.clothconfig.ClothConfigCompat;
import org.dimdev.dimdoors.compat.create.CreateClientCompat;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.screen.ModScreenHandlerTypes;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.dimdev.dimdoors.item.door.DimensionalDoorItemRegistrar.PREFIX;

@Mod(dist = Dist.CLIENT, value = DimensionalDoors.MOD_ID)
public class DimensionalDoorsForgeClient implements IClientSided {
    public static final EnumProxy<RecipeBookCategories> TESSELLATING_GENERAL = new EnumProxy<>(RecipeBookCategories.class, (Supplier<List<ItemStack>>) () -> List.of(ModItems.WORLD_THREAD.getDefaultInstance()));
    public static final EnumProxy<RecipeBookCategories> TESSELLATING_SEARCH = new EnumProxy<>(RecipeBookCategories.class, (Supplier<List<ItemStack>>) () -> List.of(Items.COMPASS.getDefaultInstance()));
    private final IEventBus bus;

    public Supplier<RecipeBookCategories> getRecipBookCategories(String name, Supplier<ItemStack> itemStack) {
        return switch (name) {
            case "TESSELATING_GENERAL" -> TESSELLATING_GENERAL::getValue;
            case "TESSELATING_SEARCH" -> TESSELLATING_SEARCH::getValue;
            default -> throw new IllegalArgumentException("Unknown tesselating recipe book category: " + name);
        };
    }

    public DimensionalDoorsForgeClient(IEventBus bus, ModContainer container) {
        this.bus = bus;
        DimensionalDoorsClient.init(this);
        if(DimensionalDoors.getSided().isModLoaded("cloth_config")) container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, previous) -> ClothConfigCompat.createScreen(previous));

        bus.addListener(DimensionalDoorsForgeClient::registerRecipeBookCategories);
        bus.addListener(DimensionalDoorsForgeClient::registerParticles);
        bus.addListener(DimensionalDoorsForgeClient::initalizeMenuScreens);
        bus.addListener(DimensionalDoorsForgeClient::initializeClient);
        bus.addListener(DimensionalDoorsForgeClient::registerEntities);
        bus.addListener(DimensionalDoorsForgeClient::registerModelLayers);
        bus.addListener(DimensionalDoorsForgeClient::onRegisterAdditionalModels);
        bus.addListener(DimensionalDoorsForgeClient::onModifyBakingResult);
        bus.addListener(DimensionalDoorsForgeClient::registerDimensionEffect);
        bus.addListener(DimensionalDoorsForgeClient::initalizeFluidModels);
    }

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

    public static void registerParticles(RegisterParticleProvidersEvent event) {
        DimensionalDoorsClient.initParticles(
                (particleType, particleProvider) -> event.registerSpecial((ParticleType) particleType, (ParticleProvider) particleProvider),
                (particleType, spriteSetFunction) -> event.registerSpriteSet(particleType, (ParticleEngine.SpriteParticleRegistration) spriteSetFunction::apply));

    }

    private static void initalizeMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModScreenHandlerTypes.TESSELATING_LOOM, TesselatingLoomScreen::new);
    }

    private static void initializeClient(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new FluidExtension(ModFluids.ETERNAL_FLUID_DETAILS), ModFluids.ETERNAL_FLUID.getFluidType());
        event.registerFluidType(new FluidExtension(ModFluids.LEAK_DETAILS), ModFluids.LEAK.getFluidType());
    }

    static void initalizeFluidModels(RegisterFluidModelsEvent event) {
        event.register(createFluidModel(ModFluids.ETERNAL_FLUID_DETAILS), ModFluids.ETERNAL_FLUID);
    }

    public static FluidModel.Unbaked createFluidModel(ModFluids.FluidDetails details) {
        return new FluidModel.Unbaked(new Material(details.flowing()), new Material(details.still()), new Material(details.overlay()), null);
    }

    public record FluidExtension(Identifier flowing, Identifier still, Identifier overlay) implements IClientFluidTypeExtensions {
        public FluidExtension(ModFluids.FluidDetails attributes) {
            this(attributes.flowing(), attributes.still(), attributes.overlay());
        }
    }

    public static void registerEntities(EntityRenderersEvent.RegisterRenderers event) {
        DimensionalDoorsClient.initEntitiesClient(event::registerEntityRenderer, event::registerBlockEntityRenderer);

        if (DimensionalDoors.getSided().isModLoaded("create")) {
            CreateClientCompat.initBlockEntityRenderers(event::registerBlockEntityRenderer);
        }
    }

    public static void registerModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        ModEntityModelLayers.initClient(event::registerLayerDefinition);
    }

    public static void onRegisterAdditionalModels(ModelEvent.RegisterStandalone event) {
        event.
        event.register(new StandaloneModelKey<>(DimensionalDoorsClient.childItem, ModelIdentifier.STANDALONE_VARIANT));
    }

    /**
     * Replaces:
     *  - all generated DimDoors blockstate models with the baked template model
     *  - all items whose path starts with PREFIX (inventory variant) with the baked template model
     */
    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        ModelBakery bakery = event.getModelBakery();
        var models = event.getModels();

        BakedModel childBaked = bakery.getBakedTopLevelModels().get(
                new ModelIdentifier(DimensionalDoorsClient.childItem, ModelIdentifier.STANDALONE_VARIANT)
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
                .forEach(id -> models.put(ModelIdentifier.inventory(id), childBaked));
    }

    public static void registerDimensionEffect(RegisterDimensionSpecialEffectsEvent event) {
        event.register(DimensionalDoors.id("limbo"), new NfVoidDimensionEffects(LimboDimensionEffect.INSTANCE));
        event.register(DimensionalDoors.id("dungeon"), new NfVoidDimensionEffects(DungeonDimensionEffect.INSTANCE));
    }

    @Override
    public void onClientPlayerJoin(Runnable listener) {
        NeoForge.EVENT_BUS.<ClientPlayerNetworkEvent.LoggingIn>addListener(event -> listener.run());
    }

    @Override
    public void registerCoreShader(Identifier id, VertexFormat vertexFormat, Consumer<ShaderInstance> loadCallback) {


        bus.<RegisterShadersEvent>addListener(event -> {
            try {
                event.registerShader(new ShaderInstance(event.getResourceProvider(), id, vertexFormat), loadCallback);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public <T extends CustomPacketPayload> void sendPacket(T packet) {
        ClientPacketDistributor.sendToServer(packet);
    }
}
