package org.dimdev.dimdoors.api.item

import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponentType
import net.minecraft.world.item.ItemStack

fun <T : Any> ItemStack.has(holder: Holder<DataComponentType<T>>) = holder.value().let(this::has)
fun <T : Any> ItemStack.get(holder: Holder<DataComponentType<T>>) = holder.value().let(this::get)
fun <T : Any> ItemStack.getOrDefault(holder: Holder<DataComponentType<T>>, value: T) = this.getOrDefault(holder.value(), value)