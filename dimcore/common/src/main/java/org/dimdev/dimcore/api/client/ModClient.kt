package org.dimdev.dimcore.api.client

import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.MenuAccess
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.renderer.DimensionSpecialEffects
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Item
import net.minecraft.world.level.material.FlowingFluid
import net.minecraft.world.level.material.Fluid
import org.dimdev.dimcore.api.fluid.FluidDetails

interface ModClient<T : IClientSided<*>> {
    fun init(sided: T)

    val modId: String

    fun initParticles(regularParticleRegister: RegularParticleRegister, specialParticleRegister: SpecialParticleRegister) {}

    fun initFluids(register: (FlowingFluid, Fluid, FluidDetails) -> Unit) {}

    fun initScreens(screenRegister: ScreenRegister) {}

    fun initModelLayers(consumer: (ModelLayerLocation, () -> LayerDefinition) -> Unit) {}

    fun initDimensionEffects(effectsRegister: (ResourceLocation, DimensionSpecialEffects) -> Unit) {}

    fun initShaders(shaderRegister: (ResourceLocation, VertexFormat, (ShaderInstance) -> Unit) -> Unit) {}

    fun initItemProperties(consumer: (Item, ResourceLocation, ClampedItemPropertyFunction) -> Unit) {}

    fun delayedInit() {}

    interface ScreenRegister {
        fun <U : AbstractContainerMenu, M> register(menuType: MenuType<U>, factory: (U, Inventory, Component) -> M) where M : Screen, M : MenuAccess<U>
    }

    interface SpecialParticleRegister {
        fun <P : ParticleOptions> register(particleType: ParticleType<P>, provider: ParticleProvider<P>)
    }

    interface RegularParticleRegister {
        fun <P : ParticleOptions> register(particleType: ParticleType<P>, provider: (SpriteSet) -> ParticleProvider<P>)
    }
}
