package org.dimdev.dimdoors.pockets;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.LimboTarget;
import org.dimdev.dimdoors.rift.targets.PocketEntranceMarker;
import org.dimdev.dimdoors.rift.targets.RandomTarget;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;

public interface DefaultDungeonDestinations { // TODO: lower weights?
    LinkProperties POCKET_LINK_PROPERTIES = LinkProperties.builder()
            .groups(new HashSet<>(Arrays.asList(0, 1)))
            .linksRemaining(1)
            .build();

    LinkProperties OVERWORLD_LINK_PROPERTIES = LinkProperties.builder()
            .groups(new HashSet<>(Arrays.asList(0, 1)))
            .entranceWeight(50)
            .linksRemaining(1)
            .build();

    VirtualTarget DEEPER_DUNGEON_DESTINATION = RandomTarget.builder()
            .acceptedGroups(Collections.singleton(0))
            .coordFactor(1)
            .negativeDepthFactor(10000)
            .positiveDepthFactor(160)
            .weightMaximum(100)
            .newRiftWeight(1)
            .build();

    VirtualTarget SHALLOWER_DUNGEON_DESTINATION = RandomTarget.builder()
            .acceptedGroups(Collections.singleton(0))
            .coordFactor(1)
            .negativeDepthFactor(160)
            .positiveDepthFactor(10000)
            .weightMaximum(100)
            .newRiftWeight(1)
            .build();

    VirtualTarget OVERWORLD_DESTINATION = RandomTarget.builder()
            .acceptedGroups(Collections.singleton(0))
            .coordFactor(1)
            .negativeDepthFactor(0.00000000001) // The division result is cast to an int, so Double.MIN_VALUE would cause an overflow
            .positiveDepthFactor(Double.POSITIVE_INFINITY)
            .weightMaximum(100)
            .newRiftWeight(1)
            .build();

    VirtualTarget TWO_WAY_POCKET_ENTRANCE = PocketEntranceMarker.builder()
            .weight(1)
            .ifDestination(new PocketEntranceMarker())
            .otherwiseDestination(RandomTarget.builder()
                    .acceptedGroups(Collections.singleton(0))
                    .coordFactor(1)
                    .negativeDepthFactor(80)
                    .positiveDepthFactor(10000)
                    .weightMaximum(100)
                    .newRiftWeight(1)
                    .build())
            .build();

    VirtualTarget GATEWAY_DESTINATION = RandomTarget.builder()
            .acceptedGroups(Collections.singleton(0))
            .coordFactor(1) // TODO: lower value?
            .negativeDepthFactor(Double.POSITIVE_INFINITY)
            .positiveDepthFactor(160) // TODO: lower value?
            .weightMaximum(300) // Link further away
            .newRiftWeight(1)
            .build();

    VirtualTarget LIMBO = LimboTarget.INSTANCE;
}
