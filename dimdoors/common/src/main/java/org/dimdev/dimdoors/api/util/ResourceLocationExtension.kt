package org.dimdev.dimdoors.api.util

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimdoors.DimensionalDoors.Companion.id

fun <T : Any> ResourceKey<Registry<T>>.key(name: ResourceLocation) = ResourceKey.create(this, name)
fun <T : Any> ResourceKey<Registry<T>>.key(name: String) = name.id().let(this::key)