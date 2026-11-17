package org.dimdev.dimdoors.decay.conditions

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.TagKey
import net.minecraft.util.StringRepresentable
import net.minecraft.world.level.material.Fluid
import org.dimdev.dimdoors.decay.Decay
import org.dimdev.dimdoors.decay.DecaySource
import org.dimdev.dimdoors.util.codec.CodecUtils
import java.util.*

class FluidDecayCondition(
    tagOrElementLocation: CodecUtils.TagOrElementLocation<Fluid>,
    invert: Boolean,
    val fluidType: Type
) : GenericDecayCondition<Fluid>(tagOrElementLocation, invert) {
    override val type get() = DecayConditions.FLUID


    override fun test(level: ServerLevel, snapshot: Decay.Snapshot, source: DecaySource): Boolean = super.test(level, snapshot, source) && (fluidType == Type.BOTH || (snapshot.fluidState.isSource && fluidType == Type.SOURCE))

    override fun getHolder(level: ServerLevel, snapshot: Decay.Snapshot, source: DecaySource): Holder<Fluid> = snapshot.fluidState.holder()

    override fun registry(): ResourceKey<Registry<Fluid>> = Registries.FLUID

    enum class Type : StringRepresentable {
        FLOWING, SOURCE, BOTH;

        private val serializedName = name.lowercase(Locale.getDefault())

        override fun getSerializedName(): String = serializedName
    }

    companion object {
        val CODEC: MapCodec<FluidDecayCondition> = RecordCodecBuilder.mapCodec { instance ->
            CodecUtils.decayConditionFields(instance, Registries.FLUID)
                .and(StringRepresentable.fromEnum(Type.entries::toTypedArray).optionalFieldOf("state", Type.BOTH).forGetter(FluidDecayCondition::fluidType)
                ).apply(instance, ::FluidDecayCondition)
        }

        @JvmStatic @JvmOverloads
        fun of(tag: TagKey<Fluid>, invert: Boolean = false, type: Type = Type.BOTH): FluidDecayCondition = FluidDecayCondition(CodecUtils.TagOrElementLocation.of(tag, Registries.FLUID), invert, type)
        @JvmStatic @JvmOverloads
        fun of(key: ResourceKey<Fluid>, invert: Boolean = false, type: Type = Type.BOTH): FluidDecayCondition = FluidDecayCondition(CodecUtils.TagOrElementLocation.of(key, Registries.FLUID), invert, type)
        @JvmStatic @JvmOverloads
        fun of(fluid: Fluid, invert: Boolean = false, type: Type = Type.BOTH): FluidDecayCondition = of(fluid.builtInRegistryHolder().key(), invert, type)
    }
}
