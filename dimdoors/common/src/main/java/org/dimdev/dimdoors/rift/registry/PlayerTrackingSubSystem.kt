package org.dimdev.dimdoors.rift.registry

import com.mojang.datafixers.Products.P1
import com.mojang.serialization.Codec.unboundedMap
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import net.minecraft.server.level.ServerLevel
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.util.CodecUtils.nullable
import org.dimdev.dimdoors.world.pocket.type.Pocket
import java.util.*

abstract class PlayerTrackingSubSystem<V, P : Pocket<*, *>, T : PlayerTrackingSubSystem<V, P, T>>(var locations: MutableMap<UUID, PlayerRiftConnection>) : SubSystem<T>(), VertexProvider {
    protected val logger: Logger = LogManager.getLogger()


    abstract fun getPocketFromKey(uuid: V?): P?

    override fun collectVertices(): MutableList<out RegistryVertex> {
        val vertices = mutableListOf<PlayerRiftPointer>()
        for (connection in this.locations.values) {
            connection.entrance?.let(::PlayerRiftPointer)?.run(vertices::add)
            connection.exit?.let(::PlayerRiftPointer)?.run(vertices::add)
        }

        return vertices
    }

    private fun getPlayerConnection(uuid: UUID): PlayerRiftConnection = this.locations.computeIfAbsent(uuid) { _ -> PlayerRiftConnection() }


    fun getEntrance(uuid: UUID?): UUID? = this.locations[uuid]?.entrance

    fun getEntranceLocation(uuid: UUID?): Location? = this.getPointedRiftLocation(this.getEntrance(uuid))


    protected fun setEntrance(uuid: UUID, entrance: UUID?) {

        val connection = getPlayerConnection(uuid)
        val previous = connection.entrance
        connection.entrance = entrance
        this.removeIfEmpty(uuid, connection)

        if (previous != entrance) {
            this.setDirty()
        }
    }

    open fun setEntrance(uuid: UUID, entrance: Location?) {
        this.setEntrance(uuid, this.createPlayerRiftPointer(this.getEntrance(uuid), entrance))
    }

    fun getExit(uuid: UUID?): UUID? = this.locations[uuid]?.exit


    fun getExitLocation(uuid: UUID?): Location? {
        return this.getPointedRiftLocation(this.getExit(uuid))
    }

    protected fun setExit(uuid: UUID, exit: UUID?) {
        val connection = this.locations.computeIfAbsent(uuid) { _ -> PlayerRiftConnection() }
        val previous = connection.exit
        connection.exit = exit
        this.removeIfEmpty(uuid, connection)

        if (previous != exit) {
            this.setDirty()
        }
    }

    open fun setExit(uuid: UUID, exit: Location?) {
        this.setExit(uuid, this.createPlayerRiftPointer(this.getExit(uuid), exit))
    }

    private fun createPlayerRiftPointer(currentPointer: UUID?, location: Location?): UUID? {

        if (currentPointer != null) RiftGraph.getInstance().removeVertex(currentPointer)

        if (location == null) return null

        val target = RiftRegistry.instance.getRiftOrPlaceholder(location)
        val pointer = PlayerRiftPointer()
        RiftGraph.getInstance().addVertex(pointer)
        RiftGraph.getInstance().addEdge(pointer, target)
        target.markDirty()
        return pointer.id
    }

    private fun getPointedRiftLocation(pointer: UUID?): Location? = RiftGraph.getInstance().followPointer(pointer)?.let(RiftRegistry.instance::findRift)?.location

    private fun removeIfEmpty(uuid: UUID?, connection: PlayerRiftConnection) {
        if (connection.entrance == null && connection.exit == null) {
            this.locations.remove(uuid)
        }
    }

    abstract fun setNewPocket(uuid: UUID?, key: V?, pocket: P?)

    abstract fun isCorrectDimensionForPocket(world: ServerLevel): Boolean

    abstract fun setCurrentKey(uuid: UUID?, key: V?)

    class PlayerRiftConnection (var entrance: UUID? = null, var exit: UUID? = null) {
        companion object {
            val CODEC = RecordCodecBuilder.create { instance -> instance.group(
                    UUIDUtil.CODEC.optionalFieldOf("entranceId").nullable().forGetter(PlayerRiftConnection::entrance),
                    UUIDUtil.CODEC.optionalFieldOf("exitId").nullable().forGetter(PlayerRiftConnection::exit)
                ).apply(instance, ::PlayerRiftConnection)
            }


            val MAP_CODEC = unboundedMap(UUIDUtil.STRING_CODEC, CODEC)
        }
    }

    abstract fun getKeyFromPlayer(playerUUID: UUID?): V?

    open fun getPocketFromPlayer(uuid: UUID?): P? = getKeyFromPlayer(uuid)?.let(this::getPocketFromKey)

    abstract fun invalidKeyErrorMessage(): String
    abstract fun invalidPocketErrorMessage(): String

    fun resolveEntrance(playerUUID: UUID?): Location? {
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

        val entrance = this.getEntranceLocation(playerUUID)
        if (entrance != null && PocketRegistry.instance.getPocketEntrances(pocket).contains(entrance)) {
            return entrance
        }

        return PocketRegistry.instance.getPocketEntrance(pocket)
    }

    companion object {
        protected inline fun <reified V, P : Pocket<*, *>, T : PlayerTrackingSubSystem<V, P, T>> commonFields(instance: RecordCodecBuilder.Instance<T>): P1<RecordCodecBuilder.Mu<T>, MutableMap<UUID, PlayerRiftConnection>> {
            return instance.group(PlayerRiftConnection.MAP_CODEC.fieldOf("locations").forGetter(PlayerTrackingSubSystem<V, P, T>::locations))
        }
    }
}
