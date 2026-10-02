package org.dimdev.dimdoors.world.pocket.type.addon.environment.sky

import org.dimdev.dimdoors.SingletonInstance

object EndSkyData : SkyData, SingletonInstance<EndSkyData>() {
    override val type get() = SkyDatum.END
}
