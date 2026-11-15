package org.dimdev.dimcore.api.ext

import net.minecraft.core.component.DataComponents
import net.minecraft.world.Container
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ItemFrame
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

fun ItemStack.nested(): Sequence<ItemStack> = sequence {
    yield(this@nested)
    get(DataComponents.CONTAINER)?.nonEmptyItems()?.forEach { yieldAll(it.nested()) }
    get(DataComponents.BUNDLE_CONTENTS)?.items()?.forEach { yieldAll(it.nested()) }
}

val Container.stacks: List<ItemStack> get() = (0 until containerSize).map(::getItem)

val Entity.heldStacks: List<ItemStack> get() = when (this) {
    is ItemEntity -> listOf(item)
    is ItemFrame -> listOf(item)
    is Player -> inventory.stacks + enderChestInventory.stacks
    is LivingEntity -> allSlots.toList()
    is Container -> stacks
    else -> emptyList()
}
