package StevenDimDoors.mod_pocketDim.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.TileEntityRift;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDimClient.ClosingRiftFX;
import StevenDimDoors.mod_pocketDimClient.GoggleRiftFX;
import StevenDimDoors.mod_pocketDimClient.RiftFX;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRift extends BlockContainer
{
	private static final float MIN_IMMUNE_HARDNESS = 200.0F;
	private static final int BLOCK_DESTRUCTION_RANGE = 4;
	private static final int BLOCK_DESTRUCTION_VOLUME = (int) Math.pow(2 * BLOCK_DESTRUCTION_RANGE + 1, 3);
	private static final int MAX_BLOCK_SEARCH_CHANCE = 100;
	private static final int BLOCK_SEARCH_CHANCE = 50;
	private static final int MAX_BLOCK_DESTRUCTION_CHANCE = 100;
	private static final int BLOCK_DESTRUCTION_CHANCE = 50;
	
	private final DDProperties properties;
	private final ArrayList<Integer> blocksImmuneToRift;
	
	public BlockRift(int i, int j, Material par2Material, DDProperties properties) 
	{
		super(i, par2Material);
		this.setTickRandomly(true);
		this.properties = properties;
		this.blocksImmuneToRift = new ArrayList<Integer>();
		this.blocksImmuneToRift.add(properties.FabricBlockID);
		this.blocksImmuneToRift.add(properties.PermaFabricBlockID);
		this.blocksImmuneToRift.add(properties.DimensionalDoorID);
		this.blocksImmuneToRift.add(properties.WarpDoorID);
		this.blocksImmuneToRift.add(properties.TransTrapdoorID);
		this.blocksImmuneToRift.add(properties.UnstableDoorID);
		this.blocksImmuneToRift.add(properties.RiftBlockID);
		this.blocksImmuneToRift.add(properties.TransientDoorID);
		this.blocksImmuneToRift.add(Block.blockIron.blockID);
		this.blocksImmuneToRift.add(Block.blockDiamond.blockID);
		this.blocksImmuneToRift.add(Block.blockEmerald.blockID);
		this.blocksImmuneToRift.add(Block.blockGold.blockID);
		this.blocksImmuneToRift.add(Block.blockLapis.blockID);
	}
	
	@Override
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2());
	}
	
	//sends a packet informing the client that there is a link present so it renders properly. (when placed)
	@Override
	public void onBlockAdded(World par1World, int par2, int par3, int par4) 
	{
		try
		{
		//	PacketHandler.onLinkCreatedPacket(dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		//	this.updateTick(par1World, par2, par3, par4, new Random());
	}
	
	@Override
	public boolean isCollidable()
	{
		return false;
	}
	
	@Override
	public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5) {}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	/**
	 * Returns whether this block is collideable based on the arguments passed in Args: blockMetaData, unknownFlag
	 */
	@Override
	public boolean canCollideCheck(int par1, boolean par2)
	{

		return par2;
	}

	/**
	 * Returns Returns true if the given side of this block type should be rendered (if it's solid or not), if the
	 * adjacent block is at the given coordinates. Args: blockAccess, x, y, z, side
	 */
	@Override
	public boolean isBlockSolid(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		return true;
	}
	
	//this doesnt do anything yet.
	@Override
	public int getRenderType()
	{
		if(mod_pocketDim.isPlayerWearingGoogles)
		{
			return 0;
		}

		return 8;
	}

	/**
	 * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
	 * coordinates.  Args: blockAccess, x, y, z, side
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		return true;
	}

	/**
	 * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
	 * cleared to be reused)
	 */
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
	{
		return null;
	}
	
	//function that regulates how many blocks it eats/ how fast it eats them. 
	@Override
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		if (properties.RiftGriefingEnabled && !world.isRemote &&
				PocketManager.getLink(x, y, z, world.provider.dimensionId) != null)
		{
			//Randomly decide whether to search for blocks to destroy. This reduces the frequency of search operations,
			//moderates performance impact, and controls the apparent speed of block destruction.
			if (random.nextInt(MAX_BLOCK_SEARCH_CHANCE) < BLOCK_SEARCH_CHANCE &&
					((TileEntityRift) world.getBlockTileEntity(x, y, z)).isNearRift )
			{
				destroyNearbyBlocks(world, x, y, z, random);
			}
		}
	}
	
	private void destroyNearbyBlocks(World world, int x, int y, int z, Random random)
	{
		HashMap<Point3D, Integer> pointDistances = new HashMap<Point3D, Integer>(BLOCK_DESTRUCTION_VOLUME);
		Queue<Point3D> points = new LinkedList<Point3D>();
		
		//Perform a breadth-first search outwards from the point at which the rift is located. Record the distances
		//of the points we visit to stop the search at its maximum range.
		pointDistances.put(new Point3D(x, y, z), 0);
		addAdjacentBlocks(x, y, z, 0, pointDistances, points);
		while (!points.isEmpty())
		{
			Point3D current = points.remove();
			int distance = pointDistances.get(current);
			
			//If the current block is air, continue searching. Otherwise, try destroying the block.
			if (world.isAirBlock(current.getX(), current.getY(), current.getZ()))
			{
				//Make sure we stay within the search range
				if (distance < BLOCK_DESTRUCTION_RANGE)
				{
					addAdjacentBlocks(current.getX(), current.getY(), current.getZ(), distance, pointDistances, points);
				}
			}
			else
			{
				//Check if the current block is immune to destruction by rifts. If not, randomly decide whether to destroy it.
				//The randomness makes it so the destroyed area appears "noisy" if the rift is exposed to a large surface.
				if (!isBlockImmune(world, current.getX(), current.getY(), current.getZ()) &&
						random.nextInt(MAX_BLOCK_DESTRUCTION_CHANCE) < BLOCK_DESTRUCTION_CHANCE)
				{
					world.setBlockToAir(current.getX(), current.getY(), current.getZ());
				}
			}
		}
	}
	
	private void addAdjacentBlocks(int x, int y, int z, int distance, HashMap<Point3D, Integer> pointDistances, Queue<Point3D> points)
	{
		Point3D[] neighbors = new Point3D[] {
				new Point3D(x - 1, y, z),
				new Point3D(x + 1, y, z),
				new Point3D(x, y - 1, z),
				new Point3D(x, y + 1, z),
				new Point3D(x, y, z - 1),
				new Point3D(x, y, z + 1)
		};
		for (int index = 0; index < neighbors.length; index++)
		{
			if (!pointDistances.containsKey(neighbors[index]))
			{
				pointDistances.put(neighbors[index], distance + 1);
				points.add(neighbors[index]);
			}
		}
	}
	
	
	/**
	 * regulates the render effect, especially when multiple rifts start to link up. Has 3 main parts- Grows toward and away from nearest rft, bends toward it, and a randomization function
	 */
	@Override
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
	
	public boolean isBlockImmune(World world, int x, int y, int z)
	{
		Block block = Block.blocksList[world.getBlockId(x, y, z)];
		if (block != null)
		{
			float hardness = block.getBlockHardness(world, x, y, z);
			return (hardness < 0 || hardness >= MIN_IMMUNE_HARDNESS || blocksImmuneToRift.contains(block.blockID));
		}
		return false;
	}

	@Override
	public int idPicked(World par1World, int par2, int par3, int par4)
	{
		return 0;
	}

	@Override
	public int idDropped(int par1, Random par2Random, int par3)
	{
		return 0;
	}

	@Override
	public TileEntity createNewTileEntity(World var1) 
	{
		return new TileEntityRift();
	}
}