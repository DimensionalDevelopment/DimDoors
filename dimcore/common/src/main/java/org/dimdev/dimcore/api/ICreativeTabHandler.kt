package org.dimdev.dimcore.api

import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

interface ICreativeTabHandler {
    fun modify(tab: ResourceKey<CreativeModeTab>, filler: (CreativeTabOutput) -> Unit)

    interface CreativeTabOutput : CreativeModeTab.Output {
        fun acceptAfter(after: ItemStack, stack: ItemStack, visibility: CreativeModeTab.TabVisibility)
        fun acceptAfter(after: ItemStack, stack: ItemStack) = acceptAfter(after, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS)
        fun acceptBefore(before: ItemStack, stack: ItemStack, visibility: CreativeModeTab.TabVisibility)
        fun acceptBefore(after: ItemStack, stack: ItemStack) = acceptBefore(after, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS)
        override fun accept(stack: ItemStack, visibility: CreativeModeTab.TabVisibility) = acceptAfter(ItemStack.EMPTY, stack, visibility)
    }
}
