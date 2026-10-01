package org.dimdev.dimdoors.world.decay.pattern

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.decoration.Painting
import net.minecraft.world.entity.decoration.PaintingVariant
import org.dimdev.dimdoors.util.CodecUtils
import org.dimdev.dimdoors.world.decay.Decay
import java.util.stream.Stream

data class PaintingDecayPattern(val from: CodecUtils.TagOrElementLocation<PaintingVariant>, val to: ResourceKey<PaintingVariant>) : DecayPattern {
    override val type get() = DecayPatterns.PAINTING

    override fun test(context: Decay.DecayContext): Boolean {
        val painting = context.targetEntity as? Painting ?: return false
        return from.test(painting.variant)
    }

    override fun process(context: Decay.DecayContext): Int {
        val painting = context.targetEntity as? Painting ?: return 0
        painting.setVariant(context.world.registryAccess().registryOrThrow(Registries.PAINTING_VARIANT).getHolderOrThrow(to))
        return 1
    }

    override fun constructApplicable(access: RegistryAccess): Stream<ResourceKey<*>> {
        return access.lookup(Registries.PAINTING_VARIANT).map(from::getValues).stream().flatMap<ResourceKey<*>> { it.stream() }
    }

    class Builder : DecayPattern.Builder<PaintingDecayPattern> {
        private var from: CodecUtils.TagOrElementLocation<PaintingVariant>? = null
        private var to: ResourceKey<PaintingVariant>? = null

        fun from(tag: TagKey<PaintingVariant>): Builder {
            from = CodecUtils.TagOrElementLocation.of(tag, Registries.PAINTING_VARIANT)
            return this
        }

        fun from(key: ResourceKey<PaintingVariant>): Builder {
            from = CodecUtils.TagOrElementLocation.of(key, Registries.PAINTING_VARIANT)
            return this
        }

        fun to(key: ResourceKey<PaintingVariant>): Builder {
            to = key
            return this
        }

        override fun build(provider: HolderLookup.Provider?): PaintingDecayPattern {
            return PaintingDecayPattern(from!!, to!!)
        }
    }

    companion object {
        const val KEY: String = "painting"

        @JvmField
        val CODEC: MapCodec<PaintingDecayPattern> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                CodecUtils.TagOrElementLocation.codec(Registries.PAINTING_VARIANT).fieldOf("from").forGetter(PaintingDecayPattern::from),
                ResourceKey.codec(Registries.PAINTING_VARIANT).fieldOf("to").forGetter(PaintingDecayPattern::to)
            ).apply(instance, ::PaintingDecayPattern)
        }

        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
