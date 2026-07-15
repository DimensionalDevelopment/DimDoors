package org.dimdev.dimdoors.client.neoforge;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidType;
import org.apache.commons.lang3.function.TriConsumer;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.function.TriFunction;
import org.dimdev.dimdoors.client.*;
import org.dimdev.dimdoors.fluid.ModFluids;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NeoForgeClientSided<V extends NeoForgeClientSided<V, T>, T extends ModClient<? super V>> implements IClientSided<V> {
    private List<ModelLoadingRegistration> modelLoadingOverrides = new ArrayList<>();
    private final List<Runnable> loginRunnables = new ArrayList<>();

    public NeoForgeClientSided(IEventBus bus, ModContainer container, T client) {

        client.initModels((id, consumer) -> modelLoadingOverrides.add(new ModelLoadingRegistration(id, consumer)));

        bus.<RegisterParticleProvidersEvent>addListener(event -> client.initParticles(new ModClient.RegularParticleRegister() {
            @Override
            public <P extends ParticleOptions> void register(ParticleType<P> particleType, Function<SpriteSet, ParticleProvider<P>> provider) {
                event.registerSpriteSet(particleType, provider::apply);
            }
        }, event::registerSpecial));

        bus.<RegisterMenuScreensEvent>addListener(event -> client.initScreens(new ModClient.ScreenRegister() {
            @Override
            public <U extends AbstractContainerMenu, M extends Screen & MenuAccess<U>> void register(MenuType<U> menuType, TriFunction<U, Inventory, Component, M> factory) {
                event.register(menuType, factory::apply);
            }
        }));
        bus.<RegisterClientExtensionsEvent>addListener(event -> client.initFluids((flowing, fluid, details) -> event.registerFluidType(new NeoforgeMultiplatformData.FluidExtension(details), fluid.getFluidType())));
        bus.<EntityRenderersEvent.RegisterRenderers>addListener(event -> {
            client.initEntityRenderers(event::registerEntityRenderer);
            client.initBlockEntityRenderers(event::registerBlockEntityRenderer);
        });
        bus.<EntityRenderersEvent.RegisterLayerDefinitions>addListener(event -> client. initModelLayers(event::registerLayerDefinition));
        bus.<ModelEvent.RegisterAdditional>addListener(event -> {
            modelLoadingOverrides.stream()
                    .map(ModelLoadingRegistration::replacementModel)
                    .forEach(event::register);
        });
        bus.<ModelEvent.ModifyBakingResult>addListener(event -> {
            var bakedModels = event.getModelBakery().getBakedTopLevelModels();
            var models = event.getModels();

            for (ModelLoadingRegistration registration : modelLoadingOverrides) {
                ModelLoadingOverride override = registration.resolve();
                BakedModel replacementModel = bakedModels.get(override.replacementModel());
                if (replacementModel == null) {
                    DimensionalDoors.LOGGER.error("Missing replacement model {} at bake time", override.replacementModel());
                    continue;
                }

                override.resolvedTargets()
                        .forEach(location -> models.put(location, replacementModel));
            }
        });
        bus.<RegisterDimensionSpecialEffectsEvent>addListener(event -> client.initDimensionEffects(event::register));
        bus.<FMLClientSetupEvent>addListener(event -> event.enqueueWork(client::delayedInit));
        bus.<RegisterShadersEvent>addListener(event -> {
            var provider = event.getResourceProvider();

            client.initShaders((id, vertexFormat, consumer) -> {
                try {
                    event.registerShader(new ShaderInstance(provider, id, vertexFormat), consumer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });
        });

        NeoForge.EVENT_BUS.<ClientPlayerNetworkEvent.LoggingIn>addListener(event -> loginRunnables.forEach(Runnable::run));
    }

    private record ModelLoadingRegistration(ModelResourceLocation replacementModel, Consumer<ModelLoadingRegistry> registration) {
        private ModelLoadingOverride resolve() {
            return ModelLoadingOverride.create(replacementModel, registration);
        }
    }

    @Override
    public void register(RenderType type, Block... blocks) {
        for (Block block : blocks) {
            ItemBlockRenderTypes.setRenderLayer(block, type);
        }
    }

    @Override
    public void onClientPlayerJoin(Runnable listener) {
        loginRunnables.add(listener);
    }
}
