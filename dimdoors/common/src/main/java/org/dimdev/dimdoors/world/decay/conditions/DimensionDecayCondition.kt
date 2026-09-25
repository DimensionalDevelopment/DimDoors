package org.dimdev.dimdoors.world.decay.conditions

import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.world.level.dimension.DimensionType
import org.dimdev.dimdoors.util.CodecUtils
import org.dimdev.dimdoors.util.CodecUtils.createCodec
import org.dimdev.dimdoors.world.decay.Decay

class DimensionDecayCondition private constructor(
    tagOrElementLocation: CodecUtils.TagOrElementLocation<DimensionType>,
    invert: Boolean
) : GenericDecayCondition<DimensionType>(tagOrElementLocation, invert) {
    override val type get() = DecayConditions.DIMENSION

    public override fun getHolder(context: Decay.DecayContext): Holder<DimensionType> {
        return context.world.dimensionTypeRegistration()
    }

    override fun registry(): ResourceKey<Registry<DimensionType>> = Registries.DIMENSION_TYPE

    companion object {
        var CODEC= createCodec(::DimensionDecayCondition, Registries.DIMENSION_TYPE)

        fun of(tag: TagKey<DimensionType>, invert: Boolean = false) = DimensionDecayCondition(CodecUtils.TagOrElementLocation.of(tag, Registries.DIMENSION_TYPE), invert)
        fun of(key: ResourceKey<DimensionType>, invert: Boolean = false) = DimensionDecayCondition(CodecUtils.TagOrElementLocation.of(key, Registries.DIMENSION_TYPE), invert)
    }
}
