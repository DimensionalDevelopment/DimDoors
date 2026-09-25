package org.dimdev.dimdoors.pockets.modifier

import com.google.common.base.MoreObjects
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.StringRepresentable
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.pockets.PocketGenerationContext
import org.dimdev.dimdoors.rift.targets.VirtualTarget
import org.dimdev.dimdoors.world.pocket.type.Pocket

data class RelativeReferenceModifier(val point_a: Int, val point_b: Int, val connection: ConnectionType) : Modifier {
    override val type get() = Modifiers.RELATIVE_REFERENCE

    override fun apply(parameters: PocketGenerationContext, manager: RiftManager) {
        val riftA = manager[point_a]?.location ?: return
        val riftB = manager[point_b]?.location ?: return

        val link1 = riftB.target
        val link2 = riftA.target

        manager.consume(point_a) { rift -> addLink(rift, link1) }

        if (connection == ConnectionType.BOTH) manager.consume(point_b) { rift -> addLink(rift, link2) }
    }

    override fun apply(parameters: PocketGenerationContext, builder: Pocket.PocketBuilder<*, *>) {}

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
            .add("point_a", point_a)
            .add("point_b", point_b)
            .add("connection", connection.serializedName)
            .toString()
    }

    private fun addLink(rift: Rift, link: VirtualTarget<*>): Boolean {
        rift.setDestination(link)
        return true
    }

    enum class ConnectionType(private val id: String) : StringRepresentable {
        BOTH("both"),
        ONE_WAY("one_way");

        override fun getSerializedName(): String {
            return id
        }

        companion object {
            val CODEC: Codec<ConnectionType> = StringRepresentable.fromValues { entries.toTypedArray() }
        }
    }

    companion object {
        val CODEC = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.INT.fieldOf("point_a").forGetter(RelativeReferenceModifier::point_a),
                Codec.INT.fieldOf("point_b").forGetter(RelativeReferenceModifier::point_b),
                ConnectionType.CODEC.optionalFieldOf("connection", ConnectionType.BOTH).forGetter(RelativeReferenceModifier::connection)
            ).apply(instance, ::RelativeReferenceModifier)
        }

        const val KEY: String = "relative"
    }
}
