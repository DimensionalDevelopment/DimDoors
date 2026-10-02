package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder

class AvailableLinkTarget(
    newRiftWeight: Float,
    weightMaximum: Double,
    coordFactor: Double,
    positiveDepthFactor: Double,
    negativeDepthFactor: Double,
    acceptedGroups: MutableSet<Int>,
    noLink: Boolean,
    noLinkBack: Boolean
) : RandomTarget<AvailableLinkTarget>(
    newRiftWeight,
    weightMaximum,
    coordFactor,
    positiveDepthFactor,
    negativeDepthFactor,
    acceptedGroups,
    noLink,
    noLinkBack
) {
    override val type get() = VirtualTargets.AVAILABLE_LINK

    override fun copy(): AvailableLinkTarget {
        return AvailableLinkTarget(
            this.newRiftWeight,
            weightMaximum,
            coordFactor,
            positiveDepthFactor,
            negativeDepthFactor,
            acceptedGroups,
            isNoLink,
            isNoLinkBack
        )
    }

    class AvailableLinkTargetBuilder : RandomTargetBuilder<AvailableLinkTarget, AvailableLinkTargetBuilder>() {
        public override fun build(): AvailableLinkTarget {
            return AvailableLinkTarget(
                this.newRiftWeight,
                this.weightMaximum,
                this.coordFactor,
                this.positiveDepthFactor,
                this.negativeDepthFactor,
                this.acceptedGroups,
                this.noLink,
                this.noLinkBack
            )
        }
    }

    companion object {
        val CODEC: MapCodec<AvailableLinkTarget> = RecordCodecBuilder.mapCodec<AvailableLinkTarget> { instance -> common(instance).apply(instance, ::AvailableLinkTarget) }

        @JvmStatic
        fun builder(): AvailableLinkTargetBuilder {
            return AvailableLinkTargetBuilder()
        }
    }
}
