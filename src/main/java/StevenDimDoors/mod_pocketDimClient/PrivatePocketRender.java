package StevenDimDoors.mod_pocketDimClient;

import org.lwjgl.opengl.GL11;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class PrivatePocketRender implements ISimpleBlockRenderingHandler
{
	public static int renderID;
	
	public PrivatePocketRender(int renderID)
	{
		super();
		PrivatePocketRender.renderID = renderID;
	}
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
	{

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{

        Tessellator tessellator = Tessellator.instance;
        boolean flag = false;

        Icon icon = renderer.getBlockIcon(block, world, x, y, z, 2);
        


        tessellator.setColorOpaque_F(1F, 1F, 1F);
        
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y - 1, z, 0))
        {
            renderer.renderFaceYNeg(block, (double)x, (double)y, (double)z, icon);
            flag = true;
        }

        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y + 1, z, 1))
        {
            renderer.renderFaceYPos(block, (double)x, (double)y, (double)z, icon);
            flag = true;
        }

      

        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y, z - 1, 2))
        {
            renderer.renderFaceZNeg(block, (double)x, (double)y, (double)z, icon);


            flag = true;
        }

        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y, z + 1, 3))
        {
            renderer.renderFaceZPos(block, (double)x, (double)y, (double)z, icon);

           

            flag = true;
        }

        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x - 1, y, z, 4))
        {
            renderer.renderFaceXNeg(block, (double)x, (double)y, (double)z, icon);

           

            flag = true;
        }

        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x + 1, y, z, 5))
        {
            renderer.renderFaceXPos(block, (double)x, (double)y, (double)z, icon);

       

            flag = true;
        }

        return flag;
	}

	
	@Override
	public boolean shouldRender3DInInventory()
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
