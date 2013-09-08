package StevenDimDoors.mod_pocketDimClient;

import java.nio.FloatBuffer;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.blocks.TransTrapdoor;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityTransTrapdoor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTransTrapdoor extends TileEntitySpecialRenderer
{
    private FloatBuffer field_76908_a = GLAllocation.createDirectFloatBuffer(16);
	private static DDProperties properties = null;
	
    public RenderTransTrapdoor()
    {
		if (properties == null)
			properties = DDProperties.instance();
	}
    
    /**
     * Renders the dimdoor.
     */
    public void renderTransTrapdoorTileEntity(TileEntityTransTrapdoor tile, double x, double y, double z, float par8)
    {
    	try
    	{
    		mod_pocketDim.transTrapdoor.updateAttachedTile(tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	
       // float playerX = (float)this.tileEntityRenderer.playerX;
       // float playerY = (float)this.tileEntityRenderer.playerY;
       // float playerZ = (float)this.tileEntityRenderer.playerZ;
        
        //float distance = (float) tile.getDistanceFrom(playerX, playerY, playerZ);
        GL11.glDisable(GL11.GL_LIGHTING);
        Random random = new Random(31100L);
        int metadata = tile.worldObj.getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord);
        	
        for (int count = 0; count < 16; ++count)
        {
            GL11.glPushMatrix();
            float var15 = (float)(16 - count);
            float var16 = 0.2625F;
            float var17 = 1.0F / (var15 + 1.0F);

            if (count == 0)
            {
                this.bindTextureByName("/RIFT.png");
                var17 = 0.1F;
                var15 = 25.0F;
                var16 = 0.125F;
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            }

            if (count == 1)
            {
                this.bindTextureByName("/WARP.png");
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
                var16 = .5F;
            }

            GL11.glTranslatef( (float)(Minecraft.getSystemTime() % 200000L) / 200000.0F,0, 0.0F);
            GL11.glTranslatef(0, (float)(Minecraft.getSystemTime() % 200000L) / 200000.0F, 0.0F);

            GL11.glTranslatef(0,0, (float)(Minecraft.getSystemTime() % 200000L) / 200000.0F);

            GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
            GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
            GL11.glTexGeni(GL11.GL_R, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
            GL11.glTexGeni(GL11.GL_Q, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_EYE_LINEAR);
         
            GL11.glTexGen(GL11.GL_S, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(1.0F, 0.0F, 0.0F, 0.0F));
            GL11.glTexGen(GL11.GL_T, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 0.0F, 1.0F, 0.0F));
            GL11.glTexGen(GL11.GL_R, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 0.0F, 0.0F, 1.0F));
            GL11.glTexGen(GL11.GL_Q, GL11.GL_EYE_PLANE, this.getFloatBuffer(0.0F, 1.0F, 0.0F, 0.0F));

            
            GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
            GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
            GL11.glEnable(GL11.GL_TEXTURE_GEN_R);
            GL11.glEnable(GL11.GL_TEXTURE_GEN_Q);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0F, (float)(Minecraft.getSystemTime() % 200000L) / 200000.0F*var15, 0.0F);
            GL11.glScalef(var16, var16, var16);
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            GL11.glRotatef((float)(count * count * 4321 + count * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        
            GL11.glBegin(GL11.GL_QUADS);
            
            float var21 = random.nextFloat() * 0.5F + 0.1F;
            float var22 = random.nextFloat() * 0.4F + 0.4F;
            float var23 = random.nextFloat() * 0.6F + 0.5F;

            if (count == 0)
            {
                var23 = 1.0F;
                var22 = 1.0F;
            }
            GL11.glColor4d(var21 * var17, var22 * var17, var23 * var17, 1.0F);
            if (TransTrapdoor.isTrapdoorSetLow(metadata))
            {
            	if (TransTrapdoor.isTrapdoorOpen(metadata))
            	{
            		GL11.glVertex3d(x, y+0.2, z);
                	GL11.glVertex3d(x, y+0.2,  z+1);
                	GL11.glVertex3d(x+1 , y+0.2 , z+1);
                	GL11.glVertex3d(x+1 , y+0.2 , z);
            	}
            	else
            	{
            		GL11.glVertex3d(x, y+0.15, z);
                	GL11.glVertex3d(x, y+0.15,  z+1);
                	GL11.glVertex3d(x+1 , y+0.15 , z+1);
                	GL11.glVertex3d(x+1 , y+0.15 , z);
            	}
            }
            else
            {
            	if (TransTrapdoor.isTrapdoorOpen(metadata))
            	{
            		GL11.glVertex3d(x, y+0.95, z);
                	GL11.glVertex3d(x, y+0.95,  z+1);
                	GL11.glVertex3d(x+1 , y+0.95 , z+1);
                	GL11.glVertex3d(x+1 , y+0.95 , z);
            	}
            	else
            	{
            		GL11.glVertex3d(x, y+0.85, z);
                	GL11.glVertex3d(x, y+0.85,  z+1);
                	GL11.glVertex3d(x+1 , y+0.85 , z+1);
                	GL11.glVertex3d(x+1 , y+0.85 , z);
            	}
            }
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_S);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_R);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_Q);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    private FloatBuffer getFloatBuffer(float par1, float par2, float par3, float par4)
    {
        this.field_76908_a.clear();
        this.field_76908_a.put(par1).put(par2).put(par3).put(par4);
        this.field_76908_a.flip();
        return this.field_76908_a;
    }

    public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
    {
    	if (properties.DoorRenderingEnabled)
    	{
    		this.renderTransTrapdoorTileEntity((TileEntityTransTrapdoor)par1TileEntity, par2, par4, par6, par8);
    	}
    }
}
