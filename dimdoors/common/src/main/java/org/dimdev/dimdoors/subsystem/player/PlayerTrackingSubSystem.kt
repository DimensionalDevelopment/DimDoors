package org.dimdev.dimdoors.subsystem.player

import com.mojang.datafixers.Products.P1
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.StringRepresentable
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimdoors.pockets.type.Pocket
import org.dimdev.dimdoors.rift.block.LocationRiftRegistry
import org.dimdev.dimdoors.rift.targets.LocationProvider
import org.dimdev.dimdoors.subsystem.graph.RiftGraph
import org.dimdev.dimdoors.subsystem.pocket.PocketRegistry
import org.dimdev.dimdoors.subsystem.registry.SubSystem
import org.dimdev.dimdoors.util.Location
import org.dimdev.dimdoors.util.UUIDExtensions
import org.dimdev.dimdoors.util.UUIDExtensions.derive
import java.util.*

abstract class PlayerTrackingSubSystem<V, P : Pocket<*, *>, T : PlayerTrackingSubSystem<V, P, T>>(var locations: MutableMap<UUID, UUID>) : SubSystem<T>() {
    protected val logger: Logger = LogManager.getLogger()


    abstract fun getPocketFromKey(uuid: V?): P?

    fun getRift(trackingId: UUID) = locations[trackingId]

    private fun replaceRiftId(currentPointer: UUID, provider: LocationProvider?): UUID? {
        RiftGraph.getInstance().removeVertex(currentPointer)

        val location = provider?.providedLocation ?: return null

        return LocationRiftRegistry.instance.getRiftOrPlaceholder(location)
    }


    fun getRift(playerId: UUID, variant: Variant) = variant.getTrackingId(playerId, type()).let(locations::get)

    fun setRift(uuid: UUID, variant: Variant, location: LocationProvider?) {
        val trackingId = variant.getTrackingId(uuid, type())

        val riftId = replaceRiftId(trackingId, location)

        setRift(uuid, variant, riftId)
    }

    open fun setRift(playerId: UUID, variant: Variant, riftId: UUID?) = setRift(variant.getTrackingId(playerId, type()), riftId)

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

        val entrance = this.getLocation(playerUUID, Variant.Entrance)
        if (entrance != null && PocketRegistry.instance.getPocketEntrances(pocket).contains(entrance)) {
            return entrance
        }

        return PocketRegistry.instance.getPocketEntrance(pocket)
    }

    fun getLocation(
        playerId: UUID,
        variant: Variant
    ): Location? = getRift(playerId, variant)?.let(LocationRiftRegistry.instance::locationOf)

    enum class Variant(val serialized: String) : StringRepresentable {
        Entrance("entrance"), Exit("exit");

        override fun getSerializedName(): String = serialized
        fun getTrackingId(playerId: UUID, type: Type<*>): UUID = playerId.derive(serialized, type.name)
    }

    companion object {
        fun <V, P : Pocket<*, *>, T : PlayerTrackingSubSystem<V, P, T>> commonFields(instance: RecordCodecBuilder.Instance<T>): P1<RecordCodecBuilder.Mu<T>, MutableMap<UUID, UUID>> {
            return instance.group(UUIDExtensions.MAP_CODEC.fieldOf("locations").forGetter(PlayerTrackingSubSystem<V, P, T>::locations))
        }
    }
}
