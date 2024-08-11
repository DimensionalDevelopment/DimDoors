package org.dimdev.dimdoors.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.screen.TessellatingContainer;

public class TesselatingLoomScreen extends AbstractContainerScreen<TessellatingContainer> /*implements RecipeUpdateListener*/ {
	private static final ResourceLocation TEXTURE = DimensionalDoors.id("textures/screen/container/tesselating_loom.png");
	private static final ResourceLocation RECIPE_BUTTON_TEXTURE = ResourceLocation.tryParse("textures/gui/recipe_button.png");

//	private final RecipeBookComponent recipeBook = new RecipeBookComponent();
//	private boolean narrow;

	public TesselatingLoomScreen(TessellatingContainer handler, Inventory inventory, Component title) {
		super(handler, inventory, title);
	}

	public void init() {
		super.init();
//		this.narrow = this.width < 379;
//		this.recipeBook.init(this.width, this.height, this.minecraft, this.narrow, this.menu);
//		this.leftPos = this.recipeBook.updateScreenPosition(this.width, this.imageWidth);
//		this.addRenderableWidget(new ImageButton(this.leftPos + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, (button) -> {
//			this.recipeBook.toggleVisibility();
//			this.leftPos = this.recipeBook.updateScreenPosition(this.width, this.imageWidth);
//			button.setPosition(this.leftPos + 5, this.height / 2 - 49);
//		}));
//		this.addWidget(this.recipeBook);
//		this.setInitialFocus(this.recipeBook);
		this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
	}


	public void render(GuiGraphics matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices, mouseX, mouseY, delta);
//		if (this.recipeBook.isVisible() && this.narrow) {
//			this.renderBg(matrices, delta, mouseX, mouseY);
//			this.recipeBook.render(matrices, mouseX, mouseY, delta);
//		} else {
//			this.recipeBook.render(matrices, mouseX, mouseY, delta);
			super.render(matrices, mouseX, mouseY, delta);
//			this.recipeBook.renderGhostRecipe(matrices, this.leftPos, this.topPos, true, delta);
//		}

		this.renderTooltip(matrices, mouseX, mouseY);
//		this.recipeBook.renderTooltip(matrices, this.leftPos, this.topPos, mouseX, mouseY);
	}

	protected void renderBg(GuiGraphics matrices, float delta, int mouseX, int mouseY) {
		int i = this.leftPos;
		int j = (this.height - this.imageHeight) / 2;
		matrices.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageWidth);

		if (this.menu.isWeaving()) {
			int k = (this.menu).getWeavProgress(22);
			matrices.blit(TEXTURE, i + 89, j + 34, 176, 0, k + 1, 16);
		}
	}

	protected boolean isHovering(int x, int y, int width, int height, double pointX, double pointY) {
		return /*(!this.narrow || !this.recipeBook.isVisible()) && */super.isHovering(x, y, width, height, pointX, pointY);
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
//		if (this.recipeBook.mouseClicked(mouseX, mouseY, button)) {
//			this.setFocused(this.recipeBook);
//			return true;
//		} else {
			return /*this.narrow && this.recipeBook.isVisible() ||*/ super.mouseClicked(mouseX, mouseY, button);
//		}
	}

	protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
		boolean bl = mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.imageWidth) || mouseY >= (double)(top + this.imageHeight);
		return /*this.recipeBook.hasClickedOutside(mouseX, mouseY, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, button) &&*/ bl;
	}

	protected void slotClicked(Slot slot, int slotId, int button, ClickType actionType) {
		super.slotClicked(slot, slotId, button, actionType);
//		this.recipeBook.slotClicked(slot);
	}

//	public void recipesUpdated() {
//		this.recipeBook.recipesUpdated();
//	}

//	public RecipeBookComponent getRecipeBookComponent() {
//		return this.recipeBook;
//	}
}
