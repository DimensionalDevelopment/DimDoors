package org.dimdev.dimdoors.world.pocket.type

import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.world.pocket.DialingPocket
import org.dimdev.dimdoors.world.pocket.type.IdReferencePocket.IdReferencePocketBuilder

object Pockets: PlatformRegistry.BuilderTypePlatformRegistry<AbstractPocket<*, *>, AbstractPocket.AbstractPocketBuilder<*, *>>(ModRegistryKeys.POCKET_TYPE, DimensionalDoors.getSided()) {
    val ID_REFERENCE = create(IdReferencePocket.KEY, IdReferencePocket.CODEC, IdReferencePocket.IdReferencePocketBuilder.CODEC, ::IdReferencePocketBuilder)
    val POCKET = create(Pocket.KEY, PocketImpl.CODEC, PocketImpl.Builder.CODEC, PocketImpl::builder)
    val PRIVATE_POCKET = create(PrivatePocket.KEY, PrivatePocket.CODEC, PrivatePocket.PrivatePocketBuilder.CODEC,
        PrivatePocket::builderPrivatePocket)
    val DIALING = create("dialing_pocket", DialingPocket.CODEC, DialingPocket.Builder.CODEC, DialingPocket::builderDialingPocket)
}