package StevenDimDoors.mod_pocketDim.blocks;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.world.PocketProvider;

public class dimHatch extends BlockTrapDoor
{

	public dimHatch(int par1,int par2, Material par2Material) 
	{
		super(par1, Material.iron);
		this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);
		// this.setTextureFile("/PocketBlockTextures.png");
		//	 this.blockIndexInTexture =  16;
	}

	public void registerIcons(IconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2());

	}

	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
	{

		{
			int var10 = par1World.getBlockMetadata(par2, par3, par4);
			par1World.setBlockMetadataWithNotify(par2, par3, par4, var10 ^ 4,2);
			par1World.playAuxSFXAtEntity(par5EntityPlayer, 1003, par2, par3, par4, 0);
			return true;
		}
	}

	//Teleports the player to the exit link of that dimension, assuming it is a pocket
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) 
	{
		int num = par1World.getBlockMetadata(par2, par3, par4);

		if (!par1World.isRemote&&(num>3&&num<8||num>11)&&par1World.provider instanceof PocketProvider)
		{
			this.onPoweredBlockChange(par1World, par2, par3, par4, false);

			/* FIXME: No point in fixing the following code when it's going to be rewritten later anyway. ~SenseiKiwi

			NewDimData newDimData = (NewDimData) dimHelper.PocketManager.dimList.get(par1World.provider.dimensionId);
			ILinkData exitLink=newDimData.exitDimLink;
			exitLink.locDimID=par1World.provider.dimensionId;
			PocketManager.instance.traverseDimDoor(par1World, exitLink, par5Entity);*/
		}
	}

	public void onPoweredBlockChange(World par1World, int par2, int par3, int par4, boolean par5)
	{
		int var6 = par1World.getBlockMetadata(par2, par3, par4);
		boolean var7 = (var6 & 4) > 0;

		if (var7 != par5)
		{
			par1World.setBlockMetadataWithNotify(par2, par3, par4, var6 ^ 4,2);
			par1World.playAuxSFXAtEntity((EntityPlayer)null, 1003, par2, par3, par4, 0);
		}
	}
}