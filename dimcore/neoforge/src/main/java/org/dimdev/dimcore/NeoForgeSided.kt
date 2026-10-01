package org.dimdev.dimcore

import com.mojang.serialization.Codec
import net.minecraft.core.Holder
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
import org.dimdev.dimcore.api.EntityAttributeProvider
import org.dimdev.dimcore.api.Hooks
import org.dimdev.dimcore.api.ICreativeTabHandler
import org.dimdev.dimcore.api.ModCommon
import org.dimdev.dimcore.api.PackProvider
import org.dimdev.dimcore.api.PacketProvider
import org.dimdev.dimcore.api.PacketRegister
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimcore.api.RegistrationHooks
import org.dimdev.dimcore.api.ServerReloadListenerProvider
import org.dimdev.dimcore.api.SidedImpl
import org.dimdev.dimcore.api.cast
import org.dimdev.dimcore.api.CreativeTabType
import org.dimdev.dimcore.api.DataValueType
import java.util.*
import java.util.function.*

abstract class NeoForgeSided<V : NeoForgeSided<V, T>, T : ModCommon<in V>>(private val bus: IEventBus, common: T) : SidedImpl<V, T>(common) {
    private val toRegister = mutableMapOf<ResourceKey<*>, MutableMap<ResourceLocation, Any>>()
    private val toRegisterHolder = mutableMapOf<ResourceKey<*>, MutableMap<ResourceLocation, HolderRegistration<*>>>()
    private var hooks: Hooks? = null
    private var activeKey: ResourceKey<out Registry<*>>? = null
    private val registerRunnables = mutableMapOf<ResourceKey<*>, MutableList<() -> Unit>>()
    private val registriesToRegister = mutableListOf<Registry<*>>()
    private val dataPackRegistries = mutableListOf<DataPackRegistryRegistration<*>>()


    override fun <T> entryRegister(resourceKey: ResourceKey<Registry<T>>, registry: Registry<T>?): PlatformRegistry.EntryRegister<T> {
        if (resourceKey == PlatformRegistry.DataValuePlatformRegistry.KEY) return object : PlatformRegistry.EntryRegister<T>() {
            private val deferredRegister = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, modId()).also { it.register(bus) }

            override fun <V : T> register(name: String, supplier: () -> V): V {
                val type = supplier() as DataValueType<Any>
                val builder = AttachmentType.builder(Supplier { type.defaultValue() }).serialize(type.codec)
                type.streamCodec?.let(builder::sync)
                val attachment = builder.build()
                val holder = deferredRegister.register(name, Supplier { attachment })
                val value: V by lazy { holder.value().cast() }
                return value;
            }

            override fun createRegistry(): Registry<T> = NeoForgeRegistries.ATTACHMENT_TYPES.cast()
        }

        if (resourceKey == PlatformRegistry.CreativeTabPlatformRegistry.KEY) return object : PlatformRegistry.EntryRegister<T>() {
            private val deferredRegister = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, modId()).also { it.register(bus) }

            override fun <V : T> register(name: String, supplier: () -> V): V {
                val type = supplier() as CreativeTabType
                val holder = deferredRegister.register(name, Supplier { CreativeModeTab.builder().apply(type.block).build() })
                val value: V by lazy { holder.value().cast() }

                return value
            }

