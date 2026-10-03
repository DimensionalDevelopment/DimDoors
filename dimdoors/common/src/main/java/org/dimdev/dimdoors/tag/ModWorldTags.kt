package org.dimdev.dimdoors.tag

import net.minecraft.core.registries.Registries
import org.dimdev.dimdoors.api.util.tag

object ModWorldTags {
    @JvmField val MONOLITHS_CAN_EXIST = Registries.DIMENSION_TYPE.tag("monoliths_can_exist")
    @JvmField val UNRAVELLED_FABRIC_CAN_UNRAVEL = Registries.DIMENSION_TYPE.tag("unravelled_fabric_can_unravel")
}
