package org.dimdev.dimdoors.subsystem.graph

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import org.dimdev.dimdoors.rift.targets.LocationProvider
import org.dimdev.dimdoors.util.Location
import java.util.*

data class Vertex(val id: UUID, val type: RegistryVertex) : LocationProvider {
    override val providedLocation: Location? get() = type.getLocation(id)

    companion object {
        val CODEC: Codec<Vertex> = RecordCodecBuilder.create { instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("id").forGetter(Vertex::id),
                RegistryVertex.CODEC.fieldOf("type").forGetter(Vertex::type)
            ).apply(instance, ::Vertex)
        }
    }
}