package StevenDimDoors.mod_pocketDim.blocks;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.PacketHandler;
import StevenDimDoors.mod_pocketDim.TileEntityRift;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import StevenDimDoors.mod_pocketDimClient.ClosingRiftFX;
import StevenDimDoors.mod_pocketDimClient.GoggleRiftFX;
import StevenDimDoors.mod_pocketDimClient.RiftFX;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRift extends BlockContainer
{
	private static DDProperties properties = null;
	
	public BlockRift(int i, int j, Material par2Material) 
	{
		super(i, Material.air);
		setTickRandomly(true);
		//  this.setCreativeTab(CreativeTabs.tabBlock);
		this.setLightOpacity(14);
		if (properties == null)
			properties = DDProperties.instance();
	}
	
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2());
	}
	
	//sends a packet informing the client that there is a link present so it renders properly. (when placed)
	public void onBlockAdded(World par1World, int par2, int par3, int par4) 
	{
		try
		{
			PacketHandler.onLinkCreatedPacket(dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		//	this.updateTick(par1World, par2, par3, par4, new Random());

	}
	public boolean isCollidable()
	{
		return false;
	}

	public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5) {}


	public boolean isOpaqueCube()
	{
		return false;
	}

	/**
	 * Returns whether this block is collideable based on the arguments passed in Args: blockMetaData, unknownFlag
	 */
	public boolean canCollideCheck(int par1, boolean par2)
	{

		return par2;
	}

	/**
	 * Returns Returns true if the given side of this block type should be rendered (if it's solid or not), if the
	 * adjacent block is at the given coordinates. Args: blockAccess, x, y, z, side
	 */
	public boolean isBlockSolid(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		return true;
	}
	//this doesnt do anything yet.
	public int getRenderType()
	{
		if(mod_pocketDim.isPlayerWearingGoogles)
		{
			return 0;
		}

		return 8;
	}

	@SideOnly(Side.CLIENT)

	/**
	 * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
	 * coordinates.  Args: blockAccess, x, y, z, side
	 */
	public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		return true;
	}

	/**
	 * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
	 * cleared to be reused)
	 */
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
	{
		return null;
	}
	//function that regulates how many blocks it eats/ how fast it eates them. 
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		if(!world.isRemote&&dimHelper.instance.getLinkDataFromCoords(x, y, z, world.provider.dimensionId)!=null && properties.RiftGriefingEnabled)
		{
			TileEntityRift rift = (TileEntityRift) world.getBlockTileEntity(x, y, z);
			if(rift.isNearRift)
			{

				int range=4;

				float distance=range+range/4;
				int i=-range;
				int j=-range;
				int k=-range;
				boolean flag=true;
				while (i<range&&flag)
				{
					while (j<range&&flag)
					{
						while (k<range&&flag)
						{
							if(!mod_pocketDim.blocksImmuneToRift.contains(world.getBlockId(x+i, y+j, z+k))&&MathHelper.abs(i)+MathHelper.abs(j)+MathHelper.abs(k)<distance&&!world.isAirBlock(x+i, y+j, z+k))
							{
								if(MathHelper.abs(i)+MathHelper.abs(j)+MathHelper.abs(k)!=0&&random.nextInt(2)==0)
								{
									world.setBlock(x+i, y+j, z+k,0);
									flag=random.nextBoolean()||random.nextBoolean();
								}

							}
							k++;
						}
						k=-range;
						j++;

					}
					j=-range;
					i++;		

				}



			}

		}
	}
	/**
	 * regulates the render effect, especially when multiple rifts start to link up. Has 3 main parts- Grows toward and away from nearest rft, bends toward it, and a randomization function
	 */
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random rand)
	{

		int count;
		//growth in the direction towards the nearby rift
		float xGrowth=0;
		float yGrowth=0;
		float zGrowth=0;
		//growth away from the nearby rift
		float xGrowthn=0;
		float yGrowthn=0;
		float zGrowthn=0;
		//how far the particles are away from original rift. Used to decrease noise the farther they are away. 
		float xChange = 0;
		float yChange = 0;
		float zChange = 0;

		TileEntityRift tile = (TileEntityRift)par1World.getBlockTileEntity(par2, par3, par4);

		//the noise, ie, how far the rift particles are away from the intended location. 
		float offset=0;
		float Xoffset=0;
		float Yoffset=0;
		float Zoffset=0;
		for (count = 0; count < 12 && tile!=null; ++count)
		{
			//TODO change to a switch statement for clarity
			if(tile.xOffset>0)
			{
				if(rand.nextInt(tile.xOffset)==0)
				{
					xGrowth =xGrowth+.15F*tile.xOffset;

				}
			}
			else if(tile.xOffset<0)
			{
				if(rand.nextInt(-tile.xOffset)==0)
				{
					xGrowthn =xGrowthn-.15F*-tile.xOffset;

				}
			}

			if(tile.yOffset>0)
			{
				if(rand.nextInt(tile.yOffset)==0)
				{
					yGrowth =yGrowth+.15F*tile.yOffset;

				}
			}
			else if(tile.yOffset<0)
			{
				if(rand.nextInt(-tile.yOffset)==0)
				{
					yGrowthn =yGrowthn-.15F*-tile.yOffset;

				}
			}

			if(tile.zOffset>0)
			{
				if(rand.nextInt(tile.zOffset)==0)
				{
					zGrowth =zGrowth+.15F*tile.zOffset;

				}
			}
			else if(tile.zOffset<0)
			{
				if(rand.nextInt(-tile.zOffset)==0)
				{
					zGrowthn =zGrowthn-.15F*-tile.zOffset;

				}
			}


			xChange=(float) ((xGrowth+xGrowthn)+rand.nextGaussian()*.05F);
			yChange=(float) ((yGrowth+yGrowthn)+rand.nextGaussian()*.05F);
			zChange=(float) ((zGrowth+zGrowthn)+rand.nextGaussian()*.05F);

			offset=  (float) ((0.2F/(1+Math.abs(xChange)+Math.abs(yChange)+Math.abs(zChange))));
			Xoffset=  (float) ((0.25F/(1+Math.abs(xChange))));

			Yoffset=  (float) ((0.25F/(1+Math.abs(yChange))));
			Zoffset=  (float) ((0.25F/(1+Math.abs(zChange))));




			FMLClientHandler.instance().getClient().effectRenderer.addEffect(new RiftFX(par1World,par2+.5+xChange+Xoffset*rand.nextGaussian(), par3+.5+yChange+Yoffset*rand.nextGaussian() , par4+.5+zChange+Zoffset*rand.nextGaussian(), rand.nextGaussian() * 0.001D, rand.nextGaussian()  * 0.001D, rand.nextGaussian() * 0.001D, FMLClientHandler.instance().getClient().effectRenderer));
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(new RiftFX(par1World,par2+.5-xChange-Xoffset*rand.nextGaussian(), par3+.5-yChange-Yoffset*rand.nextGaussian() , par4+.5-zChange-Zoffset*rand.nextGaussian(), rand.nextGaussian() * 0.001D, rand.nextGaussian()  * 0.001D, rand.nextGaussian() * 0.001D, FMLClientHandler.instance().getClient().effectRenderer));


			if(rand.nextBoolean())
			{
				//renders an extra little blob on top of the actual rift location so its easier to find. Eventually will only render if the player has the goggles. 
				FMLClientHandler.instance().getClient().effectRenderer.addEffect(new GoggleRiftFX(par1World,par2+.5, par3+.5, par4+.5, rand.nextGaussian() * 0.01D, rand.nextGaussian()  * 0.01D, rand.nextGaussian() * 0.01D, FMLClientHandler.instance().getClient().effectRenderer));
			}
			if(tile.shouldClose)
			{
				//renders an opposite color effect if it is being closed by the rift remover
				FMLClientHandler.instance().getClient().effectRenderer.addEffect(new ClosingRiftFX(par1World,par2+.5, par3+.5, par4+.5, rand.nextGaussian() * 0.01D, rand.nextGaussian()  * 0.01D, rand.nextGaussian() * 0.01D, FMLClientHandler.instance().getClient().effectRenderer));

			}

		}

	}
	public int idPicked(World par1World, int par2, int par3, int par4)
	{
		return 0;
	}

	public int idDropped(int par1, Random par2Random, int par3)
	{
		return 0;
	}

	@Override
	public TileEntity createNewTileEntity(World var1) 

	{
		// TODO Auto-generated method stub
		return new TileEntityRift();
	}



}
