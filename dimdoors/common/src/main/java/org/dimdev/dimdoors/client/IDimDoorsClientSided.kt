package org.dimdev.dimdoors.client

import net.minecraft.client.RecipeBookCategories
import net.minecraft.world.item.ItemStack
import org.dimdev.dimcore.api.client.IClientSided
import org.dimdev.dimdoors.client.effect.DimensionEffect
import org.dimdev.dimdoors.client.effect.VoidDimensionSpecialEffects

interface IDimDoorsClientSided<T : IDimDoorsClientSided<T>> : IClientSided<T> {
    fun checkCompat() {}

    fun getRecipBookCategories(name: String, itemStack: () -> ItemStack): () -> RecipeBookCategories

    fun createVoidEffect(effect: DimensionEffect): VoidDimensionSpecialEffects

    fun onPreRender(onPrerender: PreRender)

    fun interface PreRender {
        fun preRender(tick: Long, partialTick: Float)
    }
}
