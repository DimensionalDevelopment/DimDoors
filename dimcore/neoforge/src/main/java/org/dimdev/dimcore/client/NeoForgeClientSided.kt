package org.dimdev.dimcore.client

import net.minecraft.client.KeyMapping
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.MenuAccess
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.*
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
import org.dimdev.dimcore.NeoforgeResourceLoader
import org.dimdev.dimcore.api.client.ClientReloadListenerProvider
import org.dimdev.dimcore.api.client.IClientSided
import org.dimdev.dimcore.api.client.ModClient
import org.dimdev.dimcore.api.fluid.FluidDetails
import java.io.IOException

open class NeoForgeClientSided<V : NeoForgeClientSided<V, T>, T : ModClient<in V>>(
    bus: IEventBus,
    private val client: T
) : IClientSided<V> {
    private val keyMappings = mutableListOf<KeyMapping>()

    init {
        client.init(self())

        bus.addListener<RegisterParticleProvidersEvent> { event ->
            client.initParticles(
                object : ModClient.RegularParticleRegister {
                    override fun <P : ParticleOptions> register(
                        particleType: ParticleType<P>,
                        provider: (SpriteSet) -> ParticleProvider<P>
                    ) = event.registerSpriteSet(particleType, provider)
                }, object : ModClient.SpecialParticleRegister {
                    override fun <P : ParticleOptions> register(
                        particleType: ParticleType<P>,
                        provider: ParticleProvider<P>
                    ) = event.registerSpecial(particleType, provider)
                })
        }

        bus.addListener<RegisterMenuScreensEvent> { event ->
            client.initScreens(object : ModClient.ScreenRegister {
                override fun <U : AbstractContainerMenu, M> register(
                    menuType: MenuType<U>,
                    factory: (U, Inventory, Component) -> M
                ) where M : Screen, M : MenuAccess<U> {
                    event.register<U, M>(menuType, factory)
                }
            })
        }

        bus.addListener<RegisterClientExtensionsEvent> { event ->
            client.initFluids { _, fluid, details -> event.registerFluidType(FluidExtension(details), fluid.fluidType) }
        }
        bus.addListener<EntityRenderersEvent.RegisterRenderers> { event ->
            client.initEntityRenderers(object : ModClient.EntityRegister {
                override fun <T : Entity> register(type: EntityType<T>, provider: EntityRendererProvider<T>) =
                    event.registerEntityRenderer(type, provider)
            })
            client.initBlockEntityRenderers(object : ModClient.BlockEntityRegister {
                override fun <T : BlockEntity> register(
                    type: BlockEntityType<T>,
                    provider: BlockEntityRendererProvider<T>
                ) = event.registerBlockEntityRenderer(type, provider)
            })
        }

        bus.addListener<EntityRenderersEvent.RegisterLayerDefinitions> { event ->
            client.initModelLayers { location, supplier -> event.registerLayerDefinition(location, supplier) }
        }

        if (!keyMappings.isEmpty()) bus.addListener<RegisterKeyMappingsEvent> { event -> keyMappings.forEach(event::register) }

        val mod: Any = client
        if (mod is ClientReloadListenerProvider) bus.addListener<RegisterClientReloadListenersEvent> { event ->
            mod.registerClientReloadListeners { name, consumer -> event.registerReloadListener(NeoforgeResourceLoader.Client(client.modId id name, consumer)) }
        }
        bus.addListener<RegisterDimensionSpecialEffectsEvent> { event -> client.initDimensionEffects(event::register) }

        bus.addListener<FMLClientSetupEvent> { event ->
            event.enqueueWork {
                client.delayedInit()
                client.initItemProperties(ItemProperties::register)
            }
        }

        bus.addListener<RegisterShadersEvent> { event ->
            val provider = event.resourceProvider

            client.initShaders { id, vertexFormat, consumer ->
                try {
                    event.registerShader(ShaderInstance(provider, id, vertexFormat), consumer)
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
            }
        }
    }

    @JvmRecord
    data class FluidExtension(
        val flowing: ResourceLocation,
        val still: ResourceLocation,
        val overlay: ResourceLocation
    ) : IClientFluidTypeExtensions {
        constructor(attributes: FluidDetails) : this(attributes.flowing, attributes.still, attributes.overlay)

        override fun getFlowingTexture(): ResourceLocation = flowing
        override fun getOverlayTexture(): ResourceLocation = overlay
        override fun getStillTexture(): ResourceLocation = still
    }

    override fun registerKeyBinding(mapping: KeyMapping) {
        keyMappings.add(mapping)
    }

    infix fun String.id(that: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(this, that)
}
