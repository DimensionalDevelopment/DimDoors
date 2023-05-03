package org.dimdev.dimdoors.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.screen.TesselatingScreenHandler;

public class TesselatingLoomScreen extends HandledScreen<TesselatingScreenHandler> implements RecipeBookProvider {
	private static final Identifier TEXTURE = DimensionalDoors.id("textures/screen/container/tesselating_loom.png");
	private static final Identifier RECIPE_BUTTON_TEXTURE = new Identifier("textures/gui/recipe_button.png");

	private final RecipeBookWidget recipeBook = new RecipeBookWidget();
	private boolean narrow;

	public TesselatingLoomScreen(TesselatingScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	public void init() {
		super.init();
		this.narrow = this.width < 379;
		this.recipeBook.initialize(this.width, this.height, this.client, this.narrow, this.handler);
		this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
		this.addDrawableChild(new TexturedButtonWidget(this.x + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, (button) -> {
			this.recipeBook.toggleOpen();
			this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
			button.setPosition(this.x + 5, this.height / 2 - 49);
		}));
		this.addSelectableChild(this.recipeBook);
		this.setInitialFocus(this.recipeBook);
		this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
	}


	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		if (this.recipeBook.isOpen() && this.narrow) {
			this.drawBackground(matrices, delta, mouseX, mouseY);
			this.recipeBook.render(matrices, mouseX, mouseY, delta);
		} else {
			this.recipeBook.render(matrices, mouseX, mouseY, delta);
			super.render(matrices, mouseX, mouseY, delta);
			this.recipeBook.drawGhostSlots(matrices, this.x, this.y, true, delta);
		}

		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
		this.recipeBook.drawTooltip(matrices, this.x, this.y, mouseX, mouseY);
	}

	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		int i = this.x;
		int j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);

		if (this.handler.isWeaving()) {
			int k = (this.handler).getBurnProgress(22);
			this.drawTexture(matrices, i + 89, j + 34, 176, 0, k + 1, 16);
		}
	}

	protected boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
		return (!this.narrow || !this.recipeBook.isOpen()) && super.isPointWithinBounds(x, y, width, height, pointX, pointY);
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.recipeBook.mouseClicked(mouseX, mouseY, button)) {
			this.setFocused(this.recipeBook);
			return true;
		} else {
			return this.narrow && this.recipeBook.isOpen() || super.mouseClicked(mouseX, mouseY, button);
		}
	}

	protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
		boolean bl = mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
		return this.recipeBook.isClickOutsideBounds(mouseX, mouseY, this.x, this.y, this.backgroundWidth, this.backgroundHeight, button) && bl;
	}

	protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
		super.onMouseClick(slot, slotId, button, actionType);
		this.recipeBook.slotClicked(slot);
	}

	public void refreshRecipeBook() {
		this.recipeBook.refresh();
	}

	public RecipeBookWidget getRecipeBookWidget() {
		return this.recipeBook;
	}
}
