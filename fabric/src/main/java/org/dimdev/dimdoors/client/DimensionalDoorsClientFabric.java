package org.dimdev.dimdoors.client;

import com.chocohead.mm.api.ClassTinkerers;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import org.dimdev.dimdoors.client.screen.TesselatingLoomScreen;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.network.client.ClientPacketListener;
import org.dimdev.dimdoors.network.packet.s2c.*;
import org.dimdev.dimdoors.screen.ModScreenHandlerTypes;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.dimdev.dimdoors.client.DimensionalDoorsClient.initGeneratedDoorCutouts;

public class DimensionalDoorsClientFabric implements ClientModInitializer, IClientSided {

    @Override
    public void onInitializeClient() {
        DimensionalDoorsClient.init(this);
        DimensionalDoorsClient.initClient();
        initGeneratedDoorCutouts();

        RecipeBookManager.init();
        ModelLoadingPlugin.register(new DimensionalDoorsModelLoadingPlugin());

        MenuScreens.register(ModScreenHandlerTypes.TESSELATING_LOOM, TesselatingLoomScreen::new);

        DimensionRenderering.initClient();
        DimensionalDoorsClient.initParticles(
                (particleType, particleProvider) -> ParticleFactoryRegistry.getInstance().register((ParticleType) particleType, (ParticleProvider) particleProvider),
                (particleType, spriteSetFunction) -> ParticleFactoryRegistry.getInstance().register(particleType, (ParticleFactoryRegistry.PendingParticleFactory) spriteSetFunction::apply));
        DimensionalDoorsClient.initEntitiesClient(EntityRendererRegistry::register, BlockEntityRenderers::register);
        ModEntityModelLayers.initClient((modelLayerLocation, layerDefinitionSupplier) -> EntityModelLayerRegistry.registerModelLayer(modelLayerLocation, layerDefinitionSupplier::get));

        initClientSideHandler();

        registerFluid(ModFluids.LEAK, ModFluids.FLOWING_LEAK, ModFluids.LEAK_DETAILS);
        registerFluid(ModFluids.ETERNAL_FLUID, ModFluids.FLOWING_ETERNAL_FLUID, ModFluids.ETERNAL_FLUID_DETAILS);

    }

    private void registerFluid(FlowingFluid flowing, Fluid still, ModFluids.FluidDetails details) {
        FluidRenderHandlerRegistry.INSTANCE.register(flowing, still, new SimpleFluidRenderHandler(details.still(), details.flowing(), details.overlay()));
    }

    private void initClientSideHandler() {
        ClientPlayNetworking.registerGlobalReceiver(PlayerInventorySlotUpdateS2CPacket.TYPE, (packet, context) -> ClientPacketListener.onPlayerInventorySlotUpdate(packet));
        ClientPlayNetworking.registerGlobalReceiver(SyncPocketAddonsS2CPacket.TYPE, (packet, context) -> ClientPacketListener.onSyncPocketAddons(packet));
        ClientPlayNetworking.registerGlobalReceiver(MonolithAggroParticlesPacket.TYPE, (packet, context) -> ClientPacketListener.onMonolithAggroParticles(packet));
        ClientPlayNetworking.registerGlobalReceiver(MonolithTeleportParticlesPacket.TYPE, (packet, context) -> ClientPacketListener.onMonolithTeleportParticles(packet));
        ClientPlayNetworking.registerGlobalReceiver(RenderBreakBlockS2CPacket.TYPE, (packet, context) -> ClientPacketListener.onRenderBreakBlock(packet));
    }

    @Override
    public Supplier<RecipeBookCategories> getRecipBookCategories(String name, Supplier<ItemStack> itemStack) {
        return () -> ClassTinkerers.getEnum(RecipeBookCategories.class, name);
    }

    @Override
    public void register(RenderType type, Block... blocks) {
        BlockRenderLayerMap.INSTANCE.putBlocks(type, blocks);
    }

    @Override
    public void onClientPlayerJoin(Runnable listener) {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> listener.run());
    }

    @Override
    public void registerCoreShader(ResourceLocation id, VertexFormat vertexFormat, Consumer<ShaderInstance> loadCallback) {
        CoreShaderRegistrationCallback.EVENT.register(context -> context.register(id, vertexFormat, loadCallback));
    }
}
