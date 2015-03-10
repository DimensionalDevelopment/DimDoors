package StevenDimDoors.mod_pocketDimClient;

import static org.lwjgl.opengl.GL11.*;
import java.awt.Point;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityRift;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderRift extends TileEntitySpecialRenderer
{

	@Override
	public void renderTileEntityAt(TileEntity te, double xWorld, double yWorld, double zWorld, float f)
	{
		// prepare fb for drawing
		GL11.glPushMatrix();

		// make the rift render on both sides, disable texture mapping and
		// lighting
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL_TEXTURE_2D);
		GL11.glDisable(GL_LIGHTING);
		GL11.glEnable(GL_BLEND);
		/**
		 * GL11.glLogicOp(GL11.GL_INVERT);
		 * GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
		 */
		TileEntityRift rift = (TileEntityRift) te;
		// draws the verticies corresponding to the passed it

		GL11.glDisable(GL_BLEND);
		// reenable all the stuff we disabled
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL_TEXTURE_2D);

		GL11.glPopMatrix();
	}
}