package org.dimdev.dimdoors.compat.rei.tesselating

import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay
import me.shedaniel.rei.api.common.display.basic.BasicDisplay
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.entry.EntryStack
import me.shedaniel.rei.api.common.entry.InputIngredient
import me.shedaniel.rei.api.common.util.EntryIngredients
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.crafting.RecipeHolder
import org.dimdev.dimdoors.compat.rei.TesselatingReiCompatClient
import org.dimdev.dimdoors.recipe.TesselatingRecipe
import java.util.*

abstract class DefaultTesselatingDisplay<C : TesselatingRecipe>(val recipe: RecipeHolder<C>) : BasicDisplay(
    EntryIngredients.ofIngredients(recipe.value().ingredients),
    listOf(EntryIngredients.of(recipe.value().getResultItem(registryAccess()))),
    Optional.of(recipe.id())
), SimpleGridMenuDisplay {
    val weavingTime: Int = recipe.value().weavingTime

    open val isShapeless: Boolean get() = false

    override fun getCategoryIdentifier() = TesselatingReiCompatClient.TESSELATING

    fun getOrganisedInputEntries(menuWidth: Int, menuHeight: Int): List<EntryIngredient> {
        val list = MutableList(menuWidth * menuHeight) { EntryIngredient.empty() }
        inputEntries.forEachIndexed { i, entry -> list[slotWithSize(getInputWidth(menuWidth, 3), i, menuWidth)] = entry }
        return list
    }

    override fun getInputIngredients(menu: AbstractContainerMenu?, player: Player?): List<InputIngredient<EntryStack<*>>> = getInputIngredients(3, 3)

    fun getInputIngredients(craftingWidth: Int, craftingHeight: Int): List<InputIngredient<EntryStack<*>>> {
        val inputWidth = getInputWidth(craftingWidth, craftingHeight)
        val list = MutableList(craftingWidth * craftingHeight) { InputIngredient.empty<EntryStack<*>>(it) }

        inputEntries.forEachIndexed { i, stacks ->
            if (stacks.isEmpty()) return@forEachIndexed
            val x = i % inputWidth
            val y = i / inputWidth
            if (x < craftingWidth && y < craftingHeight) {
                list[craftingWidth * y + x] = InputIngredient.of(slotWithSize(inputWidth, i, craftingWidth), 3 * y + x, stacks)
            }
        }

        return list
    }

    companion object {
        private fun slotWithSize(recipeWidth: Int, index: Int, craftingGridWidth: Int): Int {
            val x = index % recipeWidth
            return craftingGridWidth * ((index - x) / recipeWidth) + x
        }
    }
}
