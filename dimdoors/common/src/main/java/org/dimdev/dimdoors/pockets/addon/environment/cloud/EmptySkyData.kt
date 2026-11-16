package org.dimdev.dimdoors.pockets.addon.environment.cloud

import org.dimdev.dimdoors.pockets.addon.environment.sky.SkyData
import org.dimdev.dimdoors.pockets.addon.environment.sky.SkyDatum
import org.dimdev.dimdoors.util.codec.SingletonInstance

object EmptySkyData : SkyData, SingletonInstance<EmptySkyData>() {
    override val type get() = SkyDatum.EMPTY
}
