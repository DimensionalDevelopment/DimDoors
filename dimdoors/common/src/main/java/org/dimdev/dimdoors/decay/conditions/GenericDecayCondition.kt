package org.dimdev.dimdoors.decay.conditions

import net.minecraft.core.Holder
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import org.dimdev.dimdoors.decay.Decay
import org.dimdev.dimdoors.decay.DecaySource
import org.dimdev.dimdoors.util.codec.CodecUtils
import java.util.stream.Stream

abstract class GenericDecayCondition<T>(val tagOrElementLocation: CodecUtils.TagOrElementLocation<T>, private val invert: Boolean) : DecayCondition, Applicator<T> {
    override fun test(level: ServerLevel, snapshot: Decay.Snapshot, source: DecaySource): Boolean {
        return invert != tagOrElementLocation.test(getHolder(level, snapshot, source))
    }

    abstract fun getHolder(level: ServerLevel, snapshot: Decay.Snapshot, source: DecaySource): Holder<T>

    fun invert() = invert

    override fun constructApplicable(lookup: RegistryAccess): Stream<ResourceKey<T>> = lookup.lookup<T>(registry()).map(tagOrElementLocation::getValues).stream().flatMap<ResourceKey<T>>(Set<ResourceKey<T>>::stream)
}

