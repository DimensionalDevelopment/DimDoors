package org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud

import org.dimdev.dimcore.api.TypeHasHolder

interface CloudData : TypeHasHolder<CloudData> {

    companion object {
        val CODEC = CloudDatum.codec
        val STREAM_CODEC = CloudDatum.streamCodec
    }
}
