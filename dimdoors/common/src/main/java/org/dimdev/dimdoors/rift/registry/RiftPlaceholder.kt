package org.dimdev.dimdoors.rift.registry

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import org.dimdev.dimdoors.api.util.Location
import java.util.*

class RiftPlaceholder(location: Location) : Rift(location) {
    private constructor(id: UUID, location: Location) : this(location) {
        this.id = id
    }

    override fun sourceGone(source: RegistryVertex) {}

    override fun targetGone(target: RegistryVertex) {}

    override fun sourceAdded(source: RegistryVertex) {}

    override fun targetAdded(target: RegistryVertex) {}

    override fun targetChanged(target: RegistryVertex) {}

    override fun markDirty() {}

    override val type get() = RegistryVertices.RIFT_PLACEHOLDER

    companion object {
        // TODO: don't extend rift
        val MAP_CODEC: MapCodec<RiftPlaceholder> = RecordCodecBuilder.mapCodec { instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("id").forGetter(RegistryVertex::id),
            Location.CODEC.fieldOf("location").forGetter(RiftPlaceholder::location)
                ).apply(instance, ::RiftPlaceholder)
        }
    }
}
