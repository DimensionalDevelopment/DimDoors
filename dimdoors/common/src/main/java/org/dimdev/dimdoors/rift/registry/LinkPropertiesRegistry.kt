package org.dimdev.dimdoors.rift.registry

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import org.dimdev.dimdoors.api.util.unboundedMap
import org.dimdev.dimdoors.rift.registry.SubsystemTypes.PROPERTIES
import java.util.*

class LinkPropertiesRegistry(val propertiesMap: MutableMap<UUID, Entry> = mutableMapOf()) : SubSystem<LinkPropertiesRegistry>() {
    data class Entry(var properties: LinkProperties? = null, var isDetached: Boolean = false) {
        companion object {
            val CODEC = RecordCodecBuilder.create { instance -> instance.group(
                LinkProperties.CODEC.fieldOf("properties").forGetter(Entry::properties),
                Codec.BOOL.fieldOf("isDetached").forGetter(Entry::isDetached)
                ).apply(instance, ::Entry)
            }
        }
    }

    override fun type(): Type<LinkPropertiesRegistry> = SubsystemTypes.PROPERTIES

    private fun getOrCreate(rift: UUID): Entry {
        return propertiesMap.computeIfAbsent(rift) { Entry() }
    }

    fun setDetached(rift: UUID, detached: Boolean) {
        getOrCreate(rift).isDetached = detached
    }

    fun setProperties(rift: UUID, properties: LinkProperties?) {
        getOrCreate(rift).properties = properties
    }

    fun getProperties(rift: UUID): LinkProperties? {
        return propertiesMap[rift]?.properties
    }

    companion object {
        val CODEC = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                UUIDUtil.STRING_CODEC.unboundedMap(Entry.CODEC).fieldOf("properties_map")
                    .forGetter(LinkPropertiesRegistry::propertiesMap)
            ).apply(instance, ::LinkPropertiesRegistry)
        }

        val instance get() = getInstance(PROPERTIES)!!

        fun registerEvents() {
            RiftRegistry.RiftEvents.RIFT_REMOVED.register { id, _ ->
                if (instance.propertiesMap.remove(id) != null) instance.setDirty()
            }
        }
    }
}
