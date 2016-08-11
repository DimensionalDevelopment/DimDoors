package com.zixiken.dimdoors.client;

import static org.lwjgl.opengl.GL11.*;

import com.zixiken.dimdoors.tileentities.TileEntityRift;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderRift extends TileEntitySpecialRenderer {
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
		// prepare fb for drawing
		GL11.glPushMatrix();

		// make the rift render on both sides, disable texture mapping and
		// lighting
		GlStateManager.disableCull();
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		/**
		 * GL11.glLogicOp(GL11.GL_INVERT);
		 * GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
		 */
		TileEntityRift rift = (TileEntityRift) te;
		// draws the verticies corresponding to the passed it

		// reenable all the stuff we disabled
		GlStateManager.disableBlend();
		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();

		GlStateManager.popMatrix();
	}
}