package org.dimdev.dimcore

import com.mojang.serialization.Codec
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate
import net.fabricmc.fabric.api.event.registry.DynamicRegistries
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.ResourcePackActivationType
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import org.dimdev.dimcore.api.*

abstract class FabricSided<V : FabricSided<V, S>, S : ModCommon<in V>>(common: S) : SidedImpl<V, S>(common), ModInitializer {
    override fun onInitialize() {
        common.init(self())

        val mod: Any = common
        if (mod is EntityAttributeProvider) mod.registerEntityAttributes(FabricEntityAttributeRegister)
        if (mod is PacketProvider) mod.registerPackets(FabricPacketRegister)
        if (mod is RegistrationHooks) Hooks().also(mod::registrationHooks).onEntry.forEach { (key, handlers) -> hookRegistry<Any>(key, handlers) }
        if (mod is PackProvider) mod.registerPacks(::addPack)
        if (mod is ServerReloadListenerProvider) mod.registerServerReloadListeners(::registerServerLoader)
    }

    private fun <T> hookRegistry(key: ResourceKey<*>, handlers: List<(ResourceLocation, Any?) -> Unit>) {
        val registry = BuiltInRegistries.REGISTRY.get(key.location())!!.cast<Registry<T>>()
        registry.entrySet().toList().forEach { (entryKey, value) -> handlers.forEach { it(entryKey.location(), value) } }
        RegistryEntryAddedCallback.event(registry).register { _, id, value -> handlers.forEach { it(id, value) } }
    }

    override fun <T: Any, V : T> register(key: ResourceKey<Registry<T>>, id: ResourceLocation, obj: V): V {
        val registry = BuiltInRegistries.REGISTRY.get(key.location())!!.cast<Registry<T>>()
        Registry.register(registry, id, obj)
        return obj
    }

    override fun <U: Any, R : U> registerHolder(
        key: ResourceKey<Registry<U>>,
        id: ResourceLocation,
        obj: R
    ): Holder<U> {
        val registry = BuiltInRegistries.REGISTRY.get(key.location())?.cast<Registry<U>>()
        requireNotNull(registry) { "Unknown registry: " + key.location() }

        return Registry.registerForHolder<U>(registry, id, obj)
    }

    private data class PlayPayloadHandlerReturnable<T : CustomPacketPayload>(val packetFunction: (T, ServerPlayer) -> CustomPacketPayload?) : ServerPlayNetworking.PlayPayloadHandler<T> {
        override fun receive(payload: T, context: ServerPlayNetworking.Context) {
            packetFunction.invoke(payload, context.player()).also { context.responseSender().sendPacket(it) }
        }
    }

    override fun modify(tab: ResourceKey<CreativeModeTab>, filler: (ICreativeTabHandler.CreativeTabOutput) -> Unit) {
        ItemGroupEvents.modifyEntriesEvent(tab).register { entries ->
            filler(object : ICreativeTabHandler.CreativeTabOutput {
                override fun acceptAfter(after: ItemStack, stack: ItemStack, visibility: CreativeModeTab.TabVisibility) = if (after.isEmpty) entries.accept(stack, visibility) else entries.addAfter(after, listOf(stack), visibility)
                override fun acceptBefore(before: ItemStack, stack: ItemStack, visibility: CreativeModeTab.TabVisibility) = if (before.isEmpty) entries.accept(stack, visibility) else entries.addBefore(before, listOf(stack), visibility)
            })
        }
    }

    private fun registerServerLoader(name: String, loadAfterTags: Boolean, consumer: (HolderLookup.Provider, ResourceManager) -> Unit) {
        val id = ResourceLocation.fromNamespaceAndPath(common.modId, name)
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(id) { provider ->
            FabricResourceLoader(
                id,
                { manager: ResourceManager -> consumer.invoke(provider, manager) },
                if (loadAfterTags) mutableListOf(ResourceReloadListenerKeys.TAGS) else mutableListOf()
            )
        }
    }

    override fun <T> createDynamicRegistry(key: ResourceKey<Registry<T>>, codec: Codec<T>, networkCodec: Codec<T>?) = if (networkCodec != null) DynamicRegistries.registerSynced<T>(key, codec, networkCodec) else DynamicRegistries.register<T>(key, codec)

    private fun addPack(type: PackType, id: String, name: String, defaultedOn: Boolean) {
        ResourceManagerHelper.registerBuiltinResourcePack(
            ResourceLocation.fromNamespaceAndPath(common.modId, id),
            FabricLoader.getInstance().getModContainer(common.modId).get(),
            Component.literal(name),
            if (defaultedOn) ResourcePackActivationType.DEFAULT_ENABLED else ResourcePackActivationType.NORMAL
        )
    }

    override fun <T> entryRegister(
        resourceKey: ResourceKey<Registry<T>>,
        registry: Registry<T>?
    ): PlatformRegistry.EntryRegister<T> {
        if (resourceKey == PlatformRegistry.DataValuePlatformRegistry.KEY) return object : PlatformRegistry.EntryRegister<T>() {
            override fun createRegistry(): Registry<T> =
                (BuiltInRegistries.REGISTRY.get(resourceKey.location()) ?: FabricRegistryBuilder.createSimple(resourceKey).buildAndRegister()).cast()

            override fun <V : T> register(name: String, supplier: () -> V): V {
                val type = supplier() as DataValueType<Any>
                val attachment = AttachmentRegistry.create<Any?>(ResourceLocation.fromNamespaceAndPath(common.modId, name)) { builder ->
                    builder.initializer(type.defaultValue)
                    builder.persistent(type.codec)
                    type.streamCodec?.let { builder.syncWith(it, AttachmentSyncPredicate.all()) }
                }
                return Holder.direct(attachment).cast()
            }
        }

        if (resourceKey == PlatformRegistry.CreativeTabPlatformRegistry.KEY) return object : PlatformRegistry.EntryRegister<T>() {
            override fun createRegistry(): Registry<T> = BuiltInRegistries.CREATIVE_MODE_TAB.cast()

            override fun <V : T> register(name: String, supplier: () -> V): V {
                val type = supplier() as CreativeTabType
                return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(common.modId, name), FabricItemGroup.builder().apply(type.block).build()).cast()
            }
        }

        return object : PlatformRegistry.EntryRegister<T>() {
            private val target: Registry<T> by lazy { registry ?: FabricRegistryBuilder.createSimple(resourceKey).buildAndRegister() }

            override fun createRegistry(): Registry<T> = target

            override fun <V : T> register(name: String, supplier: () -> V): V {
                val value = supplier()
                Registry.register(target, ResourceLocation.fromNamespaceAndPath(common.modId, name), value)
                return value
            }
        }
    }
}
