package org.dimdev.dimcore.api.ext

import com.google.common.collect.Iterables
import net.minecraft.core.component.DataComponents
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack

val ItemStack.children: Iterable<ItemStack> get() = Iterables.concat(
    get(DataComponents.CONTAINER)?.nonEmptyItems() ?: emptyList(),
    get(DataComponents.BUNDLE_CONTENTS)?.items() ?: emptyList())

val Container.stacks: List<ItemStack> get() = (0 until containerSize).map(::getItem)

