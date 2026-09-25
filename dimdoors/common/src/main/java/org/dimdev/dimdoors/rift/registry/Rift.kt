package org.dimdev.dimdoors.rift.registry

import com.mojang.datafixers.util.Function4
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.CompoundTag
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.rift.RiftUtils
import org.dimdev.dimdoors.rift.registry.RiftRegistry.Companion.instance
import org.dimdev.dimdoors.util.CodecUtils.nullable
import java.util.*
import java.util.function.Consumer
import java.util.function.Function

open class Rift : RegistryVertex {
    lateinit var location: Location
    var isDetached: Boolean = false
    var properties: LinkProperties? = null
    var levelSpaceId: UUID? = null

    constructor(location: Location) {
        this.location = location
        this.world = location.worldId
    }

    constructor(location: Location, isDetached: Boolean, properties: LinkProperties?) {
        this.location = location
        this.world = location.worldId
        this.isDetached = isDetached
        this.properties = properties
    }

    constructor(id: UUID, location: Location, isDetached: Boolean, properties: LinkProperties?) {
        this.location = location
        this.world = location.worldId
        this.isDetached = isDetached
        this.properties = properties
        this.id = id
    }

    private constructor(
        id: UUID?,
        location: Location,
        isDetached: Boolean,
        properties: Optional<LinkProperties?>
    ) : this(id, location, isDetached, properties.orElse(null))

    public override fun sourceGone(source: RegistryVertex) {
        super.sourceGone(source)

        RiftUtils.runIfRiftAt(location) { rift ->
            if (source is Rift) {
                rift.handleSourceGone(source.location)
            }
        }
    }

    public override fun targetGone(target: RegistryVertex) {
        super.targetGone(target)

        RiftUtils.runIfRiftAt(location) { rift ->
            if (target is Rift) {
                rift.handleTargetGone(target.location)
            }
            rift.updateColor()
        }
    }

    public override fun targetMoved(target: RegistryVertex) {
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
        RiftUtils.runIfRiftAt(location, { rift -> rift.updateColor() })

        for (location in instance.getTargets(this.location)) {
            instance.getRift(location).targetChanged(this)
        }
    }

    override val type = RegistryVertices.RIFT

    fun getLocation(): Location {
        return location
    }

    fun setLocation(location: Location?) {
        this.location = location
        if (location != null) this.world = location.worldId
    }

    companion object {
        private val LOGGER: Logger = LogManager.getLogger()
        val MAP_CODEC = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                UUIDUtil.CODEC.fieldOf("id").forGetter(RegistryVertex::id),
                Location.CODEC.fieldOf("location").forGetter(Rift::location),
                Codec.BOOL.optionalFieldOf("isDetached", false).forGetter(Rift::isDetached),
                LinkProperties.CODEC.optionalFieldOf("properties").nullable().forGetter(Rift::properties),
                UUIDUtil.CODEC.optionalFieldOf("level_space_id").nullable().forGetter(Rift::levelSpaceId)
            ).apply(instance) { id, location, isDetached, properties, levelSpaceId ->
                Rift(id, location, isDetached, properties).also { it.levelSpaceId = levelSpaceId }
            }
        }
    }
}