            override fun createRegistry(): Registry<T> = BuiltInRegistries.CREATIVE_MODE_TAB.cast()
        }

        return object : PlatformRegistry.EntryRegister<T>() {
            private val deferredRegister = DeferredRegister.create(resourceKey, modId()).also { it.register(bus) }

            override fun <V : T> register(name: String, supplier: () -> V): V {
                val holder = deferredRegister.register(name, supplier)

                val value: V by lazy { holder.value() }

                return value;
            }

            override fun createRegistry(): Registry<T> = registry ?: deferredRegister.makeRegistry {}
        }
    }

    private fun onDataPackRegister(event: DataPackRegistryEvent.NewRegistry) {
        dataPackRegistries.forEach(Consumer { registration: DataPackRegistryRegistration<*>? ->
            registration!!.register(
                event
            )
        })
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

    fun <T> populateHolders(registry: Registry<T>, map: MutableMap<ResourceLocation, HolderRegistration<*>>) {
        map.forEach { (resourceLocation: ResourceLocation?, registration: HolderRegistration<*>?) ->
            (registration as HolderRegistration<T?>).register(
                registry,
                resourceLocation!!
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

    override fun <T : Any, V : T> register(key: ResourceKey<Registry<T>>, id: ResourceLocation, obj: V): V {
        if (key == activeKey) {
            return Registry.register<T, V>(BuiltInRegistries.REGISTRY.get(key.location()) as Registry<T>, id, obj)
        } else {
            val map = this.toRegister.computeIfAbsent(key) { mutableMapOf<ResourceLocation, Any>() }

            map.putIfAbsent(id, obj)

            return obj
        }
    }

    override fun <T : Any, V : T> registerHolder(
        key: ResourceKey<Registry<T>>,
        id: ResourceLocation,
        obj: V
    ): Holder<T> {
        if (key == activeKey) {
            return Registry.registerForHolder<T>(BuiltInRegistries.REGISTRY.get(key.location()) as Registry<T>, id, obj)
        } else {
            val map =
                this.toRegisterHolder.computeIfAbsent(key) { mutableMapOf<ResourceLocation, HolderRegistration<*>>() }
            val registration = map.computeIfAbsent(id) { ignored: ResourceLocation? ->
                HolderRegistration(
                    obj,
                    BindableDeferredHolder.Companion.createBindable<T, T>(key, id)
                )
            } as HolderRegistration<T>

            return registration.holder
        }
    }

    @JvmRecord
    data class HolderRegistration<T>(val obj: T?, val holder: BindableDeferredHolder<T?, out T?>) {
        fun register(registry: Registry<T?>, id: ResourceLocation) {
            Registry.registerForHolder<T?>(registry, id, obj)
            holder.bind()
        }
    }

    class BindableDeferredHolder<R, T : R?>(key: ResourceKey<R?>) : DeferredHolder<R?, T?>(key) {
        fun bind() {
            bind(false)
        }

        companion object {
            private fun <R, T : R?> createBindable(
                registryKey: ResourceKey<out Registry<R?>?>,
                id: ResourceLocation
            ): BindableDeferredHolder<R?, T?> {
                return BindableDeferredHolder<R?, T?>(ResourceKey.create<R?>(registryKey, id))
            }
        }
    }

    private fun <T> runHooks(registry: Registry<T>, handlers: List<(ResourceLocation, Any?) -> Unit>) {
        registry.entrySet().toList().forEach { (key, value) -> handlers.forEach { it(key.location(), value) } }
        registry.addCallback(AddCallback<T> { callbackRegistry, _, key, value ->
            val previousKey = activeKey
            activeKey = callbackRegistry.key()
            try {
                handlers.forEach { it(key.location(), value) }
            } finally {
                activeKey = previousKey
            }
        })
    }


    override fun <T> createRegistry(
        key: ResourceKey<Registry<T>>,
        defaultId: ResourceLocation,
        sync: Boolean
    ): Registry<T> {
        val registry = RegistryBuilder<T?>(key).sync(sync).defaultKey(defaultId).create()

        registriesToRegister.add(registry)

        return registry
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

        bus.addListener<NewRegistryEvent?>(Consumer { event: NewRegistryEvent? ->
            registriesToRegister.forEach(Consumer { registry: Registry<*>? ->
                event!!.register(
                    registry
                )
            })
        })

        bus.addListener<RegisterEvent>(EventPriority.LOWEST, { event ->
            val key = event.registryKey
            this@NeoForgeSided.activeKey = key
            try {
                registerRunnables.remove(key)?.forEach({ obj -> obj.invoke() })

                val registry = event.registry

                toRegister[key]?.takeIf { it.isNotEmpty() }.also { populate(registry, it) }

                val holderMap = toRegisterHolder.remove(key)
                if (holderMap != null && !holderMap.isEmpty()) {
                    populateHolders(registry, holderMap)
                }

                hooks?.onEntry?.get(key)?.let { runHooks(registry, it) }
            } finally {
                this@NeoForgeSided.activeKey = null
            }
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

        common!!.init(self()!!)
        if (mod is RegistrationHooks) hooks = Hooks().also(mod::registrationHooks)
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
        TODO("Not yet implemented")
    }

    override fun <T> createDynamicRegistry(
        key: ResourceKey<Registry<T>>,
        codec: Codec<T>,
        networkCodec: Codec<T?>
    ) {
        dataPackRegistries.add(DataPackRegistryRegistration<T?>(key, codec, networkCodec))
    }
}
