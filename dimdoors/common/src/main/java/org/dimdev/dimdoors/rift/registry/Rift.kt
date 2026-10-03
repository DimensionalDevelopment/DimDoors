package org.dimdev.dimdoors.rift.registry

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.rift.RiftUtils
import org.dimdev.dimdoors.rift.registry.RiftRegistry.Companion.instance
import org.dimdev.dimdoors.api.util.nullableForGetter
import kotlin.jvm.optionals.getOrNull
import java.util.*

open class Rift(location: Location) : RegistryVertex() {
    var location: Location = location
        set(value) {
            field = value
            this.world = value.worldId
        }

    var isDetached: Boolean = false
    var properties: LinkProperties? = null
    var levelSpaceId: UUID? = null

    init {
        this.world = location.worldId
    }

    constructor(location: Location, isDetached: Boolean, properties: LinkProperties?) : this(location) {
        this.isDetached = isDetached
        this.properties = properties
    }

    constructor(id: UUID, location: Location, isDetached: Boolean, properties: LinkProperties?) : this(location) {
        this.isDetached = isDetached
        this.properties = properties
        this.id = id
    }

    override fun sourceGone(source: RegistryVertex) {
        super.sourceGone(source)

        RiftUtils.runIfRiftAt(location) { rift ->
            if (source is Rift) {
                rift.handleSourceGone(source.location)
            }
        }
    }

    override fun targetGone(target: RegistryVertex) {
        super.targetGone(target)

        RiftUtils.runIfRiftAt(location) { rift ->
            if (target is Rift) {
                rift.handleTargetGone(target.location)
            }
            rift.updateColor()
        }
    }

    override fun targetMoved(target: RegistryVertex) {
        super.sourceAdded(target)

        RiftUtils.runIfRiftAt(location) { rift ->
            if (target is Rift) {
                rift.handleSourceMoved(target.location)
            }
            rift.updateColor()
        }
    }

    open fun targetChanged(target: RegistryVertex) {
        LOGGER.debug("Rift {} notified of target {} having changed. Updating color.", this, target)
        RiftUtils.runIfRiftAt(location) { rift -> rift.updateColor() }
    }

    open fun markDirty() {
        RiftUtils.runIfRiftAt(location) { rift -> rift.updateColor() }

        for (location in instance.getTargets(this.location)) {
            instance.getRift(location).targetChanged(this)
        }
    }

    override val type: MapCodec<out Rift> = RegistryVertices.RIFT

    companion object {
        private val LOGGER: Logger = LogManager.getLogger()
        val MAP_CODEC: MapCodec<Rift> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                UUIDUtil.CODEC.fieldOf("id").forGetter(RegistryVertex::id),
                Location.CODEC.fieldOf("location").forGetter(Rift::location),
                Codec.BOOL.optionalFieldOf("isDetached", false).forGetter(Rift::isDetached),
                LinkProperties.CODEC.optionalFieldOf("properties").nullableForGetter(Rift::properties),
                UUIDUtil.CODEC.optionalFieldOf("level_space_id").nullableForGetter(Rift::levelSpaceId)
            ).apply(instance) { id, location, isDetached, properties, levelSpaceId ->
                Rift(id, location, isDetached, properties.getOrNull()).also { it.levelSpaceId = levelSpaceId.getOrNull() }
            }
        }
    }
}
