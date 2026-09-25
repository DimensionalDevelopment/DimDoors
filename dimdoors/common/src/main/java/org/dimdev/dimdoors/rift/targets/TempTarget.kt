package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import org.dimdev.dimcore.api.castOrNull
import org.dimdev.dimdoors.api.rift.target.Target
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.block.RiftVariantProvider
import org.dimdev.dimdoors.block.RiftVariantProvider.revertToBaseVariant
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.block.entity.Rift.setDestination
import java.util.function.BiFunction
import java.util.function.Function

class TempTarget(private val temp: VirtualTarget<*>, private val original: VirtualTarget<*>) : VirtualTarget<TempTarget>() {
    override val type get() = VirtualTargets.TEMP

    override var location: Location
        get() = super.location
        set(value) {
            super.location = value
            temp.location = value
        }

    override fun receiveOther(): Target {
        val rift = this.location.blockEntity?.castOrNull<Rift>() ?: return temp
        rift.takeIf { original === NoneTarget }?.castOrNull<RiftVariantProvider>()?.revertToBaseVariant(this.location.world, rift.riftBlockPos, rift.riftBlockState) ?: rift.setDestination(original)
        return temp
    }

    override fun copy() = TempTarget(temp, original)

    companion object {
        val CODEC = RecordCodecBuilder.mapCodec { instance -> instance.group(
                VirtualTarget.CODEC.fieldOf("temp").forGetter(TempTarget::temp),
                VirtualTarget.CODEC.fieldOf("original").forGetter(TempTarget::original)
            ).apply(instance, ::TempTarget)
        }
    }
}
