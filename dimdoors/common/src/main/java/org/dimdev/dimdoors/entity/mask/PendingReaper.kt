package org.dimdev.dimdoors.entity.mask

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos

data class PendingReaper(val entry: BlockPos, val ticks: Int) {
    companion object {
        @JvmField val CODEC: Codec<PendingReaper> = RecordCodecBuilder.create { instance ->
            instance.group(
                BlockPos.CODEC.fieldOf("entry").forGetter(PendingReaper::entry),
                Codec.INT.fieldOf("ticks").forGetter(PendingReaper::ticks)
            ).apply(instance, ::PendingReaper)
        }
    }
}
