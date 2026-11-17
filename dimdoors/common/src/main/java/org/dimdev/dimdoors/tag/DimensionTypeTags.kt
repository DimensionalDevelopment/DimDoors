package org.dimdev.dimdoors.tag

import net.minecraft.core.registries.Registries
import org.dimdev.dimdoors.util.ext.tag

object DimensionTypeTags {
    @JvmField val MONOLITHS_CAN_EXIST = Registries.DIMENSION_TYPE.tag("monoliths_can_exist")
    @JvmField val ALLOWS_DECAY = Registries.DIMENSION_TYPE.tag("allows_decay")
}
