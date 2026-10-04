package org.dimdev.dimcore

import com.mojang.serialization.Codec
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.PackLocationInfo
import net.minecraft.server.packs.PackSelectionConfig
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.PathPackResources
import net.minecraft.server.packs.repository.KnownPack
import net.minecraft.server.packs.repository.Pack
import net.minecraft.server.packs.repository.PackSource
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModList
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.AddPackFindersEvent
import net.neoforged.neoforge.event.AddReloadListenerEvent
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.handling.IPayloadHandler
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import net.neoforged.neoforge.registries.*
import net.neoforged.neoforge.registries.callback.AddCallback
import org.dimdev.dimcore.api.*
import org.dimdev.dimcore.api.ext.cast
import java.util.*
import java.util.function.Supplier

abstract class NeoForgeSided<V : NeoForgeSided<V, T>, T : ModCommon<in V>>(private val bus: IEventBus, common: T) : SidedImpl<V, T>(common) {
    private val toRegister = mutableMapOf<ResourceKey<*>, MutableMap<ResourceLocation, Any>>()
    private var hooks: Hooks? = null
    private val registriesToRegister = mutableListOf<Registry<*>>()
    private val dataPackRegistries = mutableListOf<DataPackRegistryRegistration<*>>()

    private fun id(name: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(modId(), name)

    private fun queue(key: ResourceKey<*>, name: String, value: Any) {
        toRegister.getOrPut(key) { linkedMapOf() }[id(name)] = value
    }

    override fun <T> entryRegister(resourceKey: ResourceKey<Registry<T>>, registry: Registry<T>?, synced: Boolean): PlatformRegistry.EntryRegister<T> {
        if (resourceKey == PlatformRegistry.DataValuePlatformRegistry.KEY) return object : PlatformRegistry.EntryRegister<T>() {
            override fun <V : T> register(name: String, supplier: () -> V): V {
                val type = supplier() as DataValueType<Any>
                val builder = AttachmentType.builder(Supplier { type.defaultValue() }).serialize(type.codec)
                type.streamCodec?.let(builder::sync)
                val attachment = builder.build()
                queue(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, name, attachment)
                return attachment as V
            }

            override fun createRegistry(): Registry<T> = NeoForgeRegistries.ATTACHMENT_TYPES.cast()
        }

        if (resourceKey == PlatformRegistry.CreativeTabPlatformRegistry.KEY) return object : PlatformRegistry.EntryRegister<T>() {
            override fun <V : T> register(name: String, supplier: () -> V): V {
                val type = supplier() as CreativeTabType
                queue(Registries.CREATIVE_MODE_TAB, name, CreativeModeTab.builder().apply(type.block).build())
                return DeferredHolder.create<CreativeModeTab, CreativeModeTab>(Registries.CREATIVE_MODE_TAB, id(name)) as V
            }

            override fun createRegistry(): Registry<T> = BuiltInRegistries.CREATIVE_MODE_TAB.cast()
        }

        return object : PlatformRegistry.EntryRegister<T>() {
            private val target: Registry<T> by lazy { registry ?: RegistryBuilder(resourceKey).sync(synced).create().also(registriesToRegister::add) }

            override fun <V : T> register(name: String, supplier: () -> V): V {
                val value = supplier()
                queue(resourceKey, name, value as Any)
                return value
            }

            override fun createRegistry(): Registry<T> = target
        }
    }

    private fun onDataPackRegister(event: DataPackRegistryEvent.NewRegistry) {
        dataPackRegistries.forEach { it.register(event) }
    }

    @JvmRecord
    private data class DataPackRegistryRegistration<T>(
        val key: ResourceKey<Registry<T>>,
        val codec: Codec<T>,
        val networkCodec: Codec<T>?
    ) {
        fun register(event: DataPackRegistryEvent.NewRegistry) {
            if (networkCodec != null) {
                event.dataPackRegistry<T>(key, codec, networkCodec)
            } else {
                event.dataPackRegistry<T>(key, codec)
            }
        }
    }

    fun <T> populate(registry: Registry<T>, map: MutableMap<ResourceLocation, Any>) {
        map.forEach { (resourceLocation, obj) ->
            Registry.register<T, T>(
                registry,
                resourceLocation,
                obj as T
            )
        }
    }

    override fun modify(tab: ResourceKey<CreativeModeTab>, filler: (ICreativeTabHandler.CreativeTabOutput) -> Unit) {
        bus.addListener<BuildCreativeModeTabContentsEvent> { event -> if (event.tabKey == tab) filler(wrapTabOutput(event)) }
    }

    private fun wrapTabOutput(event: BuildCreativeModeTabContentsEvent): ICreativeTabHandler.CreativeTabOutput {
        return object : ICreativeTabHandler.CreativeTabOutput {
            override fun acceptAfter(after: ItemStack, stack: ItemStack, visibility: CreativeModeTab.TabVisibility) {
                if (after.isEmpty) event.accept(stack, visibility) else event.insertAfter(after, stack, visibility)
            }

            override fun acceptBefore(before: ItemStack, stack: ItemStack, visibility: CreativeModeTab.TabVisibility) {
                if (before.isEmpty) event.accept(stack, visibility) else event.insertBefore(before, stack, visibility)
            }
        }
    }


    @JvmRecord
    private data class PlayPayloadHandlerReturnable<T : CustomPacketPayload>(val packetFunction: (T, ServerPlayer) -> CustomPacketPayload?) : IPayloadHandler<T> {
        override fun handle(payload: T, context: IPayloadContext) {
            val returnPayload = packetFunction.invoke(payload, context.player() as ServerPlayer) ?: return
            context.handle(returnPayload)
        }
    }

    private fun <T> runHooks(registry: Registry<T>, handlers: List<(ResourceLocation, Any?) -> Unit>) {
        registry.entrySet().toList().forEach { (key, value) -> handlers.forEach { it(key.location(), value) } }
        registry.addCallback(AddCallback<T> { _, _, key, value -> handlers.forEach { it(key.location(), value) } })
    }


    private class NeoForgePacketRegister(private val registrar: PayloadRegistrar) : PacketRegister {
        override fun <T : CustomPacketPayload> registerServerPacket(
            type: CustomPacketPayload.Type<T>,
            streamCodec: StreamCodec<RegistryFriendlyByteBuf, T>,
            function: (T, ServerPlayer) -> CustomPacketPayload?
        ) {
            registrar.playToServer(type, streamCodec, PlayPayloadHandlerReturnable(function))
        }

        override fun <T : CustomPacketPayload> registerClientPacket(
            type: CustomPacketPayload.Type<T>,
            streamCodec: StreamCodec<RegistryFriendlyByteBuf, T>,
            consumer: (T) -> Unit
        ) {
            registrar.playToClient(type, streamCodec) { payload, _ -> consumer.invoke(payload) }
        }
    }

    init {
        val mod: Any = common
        if (mod is EntityAttributeProvider) bus.addListener<EntityAttributeCreationEvent> { event -> mod.registerEntityAttributes { type, function -> event.put(type, function.invoke().build()) } }

        bus.addListener(this::onDataPackRegister)

        bus.addListener<NewRegistryEvent> { event -> registriesToRegister.forEach { event.register(it) } }

        bus.addListener<RegisterEvent>(EventPriority.LOWEST, { event ->
            val key = event.registryKey
            val registry = event.registry

            toRegister.remove(key)?.takeIf { it.isNotEmpty() }?.let { populate(registry, it) }

            hooks?.onEntry?.get(key)?.let { runHooks(registry, it) }
        })

        if (mod is PacketProvider) bus.addListener<RegisterPayloadHandlersEvent> { event -> mod.registerPackets(NeoForgePacketRegister(event.registrar("1"))) }

        if (mod is ServerReloadListenerProvider) NeoForge.EVENT_BUS.addListener<AddReloadListenerEvent> { event ->
            mod.registerServerReloadListeners { name, _, consumer -> event.addListener(NeoforgeResourceLoader.Server(ResourceLocation.fromNamespaceAndPath(common.modId, name), consumer)) }
        }

        if (mod is PackProvider) bus.addListener<AddPackFindersEvent> { event ->
            val packs = mutableListOf<PackInfo>()
            mod.registerPacks { type, id, name, defaultedOn -> if (type == event.packType) packs += PackInfo(id, name, defaultedOn) }
            event.addRepositorySource { source -> packs.forEach { pack -> pack.create(common.modId, event.packType)?.let(source::accept) } }
        }

        common.initRegistries(self())

        var initialized = false
        bus.addListener<RegisterEvent>(EventPriority.HIGHEST, { _ ->
            if (initialized) return@addListener
            initialized = true
            common.init(self())
            if (mod is RegistrationHooks) hooks = Hooks().also(mod::registrationHooks)
        })
    }

    @JvmRecord
    private data class PackInfo(val id: String?, val name: String?, val defaultedOn: Boolean) {
        fun create(modId: String?, type: PackType): Pack? {
            val resourcePath = ModList.get().getModFileById(modId).getFile().findResource("resourcepacks", id)
            return Pack.readMetaAndCreate(
                PackLocationInfo(id, Component.literal(name), PackSource.BUILT_IN, Optional.empty<KnownPack?>()),
                PathPackResources.PathResourcesSupplier(resourcePath),
                type,
                PackSelectionConfig(false, Pack.Position.BOTTOM, false)
            )
        }
    }


    override fun <S> createDynamicRegistry(key: ResourceKey<Registry<S>>, codec: Codec<S>, networkCodec: Codec<S>?) {
        dataPackRegistries.add(DataPackRegistryRegistration(key, codec, networkCodec))
    }
}
