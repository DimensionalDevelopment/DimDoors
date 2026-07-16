package org.dimdev.dimdoors.client;

import com.chocohead.mm.api.ClassTinkerers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimdoors.client.effect.DimensionEffect;
import org.dimdev.dimdoors.client.effect.VoidDimensionSpecialEffects;
import org.dimdev.limlib.client.FabricClientSided;
import org.dimdev.limlib.client.IDimensionSpecialEffectExtension;
import org.joml.Matrix4f;

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
