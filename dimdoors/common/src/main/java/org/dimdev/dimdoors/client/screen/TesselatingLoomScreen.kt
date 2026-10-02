package org.dimdev.dimdoors.client.screen

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.ImageButton
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.Slot
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.screen.TessellatingContainer

class TesselatingLoomScreen(handler: TessellatingContainer, inventory: Inventory, title: Component) : AbstractContainerScreen<TessellatingContainer>(handler, inventory, title), RecipeUpdateListener {
    private val recipeBook = RecipeBookComponent()
    private var narrow = false

    public override fun init() {
        super.init()
        this.narrow = this.width < 379
        this.recipeBook.init(this.width, this.height, this.minecraft!!, this.narrow, this.menu)
        this.leftPos = this.recipeBook.updateScreenPosition(this.width, this.imageWidth)
        this.addRenderableWidget(
            ImageButton(
                this.leftPos + 5,
                this.height / 2 - 49,
                20,
                18,
                RecipeBookComponent.RECIPE_BUTTON_SPRITES
            ) { button ->
                this.recipeBook.toggleVisibility()
                this.leftPos = this.recipeBook.updateScreenPosition(this.width, this.imageWidth)
                button.setPosition(this.leftPos + 5, this.height / 2 - 49)
            }
        )
        this.addWidget(this.recipeBook)
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2
    }

    public override fun containerTick() {
        super.containerTick()
        this.recipeBook.tick()
    }

    override fun render(matrices: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        if (this.recipeBook.isVisible && this.narrow) {
            this.renderBackground(matrices, mouseX, mouseY, delta)
            this.recipeBook.render(matrices, mouseX, mouseY, delta)
        } else {
            super.render(matrices, mouseX, mouseY, delta)
            this.recipeBook.render(matrices, mouseX, mouseY, delta)
            this.recipeBook.renderGhostRecipe(matrices, this.leftPos, this.topPos, true, delta)
        }

        this.renderTooltip(matrices, mouseX, mouseY)
        this.recipeBook.renderTooltip(matrices, this.leftPos, this.topPos, mouseX, mouseY)
    }

    override fun renderBg(matrices: GuiGraphics, delta: Float, mouseX: Int, mouseY: Int) {
        val i = this.leftPos
        val j = this.topPos
        matrices.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight)

        if (this.menu.isWeaving) {
            val k = this.menu.getWeaveProgress(22)
            matrices.blit(TEXTURE, i + 89, j + 34, 176, 0, k + 1, 16)
        }
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return this.recipeBook.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(
            keyCode,
            scanCode,
            modifiers
        )
    }

    override fun charTyped(codePoint: Char, modifiers: Int): Boolean {
        return this.recipeBook.charTyped(codePoint, modifiers) || super.charTyped(codePoint, modifiers)
    }

    override fun isHovering(x: Int, y: Int, width: Int, height: Int, pointX: Double, pointY: Double): Boolean {
        return (!this.narrow || !this.recipeBook.isVisible) && super.isHovering(x, y, width, height, pointX, pointY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (this.recipeBook.mouseClicked(mouseX, mouseY, button)) {
            this.focused = this.recipeBook
            return true
        }

        return this.narrow && this.recipeBook.isVisible || super.mouseClicked(mouseX, mouseY, button)
    }

    override fun hasClickedOutside(mouseX: Double, mouseY: Double, left: Int, top: Int, button: Int): Boolean {
        val outside =
            mouseX < left.toDouble() || mouseY < top.toDouble() || mouseX >= (left + this.imageWidth).toDouble() || mouseY >= (top + this.imageHeight).toDouble()
        return this.recipeBook.hasClickedOutside(
            mouseX,
            mouseY,
            this.leftPos,
            this.topPos,
            this.imageWidth,
            this.imageHeight,
            button
        ) && outside
    }

    override fun slotClicked(slot: Slot, slotId: Int, button: Int, actionType: ClickType) {
        super.slotClicked(slot, slotId, button, actionType)
        this.recipeBook.slotClicked(slot)
    }

    override fun recipesUpdated() {
        this.recipeBook.recipesUpdated()
    }

    override fun getRecipeBookComponent(): RecipeBookComponent {
        return this.recipeBook
    }

    companion object {
        private val TEXTURE = DimensionalDoors.id("textures/screen/container/tesselating_loom.png")
    }
}
