package StevenDimDoors.mod_pocketDimClient;

import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class PrivatePocketRender implements ISimpleBlockRenderingHandler
{
	public static int renderID;
	
	public PrivatePocketRender(int renderID)
	{
		PrivatePocketRender.renderID = renderID;
	}
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		Tessellator tessellator = Tessellator.instance;
		
		float f2;
		float f3;
		int k;
		  block.setBlockBoundsForItemRender();
		  renderer.setRenderBoundsFromBlock(block);
          GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
          GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
          tessellator.startDrawingQuads();
          tessellator.setNormal(0.0F, -1.0F, 0.0F);
          renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, metadata));
          tessellator.draw();

          if (renderer.useInventoryTint)
          {
              k = block.getRenderColor(metadata);
              f2 = (float)(k >> 16 & 255) / 255.0F;
              f3 = (float)(k >> 8 & 255) / 255.0F;
              float f7 = (float)(k & 255) / 255.0F;
              //GL11.glColor4f(f2 * par3, f3 * par3, f7 * par3, 1.0F);
          }

          tessellator.startDrawingQuads();
          tessellator.setNormal(0.0F, 1.0F, 0.0F);
          renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, metadata));
          tessellator.draw();

          if (renderer.useInventoryTint)
          {
           //   GL11.glColor4f(par3, par3, par3, 1.0F);
          }

          tessellator.startDrawingQuads();
          tessellator.setNormal(0.0F, 0.0F, -1.0F);
          renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, metadata));
          tessellator.draw();
          tessellator.startDrawingQuads();
          tessellator.setNormal(0.0F, 0.0F, 1.0F);
          renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, metadata));
          tessellator.draw();
          tessellator.startDrawingQuads();
          tessellator.setNormal(-1.0F, 0.0F, 0.0F);
          renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, metadata));
          tessellator.draw();
          tessellator.startDrawingQuads();
          tessellator.setNormal(1.0F, 0.0F, 0.0F);
          renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, metadata));
          tessellator.draw();
          GL11.glTranslatef(0.5F, 0.5F, 0.5F);	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{

        Tessellator tessellator = Tessellator.instance;
        boolean flag = false;

        IIcon icon = renderer.getBlockIcon(block, world, x, y, z, 2);
        


        tessellator.setColorOpaque_F(1F, 1F, 1F);
        
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y - 1, z, 0))
        {
            renderer.renderFaceYNeg(block, (double)x, (double)y, (double)z, icon);
            flag = true;
        }
        tessellator.setColorOpaque_F(1F, 1F, 1F);

        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y + 1, z, 1))
        {
            renderer.renderFaceYPos(block, (double)x, (double)y, (double)z, icon);
            flag = true;
        }

      
        tessellator.setColorOpaque_F(1F, 1F, 1F);

        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y, z - 1, 2))
        {
            renderer.renderFaceZNeg(block, (double)x, (double)y, (double)z, icon);


            flag = true;
        }
        tessellator.setColorOpaque_F(1F, 1F, 1F);

        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y, z + 1, 3))
        {
            renderer.renderFaceZPos(block, (double)x, (double)y, (double)z, icon);

           

            flag = true;
        }
        tessellator.setColorOpaque_F(1F, 1F, 1F);

        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x - 1, y, z, 4))
        {
            renderer.renderFaceXNeg(block, (double)x, (double)y, (double)z, icon);

           

            flag = true;
        }
        tessellator.setColorOpaque_F(1F, 1F, 1F);

        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x + 1, y, z, 5))
        {
            renderer.renderFaceXPos(block, (double)x, (double)y, (double)z, icon);

       

            flag = true;
        }

        return flag;
	}

	
	@Override
	public boolean shouldRender3DInInventory(int data)
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int getRenderId()
	{
		// TODO Auto-generated method stub
		return renderID;
	}

}
