package org.dimdev.dimdoors.screen

import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.RecipeBookMenu
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.RecipeHolder
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided
import org.dimdev.dimdoors.block.entity.TesselatingLoomBlockEntity
import org.dimdev.dimdoors.recipe.TesselatingRecipe
import kotlin.math.min

class TessellatingContainer(
    id: Int,
    inventory: Container,
    playerInventory: Inventory,
    propertyDelegate: ContainerData
) : RecipeBookMenu<CraftingInput, TesselatingRecipe>(ModScreenHandlerTypes.TESSELATING_LOOM, id) {
    private val playerInventory: Inventory
    private val recipeInv: Container
    private val data: ContainerData

    constructor(id: Int, playerInventory: Inventory) : this(
        id,
        SimpleContainer(10),
        playerInventory,
        SimpleContainerData(2)
    )

    init {
        checkContainerSize(inventory, 10)
        checkContainerDataCount(propertyDelegate, 2)
        this.playerInventory = playerInventory
        this.recipeInv = inventory
        this.data = propertyDelegate

        if (inventory is TesselatingLoomBlockEntity) {
            inventory.addOpenContainer(this)
        }

        this.addSlot(ResultSlot(playerInventory.player, inventory, 9, 124, 35))

        for (y in 0..2) {
            for (x in 0..2) {
                this.addSlot(Slot(this.recipeInv, x + y * 3, 30 + x * 18, 17 + y * 18))
            }
        }

        for (y in 0..2) {
            for (x in 0..8) {
                this.addSlot(Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18))
            }
        }

        for (i in 0..8) {
            this.addSlot(Slot(playerInventory, i, 8 + i * 18, 142))
        }

        this.addDataSlots(data)
    }

    override fun fillCraftSlotsStackedContents(stackedContents: StackedContents) {
        for (slot in 0..8) {
            stackedContents.accountSimpleStack(this.recipeInv.getItem(slot))
        }
    }

    override fun clearCraftingContent() {
        for (slot in 0..8) {
            this.recipeInv.setItem(slot, ItemStack.EMPTY)
        }
    }

    override fun recipeMatches(recipeHolder: RecipeHolder<TesselatingRecipe>) = recipeHolder.value().matches(this.asCraftInput(), this.playerInventory.player.level())

    override fun getResultSlotIndex() = 0

    override fun getGridWidth() = 3

    override fun getGridHeight() = 3

    override fun getSize() = 10

    override fun getRecipeBookType() = getSided().tesselatingRecipeBookType

    override fun shouldMoveToInventory(index: Int) = index != this.getResultSlotIndex()

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        val slot = slots[index]
        if (!slot.hasItem()) return ItemStack.EMPTY

        val stack = slot.item
        val original = stack.copy()

        val moved = when (index) {
            0 -> moveItemStackTo(stack, 10, 46, true).also { if (it) slot.onQuickCraft(stack, original) }
            in 10..45 -> moveItemStackTo(stack, 1, 10, false) || if (index < 37) moveItemStackTo(stack, 37, 46, false) else moveItemStackTo(stack, 10, 37, false)
            else -> moveItemStackTo(stack, 10, 46, false)
        }
        if (!moved) return ItemStack.EMPTY

        if (stack.isEmpty) slot.setByPlayer(ItemStack.EMPTY) else slot.setChanged()

        if (stack.count == original.count) return ItemStack.EMPTY

        slot.onTake(player, stack)

        return original
    }

    override fun stillValid(player: Player): Boolean = this.recipeInv.stillValid(player)

    override fun removed(player: Player) {
        super.removed(player)
        if (this.recipeInv is TesselatingLoomBlockEntity) recipeInv.removeOpenContainer(this)
    }

    private fun asCraftInput(): CraftingInput {
        return CraftingInput.of(
            3, 3, listOf(
                this.recipeInv.getItem(0),
                this.recipeInv.getItem(1),
                this.recipeInv.getItem(2),
                this.recipeInv.getItem(3),
                this.recipeInv.getItem(4),
                this.recipeInv.getItem(5),
                this.recipeInv.getItem(6),
                this.recipeInv.getItem(7),
                this.recipeInv.getItem(8)
            )
        )
    }

    fun getWeaveProgress(pixels: Int): Int {
        val i = this.data.get(DATA_WEAVE_TIME)
        val j = this.data.get(DATA_WEAVE_TIME_TOAL)
        return if (j != 0 && i != 0) i * pixels / j else 0
    }

    val isWeaving: Boolean
        get() = this.data.get(DATA_WEAVE_TIME) > 0

    class ResultSlot(private val player: Player, container: Container, slot: Int, x: Int, y: Int) :
        Slot(container, slot, x, y) {
        private var removeCount = 0

        override fun mayPlace(stack: ItemStack): Boolean {
            return false
        }

        override fun remove(amount: Int): ItemStack {
            if (this.hasItem()) {
                this.removeCount += min(amount, this.item.count)
            }

            return super.remove(amount)
        }

        override fun onQuickCraft(stack: ItemStack, amount: Int) {
            this.removeCount += amount
            this.checkTakeAchievements(stack)
        }

        override fun onTake(player: Player, stack: ItemStack) {
            this.checkTakeAchievements(stack)
            super.onTake(player, stack)
        }

        override fun checkTakeAchievements(stack: ItemStack) {
            if (this.removeCount > 0) {
                stack.onCraftedBy(this.player.level(), this.player, this.removeCount)
            }
            this.removeCount = 0
        }
    }

    companion object {
        const val DATA_WEAVE_TIME: Int = 0
        const val DATA_WEAVE_TIME_TOAL: Int = 1
    }
}
