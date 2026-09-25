package org.dimdev.dimdoors.rift.registry

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.nbt.CompoundTag
import org.apache.commons.lang3.builder.ToStringBuilder
import org.dimdev.dimdoors.util.Copyable
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors

data class LinkProperties(// TODO: depend on rift properties (ex. size, stability, or maybe a getWeightFactor method) rather than rift type
    var floatingWeight: Float,
    val entranceWeight: Float,
    val groups: MutableSet<Int>,
    val linksRemaining: Int,
    val isOneWay: Boolean
): Copyable<LinkProperties> {
    override fun copy(): LinkProperties {
        return builder {
            floatingWeight(floatingWeight)
            entranceWeight(entranceWeight)
            groups(groups.toMutableSet())
        }
    }

    fun toBuilder(): LinkPropertiesBuilder {
        return LinkPropertiesBuilder().floatingWeight(this.floatingWeight).entranceWeight(this.entranceWeight)
            .groups(this.groups).linksRemaining(this.linksRemaining).oneWay(this.isOneWay)
    }

    fun withLinksRemaining(linksRemaining: Int): LinkProperties {
        return toBuilder().linksRemaining(linksRemaining).build()
    }

    class LinkPropertiesBuilder internal constructor() {
        private var floatingWeight = 0f
        private var entranceWeight = 0f
        private var groups = mutableSetOf<Int>()
        private var linksRemaining = 0
        private var oneWay = false

        fun floatingWeight(floatingWeight: Float): LinkPropertiesBuilder {
            this.floatingWeight = floatingWeight
            return this
        }

        fun entranceWeight(entranceWeight: Float): LinkPropertiesBuilder {
            this.entranceWeight = entranceWeight
            return this
        }

        fun groups(groups: MutableSet<Int>): LinkPropertiesBuilder {
            this.groups = groups
            return this
        }

        fun linksRemaining(linksRemaining: Int): LinkPropertiesBuilder {
            this.linksRemaining = linksRemaining
            return this
        }

        fun oneWay(oneWay: Boolean): LinkPropertiesBuilder {
            this.oneWay = oneWay
            return this
        }

        fun build(): LinkProperties {
            return LinkProperties(
                this.floatingWeight,
                this.entranceWeight,
                this.groups,
                this.linksRemaining,
                this.oneWay
            )
        }

        override fun toString(): String {
            return ToStringBuilder(this)
                .append("floatingWeight", floatingWeight)
                .append("entranceWeight", entranceWeight)
                .append("groups", groups)
                .append("linksRemaining", linksRemaining)
                .append("oneWay", oneWay)
                .toString()
        }

        fun groups(vararg groups: Int): LinkPropertiesBuilder {
            return groups(groups.toMutableSet())
        }
    }

    companion object {
        @JvmField
        val NONE: LinkProperties = builder {}
        private val GROUPS_CODEC = Codec.INT_STREAM.xmap({ stream -> stream.boxed().collect(Collectors.toSet()) }, { groups -> groups.stream().mapToInt { it } })

        val CODEC =
            RecordCodecBuilder.create<LinkProperties?>(Function { instance: RecordCodecBuilder.Instance<LinkProperties?>? ->
                instance!!.group(
                    Codec.FLOAT.optionalFieldOf("floatingWeight", 0.0f).forGetter(LinkProperties::floatingWeight),
                    Codec.FLOAT.optionalFieldOf("entranceWeight", 0.0f).forGetter(LinkProperties::entranceWeight),
                    GROUPS_CODEC.optionalFieldOf("groups", mutableSetOf()).forGetter(LinkProperties::groups),
                    Codec.INT.optionalFieldOf("linksRemaining", 0).forGetter(LinkProperties::linksRemaining),
                    Codec.BOOL.optionalFieldOf("oneWay", false).forGetter(LinkProperties::isOneWay)
                ).apply(
                    instance, ::LinkProperties)
            })

        @JvmStatic
        fun builder(block: LinkPropertiesBuilder.() -> Unit): LinkProperties = LinkPropertiesBuilder().also(block).build()

        @JvmStatic
        fun toNbt(properties: LinkProperties): CompoundTag {
            val nbt = CompoundTag()
            nbt.putFloat("floatingWeight", properties.floatingWeight)
            nbt.putFloat("entranceWeight", properties.entranceWeight)
            nbt.putIntArray("groups", ArrayList(properties.groups))
            nbt.putInt("linksRemaining", properties.linksRemaining)
            nbt.putBoolean("oneWay", properties.isOneWay)
            return nbt
        }

        @JvmStatic
        fun fromNbt(nbt: CompoundTag): LinkProperties {
            return builder {
                floatingWeight(nbt.getFloat("floatingWeight"))
                entranceWeight(nbt.getFloat("entranceWeight"))
                groups(Arrays.stream(nbt.getIntArray("groups")).boxed().collect(Collectors.toSet()))
                linksRemaining(nbt.getInt("linksRemaining"))
                oneWay(nbt.getBoolean("oneWay"))
            }
        }
    }
}