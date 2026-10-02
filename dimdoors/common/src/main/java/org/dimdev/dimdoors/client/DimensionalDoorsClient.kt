package org.dimdev.dimdoors.client

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import foundry.imgui.api.ImGuiMCEvents
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.renderer.DimensionSpecialEffects
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.CrossbowItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.material.FlowingFluid
import net.minecraft.world.level.material.Fluid
import org.dimdev.dimcore.DimCore.clientPlatform
import org.dimdev.dimcore.DimCore.platform
import org.dimdev.dimcore.api.client.ActionKeyMapping
import org.dimdev.dimcore.api.client.ModClient
import org.dimdev.dimcore.api.fluid.FluidDetails
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.api.client.DimensionalPortalRenderer
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity
import org.dimdev.dimdoors.block.entity.DialingDoorBlockEntity
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes
import org.dimdev.dimdoors.client.effect.DungeonDimensionEffect
import org.dimdev.dimdoors.client.effect.LimboDimensionEffect
import org.dimdev.dimdoors.client.effect.sky.EnvironmentAddonClient
import org.dimdev.dimdoors.client.screen.TesselatingLoomScreen
import org.dimdev.dimdoors.compat.imgui.PortalColorGui
import org.dimdev.dimdoors.compat.iris.IrisCompat
import org.dimdev.dimdoors.entity.MaskEntity
import org.dimdev.dimdoors.entity.ModEntityTypes
import org.dimdev.dimdoors.fluid.ModFluids
import org.dimdev.dimdoors.item.ModItems
import org.dimdev.dimdoors.network.client.ClientPacketListener
import org.dimdev.dimdoors.particle.ModParticleTypes
import org.dimdev.dimdoors.particle.client.LimboAshParticle
import org.dimdev.dimdoors.particle.client.MonolithParticle
import org.dimdev.dimdoors.particle.client.RiftParticle
import org.dimdev.dimdoors.screen.ModScreenHandlerTypes
import org.dimdev.dimdoors.screen.TessellatingContainer
import org.dimdev.dimdoors.util.Timer
import org.lwjgl.glfw.GLFW

class DimensionalDoorsClient : ModClient<IDimDoorsClientSided<*>> {
    var renderTick: Float = 0f
        private set

    override fun init(sided: IDimDoorsClientSided<*>) {
        clientSided = sided
        clientPlatform.onClientPlayerJoin(ClientPacketListener::clearPocketAddons)
        registerCompats()

        sided.onPreRender(this::preRender)

        if (platform.isModLoaded("imguimc")) {
            sided.registerKeyBinding(ActionKeyMapping("key.dimdoors.portal_colors_editor", GLFW.GLFW_KEY_N, "key.categories.dimdoors", PortalColorGui::toggle))

            ImGuiMCEvents.INSTANCE.preRenderImGuiEvent { PortalColorGui.render() }
        }
    }

    override val modId: String = DimensionalDoors.MOD_ID

    override fun initParticles(
        regularParticleRegister: ModClient.RegularParticleRegister,
        specialParticleRegister: ModClient.SpecialParticleRegister
    ) {
        specialParticleRegister.register<SimpleParticleType>(
            ModParticleTypes.MONOLITH
        ) { _, clientLevel, x, y, z, _, _, _ -> MonolithParticle(clientLevel, x, y, z) }
        regularParticleRegister.register(ModParticleTypes.RIFT) { spriteProvider -> RiftParticle.Factory(spriteProvider) }
        regularParticleRegister.register<SimpleParticleType>(ModParticleTypes.LIMBO_ASH) { spriteProvider -> LimboAshParticle.Factory(spriteProvider) }

    }


    override fun initDimensionEffects(effectsRegister: (ResourceLocation, DimensionSpecialEffects) -> Unit) {
        effectsRegister.invoke(DimensionalDoors.id("limbo"), sided.createVoidEffect(LimboDimensionEffect.INSTANCE))
        effectsRegister.invoke(DimensionalDoors.id("dungeon"), sided.createVoidEffect(DungeonDimensionEffect.INSTANCE)
        )
    }

    override fun initBlockEntityRenderers(register: ModClient.BlockEntityRegister) {
        register.register<EntranceRiftBlockEntity.Impl>(ModBlockEntityTypes.ENTRANCE_RIFT, ::EntranceRiftBlockEntityRenderer)
        register.register<DetachedRiftBlockEntity>(ModBlockEntityTypes.DETACHED_RIFT, ::DetachedRiftBlockEntityRenderer)
        register.register<DialingDoorBlockEntity>(ModBlockEntityTypes.DIALING_DOOR, ::DialingDoorBlockEntityRenderer)
    }

    override fun initShaders(shaderRegister: (ResourceLocation, VertexFormat, (ShaderInstance) -> Unit) -> Unit) {
        shaderRegister.invoke(DimensionalDoors.id("dimensional_portal"), DefaultVertexFormat.POSITION) {
            ModShaders.dimensionalPortal = it
        }
    }

