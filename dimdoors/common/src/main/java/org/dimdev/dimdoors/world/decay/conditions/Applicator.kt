package org.dimdev.dimdoors.world.decay.conditions

import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceKey
import java.util.stream.Stream

interface Applicator<T> {
    fun constructApplicable(lookup: RegistryAccess): Stream<ResourceKey<T>>

    fun registry(): ResourceKey<out Registry<T>>
}
