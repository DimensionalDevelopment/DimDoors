package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.codecs.RecordCodecBuilder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimdoors.rift.registry.DialingAddress

class DialingTargetImpl(override val address: DialingAddress) : VirtualTarget<DialingTargetImpl>(), DialingTarget {
    override val type get() = VirtualTargets.DIALING

    override fun copy(): DialingTargetImpl {
        return DialingTargetImpl(address)
    }

    companion object {
        private val LOGGER: Logger? = LogManager.getLogger()

        val CODEC = RecordCodecBuilder.mapCodec { instance -> instance.group(
                DialingAddress.MAP_CODEC.forGetter(DialingTarget::address)
            ).apply(instance, ::DialingTargetImpl)
        }
    }
}
