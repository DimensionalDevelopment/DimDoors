package org.dimdev.dimdoors.world.pocket

import com.google.common.collect.HashBiMap
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.rift.registry.PlayerTrackingSubSystem
import org.dimdev.dimdoors.rift.registry.PlayerTrackingSubSystem.PlayerRiftConnection
import org.dimdev.dimdoors.rift.registry.PocketRegistry
import org.dimdev.dimdoors.rift.registry.SubsystemTypes
import org.dimdev.dimdoors.util.CodecUtils.unboundedMap
import org.dimdev.dimdoors.world.ModDimensions
import org.dimdev.dimdoors.world.pocket.type.Pocket
import org.dimdev.dimdoors.world.pocket.type.PrivatePocket
import java.util.*

class PrivateRegistry(
    locations: MutableMap<UUID, PlayerRiftConnection> = mutableMapOf(),
    private var privatePockets: HashBiMap<UUID, PocketInfo> = HashBiMap.create()
) : PlayerTrackingSubSystem<UUID, PrivatePocket, PrivateRegistry>(locations) {

    override fun setEntrance(uuid: UUID, entrance: Location?) {
        logger.debug("Setting private pocket entrance for {} to {}.", uuid, entrance)
        super.setEntrance(uuid, entrance)
    }

    override fun setExit(uuid: UUID, exit: Location?) {
        logger.debug("Setting private pocket exit for {} to {}.", uuid, exit)
        super.setExit(uuid, exit)
    }

    override fun getKeyFromPlayer(playerUUID: UUID?): UUID? = playerUUID

    override fun type(): Type<PrivateRegistry> = SubsystemTypes.PRIVATE.value()

    override fun getPocketFromPlayer(uuid: UUID?): PrivatePocket? {
        requireNotNull(uuid) { "playerUUID" }
        return privatePockets[uuid]?.let { PocketRegistry.instance.getPocket(it, PrivatePocket::class.java) }
    }

    override fun getPocketFromKey(uuid: UUID?): PrivatePocket? = getPocketFromPlayer(uuid)

    fun setPrivatePocketID(playerUUID: UUID, pocket: PrivatePocket) {
        val info = PocketInfo(pocket.world, pocket.getId())

        val existingOwner = privatePockets.inverse()[info]
        check(existingOwner == null || existingOwner == playerUUID) { "Private pocket ${info.world.location()}:${info.id} is already assigned to $existingOwner, cannot assign to $playerUUID" }

        if (privatePockets.put(playerUUID, info) != info) setDirty()
    }

    fun setPrivatePocketID(playerUUID: UUID, pocket: Pocket<*, *>) {
        require(pocket is PrivatePocket) { "Cannot assign non-private pocket as private pocket: $pocket" }
        setPrivatePocketID(playerUUID, pocket)
    }

    fun removePrivatePocket(pocket: Pocket<*, *>): Boolean = removePrivatePocket(pocket.world, pocket.getId())

    fun removePrivatePocket(world: ResourceKey<Level>, id: Int): Boolean {
        privatePockets.inverse().remove(PocketInfo(world, id)) ?: return false
        setDirty()
        return true
    }

    fun getPrivatePocketOwner(pocket: Pocket<*, *>): UUID? = privatePockets.inverse()[PocketInfo(pocket.world, pocket.getId())]

    fun removePrivatePocketOwner(playerUUID: UUID): Boolean {
        privatePockets.remove(playerUUID) ?: return false
        setDirty()
        return true
    }

    fun removeStalePrivatePocketMapping(playerUUID: UUID): Boolean {
        val pocket = privatePockets[playerUUID] ?: return false

        if (PocketRegistry.instance.getPocket(pocket, PrivatePocket::class.java) != null) return false

        privatePockets.remove(playerUUID)
        setDirty()
        logger.warn("Removed stale private pocket mapping {} -> {}:{}", playerUUID, pocket.world.location(), pocket.id)
        return true
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
        val instance: PrivateRegistry get() = getInstance(SubsystemTypes.PRIVATE.value())!!
    }
}
