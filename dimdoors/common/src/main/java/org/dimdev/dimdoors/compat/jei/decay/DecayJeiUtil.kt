package org.dimdev.dimdoors.compat.jei.decay

import mezz.jei.api.gui.builder.IRecipeSlotBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.material.Fluid
import org.dimdev.dimcore.DimCore.platform
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.compat.decay.DecayDisplayData
import org.dimdev.dimdoors.world.decay.results.DecayResult

object DecayJeiUtil {
    @JvmStatic
    fun supports(data: DecayDisplayData): Boolean = supports(data.input) && data.outputs.all(DecayJeiUtil::supports)

    fun supports(`object`: Any?): Boolean {
        return asItemStack(`object`) != null || asFluid(`object`) != null
    }

    fun supports(result: DecayResult.Result?): Boolean {
        return asItemStack(result) != null || asFluid(result) != null
    }

    fun addInput(builder: IRecipeSlotBuilder, `object`: Any?) {
        val itemStack = asItemStack(`object`)
        if (itemStack != null) {
            builder.addItemStack(itemStack)
            return
        }

        val fluid = asFluid(`object`)
        if (fluid != null) {
            builder.addFluidStack(fluid, platform.bucketAmount())
        }
    }

    fun addOutput(builder: IRecipeSlotBuilder, result: DecayResult.Result?) {
        val itemStack = asItemStack(result)
        if (itemStack != null) {
            builder.addItemStack(itemStack)
            return
        }

        val fluid = asFluid(result)
        if (fluid != null) {
            builder.addFluidStack(fluid, platform.bucketAmount() * result!!.amount)
        }
    }

    private fun asItemStack(obj: Any?): ItemStack? {


        if (obj is ResourceKey<*>) {
            if (obj.isFor(Registries.BLOCK)) {
                return asItemStack(BuiltInRegistries.BLOCK.get(obj.location()))
            }
            return null
        }
        if (obj is DecayResult.Result) {
            return obj.obj.castOrNull<Block>()?.let { ItemStack(it, obj.amount) }
        }
        if (obj is ItemLike) {
            return ItemStack(obj)
        }
        return null
    }

    private fun asFluid(`object`: Any?): Fluid? {
        if (`object` is ResourceKey<*>) {
            if (`object`.isFor(Registries.FLUID)) {
                return BuiltInRegistries.FLUID.get(`object`.location())
            }
            return null
        }
        if (`object` is DecayResult.Result) {
            return `object`.obj.castOrNull<Fluid>()
        }
        if (`object` is Fluid) {
            return `object`
        }
        return null
    }
}
