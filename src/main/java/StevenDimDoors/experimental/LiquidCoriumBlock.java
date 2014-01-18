package StevenDimDoors.experimental;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class LiquidCoriumBlock extends BlockFluidFinite
{
	private Icon iconFlowing;
    private Icon iconStill;
	 
	public static Point3D[] spreadPoints= new Point3D[4];
	public LiquidCoriumBlock(int id, Fluid fluid, Material material) 
	{
		super(id, fluid, material);
		this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);
	}
	
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) 
    {
    	par1World.setBlock(par2, par3, par4, this.blockID,15,2);
    }
    
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand)
	{
		boolean didChange=false;
		int fluid = this.getQuantaValue(world, x, y, z);
		int blockBeneath = world.getBlockId(x, y-1,z);
		
		if(!(blockBeneath==0||blockBeneath==this.blockID))
		{
			for(int xCount=-1;xCount<2;xCount++)
			{
				for(int yCount=-1;yCount<1;yCount++)
				{
					for(int zCount=-1;zCount<2;zCount++)
					{
						int id= world.getBlockId(x+xCount, y+yCount, z+zCount);
						if(!(id ==0||id==this.blockID||id==Block.bedrock.blockID)&&!(Math.abs(zCount)+Math.abs(yCount)+Math.abs(xCount)>1))
						{
							Block block =Block.blocksList[id]; 
							if(block.getUnlocalizedName().contains("ore"))
							{
								world.setBlock(x+xCount, y+yCount, z+zCount,this.blockID,6,2);		
							}
							if(fluid>block.blockHardness*2&&yCount==0&&rand.nextInt(3)==0)
							{
								didChange=true;;
								world.setBlock(x+xCount, y+yCount, z+zCount,0); 
							}
							else if(fluid>block.blockHardness*2+1&&yCount==-1&&!didChange&&rand.nextBoolean())
							{
								world.setBlock(x+xCount, y+yCount, z+zCount, 0);
							}
							 
						
						}		
					}
				}
			}
		}
		
		if((fluid==1)&&blockBeneath!=this.blockID&&blockBeneath!=Block.bedrock.blockID)
		{
			world.setBlockToAir(x, y, z);
			world.setBlock(x, y-1, z,Block.bedrock.blockID);
		}
		super.updateTick(world, x, y, z, rand);

	}
	
	 @SideOnly(Side.CLIENT)
     @Override
     public void registerIcons(IconRegister ir)
     {

             iconStill =  ir.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName()+"_still");
             iconFlowing =  ir.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName()+"_flowing");
     }
	 @Override
     public Icon getIcon(int side, int meta)
     {
             return side <= 1 ? iconStill : iconFlowing;
     }


}
