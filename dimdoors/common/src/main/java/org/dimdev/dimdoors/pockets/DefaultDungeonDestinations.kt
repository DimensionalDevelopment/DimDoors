package org.dimdev.dimdoors.pockets

import net.minecraft.resources.ResourceKey
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket
import org.dimdev.dimdoors.rift.registry.LinkProperties
import org.dimdev.dimdoors.rift.targets.AvailableLinkTarget
import org.dimdev.dimdoors.rift.targets.AvailableLinkTarget.Companion.builder
import org.dimdev.dimdoors.rift.targets.DungeonTarget
import org.dimdev.dimdoors.rift.targets.PocketEntranceMarker

interface DefaultDungeonDestinations {
    companion object {
        val deeperDungeonDestination: AvailableLinkTarget
            get() = builder()
                .acceptedGroups(mutableSetOf(0))
                .coordFactor(1.0)
                .negativeDepthFactor(10000.0)
                .positiveDepthFactor(80.0)
                .weightMaximum(100.0)
                .noLink(false)
                .noLinkBack(false)
                .newRiftWeight(1f)
                .build()

        val shallowerDungeonDestination: AvailableLinkTarget
            get() = builder()
                .acceptedGroups(mutableSetOf(0))
                .coordFactor(1.0)
                .negativeDepthFactor(160.0)
                .positiveDepthFactor(10000.0)
                .weightMaximum(100.0)
                .newRiftWeight(1f)
                .build()

        val overworldDestination: AvailableLinkTarget
            get() = builder()
                .acceptedGroups(mutableSetOf(0))
                .coordFactor(1.0)
                .negativeDepthFactor(0.00000000001) // The division result is cast to an int, so Double.MIN_VALUE would cause an overflow
                .positiveDepthFactor(Double.POSITIVE_INFINITY)
                .weightMaximum(100.0)
                .newRiftWeight(1f)
                .build()

        val twoWayPocketEntrance: PocketEntranceMarker?
            get() = PocketEntranceMarker.builder()
                .weight(1f)
                .ifDestination(PocketEntranceMarker())
                .otherwiseDestination(
                    builder()
                        .acceptedGroups(mutableSetOf(0))
                        .coordFactor(1.0)
                        .negativeDepthFactor(80.0)
                        .positiveDepthFactor(10000.0)
                        .weightMaximum(100.0)
                        .newRiftWeight(1f)
                        .build()
                ).build()

        val gateway: DungeonTarget
            get() = getGateway(PocketGenerator.ALL_DUNGEONS)

        fun getGateway(resourceLocation: ResourceKey<VirtualPocket>): DungeonTarget {
            return DungeonTarget.builder()
                .dungeonGroup(resourceLocation)
                .acceptedGroups(mutableSetOf(0))
                .coordFactor(1.0)
                .negativeDepthFactor(Double.POSITIVE_INFINITY)
                .positiveDepthFactor(160.0)
                .weightMaximum(300.0) // Link further away
                .newRiftWeight(1f)
                .build()
        }

        // TODO: lower weights?
        val POCKET_LINK_PROPERTIES = LinkProperties.builder {
            groups(0, 1)
            linksRemaining(1)
        }

        val OVERWORLD_LINK_PROPERTIES = LinkProperties.builder {
            groups(0, 1)
            entranceWeight(50f)
            linksRemaining(1)
        }
    }
}