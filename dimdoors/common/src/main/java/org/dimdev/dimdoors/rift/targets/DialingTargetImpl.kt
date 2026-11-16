package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import org.dimdev.dimdoors.rift.registry.DialingAddress

class DialingTargetImpl(override var address: DialingAddress) : VirtualTarget<DialingTargetImpl>(), DialingTarget {
    override val type get() = VirtualTargets.DIALING

    override fun copy(): DialingTargetImpl {
        return DialingTargetImpl(address)
    }

    companion object {
        val CODEC: MapCodec<DialingTargetImpl> = RecordCodecBuilder.mapCodec { instance -> instance.group(
                DialingAddress.MAP_CODEC.forGetter(DialingTarget::address)
            ).apply(instance, ::DialingTargetImpl)
        }
    }
}
