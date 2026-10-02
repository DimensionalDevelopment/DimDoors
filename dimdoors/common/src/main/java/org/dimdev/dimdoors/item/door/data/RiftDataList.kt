package org.dimdev.dimdoors.item.door.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity
import org.dimdev.dimdoors.item.door.data.condition.Condition
import org.dimdev.dimdoors.item.door.data.condition.Conditions
import org.dimdev.dimdoors.rift.registry.LinkProperties
import org.dimdev.dimdoors.rift.targets.VirtualTarget
import org.dimdev.dimdoors.util.CodecUtils.nullableForGetter
import kotlin.jvm.optionals.getOrNull

@ConsistentCopyVisibility
data class RiftDataList private constructor(private val entries: List<Entry>) {

    fun getRiftData(rift: EntranceRiftBlockEntity<*>): OptRiftData =
        entries.firstOrNull { it.condition.matches(rift) }?.data ?: throw IllegalStateException("Could not find any matching rift data")

    private data class Entry(val data: OptRiftData, val condition: Condition) {
        companion object {
            val CODEC: Codec<Entry> = RecordCodecBuilder.create { instance -> instance.group(
                OptRiftData.CODEC.fieldOf("data").forGetter(Entry::data),
                Conditions.codec.fieldOf("condition").forGetter(Entry::condition)
            ).apply(instance, ::Entry) }
        }
    }

    data class OptRiftData @JvmOverloads constructor(val destination: VirtualTarget<*>, val properties: LinkProperties? = null) {
        fun copyDestination(): VirtualTarget<*> = destination.copy()

        companion object {
            @JvmField
            val CODEC: Codec<OptRiftData> = RecordCodecBuilder.create { instance -> instance.group(
                VirtualTarget.CODEC.fieldOf("destination").forGetter(OptRiftData::destination),
                LinkProperties.CODEC.optionalFieldOf("properties").nullableForGetter(OptRiftData::properties)
            ).apply(instance) { destination, properties -> OptRiftData(destination, properties.getOrNull()) }
            }
        }
    }

    class Builder {
        private val entries = mutableListOf<Entry>()

        fun add(data: OptRiftData, condition: Condition): Builder {
            entries.add(Entry(data, condition))
            return this
        }

        fun add(target: VirtualTarget<*>, condition: Condition): Builder = add(OptRiftData(target), condition)

        fun builder() = RiftDataList(entries.toList())
    }

    companion object {
        @JvmField
        val CODEC: Codec<RiftDataList> = Entry.CODEC.listOf().xmap(::RiftDataList, RiftDataList::entries)

        @JvmStatic
        fun builder() = Builder()

        @JvmStatic
        fun of(data: OptRiftData, condition: Condition) = RiftDataList(listOf(Entry(data, condition)))

        @JvmStatic
        fun of(target: VirtualTarget<*>, properties: LinkProperties, condition: Condition) = of(OptRiftData(target, properties), condition)

        @JvmStatic
        fun of(target: VirtualTarget<*>, condition: Condition) = of(OptRiftData(target), condition)
    }
}
