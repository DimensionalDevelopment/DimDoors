package org.dimdev.dimdoors.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.dimdev.dimdoors.client.screen.TesselatingLoomScreen;
import org.dimdev.dimdoors.network.client.ClientPacketListener;
import org.dimdev.dimdoors.network.packet.s2c.*;
import org.dimdev.dimdoors.screen.ModScreenHandlerTypes;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class DimensionalDoorsClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        DimensionalDoorsClient.init();
        RecipeBookManager.init();
        ModelLoadingPlugin.register(new DimensionalDoorsModelLoadingPlugin());

        MenuScreens.register(ModScreenHandlerTypes.TESSELATING_LOOM.get(), TesselatingLoomScreen::new);

        DimensionRenderering.initClient();
        DimensionalDoorsClient.initParticles(
                (particleType, particleProvider) -> ParticleFactoryRegistry.getInstance().register((ParticleType) particleType, (ParticleProvider) particleProvider),
                (particleType, spriteSetFunction) -> ParticleFactoryRegistry.getInstance().register(particleType, (ParticleFactoryRegistry.PendingParticleFactory) spriteSetFunction::apply));
        DimensionalDoorsClient.initEntitiesClient(EntityRendererRegistry::register);
        ModEntityModelLayers.initClient((modelLayerLocation, layerDefinitionSupplier) -> EntityModelLayerRegistry.registerModelLayer(modelLayerLocation, layerDefinitionSupplier::get));

        initClientSideHandler();
    }

    private void initClientSideHandler() {
        ClientPlayNetworking.registerGlobalReceiver(PlayerInventorySlotUpdateS2CPacket.TYPE, (packet, context) -> ClientPacketListener.onPlayerInventorySlotUpdate(packet));
        ClientPlayNetworking.registerGlobalReceiver(SyncPocketAddonsS2CPacket.TYPE, (packet, context) -> ClientPacketListener.onSyncPocketAddons(packet));
        ClientPlayNetworking.registerGlobalReceiver(MonolithAggroParticlesPacket.TYPE, (packet, context) -> ClientPacketListener.onMonolithAggroParticles(packet));
        ClientPlayNetworking.registerGlobalReceiver(MonolithTeleportParticlesPacket.TYPE, (packet, context) -> ClientPacketListener.onMonolithTeleportParticles(packet));
        ClientPlayNetworking.registerGlobalReceiver(RenderBreakBlockS2CPacket.TYPE, (packet, context) -> ClientPacketListener.onRenderBreakBlock(packet));
    }
}
