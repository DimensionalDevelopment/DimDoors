package org.dimdev.dimdoors.pockets.virtual

import com.mojang.serialization.MapCodec
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket.NoneVirtualPocket
import org.dimdev.dimdoors.pockets.virtual.reference.IdReference
import org.dimdev.dimdoors.pockets.virtual.reference.TagReference
import org.dimdev.dimdoors.pockets.virtual.selection.ConditionalSelector
import org.dimdev.dimdoors.pockets.virtual.selection.PathSelector

object VirtualPockets : PlatformRegistry.MapCodecPlatformRegistry<ImplementedVirtualPocket<*>>(ModRegistryKeys.VIRTUAL_POCKET_TYPE, DimensionalDoors.getSided()) {
    @JvmField val NONE = create(NoneVirtualPocket.KEY) { MapCodec.unit(NoneVirtualPocket.NONE) }
    @JvmField val ID_REFERENCE = create(IdReference.KEY) { IdReference.CODEC }
    @JvmField val TAG_REFERENCE = create(TagReference.KEY) { TagReference.CODEC }
    @JvmField val CONDITIONAL_SELECTOR = create(ConditionalSelector.KEY) { ConditionalSelector.CODEC }
    @JvmField val PATH_SELECTOR = create(PathSelector.KEY) { PathSelector.CODEC }
}
