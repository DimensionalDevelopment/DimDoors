package org.dimdev.dimdoors.world.pocket.type.addon.environment.sky

import org.dimdev.dimcore.api.TypeHasHolder

interface SkyData : TypeHasHolder<SkyData> {
    companion object {
        val CODEC = SkyDatum.codec
        val STREAM_CODEC = SkyDatum.streamCodec
    }
}
