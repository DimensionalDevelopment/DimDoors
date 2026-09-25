package org.dimdev.dimdoors.world.decay.pattern

import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import org.dimdev.dimcore.api.util.SimpleEvent
import org.dimdev.dimdoors.world.decay.Decay
import java.util.function.Function
import java.util.stream.Stream

interface DecayPattern {
    val type: DecayPatternType<out DecayPattern>

    fun test(context: Decay.DecayContext): Boolean

    fun process(context: Decay.DecayContext): Int

    fun constructApplicable(access: RegistryAccess): Stream<ResourceKey<*>>

    fun applyPattern(context: Decay.DecayContext) = ENTROPY_EVENT.invoker().entropy(context.world, context.targetBlockPos, process(context))

    fun interface EntropyEvent {
        fun entropy(world: Level, pos: BlockPos, entorpy: Int)
    }

    interface Builder<T : DecayPattern> {
        fun build(provider: HolderLookup.Provider?): T?
    }

    companion object {
        val CODEC: Codec<DecayPattern> = DecayPatternType.CODEC.dispatch<DecayPattern>(Function { obj: DecayPattern -> obj.type }, DecayPatternType::codec)

        val ENTROPY_EVENT: SimpleEvent<EntropyEvent> = SimpleEvent.of { entropyEvents -> { world, pos, entorpy -> for (event in entropyEvents) event.entropy(world, pos, entorpy) } }
    }
}
