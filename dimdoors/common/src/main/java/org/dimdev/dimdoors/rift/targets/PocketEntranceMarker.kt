package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Rotations
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import org.apache.commons.lang3.builder.ToStringBuilder
import org.dimdev.dimcore.api.util.EntityUtils.chat
import org.dimdev.dimdoors.api.rift.target.EntityTarget
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.rift.registry.Vertex

class PocketEntranceMarker @JvmOverloads constructor(
    @JvmField val weight: Float = 1f,
    @JvmField val ifDestination: VirtualTarget<*> = NoneTarget,
    @JvmField val otherwiseDestination: VirtualTarget<*> = NoneTarget
) : VirtualTarget<PocketEntranceMarker>(), EntityTarget {
    override fun receiveEntity(
        owner: Vertex,
        entity: Entity,
        relativePos: Vec3,
        relativeAngle: Rotations,
        relativeVelocity: Vec3,
        location: Location?
    ): Boolean {
        chat(
            entity,
            Component.translatable("The entrance of this dungeon has not been converted. If this is a normally generated pocket, please report this bug.")
        )
        return false
    }

    override fun toString(): String {
        return "PocketEntranceMarker(weight=" + this.weight + ", ifDestination=" + this.ifDestination + ", otherwiseDestination=" + this.otherwiseDestination + ")"
    }

    fun toBuilder(): PocketEntranceMarkerBuilder {
        return PocketEntranceMarkerBuilder().weight(this.weight).ifDestination(this.ifDestination)
            .otherwiseDestination(this.otherwiseDestination)
    }

    override val type: MapCodec<PocketEntranceMarker>
        get() = VirtualTargets.POCKET_ENTRANCE

    override fun copy(): PocketEntranceMarker {
        return PocketEntranceMarker(weight, ifDestination, otherwiseDestination)
    }

    class PocketEntranceMarkerBuilder {
        private var weight = 0f
        private var ifDestination: VirtualTarget<*> = NoneTarget
        private var otherwiseDestination: VirtualTarget<*> = NoneTarget

        fun weight(weight: Float): PocketEntranceMarkerBuilder {
            this.weight = weight
            return this
        }

        fun ifDestination(ifDestination: VirtualTarget<*>): PocketEntranceMarkerBuilder {
            this.ifDestination = ifDestination
            return this
        }

        fun otherwiseDestination(otherwiseDestination: VirtualTarget<*>): PocketEntranceMarkerBuilder {
            this.otherwiseDestination = otherwiseDestination
            return this
        }

        fun build(): PocketEntranceMarker {
            return PocketEntranceMarker(this.weight, this.ifDestination, this.otherwiseDestination)
        }

        override fun toString(): String {
            return ToStringBuilder(this)
                .append("weight", weight)
                .append("ifDestination", ifDestination)
                .append("otherwiseDestination", otherwiseDestination)
                .toString()
        }
    }

    companion object {
        val CODEC: MapCodec<PocketEntranceMarker> = RecordCodecBuilder.mapCodec { instance -> instance.group(
                    Codec.FLOAT.fieldOf("weight").forGetter(PocketEntranceMarker::weight),
                    VirtualTarget.CODEC.optionalFieldOf("ifDestination", NoneTarget).forGetter(PocketEntranceMarker::ifDestination),
                    VirtualTarget.CODEC.optionalFieldOf("otherwiseDestination", NoneTarget).forGetter(PocketEntranceMarker::otherwiseDestination)
                ).apply(instance, ::PocketEntranceMarker)
            }

        fun builder(): PocketEntranceMarkerBuilder {
            return PocketEntranceMarkerBuilder()
        }
    }
}