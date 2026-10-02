package org.dimdev.dimcore.api

import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeSupplier

interface ModCommon<T : ISided<*>> {
    fun initRegistries(sided: T) {}
    fun init(sided: T)
    val modId: String
}

interface EntityAttributeProvider {
    fun registerEntityAttributes(register: EntityAttributeRegister)
}

interface PacketProvider {
    fun registerPackets(packetRegister: PacketRegister)
}

interface RegistrationHooks {
    fun registrationHooks(hooks: Hooks)
}

interface PackProvider {
    fun registerPacks(register: PackRegister)
}

fun interface PackRegister {
    fun addPack(type: PackType, id: String, name: String, defaultedOn: Boolean)
}

interface ServerReloadListenerProvider {
    fun registerServerReloadListeners(register: ServerReloadListenerRegister)
}

fun interface ServerReloadListenerRegister {
    fun register(name: String, loadAfterTags: Boolean, consumer: (HolderLookup.Provider, ResourceManager) -> Unit)
    fun register(name: String, consumer: (HolderLookup.Provider, ResourceManager) -> Unit) = register(name, false, consumer)
}

class Hooks {
    val onEntry = mutableMapOf<ResourceKey<*>, MutableList<(ResourceLocation, Any?) -> Unit>>()

    fun <T> onEachEntry(key: ResourceKey<Registry<T>>, block: (ResourceLocation, T) -> Unit) {
        onEntry.getOrPut(key) { mutableListOf() } += { id, value -> block(id, value as T) }
    }
}

interface PacketRegister {
    fun <T : CustomPacketPayload> registerServerPacket(type: CustomPacketPayload.Type<T>, streamCodec: StreamCodec<RegistryFriendlyByteBuf, T>, function: (T, ServerPlayer) -> CustomPacketPayload?)
    fun <T : CustomPacketPayload> registerClientPacket(type: CustomPacketPayload.Type<T>, streamCodec: StreamCodec<RegistryFriendlyByteBuf, T>, consumer: (T) -> Unit)
}

fun interface EntityAttributeRegister {
    fun register(type: EntityType<out LivingEntity>, attributes: () -> AttributeSupplier.Builder)
    fun register(type: Holder<out EntityType<out LivingEntity>>, attributes: () -> AttributeSupplier.Builder) = register(type.value(), attributes)
}