    override fun initItemProperties(consumer: (Item, ResourceLocation, ClampedItemPropertyFunction) -> Unit) {
        consumer.invoke(ModItems.FARSHOT, ResourceLocation.withDefaultNamespace("pull"), ClampedItemPropertyFunction { stack: ItemStack?, _, entity: LivingEntity?, _ -> if (entity == null) 0.0f else if (CrossbowItem.isCharged(stack!!)) 0.0f else (stack.getUseDuration(entity) - entity.useItemRemainingTicks).toFloat() / CrossbowItem.getChargeDuration(stack, entity).toFloat() })
        consumer.invoke(ModItems.FARSHOT, ResourceLocation.withDefaultNamespace("pulling"), ClampedItemPropertyFunction { stack: ItemStack, _, entity: LivingEntity?, _ -> if (entity != null && entity.isUsingItem && entity.getUseItem() == stack && !CrossbowItem.isCharged(stack)) 1.0f else 0.0f })
        consumer.invoke(ModItems.FARSHOT, ResourceLocation.withDefaultNamespace("charged"), ClampedItemPropertyFunction { stack: ItemStack, _, _, _ -> if (CrossbowItem.isCharged(stack)) 1.0f else 0.0f
            })
    }

    override fun initModelLayers(consumer: (ModelLayerLocation, () -> LayerDefinition) -> Unit) {
        consumer.invoke(ModEntityModelLayers.MONOLITH, MonolithModel::texturedModelData)
    }

    fun preRender(ticks: Long, deltaTick: Float) {
        renderTick = ticks + deltaTick
        Timer.update(ticks, deltaTick)
    }

    override fun initFluids(register: (FlowingFluid, Fluid, FluidDetails) -> Unit) {
        register.invoke(ModFluids.LEAK, ModFluids.FLOWING_LEAK, ModFluids.LEAK_DETAILS)
        register.invoke(ModFluids.ETERNAL_FLUID, ModFluids.FLOWING_ETERNAL_FLUID, ModFluids.ETERNAL_FLUID_DETAILS)
    }

    override fun initScreens(screenRegister: ModClient.ScreenRegister) {
        screenRegister.register<TessellatingContainer, TesselatingLoomScreen>(ModScreenHandlerTypes.TESSELATING_LOOM, ::TesselatingLoomScreen)
    }

    override fun initEntityRenderers(register: ModClient.EntityRegister) {
        register.register(ModEntityTypes.MONOLITH, ::MonolithRenderer)
        register.register(ModEntityTypes.MASK) { context: EntityRendererProvider.Context ->
            object : EntityRenderer<MaskEntity>(context) {
                override fun getTextureLocation(entity: MaskEntity): ResourceLocation = ResourceLocation.parse("blep")
            }
        }

        register.register(ModEntityTypes.FARSHOT_ENDER_PEARL, ::FarShotEnderPearlRenderer)
    }

    override fun delayedInit() {
        EnvironmentAddonClient.init()
        initGeneratedDoorCutouts()
        clientPlatform.register(
            RenderType.cutout(),
            ModBlocks.QUARTZ_DOOR,
            ModBlocks.GOLD_DOOR,
            ModBlocks.DRIFTWOOD_LEAVES,
            ModBlocks.DRIFTWOOD_SAPLING,
            ModBlocks.DRIFTWOOD_DOOR,
            ModBlocks.DRIFTWOOD_TRAPDOOR,
            ModBlocks.UNRAVELED_SPIKE,
            ModBlocks.DRIFTWOOD_DOOR,
            ModBlocks.DIALING_DOOR
        )
    }

    companion object {
        @JvmField
        val INSTANCE: DimensionalDoorsClient = DimensionalDoorsClient()

        @JvmField
        var detector: ShaderPackDetector = { consumer -> consumer.invoke(DimensionalPortalRenderer.VANILLA_DIMENSIONAL_PORTAL_RENDER_LAYER) }
        private lateinit var sided: IDimDoorsClientSided<*>
        private fun registerCompats() {
            if (platform.isModLoaded("iris") || platform.isModLoaded("oculus")) detector = IrisCompat()
        }

        fun initGeneratedDoorCutouts() {
            val registrar = DimensionalDoors.getDimensionalDoorBlockRegistrar()

            val generatedBlocks =
                registrar.gennedIds.mapNotNull(BuiltInRegistries.BLOCK::get).toTypedArray()
            if (generatedBlocks.isNotEmpty()) { clientPlatform.register(RenderType.cutout(), *generatedBlocks) }
        }

        @JvmStatic
        var clientSided: IDimDoorsClientSided<*>
            get() = sided
            set(sided) {
                Companion.sided = sided
            }
    }
}
