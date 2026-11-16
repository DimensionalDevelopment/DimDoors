package org.dimdev.dimdoors.pockets.addon.environment.sky

import org.dimdev.dimdoors.util.codec.SingletonInstance

object EndSkyData : SkyData, SingletonInstance<EndSkyData>() {
    override val type get() = SkyDatum.END
}
