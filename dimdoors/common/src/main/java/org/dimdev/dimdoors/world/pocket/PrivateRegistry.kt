package org.dimdev.dimdoors.world.pocket

import com.google.common.collect.HashBiMap
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import net.minecraft.server.level.ServerLevel
import org.dimdev.dimdoors.rift.registry.*
import org.dimdev.dimdoors.util.CodecUtils.unboundedMap
import org.dimdev.dimdoors.world.ModDimensions
import org.dimdev.dimdoors.world.pocket.type.PrivatePocket
import java.util.*

class PrivateRegistry(
    locations: MutableMap<UUID, UUID> = mutableMapOf(),
    private var privatePockets: HashBiMap<UUID, PocketInfo> = HashBiMap.create()
) : PlayerTrackingSubSystem<UUID, PrivatePocket, PrivateRegistry>(locations) {

    override fun getKeyFromPlayer(playerUUID: UUID?): UUID? = playerUUID

    override fun type(): Type<PrivateRegistry> = SubsystemTypes.PRIVATE

    override val entranceRegistryVertex: PlayerTrackerPointer get() = RegistryVertices.PRIVATE_ENTRANCE
    override val exitRegistryVertex: PlayerTrackerPointer get() = RegistryVertices.PRIVATE_EXIT

    override fun getPocketFromPlayer(uuid: UUID?): PrivatePocket? {
        requireNotNull(uuid) { "playerUUID" }
        return privatePockets[uuid]?.let { PocketRegistry.instance.getPocket(it, PrivatePocket::class.java) }
    }

    override fun getPocketFromKey(uuid: UUID?): PrivatePocket? = getPocketFromPlayer(uuid)

    fun setPrivatePocketID(playerUUID: UUID, pocket: PrivatePocket) {
        val info = PocketInfo(pocket.world, pocket.id)

        val existingOwner = privatePockets.inverse()[info]
        check(existingOwner == null || existingOwner == playerUUID) { "Private pocket ${info.world.location()}:${info.id} is already assigned to $existingOwner, cannot assign to $playerUUID" }

        if (privatePockets.put(playerUUID, info) != info) setDirty()
    }

    override fun invalidKeyErrorMessage(): String = "Cannot resolve private entrance for {} because their uuid isn't being tracked."

    override fun invalidPocketErrorMessage(): String = "Cannot resolve private entrance for {} at {} because no private pocket is tracked."

    override fun setNewPocket(uuid: UUID?, key: UUID?, pocket: PrivatePocket?) = setPrivatePocketID(requireNotNull(uuid), requireNotNull(pocket))

    override fun isCorrectDimensionForPocket(world: ServerLevel): Boolean = ModDimensions.isPrivatePocketDimension(world)

    override fun setCurrentKey(uuid: UUID?, key: UUID?) {}

    companion object {
        @JvmField
        val CODEC: MapCodec<PrivateRegistry> = RecordCodecBuilder.mapCodec { instance -> commonFields(instance)
            .and(unboundedMap(UUIDUtil.STRING_CODEC, PocketInfo.CODEC, HashBiMap<UUID, PocketInfo>::create).fieldOf("private_pockets").forGetter(PrivateRegistry::privatePockets))
            .apply(instance, ::PrivateRegistry)
        }

        @JvmStatic
        val instance: PrivateRegistry get() = getInstance(SubsystemTypes.PRIVATE)!!
    }
}
