package org.dimdev.dimdoors.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.screen.TesselatingScreenHandler;

public class TesselatingLoomScreen extends HandledScreen<TesselatingScreenHandler> {
	private static final Identifier TEXTURE = new Identifier("dimdoors", "textures/screen/container/tesselating_loom.png");

	private final RecipeBookWidget recipeBook;

	public TesselatingLoomScreen(TesselatingScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.recipeBook = new RecipeBookWidget();
	}

	public void init() {
		super.init();
		this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
	}


	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		this.drawBackground(matrices, delta, mouseX, mouseY);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		int i = this.x;
		int j = this.y;
		this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);

		if (this.handler.isWeaving()) {
			int k = (this.handler).getBurnProgress(22);
			this.drawTexture(matrices, i + 80, j + 28, 176, 0, 16, k + 1);
		}
	}
}
