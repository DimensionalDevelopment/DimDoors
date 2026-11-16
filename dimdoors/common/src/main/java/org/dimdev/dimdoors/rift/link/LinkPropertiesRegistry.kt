package org.dimdev.dimdoors.rift.link

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import org.dimdev.dimdoors.rift.block.LocationRiftRegistry
import org.dimdev.dimdoors.rift.item.ItemRiftRegistry
import org.dimdev.dimdoors.subsystem.registry.SubSystem
import org.dimdev.dimdoors.subsystem.registry.SubsystemTypes.PROPERTIES
import org.dimdev.dimdoors.util.ext.nullableForGetter
import org.dimdev.dimdoors.util.ext.unboundedMap
import java.util.*
import kotlin.jvm.optionals.getOrNull

class LinkPropertiesRegistry(val propertiesMap: MutableMap<UUID, Entry> = mutableMapOf()) : SubSystem<LinkPropertiesRegistry>() {
    data class Entry(var properties: LinkProperties? = null, var isDetached: Boolean = false) {
        companion object {
            val CODEC: Codec<Entry> = RecordCodecBuilder.create { instance -> instance.group(
                LinkProperties.CODEC.optionalFieldOf("properties").nullableForGetter(Entry::properties),
                Codec.BOOL.fieldOf("isDetached").forGetter(Entry::isDetached)
                ).apply(instance) { a, b -> Entry(a.getOrNull(), b) }
            }
        }
    }

    override fun type(): Type<LinkPropertiesRegistry> = PROPERTIES

    private fun getOrCreate(rift: UUID): Entry {
        return propertiesMap.computeIfAbsent(rift) { Entry() }
    }

    fun setDetached(rift: UUID, detached: Boolean) {
        getOrCreate(rift).isDetached = detached
        setDirty()
    }

    fun setProperties(rift: UUID, properties: LinkProperties?) {
        getOrCreate(rift).properties = properties
        setDirty()
    }

    fun getProperties(rift: UUID): LinkProperties? {
        return propertiesMap[rift]?.properties
    }

    companion object {
        val CODEC: MapCodec<LinkPropertiesRegistry> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                UUIDUtil.STRING_CODEC.unboundedMap(Entry.CODEC).fieldOf("properties_map")
                    .forGetter(LinkPropertiesRegistry::propertiesMap)
            ).apply(instance, ::LinkPropertiesRegistry)
        }

        val instance get() = getInstance(PROPERTIES)!!

        fun registerEvents() {

            LocationRiftRegistry.EVENTS.riftRemoveEvent.register { id, _ ->
                if (instance.propertiesMap.remove(id) != null) instance.setDirty()
            }

            ItemRiftRegistry.EVENTS.riftRemoveEvent.register { id, _ ->
                if (instance.propertiesMap.remove(id) != null) instance.setDirty()
            }
        }
    }
}
