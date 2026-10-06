package org.dimdev.dimdoors.rift.registry

import com.mojang.datafixers.Products.P1
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.server.level.ServerLevel
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.rift.targets.LocationProvider
import org.dimdev.dimdoors.util.UUIDExtensions
import org.dimdev.dimdoors.world.pocket.type.Pocket
import java.util.*

abstract class PlayerTrackingSubSystem<V, P : Pocket<*, *>, T : PlayerTrackingSubSystem<V, P, T>>(var locations: MutableMap<UUID, UUID>) : SubSystem<T>() {
    protected val logger: Logger = LogManager.getLogger()


    abstract fun getPocketFromKey(uuid: V?): P?

    abstract val entranceRegistryVertex: PlayerTrackerPointer
    abstract val exitRegistryVertex: PlayerTrackerPointer

    fun getRift(trackingId: UUID) = locations[trackingId]

    private fun replaceRiftId(currentPointer: UUID, provider: LocationProvider?): UUID? {
        RiftGraph.getInstance().removeVertex(currentPointer, null)

        val location = provider?.providedLocation ?: return null

        return RiftRegistry.instance.getRiftOrPlaceholder(location)
    }


    fun getRift(playerId: UUID, variant: PlayerTrackerPointer.Variant) = variant.getTrackingId(playerId, type()).let(locations::get)

    fun setRift(uuid: UUID, variant: PlayerTrackerPointer.Variant, location: LocationProvider?) {
        val trackingId = variant.getTrackingId(uuid, type())

        val riftId = replaceRiftId(trackingId, location)

        setRift(uuid, variant, riftId)
    }

    open fun setRift(playerId: UUID, variant: PlayerTrackerPointer.Variant, riftId: UUID?) = setRift(variant.getTrackingId(playerId, type()), riftId)

    protected fun setRift(trackingId: UUID, riftId: UUID?) {

        val previous = locations.remove(trackingId)

        if(riftId != null) locations[trackingId] = riftId

        if (previous != riftId) {
            this.setDirty()
        }
    }

    abstract fun setNewPocket(uuid: UUID?, key: V?, pocket: P?)

    abstract fun isCorrectDimensionForPocket(world: ServerLevel): Boolean

    abstract fun setCurrentKey(uuid: UUID?, key: V?)

    abstract fun getKeyFromPlayer(playerUUID: UUID?): V?

    open fun getPocketFromPlayer(uuid: UUID?): P? = getKeyFromPlayer(uuid)?.let(this::getPocketFromKey)

    abstract fun invalidKeyErrorMessage(): String
    abstract fun invalidPocketErrorMessage(): String

    fun resolveEntrance(playerUUID: UUID): Location? {
        val key = getKeyFromPlayer(playerUUID)

        if(key == null) {
            logger.warn(invalidKeyErrorMessage(), playerUUID)
            return null
        }

        val pocket = this.getPocketFromKey(key)
        if (pocket == null) {
            logger.warn(invalidPocketErrorMessage(), playerUUID, key)
            return null
        }

        val entrance = this.getLocation(playerUUID, PlayerTrackerPointer.Variant.Entrance)
        if (entrance != null && PocketRegistry.instance.getPocketEntrances(pocket).contains(entrance)) {
            return entrance
        }

        return PocketRegistry.instance.getPocketEntrance(pocket)
    }

    fun getLocation(
        playerId: UUID,
        variant: PlayerTrackerPointer.Variant
    ): Location? = getRift(playerId, variant)?.let(RiftRegistry.instance::locationOf)

    companion object {
        fun <V, P : Pocket<*, *>, T : PlayerTrackingSubSystem<V, P, T>> commonFields(instance: RecordCodecBuilder.Instance<T>): P1<RecordCodecBuilder.Mu<T>, MutableMap<UUID, UUID>> {
            return instance.group(UUIDExtensions.MAP_CODEC.fieldOf("locations").forGetter(PlayerTrackingSubSystem<V, P, T>::locations))
        }
    }
}
