package StevenDimDoors.mod_pocketDimClient;

import static org.lwjgl.opengl.GL11.*;
import java.awt.Point;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityRift;
import StevenDimDoors.mod_pocketDim.util.l_systems.LSystem;
import StevenDimDoors.mod_pocketDim.util.l_systems.LSystem.PolygonStorage;
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

		/**
		 * GL11.glLogicOp(GL11.GL_INVERT);
		 * GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
		 */
		
		// draws the verticies corresponding to the passed it
		this.drawCrack(((TileEntityRift) te).riftRotation, LSystem.curves.get("terdragon 7"), 3, xWorld, yWorld, zWorld);

		// reenable all the stuff we disabled
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL_TEXTURE_2D);

		GL11.glPopMatrix();
	}

	/**
	 * method that draws the fractal and applies animations/effects
	 * 
	 * f
	 * 
	 * @param riftRotation
	 * @param poly
	 * @param size
	 * @param xWorld
	 * @param yWorld
	 * @param zWorld
	 */
	public void drawCrack(int riftRotation, PolygonStorage poly, double size, double xWorld, double yWorld, double zWorld)
	{
		// calculate the proper size for the rift render
		double scale = size / (poly.maxX - poly.minX);

		// calculate the midpoint of the fractal bounding box
		double offsetX = ((poly.maxX + poly.minX)) / 2;
		double offsetY = ((poly.maxY + poly.minY)) / 2;
		double offsetZ = 0;

		// changes how far the triangles move
		float motionMagnitude = 2.0F;

		// changes how quickly the triangles move
		float motionSpeed = 800.0F;

		// number of individual jitter waveforms to generate
		// changes how "together" the overall motions are
		int jCount = 10;

		// Calculate jitter like for monoliths
		float time = (float) (((Minecraft.getSystemTime() + 0xF1234568 * this.hashCode()) % 2000000) / motionSpeed);
		double[] jitters = new double[jCount];

		// generate a series of waveforms
		for (int i = 0; i < jCount; i += 2)
		{
			jitters[i] = Math.sin((1F + i / 10F) * time) * Math.cos(1F - (i / 10F) * time) / motionMagnitude;
			jitters[i + 1] = Math.cos((1F + i / 10F) * time) * Math.sin(1F - (i / 10F) * time) / motionMagnitude;
		}

		// determines which jitter waveform we select. Modulo so the same point
		// gets the same jitter waveform over multiple frames
		int jIndex = 0;

		// set the color for the render
		GL11.glColor4f(.3F, .3F, .3F, 1F);

		//set the blending mode
		GL11.glEnable(GL_BLEND);
		//glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE);
		glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ZERO);
		
		// start rendering triangles for the moving shards
		GL11.glBegin(GL11.GL_TRIANGLES);
		for (Point p : poly.points)
		{
			jIndex = Math.abs(p.x+p.y);

			// calculate the rotation for the fractal, apply offset, and apply
			// jitter
			double x = (((p.x + jitters[(jIndex + 1) % jCount]) - offsetX) * Math.cos(Math.toRadians(riftRotation)) - jitters[(jIndex + 2) % jCount]
					* Math.sin(Math.toRadians(riftRotation)));
			double y = p.y + (jitters[jIndex % jCount]);
			double z = (((p.x + jitters[(jIndex + 2) % jCount]) - offsetX) * Math.sin(Math.toRadians(riftRotation)) + 0 * Math
					.cos(Math.toRadians(riftRotation)));

			// apply scaling
			x *= scale;
			y *= scale;
			z *= scale;

			// apply transform to center the offset origin into the middle of a
			// block
			x += .5;
			y += .5;
			z += .5;

			// draw the vertex and apply the world (screenspace) relative
			// coordinates
			GL11.glVertex3d(xWorld + x, yWorld + y, zWorld + z);
		}
		GL11.glEnd();

		//GL11.glDisable(GL_BLEND);
		//glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_DST_COLOR);
		//glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ZERO);

		/**
		GL11.glBegin(GL11.GL_TRIANGLES);

		// draw the next set of triangles to form a background and change their
		// color slightly over time
		for (Point p : poly.points)
		{
			jIndex++;

			double x = (((p.x) - offsetX) * Math.cos(Math.toRadians(riftRotation)) - 0 * Math.sin(Math.toRadians(riftRotation)));
			double y = p.y;
			double z = (((p.x) - offsetX) * Math.sin(Math.toRadians(riftRotation)) + 0 * Math.cos(Math.toRadians(riftRotation)));

			x *= scale;
			y *= scale;
			z *= scale;

			x += .5;
			y += .5;
			z += .5;

			// the additional divisors here determine the color of the
			// stationary shards
			if (jIndex % 3 == 0)
			{
				GL11.glColor4d(jitters[(jIndex + 1) % jCount] / 11, jitters[(jIndex + 2) % jCount] / 8, jitters[(jIndex) % jCount] / 8, 1);
			}
			
			GL11.glVertex3d(xWorld + x, yWorld + y, zWorld + z);
		}
		// stop drawing triangles
		GL11.glEnd();
		**/
	}
}