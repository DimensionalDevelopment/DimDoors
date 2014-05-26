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
		float par5 = .5F;
		float par6 = .5F;
		float par7 = .5F;
        Tessellator tessellator = Tessellator.instance;
        boolean flag = false;
        float f3 = 0.5F;
        float f4 = 1.0F;
        float f5 = 0.8F;
        float f6 = 0.6F;
        float f7 = f4 * par5;
        float f8 = f4 * par6;
        float f9 = f4 * par7;
        float f10 = f3;
        float f11 = f5;
        float f12 = f6;
        float f13 = f3;
        float f14 = f5;
        float f15 = f6;
        float f16 = f3;
        float f17 = f5;
        float f18 = f6;

        if (block != Block.grass)
        {
            f10 = f3 * par5;
            f11 = f5 * par5;
            f12 = f6 * par5;
            f13 = f3 * par6;
            f14 = f5 * par6;
            f15 = f6 * par6;
            f16 = f3 * par7;
            f17 = f5 * par7;
            f18 = f6 * par7;
        }

        tessellator.setColorOpaque_F(.89F, .89F, .89F);

        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y - 1, z, 0))
        {
            renderer.renderFaceYNeg(block, (double)x, (double)y, (double)z, renderer.getBlockIcon(block, world, x, y, z, 0));
            flag = true;
        }

        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y + 1, z, 1))
        {
            renderer.renderFaceYPos(block, (double)x, (double)y, (double)z, renderer.getBlockIcon(block, world, x, y, z, 1));
            flag = true;
        }

        Icon icon;

        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y, z - 1, 2))
        {
            icon = renderer.getBlockIcon(block, world, x, y, z, 2);
            renderer.renderFaceZNeg(block, (double)x, (double)y, (double)z, icon);


            flag = true;
        }

        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y, z + 1, 3))
        {
            icon = renderer.getBlockIcon(block, world, x, y, z, 3);
            renderer.renderFaceZPos(block, (double)x, (double)y, (double)z, icon);

           

            flag = true;
        }

        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x - 1, y, z, 4))
        {
            icon = renderer.getBlockIcon(block, world, x, y, z, 4);
            renderer.renderFaceXNeg(block, (double)x, (double)y, (double)z, icon);

           

            flag = true;
        }

        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x + 1, y, z, 5))
        {
            icon = renderer.getBlockIcon(block, world, x, y, z, 5);
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
