package org.dimdev.dimdoors.pockets.type

import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys

object Pockets: PlatformRegistry.BuilderTypePlatformRegistry<Pocket<*, *>, Pocket.PocketBuilder<*, *>>(ModRegistryKeys.POCKET_TYPE, DimensionalDoors.getSided()) {
    val POCKET = create(Pocket.KEY, PocketImpl.CODEC, PocketImpl.Builder.CODEC, PocketImpl::builder)
    val PRIVATE_POCKET = create(PrivatePocket.KEY, PrivatePocket.CODEC, PrivatePocket.PrivatePocketBuilder.CODEC, PrivatePocket::builderPrivatePocket)
    val DIALING = create("dialing_pocket", DialingPocket.CODEC, DialingPocket.Builder.CODEC, DialingPocket::builderDialingPocket)
    val HUB = create("hub_pocket", HubPocket.CODEC, HubPocket.Builder.CODEC, HubPocket::builderHubPocket)
}