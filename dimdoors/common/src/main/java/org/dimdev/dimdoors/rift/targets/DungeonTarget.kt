package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceKey
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.api.util.Products
import org.dimdev.dimdoors.pockets.PocketGenerator
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket
import org.dimdev.dimdoors.rift.registry.LinkProperties
import org.dimdev.dimdoors.world.pocket.VirtualLocation
import org.dimdev.dimdoors.world.pocket.type.Pocket

class DungeonTarget(
    newRiftWeight: Float,
    weightMaximum: Double,
    coordFactor: Double,
    positiveDepthFactor: Double,
    negativeDepthFactor: Double,
    acceptedGroups: MutableSet<Int>,
    noLink: Boolean,
    noLinkBack: Boolean,
    private val dungeonGroup: ResourceKey<VirtualPocket>
) : RandomTarget<DungeonTarget>(
    newRiftWeight,
    weightMaximum,
    coordFactor,
    positiveDepthFactor,
    negativeDepthFactor,
    acceptedGroups,
    noLink,
    noLinkBack
) {
    override fun generatePocket(
        location: VirtualLocation,
        linkTo: VirtualTarget<*>,
        props: LinkProperties?
    ): Pocket<*, *>? {
        return PocketGenerator.generateDungeonPocketV2(location, linkTo, props, this.dungeonGroup)
    }

    override fun copy(): DungeonTarget {
        return DungeonTarget(
            newRiftWeight,
            weightMaximum,
            coordFactor,
            positiveDepthFactor,
            negativeDepthFactor,
            acceptedGroups,
            isNoLink,
            isNoLinkBack,
            dungeonGroup
        )
    }

    override val type get() = VirtualTargets.DUNGEON

    class DungeonTargetBuilder internal constructor() : RandomTargetBuilder<DungeonTarget, DungeonTargetBuilder>() {
        private var dungeonGroup: ResourceKey<VirtualPocket> = PocketGenerator.ALL_DUNGEONS

        fun dungeonGroup(dungeonGroup: ResourceKey<VirtualPocket>): DungeonTargetBuilder {
            this.dungeonGroup = dungeonGroup
            return this
        }

        override fun build(): DungeonTarget {
            return DungeonTarget(
                this.newRiftWeight,
                this.weightMaximum,
                this.coordFactor,
                this.positiveDepthFactor,
                this.negativeDepthFactor,
                this.acceptedGroups,
                this.noLink,
                this.noLinkBack,
                this.dungeonGroup
            )
        }
    }

    companion object {
        val CODEC: MapCodec<DungeonTarget> = RecordCodecBuilder.mapCodec { instance -> Products.and(
                common(instance),
                ResourceKey.codec(ModRegistryKeys.POCKET_GROUPS).fieldOf("dungeonGroup").forGetter(DungeonTarget::dungeonGroup)
            ).apply(
                instance, ::DungeonTarget
            )
        }

        @JvmStatic
        fun builder(): DungeonTargetBuilder {
            return DungeonTargetBuilder()
        }
    }
}
