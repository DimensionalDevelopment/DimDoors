package org.dimdev.dimdoors.compat.rei.tesselating

import it.unimi.dsi.fastutil.ints.IntIntImmutablePair
import it.unimi.dsi.fastutil.ints.IntIntPair
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay
import me.shedaniel.rei.api.common.display.basic.BasicDisplay
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.entry.EntryStack
import me.shedaniel.rei.api.common.entry.InputIngredient
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes
import me.shedaniel.rei.api.common.transfer.info.MenuInfo
import me.shedaniel.rei.api.common.transfer.info.MenuSerializationContext
import me.shedaniel.rei.api.common.transfer.info.simple.SimpleGridMenuInfo
import me.shedaniel.rei.api.common.util.CollectionUtils
import me.shedaniel.rei.api.common.util.EntryIngredients
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeHolder
import org.dimdev.dimdoors.compat.rei.TesselatingReiCompatClient
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe
import org.dimdev.dimdoors.recipe.TesselatingRecipe
import org.dimdev.dimdoors.recipe.TesselatingShapelessRecipe
import java.util.*

abstract class DefaultTesselatingDisplay<C : TesselatingRecipe>(recipe: RecipeHolder<C>) : BasicDisplay(EntryIngredients.ofIngredients(recipe.value().ingredients), mutableListOf<EntryIngredient>(EntryIngredients.of(recipe.value().getResultItem(registryAccess()))), Optional.of(recipe.id())), SimpleGridMenuDisplay {
    var optionalRecipe: RecipeHolder<C>
        protected set
    val weavingTime: Int

    init {
        this.optionalRecipe = recipe
        this.weavingTime = recipe.value().weavingTime
    }

    override fun getCategoryIdentifier() = TesselatingReiCompatClient.TESSELATING

    override fun getDisplayLocation() = Optional.of(this.optionalRecipe.id())

    fun <T : AbstractContainerMenu> getOrganisedInputEntries(
        menuInfo: SimpleGridMenuInfo<T, DefaultTesselatingDisplay<*>>,
        container: T
    ): MutableList<MutableList<ItemStack>> {
        return CollectionUtils.map<EntryIngredient, MutableList<ItemStack>>(
            getOrganisedInputEntries<AbstractContainerMenu>(
                menuInfo.getCraftingWidth(container),
                menuInfo.getCraftingHeight(container)
            )
        ) { ingredient: EntryIngredient -> CollectionUtils.filterAndMap(ingredient, { stack -> stack.getType() === VanillaEntryTypes.ITEM }, EntryStack<*>::castValue) }
    }

    fun <T : AbstractContainerMenu?> getOrganisedInputEntries(
        menuWidth: Int,
        menuHeight: Int
    ): MutableList<EntryIngredient?> {
        val list: MutableList<EntryIngredient?> = ArrayList<EntryIngredient?>(menuWidth * menuHeight)
        for (i in 0..<menuWidth * menuHeight) {
            list.add(EntryIngredient.empty())
        }
        for (i in inputEntries.indices) {
            list[getSlotWithSize(this, i, menuWidth)] = inputEntries[i]
        }
        return list
    }

    open val isShapeless: Boolean
        get() = false

    override fun getInputIngredients(
        context: MenuSerializationContext<*, *, *>,
        info: MenuInfo<*, *>?,
        fill: Boolean
    ): MutableList<InputIngredient<EntryStack<*>?>?> {
        var craftingWidth = 3
        var craftingHeight = 3

        if (info is SimpleGridMenuInfo<*, *> && fill) {
            craftingWidth = (info as SimpleGridMenuInfo<AbstractContainerMenu?, *>).getCraftingWidth(context.getMenu())
            craftingHeight = info.getCraftingHeight(context.getMenu())
        }

        return getInputIngredients(craftingWidth, craftingHeight)
    }

    override fun getInputIngredients(
        menu: AbstractContainerMenu?,
        player: Player?
    ): MutableList<InputIngredient<EntryStack<*>?>?> {
        return getInputIngredients(3, 3)
    }

    fun getInputIngredients(craftingWidth: Int, craftingHeight: Int): MutableList<InputIngredient<EntryStack<*>?>?> {
        val inputWidth = getInputWidth(craftingWidth, craftingHeight)

        val grid: MutableMap<IntIntPair?, InputIngredient<EntryStack<*>?>?> =
            HashMap<IntIntPair?, InputIngredient<EntryStack<*>?>?>()

        val inputEntries = getInputEntries()
        for (i in inputEntries.indices) {
            val stacks = inputEntries.get(i)
            if (stacks.isEmpty()) {
                continue
            }
            val index: Int = getSlotWithSize(inputWidth, i, craftingWidth)
            val x = i % inputWidth
            val y = i / inputWidth
            grid.put(IntIntImmutablePair(x, y), InputIngredient.of<EntryStack<*>?>(index, 3 * y + x, stacks))
        }

        val list: MutableList<InputIngredient<EntryStack<*>?>?> =
            ArrayList<InputIngredient<EntryStack<*>?>?>(craftingWidth * craftingHeight)
        var i = 0
        val n = craftingWidth * craftingHeight
        while (i < n) {
            list.add(InputIngredient.empty<EntryStack<*>?>(i))
            i++
        }

        for (x in 0..<craftingWidth) {
            for (y in 0..<craftingHeight) {
                val ingredient = grid[IntIntImmutablePair(x, y)]
                if (ingredient != null) {
                    val index = craftingWidth * y + x
                    list.set(index, ingredient)
                }
            }
        }

        return list
    }

    companion object {
        fun of(recipe: RecipeHolder<Recipe<*>>): DefaultTesselatingDisplay<*>? {
            if (recipe.value() is TesselatingShapelessRecipe) {
                val tesselating = recipe as Any as RecipeHolder<TesselatingShapelessRecipe>
                return DefaultTesselatingShapelessDisplay(tesselating)
            } else if (recipe.value() is ShapedTesselatingRecipe) {
                val tesselating = recipe as Any as RecipeHolder<ShapedTesselatingRecipe>
                return DefaultTesselatingShapedDisplay(tesselating)
            } /*else if (!recipe.isSpecial()) {
            NonNullList<Ingredient> ingredients = recipe.getIngredients();
            for (CraftingRecipeSizeProvider<?> pair : SIZE_PROVIDER) {
                CraftingRecipeSizeProvider.Size size = ((CraftingRecipeSizeProvider<Recipe<?>>) pair).getSize(recipe);

                if (size != null) {
                    return new DefaultCustomShapedDisplay(recipe, EntryIngredients.ofIngredients(recipe.getIngredients()),
                            Collections.singletonList(EntryIngredients.of(recipe.getResultItem(BasicDisplay.registryAccess()))),
                            size.getWidth(), size.getHeight());
                }
            }

            return new DefaultCustomDisplay(recipe, EntryIngredients.ofIngredients(recipe.getIngredients()),
                    Collections.singletonList(EntryIngredients.of(recipe.getResultItem(BasicDisplay.registryAccess()))));
        }*/

            return null
        }

        fun getSlotWithSize(display: DefaultTesselatingDisplay<*>, index: Int, craftingGridWidth: Int): Int {
            return getSlotWithSize(display.getInputWidth(craftingGridWidth, 3), index, craftingGridWidth)
        }

        fun getSlotWithSize(recipeWidth: Int, index: Int, craftingGridWidth: Int): Int {
            val x = index % recipeWidth
            val y = (index - x) / recipeWidth
            return craftingGridWidth * y + x
        }
    }
}