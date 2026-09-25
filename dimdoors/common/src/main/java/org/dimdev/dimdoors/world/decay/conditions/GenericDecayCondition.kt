package org.dimdev.dimdoors.world.decay.conditions

import net.minecraft.core.Holder
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceKey
import org.dimdev.dimdoors.util.CodecUtils
import org.dimdev.dimdoors.world.decay.Decay
import java.util.stream.Stream

abstract class GenericDecayCondition<T>(val tagOrElementLocation: CodecUtils.TagOrElementLocation<T>, private val invert: Boolean) : DecayCondition, Applicator<T> {
    override fun test(context: Decay.DecayContext): Boolean {
        return invert != tagOrElementLocation.test(getHolder(context))
    }

    abstract fun getHolder(context: Decay.DecayContext): Holder<T>

    fun invert() = invert

    override fun constructApplicable(lookup: RegistryAccess): Stream<ResourceKey<T>> = lookup.lookup<T>(registry()).map(tagOrElementLocation::getValues).stream().flatMap<ResourceKey<T>>(Set<ResourceKey<T>>::stream)
}

