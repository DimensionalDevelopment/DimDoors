package org.dimdev.dimdoors.decay.pattern

import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import org.dimdev.dimcore.api.MapCodecHasHolder
import org.dimdev.dimcore.util.util.SimpleEvent
import org.dimdev.dimdoors.decay.Decay
import org.dimdev.dimdoors.decay.DecaySource
import java.util.stream.Stream

interface DecayPattern : MapCodecHasHolder<DecayPattern> {
    fun test(level: ServerLevel, snapshot: Decay.Snapshot, source: DecaySource): Boolean

    fun process(level: ServerLevel, snapshot: Decay.Snapshot, source: DecaySource): Int

    fun constructApplicable(access: RegistryAccess): Stream<ResourceKey<*>>

    fun applyPattern(level: ServerLevel, snapshot: Decay.Snapshot, source: DecaySource) = ENTROPY_EVENT.invoker().entropy(level, snapshot.pos, process(level, snapshot, source))

    fun interface EntropyEvent {
        fun entropy(world: Level, pos: BlockPos, entorpy: Int)
    }

    interface Builder<T : DecayPattern> {
        fun build(provider: HolderLookup.Provider?): T?
    }

    companion object {
        @JvmField
        val CODEC: Codec<DecayPattern> = DecayPatterns.codec

        @JvmField
        val ENTROPY_EVENT: SimpleEvent<EntropyEvent> = SimpleEvent.of { entropyEvents -> { world, pos, entorpy -> for (event in entropyEvents) event.entropy(world, pos, entorpy) } }
    }
}
