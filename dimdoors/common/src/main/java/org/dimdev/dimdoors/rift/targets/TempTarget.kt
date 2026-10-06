package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.server.level.ServerLevel
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.api.rift.target.Target
import org.dimdev.dimdoors.block.RiftVariantProvider
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.rift.registry.Vertex

class TempTarget(private val temp: VirtualTarget<*>, private val original: VirtualTarget<*>) : VirtualTarget<TempTarget>() {
    override val type get() = VirtualTargets.TEMP

    override fun receiveOther(owner: Vertex): Target? {
        val rift = owner.providedLocation?.blockEntity?.castOrNull<Rift>() ?: return temp
        rift.takeIf { original === NoneTarget }?.castOrNull<RiftVariantProvider>()?.revertToBaseVariant(rift.riftLevel as ServerLevel, rift.riftBlockPos, rift.riftBlockState) ?: rift.setDestination(original)
        return temp
    }

    override fun copy() = TempTarget(temp, original)

    companion object {
        val CODEC: MapCodec<TempTarget> = RecordCodecBuilder.mapCodec { instance -> instance.group(
                VirtualTarget.CODEC.fieldOf("temp").forGetter(TempTarget::temp),
                VirtualTarget.CODEC.fieldOf("original").forGetter(TempTarget::original)
            ).apply(instance, ::TempTarget)
        }
    }
}
