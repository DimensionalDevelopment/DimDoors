package org.dimdev.dimcore.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.client.KeyMapping
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.MenuAccess
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import org.dimdev.dimcore.FabricResourceLoader
import org.dimdev.dimcore.api.client.ClientReloadListenerProvider
import org.dimdev.dimcore.api.client.IClientSided
import org.dimdev.dimcore.api.client.ModClient
import java.io.IOException

open class FabricClientSided<V : FabricClientSided<V, T>, T : ModClient<in V>>(private val client: T
) : ClientModInitializer, IClientSided<V> {
    override fun onInitializeClient() {
        client.init(self())

        client.initParticles(
            object : ModClient.RegularParticleRegister { override fun <P : ParticleOptions> register(particleType: ParticleType<P>, provider: (SpriteSet) -> ParticleProvider<P>) { ParticleFactoryRegistry.getInstance().register(particleType, provider) } },
            object : ModClient.SpecialParticleRegister { override fun <P : ParticleOptions> register(particleType: ParticleType<P>, provider: ParticleProvider<P>) { ParticleFactoryRegistry.getInstance().register(particleType, provider) }
        })


        client.initFluids { flowing, still, details -> FluidRenderHandlerRegistry.INSTANCE.register(flowing, still, SimpleFluidRenderHandler(details.still, details.flowing, details.overlay)) }
        client.initScreens(object : ModClient.ScreenRegister { override fun <U : AbstractContainerMenu, M> register(menuType: MenuType<U>, factory: (U, Inventory, Component) -> M) where M : Screen, M : MenuAccess<U> = MenuScreens.register<U, M>(menuType, factory) })
        client.initBlockEntityRenderers(object : ModClient.BlockEntityRegister { override fun <T : BlockEntity> register(type: BlockEntityType<T>, provider: BlockEntityRendererProvider<T>) = BlockEntityRenderers.register(type, provider) })
        client.initEntityRenderers(object : ModClient.EntityRegister { override fun <T : Entity> register(type: EntityType<T>, provider: EntityRendererProvider<T>) { EntityRendererRegistry.register(type, provider) } })
        client.initModelLayers { id, layerDefinitionSupplier -> EntityModelLayerRegistry.registerModelLayer(id, layerDefinitionSupplier) }
        client.initDimensionEffects(DimensionRenderingRegistry::registerDimensionEffects)

        CoreShaderRegistrationCallback.EVENT.register(CoreShaderRegistrationCallback { context ->
            client.initShaders { id, vertexFormat, loadCallback ->
                try {
                    context.register(id, vertexFormat, loadCallback)
                } catch (exception: IOException) {
                    throw RuntimeException(exception)
                }
            }
        })

        client.initItemProperties(ItemProperties::register)

        val mod: Any = client
        if (mod is ClientReloadListenerProvider) mod.registerClientReloadListeners(::registerClientLoader)

        client.delayedInit()
    }

    private fun registerClientLoader(name: String, consumer: (ResourceManager) -> Unit) {
        val id = ResourceLocation.fromNamespaceAndPath(client.modId, name)
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(FabricResourceLoader(id, consumer, mutableListOf()))
    }

    override fun registerKeyBinding(mapping: KeyMapping) {
        KeyBindingHelper.registerKeyBinding(mapping)
    }
}
