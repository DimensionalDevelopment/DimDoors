package org.dimdev.dimdoors.client;

import com.chocohead.mm.api.ClassTinkerers;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import org.dimdev.dimdoors.api.util.function.TriFunction;
import org.dimdev.dimdoors.client.effect.DimensionEffect;
import org.dimdev.dimdoors.client.effect.VoidDimensionSpecialEffects;
import org.dimdev.dimdoors.client.fabric.IDimensionSpecialEffectExtension;
import org.dimdev.dimdoors.client.screen.TesselatingLoomScreen;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.network.client.ClientPacketListener;
import org.dimdev.dimdoors.network.packet.s2c.*;
import org.dimdev.dimdoors.screen.ModScreenHandlerTypes;
import org.joml.Matrix4f;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


public class DimensionalDoorsClientFabric extends FabricClientSided<DimensionalDoorsClientFabric, DimensionalDoorsClient> implements IDimDoorsClientSided<DimensionalDoorsClientFabric> {
    public DimensionalDoorsClientFabric() {
        super(DimensionalDoorsClient.INSTANCE);
    }

    @Override
    public void onInitializeClient() {
        super.onInitializeClient();
        RecipeBookManager.init();
        checkCompat();
    }

    @Override
    public Supplier<RecipeBookCategories> getRecipBookCategories(String name, Supplier<ItemStack> itemStack) {
        return () -> ClassTinkerers.getEnum(RecipeBookCategories.class, name);
    }

    @Override
    public VoidDimensionSpecialEffects createVoidEffect(DimensionEffect effect) {
        return new FabricVoidDimensionSpecialEffects(effect);
    }

    private static class FabricVoidDimensionSpecialEffects extends VoidDimensionSpecialEffects implements IDimensionSpecialEffectExtension {
        private final DimensionEffect effect;

        public FabricVoidDimensionSpecialEffects(DimensionEffect effect) {
            super();
            this.effect = effect;
        }

        @Override
        public boolean extRenderSky(ClientLevel level, int ticks, float partialTick, Matrix4f modelViewMatrix, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
            return effect.renderSky(level, ticks, partialTick, modelViewMatrix, camera, projectionMatrix, isFoggy, setupFog);
        }

        @Override
        public boolean extRenderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
            return effect.renderClouds(level, ticks, partialTick, poseStack, camX, camY, camZ, modelViewMatrix, projectionMatrix);
        }

        @Override
        public boolean extRenderWeather(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
            return effect.renderWeather(level, ticks, partialTick, lightTexture, camX, camY, camZ);
        }
    }
}
