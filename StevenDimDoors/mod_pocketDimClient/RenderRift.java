package StevenDimDoors.mod_pocketDimClient;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_DST_COLOR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL11.glBlendFunc;

import java.util.HashMap;

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
	public void renderTileEntityAt(TileEntity te, double xWorld, double yWorld,
			double zWorld, float f) 
	{
		yWorld = yWorld+.75;
		GL11.glPushMatrix();
		
		GL11.glDisable(GL11.GL_CULL_FACE);
	    GL11.glDisable(GL_TEXTURE_2D);
	    GL11.glDisable(GL_LIGHTING);

	    //GL11.glLogicOp(GL11.GL_INVERT);
	   // GL11.glEnable(GL11.GL_COLOR_LOGIC_OP); 
	    
	    GL11.glColor4f(.2F, .2F, .2F, 1F);
	    
	    GL11.glEnable(GL_BLEND);
	    glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ZERO);

	    /**
	     * just draws the verticies
	     */
		this.drawCrack(TileEntityRift.class.cast(te).renderingCenters, xWorld, yWorld, zWorld);
		this.drawCrackRotated(TileEntityRift.class.cast(te).renderingCenters, xWorld, yWorld, zWorld);
	
		
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_LIGHTING);
	    GL11.glEnable(GL_TEXTURE_2D);

	    GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glPopMatrix();

	}
	
	public void drawCrack(HashMap<Integer, double[]> quads,double xWorld,double yWorld,double zWorld)
	{
	    GL11.glBegin(GL11.GL_QUAD_STRIP);

	    drawVertex(xWorld+.5, yWorld-Math.log(Math.pow(quads.size(),2)+1)/14, zWorld+.5);
	    drawVertex(xWorld+.5, yWorld+Math.log(Math.pow(quads.size(),2)+1)/14, zWorld+.5);
        for(int i = 0;;i++)
        {
        	if(!quads.containsKey(i))
        	{
        		break;
        	}
        	double[] coords = quads.get(i);
        	double width=Math.log(Math.pow(quads.size(),2-Math.log(i+1))+1)/14;
        	if(coords[3]==0)
        	{
        		
              if(quads.containsKey(i+1))
              {
            	  
            	  drawVertex(xWorld+coords[0]+.5, yWorld+coords[1]-width/2 , zWorld+coords[2]);
         		 drawVertex(xWorld+coords[0]+.5 , yWorld+coords[1]+width/2 , zWorld+coords[2]);
         		 
            	 
        		 
              }
              else
              {
            	  drawVertex(xWorld+coords[0]+.5, yWorld+coords[1]-width/200 , zWorld+coords[2]);
        		  drawVertex(xWorld+coords[0]+.5 , yWorld+coords[1]+width/200 , zWorld+coords[2]); 
              }
        	}
        	else
        	{
        		
        		
        		
        		 if(quads.containsKey(i+1))
                 {
        			 drawVertex(xWorld+coords[0], yWorld+coords[1]-width/2 , zWorld+coords[2]+.5);
                     drawVertex(xWorld+coords[0], yWorld+coords[1]+width/2 , zWorld+coords[2]+.5);
        			
             		
                 }
        		 else
        		 {
        			 drawVertex(xWorld+coords[0], yWorld+coords[1]+width/200 , zWorld+coords[2]+.5);
               		drawVertex(xWorld+coords[0], yWorld+coords[1]-width/200, zWorld+coords[2]+.5);
        		 }

                
        	}
        	
        }

       
       GL11.glEnd();
   
    
	}
	
	public void drawCrackRotated(HashMap<Integer, double[]> quads,double xWorld,double yWorld,double zWorld)
	{
	    GL11.glBegin(GL11.GL_QUAD_STRIP);

	    drawVertex(xWorld+.5, yWorld+Math.log(Math.pow(quads.size(),2)+1)/14, zWorld+.5);
	    drawVertex(xWorld+.5, yWorld-Math.log(Math.pow(quads.size(),2)+1)/14, zWorld+.5);
        for(int i = 0;;i++)
        {
        	if(!quads.containsKey(i))
        	{
        		break;
        	}
        	double[] coords = quads.get(i);
        	double width=Math.log(Math.pow(quads.size(),2-Math.log(i+1))+1)/14;
        	if(coords[3]==0)
        	{
     
        		
              if(quads.containsKey(i+1))
              {
            	  drawVertex(xWorld+coords[0]+.5, yWorld-(coords[1]-width/2) , zWorld-coords[2]+1);
          		 drawVertex(xWorld+coords[0]+.5 , yWorld-(coords[1]+width/2) , zWorld-coords[2]+1);  
        		
              }
              else
              {
            	
         		  drawVertex(xWorld+coords[0]+.5, yWorld-(coords[1]-width/200) , zWorld-coords[2]+1);
        		  drawVertex(xWorld+coords[0]+.5 , yWorld-(coords[1]+width/200) , zWorld-coords[2]+1);
              }

        	}
        	else
        	{		
        		
        		
        		 if(quads.containsKey(i+1))
                 {
        			
        			 drawVertex(xWorld-coords[0]+1, yWorld-(coords[1]-width/2) , zWorld+coords[2]+.5);
                     drawVertex(xWorld-coords[0]+1, yWorld-(coords[1]+width/2) , zWorld+coords[2]+.5);	
             		
                 }
        		  else
                  {
        			  drawVertex(xWorld-coords[0]+1, yWorld-(coords[1]+width/200) , zWorld+coords[2]+.5);
        			  drawVertex(xWorld-coords[0]+1, yWorld-(coords[1]-width/200), zWorld+coords[2]+.5);
                  }
 
        	}  	
        }

       GL11.glEnd();
	}
	
	public void testDraw(HashMap<Integer, double[]> quads,double xWorld,double yWorld,double zWorld)
	{
		GL11.glBegin(GL11.GL_QUADS);
	    for(int i = 0;;i++)
        {	
	    	
	    	if(!quads.containsKey(i))
        	{
        		break;
        	}
        	double[] coords = quads.get(i);
			drawVertex(xWorld-coords[0], yWorld , zWorld+.1);
			drawVertex(xWorld-coords[0], yWorld+.1 , zWorld+.1);
			drawVertex(xWorld-coords[0], yWorld +.1, zWorld);
			drawVertex(xWorld-coords[0], yWorld , zWorld);
			

        }
	    GL11.glEnd();

	}
	public void drawVertex(double x, double y, double z)
	{
  		GL11.glVertex3f((float)x,(float)y,(float)z);
	}
	public double[] rotateCoords(int rotation, double[] coords)
	{
		double[] rotatedCoords = new double[4];
		if(rotation == 180)
		{

			
			rotatedCoords[0]=-coords[0];
			rotatedCoords[1]=-coords[1];

			rotatedCoords[2]=-coords[2];
			rotatedCoords[3]=-coords[3];
			//return rotatedCoords;
		}
		return coords;
	
	}
}