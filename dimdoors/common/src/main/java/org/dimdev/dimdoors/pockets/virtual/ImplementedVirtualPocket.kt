package org.dimdev.dimdoors.pockets.virtual

import com.mojang.serialization.Codec
import org.dimdev.dimcore.api.MapCodecHasHolder

interface ImplementedVirtualPocket<T : ImplementedVirtualPocket<T>> : VirtualPocket, MapCodecHasHolder<ImplementedVirtualPocket<*>> {

    companion object {
        @JvmField
        val CODEC: Codec<ImplementedVirtualPocket<*>> = VirtualPockets.codec
    }
}
