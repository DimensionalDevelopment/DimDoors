package org.dimdev.dimdoors.api.util

import net.minecraft.core.Registry
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import org.dimdev.dimdoors.DimensionalDoors

fun String.id(): ResourceLocation = DimensionalDoors.id(this)

fun <T : Any> ResourceKey<Registry<T>>.key(name: ResourceLocation) = ResourceKey.create(this, name)
fun <T : Any> ResourceKey<Registry<T>>.key(name: String) = name.id().let(this::key)

fun <T: Any> ResourceKey<Registry<T>>.tag(id: String): TagKey<T> = TagKey.create<T>(this, DimensionalDoors.id(id))

fun String.translate(vararg arg: Any): Component = Component.translatable(this, *arg)
