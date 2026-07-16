package org.dimdev.limlib.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import org.dimdev.limlib.api.client.IClientSided;
import org.dimdev.limlib.api.util.function.TriFunction;
import org.dimdev.limlib.impl.client.DimensionalDoorsModelLoadingPlugin;
import org.dimdev.limlib.api.client.ModClient;
import org.dimdev.limlib.api.client.ModClient.RegularParticleRegister;
import org.dimdev.limlib.impl.client.ModelLoadingOverride;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


public class FabricClientSided<V extends FabricClientSided<V, T>, T extends ModClient<? super V>> implements ClientModInitializer, IClientSided<V> {
    private final List<ModelLoadingOverride> modelLoadingOverrides = new ArrayList<>();
    private final T client;

    public FabricClientSided(T client) {
        this.client = client;
    }

    @Override
    public void onInitializeClient() {
        client.init(self());
        client.initParticles(new RegularParticleRegister() {
            @Override
            public <P extends ParticleOptions> void register(ParticleType<P> particleType, Function<SpriteSet, ParticleProvider<P>> provider) {
                ParticleFactoryRegistry.getInstance().register(particleType, provider::apply);
            }
        }, ParticleFactoryRegistry.getInstance()::register);
        client.initFluids((flowing, still, details) -> FluidRenderHandlerRegistry.INSTANCE.register(flowing, still, new SimpleFluidRenderHandler(details.still(), details.flowing(), details.overlay())));
        client.initScreens(new ModClient.ScreenRegister() {
            @Override
            public <U extends AbstractContainerMenu, M extends Screen & MenuAccess<U>> void register(MenuType<U> menuType, TriFunction<U, Inventory, Component, M> factory) {
                MenuScreens.register(menuType, factory::apply);
            }
        });

        client.initBlockEntityRenderers(BlockEntityRenderers::register);
        client.initEntityRenderers(EntityRendererRegistry::register);
        client.initModelLayers((id, layerDefinitionSupplier) -> EntityModelLayerRegistry.registerModelLayer(id, layerDefinitionSupplier::get));
        client.initModels((replacementModel, registration) -> modelLoadingOverrides.add(ModelLoadingOverride.create(replacementModel, registration)));
        client.initDimensionEffects(DimensionRenderingRegistry::registerDimensionEffects);
        client.initShaders((id, vertexFormat, loadCallback) -> CoreShaderRegistrationCallback.EVENT.register(context -> context.register(id, vertexFormat, loadCallback)));
        client.delayedInit();
        ModelLoadingPlugin.register(new DimensionalDoorsModelLoadingPlugin(modelLoadingOverrides));
    }

    @Override
    public void register(RenderType type, Block... blocks) {
        BlockRenderLayerMap.INSTANCE.putBlocks(type, blocks);
    }

    @Override
    public void onClientPlayerJoin(Runnable listener) {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> listener.run());
    }
}
