package StevenDimDoors.mod_pocketDim.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import StevenDimDoors.mod_pocketDim.BlankTeleporter;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import StevenDimDoors.mod_pocketDim.helpers.yCoordHelper;
import cpw.mods.fml.common.FMLCommonHandler;

public class BlockDimWallPerm extends Block
{
	private static DDProperties properties = null;
	
	public BlockDimWallPerm(int i, int j, Material par2Material) 
	{
		super(i, Material.ground);
        this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);
		if (properties == null)
			properties = DDProperties.instance();
	}

	public void registerIcons(IconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2());
	}

	public int quantityDropped(Random par1Random)
	{
		return 0;
	}

	public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5) {}

	/**
	 * Only matters if the player is in limbo, acts to teleport the player from limbo back to dim 0
	 */
	public void onEntityWalking(World par1World, int par2, int par3, int par4, Entity par5Entity) 
	{
		if(!par1World.isRemote&&par1World.provider.dimensionId==properties.LimboDimensionID)
		{
			Random rand = new Random();

			LinkData link=dimHelper.instance.getRandomLinkData(false);
			if(link==null)
			{
				link =new LinkData(0,0,0,0);    		
			}
			link.destDimID = 0;
			link.locDimID = par1World.provider.dimensionId;


			if(dimHelper.getWorld(0)==null)
			{
				dimHelper.initDimension(0);
			}


			if(dimHelper.getWorld(0)!=null&&par5Entity instanceof EntityPlayerMP)
			{
				par5Entity.fallDistance=0;
				int x = (link.destXCoord + rand.nextInt(properties.LimboReturnRange)-properties.LimboReturnRange/2);
				int z = (link.destZCoord + rand.nextInt(properties.LimboReturnRange)-properties.LimboReturnRange/2);

				//make sure I am in the middle of a chunk, and not on a boundary, so it doesn't load the chunk next to me
				x = x + (x >> 4);
				z = z + (z >> 4);

				int y = yCoordHelper.getFirstUncovered(0, x, 63, z, true);
				
				EntityPlayer.class.cast(par5Entity).setPositionAndUpdate( x, y, z );
				//this complicated chunk teleports the player back to the overworld at some random location. Looks funky becaue it has to load the chunk
				link.destXCoord = x;
				link.destYCoord = y;
				link.destZCoord = z;
				dimHelper.instance.teleportEntity(par1World, par5Entity, link);
				//FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().transferPlayerToDimension((EntityPlayerMP) par5Entity, 0,new BlankTeleporter((WorldServer)par5Entity.worldObj));
				//dimHelper.instance.teleportToPocket(par1World, new LinkData(par1World.provider.dimensionId,0,x,y,z,link.locXCoord,link.locYCoord,link.locZCoord,link.isLocPocket,0), 
				//		EntityPlayer.class.cast(par5Entity));


				EntityPlayer.class.cast(par5Entity).setPositionAndUpdate( x, y, z );

				// Make absolutely sure the player doesn't spawn inside blocks, though to be honest this shouldn't ever have to be a problem...
				dimHelper.getWorld(0).setBlock(x, y, z, 0);
				dimHelper.getWorld(0).setBlock(x, y+1, z, 0);
				
				int i=x;
				int j=y;
				int k=z;


				for(int xc=-3;xc<4;xc++)
				{
					for(int zc=-3;zc<4;zc++)
					{
						for(int yc=0;yc<200;yc++)
						{
							if(yc==0)
							{

								if(Math.abs(xc)+Math.abs(zc)<rand.nextInt(3)+2)
								{
									dimHelper.getWorld(0).setBlock(i+xc, j-1+yc, k+zc, properties.LimboBlockID);
								}
								else if(Math.abs(xc)+Math.abs(zc)<rand.nextInt(3)+3)

								{
									dimHelper.getWorld(0).setBlock(i+xc, j-1+yc, k+zc,  properties.LimboBlockID,2,0);

								}
							}

						}

					}
				}



				{
					EntityPlayer.class.cast(par5Entity).setPositionAndUpdate( x, y, z );
					EntityPlayer.class.cast(par5Entity).fallDistance=0;
				}
				
				


			}
		}
	}
}
