package org.dimdev.dimdoors.client

import com.chocohead.mm.api.ClassTinkerers
import com.mojang.blaze3d.vertex.PoseStack
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.Camera
import net.minecraft.client.RecipeBookCategories
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.LightTexture
import net.minecraft.world.item.ItemStack
import org.dimdev.dimcore.client.FabricClientSided
import org.dimdev.dimcore.client.IDimensionSpecialEffectExtension
import org.dimdev.dimdoors.client.IDimDoorsClientSided.PreRender
import org.dimdev.dimdoors.client.effect.DimensionEffect
import org.dimdev.dimdoors.client.effect.VoidDimensionSpecialEffects
import org.joml.Matrix4f

class DimensionalDoorsClientFabric : FabricClientSided<DimensionalDoorsClientFabric, DimensionalDoorsClient>(DimensionalDoorsClient.INSTANCE), IDimDoorsClientSided<DimensionalDoorsClientFabric> {

        override fun onInitializeClient() {
        super.onInitializeClient()
        RecipeBookManager.init()
        checkCompat()
        ModelLoadingPlugin.register(GeneratedDoorModelCopyPlugin())
    }

    override fun getRecipBookCategories(name: String, itemStack: () -> ItemStack): () -> RecipeBookCategories = { ClassTinkerers.getEnum(RecipeBookCategories::class.java, name) }

    override fun createVoidEffect(effect: DimensionEffect): VoidDimensionSpecialEffects {
        return FabricVoidDimensionSpecialEffects(effect)
    }

    override fun onPreRender(onPrerender: PreRender) {
        WorldRenderEvents.START.register { context ->
            val level = context.world() ?: return@register
            onPrerender.preRender(level.gameTime, context.tickCounter().getGameTimeDeltaPartialTick(false))
        }
    }

    private class FabricVoidDimensionSpecialEffects(private val effect: DimensionEffect) : VoidDimensionSpecialEffects(), IDimensionSpecialEffectExtension {
        override fun extRenderSky(level: ClientLevel, ticks: Int, partialTick: Float, frustumMatrix: Matrix4f, camera: Camera, projectionMatrix: Matrix4f, isFoggy: Boolean, skyFogSetup: Runnable) = effect.renderSky(level, ticks, partialTick, frustumMatrix, camera, projectionMatrix, isFoggy, skyFogSetup)
        override fun extRenderClouds(level: ClientLevel, ticks: Int, partialTick: Float, poseStack: PoseStack, camX: Double, camY: Double, camZ: Double, modelViewMatrix: Matrix4f, projectionMatrix: Matrix4f) = effect.renderClouds(level, ticks, partialTick, poseStack, camX, camY, camZ, modelViewMatrix, projectionMatrix)
        override fun extRenderWeather(level: ClientLevel, ticks: Int, partialTick: Float, lightTexture: LightTexture, camX: Double, camY: Double, camZ: Double) = effect.renderWeather(level, ticks, partialTick, lightTexture, camX, camY, camZ)
    }
}
