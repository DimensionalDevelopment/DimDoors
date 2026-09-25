package org.dimdev.dimdoors.world.decay.pattern

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided

@JvmRecord
data class DecayPatternType<T : DecayPattern?>(val codec: MapCodec<T?>?) {
    companion object {
        val KEY: ResourceKey<Registry<DecayPatternType<out DecayPattern?>?>?> =
            ResourceKey.createRegistryKey<T?>(DimensionalDoors.id("decay_pattern_type"))
        val REGISTRY: Registry<DecayPatternType<out DecayPattern?>?> =
            getSided().createRegistry<DecayPatternType<out DecayPattern?>?>(KEY)


        val CODEC: Codec<DecayPatternType<out DecayPattern?>?> = REGISTRY.byNameCodec()

        @JvmField
        val COMPOUND: DecayPatternType<CompoundDecayPattern?> =
            register<CompoundDecayPattern?>(CompoundDecayPattern.KEY, CompoundDecayPattern.CODEC)
        @JvmField
        val PAINTING: DecayPatternType<PaintingDecayPattern?> =
            register<PaintingDecayPattern?>(PaintingDecayPattern.KEY, PaintingDecayPattern.CODEC)

        fun register() {
        }

        fun <T : DecayPattern?> register(id: String, codec: MapCodec<T?>?): DecayPatternType<T?> {
            return getSided().register<DecayPatternType<out DecayPattern?>, DecayPatternType<T?>>(
                KEY,
                id,
                DecayPatternType<T?>(codec)
            )
        }
    }
}
