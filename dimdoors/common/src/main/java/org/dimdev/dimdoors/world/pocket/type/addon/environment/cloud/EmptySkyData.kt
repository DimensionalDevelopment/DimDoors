package org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud

import org.dimdev.dimdoors.SingletonInstance
import org.dimdev.dimdoors.world.pocket.type.addon.environment.sky.SkyData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.sky.SkyDataHolder
import org.dimdev.dimdoors.world.pocket.type.addon.environment.sky.SkyDatum

object EmptySkyData : SkyData, SingletonInstance<EmptySkyData>() {
    override val type : SkyDataHolder get() = SkyDatum.EMPTY
}
