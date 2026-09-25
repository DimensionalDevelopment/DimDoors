package org.dimdev.dimdoors.item.component

import net.minecraft.world.item.ItemStack
import org.dimdev.dimdoors.item.ModDataComponentTypes

object IdCounter {
    @JvmStatic
    operator fun get(provider: ItemStack): Int = provider.getOrDefault(ModDataComponentTypes.COUNT.value(), 0)

    @JvmStatic
    operator fun set(provider: ItemStack, value: Int) {
        provider.set(ModDataComponentTypes.COUNT.value(), value)
    }


    fun increment(provider: ItemStack): Int {
        val value = get(provider) + 1

        set(provider, value)

        return value
    }

    @JvmStatic
    fun count(stack: ItemStack): Int {
        return get(stack)
    }

    @JvmStatic
    fun getAndIncrement(provider: ItemStack): Int {
        val value = get(provider)

        set(provider, value + 1)

        return value
    }
}
