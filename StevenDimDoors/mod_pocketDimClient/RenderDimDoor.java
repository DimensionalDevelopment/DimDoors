package StevenDimDoors.mod_pocketDimClient;

import java.nio.FloatBuffer;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.blocks.DimensionalDoor;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityDimDoor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderDimDoor extends TileEntitySpecialRenderer
{
    FloatBuffer field_76908_a = GLAllocation.createDirectFloatBuffer(16);

    public RenderDimDoor()
    {
		if (properties == null)
			properties = DDProperties.instance();
	}
	
	private static DDProperties properties = null;
    
    /**
     * Renders the dimdoor.
     */
    public void renderDimDoorTileEntity(TileEntityDimDoor tile, double x, double y, double z, float par8)
    {
    	try
    	{
    		mod_pocketDim.dimensionalDoor.updateAttachedTile(tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord);
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
        Random rand = new Random(31100L);
        float var13 = 0.75F;

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

            float startY = (float)(+(y + (double)var13));
            float ratioY = startY + ActiveRenderInfo.objectY;
            float ratioY2 = startY + var15 + ActiveRenderInfo.objectY;
            float yConverted = ratioY / ratioY2;
           
            float startZ = (float)(+(z + (double)var13));
            float ratioZ = startZ + ActiveRenderInfo.objectZ;
            float ratioZ2 = startZ + var15 + ActiveRenderInfo.objectZ;
            float zConverted = ratioZ / ratioZ2;
           
            float startX = (float)(+(x + (double)var13));
            float ratioX = startX + ActiveRenderInfo.objectX;
            float ratioX2 = startX + var15 + ActiveRenderInfo.objectX;
            float xConverted = ratioX / ratioX2;
 
          

            
            yConverted += (float)(y + (double)var13);
            xConverted += (float)(x + (double)var13);
            zConverted += (float)(z + (double)var13);

            GL11.glTranslatef( (float)(Minecraft.getSystemTime() % 200000L) / 200000.0F,0, 0.0F);
            GL11.glTranslatef(0, (float)(Minecraft.getSystemTime() % 200000L) / 200000.0F, 0.0F);

            GL11.glTranslatef(0,0, (float)(Minecraft.getSystemTime() % 200000L) / 200000.0F);

            GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
            GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
            GL11.glTexGeni(GL11.GL_R, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
            GL11.glTexGeni(GL11.GL_Q, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
            switch ((tile.orientation%4)+4)
            {
            case 4:
            	GL11.glTexGen(GL11.GL_S, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                GL11.glTexGen(GL11.GL_T, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                GL11.glTexGen(GL11.GL_R, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                GL11.glTexGen(GL11.GL_Q, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(1.0F, 0.0F, 0.0F, 0.0F));
            	 
            	break;
            case 5:
            	
            	GL11.glTexGen(GL11.GL_S, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                GL11.glTexGen(GL11.GL_T, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                GL11.glTexGen(GL11.GL_R, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                GL11.glTexGen(GL11.GL_Q, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 0.0F, 1.0F, 0.0F));
            	break;
            case 6:
            	GL11.glTexGen(GL11.GL_S, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                GL11.glTexGen(GL11.GL_T, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                GL11.glTexGen(GL11.GL_R, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                GL11.glTexGen(GL11.GL_Q, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(1.0F, 0.0F, 0.0F, 0.0F));
           
            	break;
            case 7:
            	GL11.glTexGen(GL11.GL_S, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                GL11.glTexGen(GL11.GL_T, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                GL11.glTexGen(GL11.GL_R, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                GL11.glTexGen(GL11.GL_Q, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 0.0F, 1.0F, 0.0F));
            	break;
            	
            		
            	
            }
           
          
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
           
            
            
            
            float var21 = rand.nextFloat() * 0.5F + 0.1F;
            float var22 = rand.nextFloat() * 0.4F + 0.4F;
            float var23 = rand.nextFloat() * 0.6F + 0.5F;

            if (count == 0)
            {
                var23 = 1.0F;
                var22 = 1.0F;
                yConverted = 1.0F;
            }
            GL11.glColor4d(var21 * var17, var22 * var17, var23 * var17, 1.0F);
            if(tile.openOrClosed)
            {
            	
            switch (tile.orientation)
            {
            case 0:
            	
            	GL11.glVertex3d(x+.01F, y-1 , z);
            	GL11.glVertex3d(x+.01, y-1, z+1.0D);
            	GL11.glVertex3d(x+.01 , y+1 , z + 1.0D);
            	GL11.glVertex3d(x+.01 , y+1 , z);
            	break;
            case 1:
            	GL11.glVertex3d(x , y+1 , z+.01);
            	GL11.glVertex3d(x+1 , y+1 , z+.01);
            	GL11.glVertex3d(x+1, y-1,  z+.01);
            	GL11.glVertex3d(x, y-1, z+.01);



            	break;
            case 2:     //      
                GL11.glVertex3d(x+.99 , y+1 , z);
                GL11.glVertex3d(x+.99 , y+1 , z + 1.0D);
                GL11.glVertex3d(x+.99, y-1, z+1.0D);
                GL11.glVertex3d(x+.99, y-1, z);
                break;
            case 3:
            	GL11.glVertex3d(x, y-1, z+.99);
            	GL11.glVertex3d(x+1, y-1,  z+.99);
            	GL11.glVertex3d(x+1 , y+1 , z+.99);
            	GL11.glVertex3d(x , y+1 , z+.99);
                break;
            case 4://
             //   GL11.glTranslatef();

            	GL11.glVertex3d(x+.15F, y-1 , z);
            	GL11.glVertex3d(x+.15, y-1, z+1.0D);
            	GL11.glVertex3d(x+.15 , y+1 , z + 1.0D);
            	GL11.glVertex3d(x+.15 , y+1 , z);
            	break;
            case 5:
            	GL11.glVertex3d(x , y+1 , z+.15);
            	GL11.glVertex3d(x+1 , y+1 , z+.15);
            	GL11.glVertex3d(x+1, y-1,  z+.15);
            	GL11.glVertex3d(x, y-1, z+.15);



            	break;
            case 6:     //      
                GL11.glVertex3d(x+.85 , y+1 , z);
                GL11.glVertex3d(x+.85 , y+1 , z + 1.0D);
                GL11.glVertex3d(x+.85, y-1, z+1.0D);
                GL11.glVertex3d(x+.85, y-1, z);
                break;
            case 7:
            	GL11.glVertex3d(x, y-1, z+.85);
            	GL11.glVertex3d(x+1, y-1,  z+.85);
            	GL11.glVertex3d(x+1 , y+1 , z+.85);
            	GL11.glVertex3d(x , y+1 , z+.85);
                break;
            default:
            	break;
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
    		this.renderDimDoorTileEntity((TileEntityDimDoor)par1TileEntity, par2, par4, par6, par8);
    	}
    }
}
